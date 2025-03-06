package com.example.aihub.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.aihub.service.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.aihub.pojo.UserChatRequest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

@CrossOrigin
@RestController
public class ChatController {
    @Autowired
    private ChatService chatService;

    @PostMapping(value = "/api/v1/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object> chat(@RequestBody UserChatRequest userChatReq) {
        // return chatService.chat(userChatReq);
        return Flux.concat(
                // 1️⃣ 先返回聊天的元数据（ID、主题等）
                Flux.just(toJson(Map.of("type", "metadata", "chatId", 1, "topic", "AI研究")) + "\n\n"),

                // 2️⃣ 然后流式返回消息内容
                chatService.chat(userChatReq)
                        .map(content -> toJson(Map.of("type", "message", "data", content)) + "\n\n"),

                // 3️⃣ 结束标志，告诉前端流结束了
                Flux.just(toJson(Map.of("type", "end")) + "\n\n"));
    }

    private String toJson(Map<String, Object> data) {
        try {
            return new ObjectMapper().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 转换失败", e);
        }
    }
}
