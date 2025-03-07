package com.example.aihub.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aihub.pojo.ChatRespType;
import com.example.aihub.pojo.UserChatRequest;
import com.example.aihub.pojo.UserChatResponse;
import com.example.aihub.service.ChatService;
import com.example.aihub.utils.Console;
import com.example.aihub.utils.JsonUtils;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;

import io.reactivex.Flowable;
import reactor.core.publisher.Flux;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private ArkService arkService;

    private final String MODEL = "deepseek-r1-250120";

    private final String REASON_PREFIX = "reason: ";

    private List<ChatMessage> chatMessages;

    public Flux<Object> chat(UserChatRequest userChatReq) {
        Console.log(userChatReq.toString());

        if (userChatReq.isNewChat()) {
            chatMessages = new ArrayList<>();
            // TODO: 保存旧的数据
        }

        // 提示词
        if (userChatReq.getPrompt() != null) {
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
                .model(MODEL)
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
                            .chatInfoId(1)
                            .topic("AI 研究")
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
                )));
    }
}
