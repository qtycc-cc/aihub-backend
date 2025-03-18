package com.example.aihub.service.impl;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.aihub.annotation.CheckDataOwner;
import com.example.aihub.exception.BussinessException;
import com.example.aihub.exception.ModelNotEqualException;
import com.example.aihub.exception.MyIllegalArgumentException;
import com.example.aihub.exception.PermissionDeniedException;
import com.example.aihub.mapper.ChatInfoMapper;
import com.example.aihub.pojo.ChatInfo;
import com.example.aihub.pojo.ChatInfoS;
import com.example.aihub.pojo.ChatRespType;
import com.example.aihub.pojo.ModelType;
import com.example.aihub.pojo.SimpleResponse;
import com.example.aihub.pojo.User;
import com.example.aihub.pojo.UserChatRequest;
import com.example.aihub.pojo.UserChatResponse;
import com.example.aihub.service.ChatService;
import com.example.aihub.service.ResourceService;
import com.example.aihub.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionResult;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import io.reactivex.Flowable;
import reactor.core.publisher.Flux;

@Service
public class ChatServiceImpl implements ChatService, ResourceService {
    private ArkService arkService;
    private ArkService topicGenerationService;
    @Autowired
    private ChatInfoMapper chatInfoMapper;

    private final String GET_TOPIC_PROMPT = """
            你是一名擅长会话的助理，你需要将用户的会话总结为 10 个字以内的标题，标题语言与用户的首要语言一致，不要使用标点符号和其他特殊符号
            """;
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

        User currentUser = (User) StpUtil.getSession().get("currentUser");

        String apiKey = currentUser.getApiKey();

        if (apiKey == null) {
            throw new PermissionDeniedException("You do not have correct apikey!");
        }

        arkService = ArkService.builder().apiKey(apiKey)
                .timeout(Duration.ofMinutes(30))
                .build();

        Integer userId = StpUtil.getLoginIdAsInt();
        Integer chatInfoId;
        String chatTopic;
        ModelType model;
        StringBuilder reasonContent = new StringBuilder("");
        StringBuilder assistantContent = new StringBuilder("");

        if (userChatReq.getChatInfoId() == null) {
            chatTopic = getTopic(userChatReq.getMessage(), apiKey); // 创建新会话的时候需要创建topic
            chatMessages = new CopyOnWriteArrayList<>();
            // some setting
            ChatInfo newChatInfo = new ChatInfo();
            newChatInfo.setUserId(userId);
            newChatInfo.setContent("[]");
            newChatInfo.setTopic(chatTopic);
            newChatInfo.setModel(userChatReq.getModel());
            chatInfoMapper.insertChatInfo(newChatInfo);
            chatInfoId = newChatInfo.getId();
            model = newChatInfo.getModel();
        } else {
            ChatInfo chatInfo = chatInfoMapper.findChatInfoById(userChatReq.getChatInfoId());
            chatInfoId = chatInfo.getId();
            chatTopic = chatInfo.getTopic();
            model = chatInfo.getModel();
            if (!model.equals(userChatReq.getModel())) {
                throw new ModelNotEqualException("Your model is not equal with history!");
            }
            chatMessages = JsonUtils.fromJson(chatInfo.getContent(), new TypeReference<List<ChatMessage>>() {
            });
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
                .fromPublisher(arkService.streamChatCompletion(chatCompletionRequest))
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
                                .build())),

                // 2️⃣ 然后流式返回消息内容
                Flux.from(flowableResponse)
                        .map(content -> JsonUtils.toJson(
                                UserChatResponse.builder()
                                        .type(ChatRespType.MESSAGE)
                                        .data(content)
                                        .build())),

                // 3️⃣ 结束标志，告诉前端流结束了
                Flux.just(JsonUtils.toJson(
                        UserChatResponse.builder()
                                .type(ChatRespType.END)
                                .build())))
                .doOnComplete(() -> {
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
    public ResponseEntity<ChatInfoS> getChatInfo(Integer id) {
        if (id == null) {
            throw new MyIllegalArgumentException("Chat id can not be null");
        }
        ChatInfo chatInfo = chatInfoMapper.findChatInfoById(id);
        Integer starId = chatInfoMapper.findStarIdByChatInfoId(id);
        ChatInfoS chatInfoS = new ChatInfoS();
        chatInfoS.setId(id);
        chatInfoS.setUserId(chatInfo.getUserId());
        chatInfoS.setTopic(chatInfo.getTopic());
        chatInfoS.setContent(chatInfo.getContent());
        chatInfoS.setModel(chatInfo.getModel());
        if (starId == null) {
            chatInfoS.setStarred(false);
        } else {
            chatInfoS.setStarred(true);
        }
        return ResponseEntity.ok().body(chatInfoS);
    }

    @Override
    @CheckDataOwner(serviceClass = ChatServiceImpl.class)
    public ResponseEntity<SimpleResponse> deleteChat(Integer id) {
        if (id == null) {
            throw new MyIllegalArgumentException("Chat id can not be null");
        }
        chatInfoMapper.deleteChatInfo(id);
        return ResponseEntity.ok().body(new SimpleResponse("Chat has been deleted!"));
    }

    @Override
    public Integer getOwnerIdById(Integer id) {
        return chatInfoMapper.findUserIdById(id);
    }

    private String getTopic(String message, String apiKey) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            List<ChatMessage> messages = new CopyOnWriteArrayList<>();
            ChatMessage sysMessage = ChatMessage.builder()
                    .role(ChatMessageRole.SYSTEM)
                    .content(GET_TOPIC_PROMPT)
                    .build();
            messages.add(sysMessage);
            ChatMessage titleMessage = ChatMessage.builder()
                    .role(ChatMessageRole.USER)
                    .content(message)
                    .build();
            messages.add(titleMessage);
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(DOUBAO_MODEL) // 使用成本较低的模型
                    .messages(messages)
                    .build();
            topicGenerationService = ArkService.builder().apiKey(apiKey)
                    .timeout(Duration.ofMinutes(30))
                    .build();
            ChatCompletionResult result = topicGenerationService.createChatCompletion(request);
            String content = (String) result.getChoices().get(0).getMessage().getContent();
            return content.replaceAll("\"", "").trim();
        });
        try {
            return future.get(8, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new BussinessException();
        }
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

    private void syncChatInfoToDatabase(Integer userId, UserChatRequest userChatRequest,
            List<ChatMessage> chatMessages) {
        if (chatMessages == null) {
            return;
        }

        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setId(userChatRequest.getChatInfoId());
        chatInfo.setUserId(userId);
        chatInfo.setContent(JsonUtils.toJson(chatMessages));

        if (chatInfo.getId() == null) {
            chatInfoMapper.insertChatInfo(chatInfo);
        } else {
            chatInfoMapper.updateChatInfo(chatInfo);
        }
    }
}
