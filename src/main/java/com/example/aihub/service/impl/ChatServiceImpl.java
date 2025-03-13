package com.example.aihub.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.aihub.annotation.CheckDataOwner;
import com.example.aihub.exception.ModelNotEqualException;
import com.example.aihub.exception.MyIllegalArgumentException;
import com.example.aihub.mapper.ChatInfoMapper;
import com.example.aihub.pojo.ChatInfo;
import com.example.aihub.pojo.ChatRespType;
import com.example.aihub.pojo.ModelType;
import com.example.aihub.pojo.UserChatRequest;
import com.example.aihub.pojo.UserChatResponse;
import com.example.aihub.service.ChatService;
import com.example.aihub.service.ResourceService;
import com.example.aihub.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import io.reactivex.Flowable;
import reactor.core.publisher.Flux;

@Service
public class ChatServiceImpl implements ChatService, ResourceService {
    @Autowired
    @Qualifier("deepseekService")
    private ArkService deepseekService;
    @Autowired
    @Qualifier("doubaoService")
    private ArkService doubaoService;
    @Autowired
    private ChatInfoMapper chatInfoMapper;

    private final String DEEPSEEK_MODEL = "deepseek-r1-250120";
    private final String DOUBAO_MODEL = "doubao-1-5-pro-256k-250115";
    private final String REASON_PREFIX = "reason: ";
    private List<ChatMessage> chatMessages;

    @Override
    @CheckDataOwner(serviceClass = ChatServiceImpl.class, idField = "chatInfoId")
    public Flux<String> chat(UserChatRequest userChatReq) {
        if (userChatReq == null
            || StrUtil.isBlank(userChatReq.getMessage())
            || userChatReq.getModel() == null) {
                throw new MyIllegalArgumentException("Request cannot be empty!");
        }

        Integer userId = StpUtil.getLoginIdAsInt();
        Integer chatInfoId;
        String chatTopic;
        ModelType model;
        StringBuilder reasonContent = new StringBuilder("");
        StringBuilder assistantContent = new StringBuilder("");

        if (userChatReq.getChatInfoId() == null) {
            chatMessages = new CopyOnWriteArrayList<>();
            ChatInfo newChatInfo = new ChatInfo();
            newChatInfo.setUserId(userId);
            newChatInfo.setContent("[]");
            newChatInfo.setTopic(userChatReq.getMessage());
            newChatInfo.setModel(userChatReq.getModel());
            chatInfoMapper.insertChatInfo(newChatInfo);
            chatInfoId = newChatInfo.getId();
            chatTopic = newChatInfo.getTopic();
            model = newChatInfo.getModel();
        } else {
            ChatInfo chatInfo = chatInfoMapper.findChatInfoById(userChatReq.getChatInfoId());
            chatInfoId = chatInfo.getId();
            chatTopic = chatInfo.getTopic();
            model = chatInfo.getModel();
            if (!model.equals(userChatReq.getModel())) {
                throw new ModelNotEqualException("Your model is not equal with history!");
            }
            chatMessages = JsonUtils.fromJson(chatInfo.getContent(), new TypeReference<List<ChatMessage>>() {});
        }

        // 提示词
        if (userChatReq.getPrompt() != null && !StrUtil.isBlank(userChatReq.getPrompt())) {
            ChatMessage sysMessage = ChatMessage.builder()
                    .role(ChatMessageRole.SYSTEM)
                    .content(userChatReq.getPrompt())
                    .build();
            chatMessages.add(sysMessage);
        }
        // 创建用户消息
        ChatMessage userMessage = ChatMessage.builder()
                .role(ChatMessageRole.USER) // 设置消息角色为用户
                .content(userChatReq.getMessage()) // 设置消息内容
                .build();
        // 将用户消息添加到消息列表
        chatMessages.add(userMessage);
        // 创建聊天完成请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(getModel(userChatReq.getModel()))
                .messages(chatMessages) // 设置消息列表
                .build();

        // 发送聊天完成请求
        // 返回流式数据
        Flowable<String> flowableResponse = Flowable
                .fromPublisher(getService(userChatReq.getModel()).streamChatCompletion(chatCompletionRequest))
                .map(choice -> {
                    if (choice.getChoices().size() > 0) {
                        ChatMessage message = choice.getChoices().get(0).getMessage();
                        String responseContent = (String) message.getContent();
                        // 处理模型输出的内容
                        if (message.getReasoningContent() != null && !message.getReasoningContent().isEmpty()) {
                            responseContent = REASON_PREFIX + message.getReasoningContent(); // 使用推理内容
                            reasonContent.append(message.getReasoningContent());
                        } else {
                            assistantContent.append(message.getContent());
                        }
                        return responseContent;
                    }
                    return "";
                })
                .doOnError(Throwable::printStackTrace);

        return Flux.concat(
                // 1️⃣ 先返回聊天的元数据（ID、主题等）
                Flux.just(JsonUtils.toJson(
                        UserChatResponse.builder()
                            .type(ChatRespType.METADATA)
                            .chatInfoId(chatInfoId)
                            .topic(chatTopic)
                            .model(model)
                            .build()
                    )),

                // 2️⃣ 然后流式返回消息内容
                Flux.from(flowableResponse)
                        .map(content -> JsonUtils.toJson(
                            UserChatResponse.builder()
                                .type(ChatRespType.MESSAGE)
                                .data(content)
                                .build()
                        )),

                // 3️⃣ 结束标志，告诉前端流结束了
                Flux.just(JsonUtils.toJson(
                    UserChatResponse.builder()
                                .type(ChatRespType.END)
                                .build()
                ))).doOnComplete(() -> {
                    chatMessages.add(ChatMessage.builder()
                                                .role(ChatMessageRole.ASSISTANT)
                                                .reasoningContent(reasonContent.toString())
                                                .build());
                    chatMessages.add(ChatMessage.builder()
                                                .role(ChatMessageRole.ASSISTANT)
                                                .content(assistantContent.toString())
                                                .build());
                    userChatReq.setChatInfoId(chatInfoId);
                    syncChatInfoToDatabase(userId, userChatReq, chatMessages);
                });
    }

    @Override
    @CheckDataOwner(serviceClass = ChatServiceImpl.class)
    public ResponseEntity<String> deleteChat(Integer id) {
        if (id == null) {
            throw new MyIllegalArgumentException("Chat id can not be null");
        }
        chatInfoMapper.deleteChatInfo(id);
        return ResponseEntity.ok().body(JsonUtils.toJson(Map.of("message", "Chat has been deleted!")));
    }

    @Override
    public Integer getOwnerIdById(Integer id) {
        return chatInfoMapper.findUserIdById(id);
    }

    private String getModel(ModelType model) {
        String res;
        switch (model) {
            case DEEPSEEK:
                res = DEEPSEEK_MODEL;
                break;
            case DOUBAO:
                res = DOUBAO_MODEL;
                break;
            default:
                res = DEEPSEEK_MODEL;
                break;
        }
        return res;
    }

    private ArkService getService(ModelType model) {
        switch (model) {
            case DEEPSEEK:
                return this.deepseekService;
            case DOUBAO:
                return this.doubaoService;
            default:
                return this.deepseekService;
        }
    }

    private void syncChatInfoToDatabase(Integer userId, UserChatRequest userChatRequest, List<ChatMessage> chatMessages) {
        if (chatMessages == null) {
            return;
        }

        ChatInfo chatInfo = ChatInfo.builder()
                                .id(userChatRequest.getChatInfoId())
                                .userId(userId)
                                .content(JsonUtils.toJson(chatMessages))
                                .build();

        if (chatInfo.getId() == null) {
            chatInfoMapper.insertChatInfo(chatInfo);
        } else {
            chatInfoMapper.updateChatInfo(chatInfo);
        }
    }
}
