package com.example.aihub.controller;

import org.springframework.web.bind.annotation.RestController;

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;

import io.reactivex.Flowable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

@CrossOrigin
@RestController
public class ChatController {
    @Autowired
    private ArkService arkService;

    private final String MODEL = "deepseek-r1-250120";

    private List<ChatMessage> chatMessages = new ArrayList<>();

    @GetMapping(value = "/api/v1/chat/{request}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object> chat(@PathVariable String request) {
        // 创建用户消息
        ChatMessage userMessage = ChatMessage.builder()
                .role(ChatMessageRole.USER) // 设置消息角色为用户
                .content(request) // 设置消息内容
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
        Flowable<String> flowableResponse = Flowable.fromPublisher(arkService.streamChatCompletion(chatCompletionRequest))
                .map(choice -> {
                    if (choice.getChoices().size() > 0) {
                        ChatMessage message = choice.getChoices().get(0).getMessage();
                        String responseContent = (String)message.getContent();
                        // 处理模型输出的内容
                        if (message.getReasoningContent() != null && !message.getReasoningContent().isEmpty()) {
                            responseContent = message.getReasoningContent(); // 使用推理内容
                        }
                        return responseContent;
                    }
                    return "";
                })
                .doOnError(Throwable::printStackTrace);

        return Flux.from(flowableResponse);
    }
}
