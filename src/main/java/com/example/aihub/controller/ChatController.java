package com.example.aihub.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.aihub.service.ChatService;
import com.example.aihub.pojo.UserChatRequest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Flux;

@CrossOrigin
@RestController
public class ChatController {
    @Autowired
    private ChatService chatService;

    @PostMapping(value = "/api/v1/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody UserChatRequest userChatReq) {
        return chatService.chat(userChatReq);
    }

    @DeleteMapping("/api/v1/chat/{id}")
    public ResponseEntity<String> deleteChat(@PathVariable Integer id) {
        return chatService.deleteChat(id);
    }
}
