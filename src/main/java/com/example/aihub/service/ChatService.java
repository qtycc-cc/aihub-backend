package com.example.aihub.service;

import com.example.aihub.pojo.UserChatRequest;

import reactor.core.publisher.Flux;

public interface ChatService {
    /**
     * 聊天服务
     * @param userChatReq 用户请求对象
     */
    Flux<String> chat(UserChatRequest userChatReq);
}
