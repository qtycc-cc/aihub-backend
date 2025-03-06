package com.example.aihub.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.aihub.service.ChatService;
import com.example.aihub.pojo.UserChatRequest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
        return chatService.chat(userChatReq);
    }
}
