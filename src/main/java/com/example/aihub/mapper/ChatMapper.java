package com.example.aihub.mapper;

import java.util.List;

import com.example.aihub.pojo.Chat;

public interface ChatMapper {
    List<Chat> findAllChats();
    Chat findChatById(Integer id);
    List<Chat> findChatByUserId(Integer userId);
}
