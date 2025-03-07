package com.example.aihub.mapper;

import java.util.List;

import com.example.aihub.pojo.ChatInfo;

public interface ChatInfoMapper {
    List<ChatInfo> findAllChatInfos();
    ChatInfo findChatInfoById(Integer id);
    List<ChatInfo> findChatInfosByUserId(Integer userId);
}
