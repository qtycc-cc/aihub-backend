package com.example.aihub.mapper;

import java.util.List;

import com.example.aihub.pojo.ChatInfo;
import com.example.aihub.pojo.ChatMeta;

public interface ChatInfoMapper {
    List<ChatInfo> findAllChatInfos();
    ChatInfo findChatInfoById(Integer id);
    List<ChatInfo> findChatInfosByUserId(Integer userId);
    List<ChatMeta> findChatMetasByUserId(Integer userId);
    List<ChatInfo> findStarredChatInfosByUserId(Integer userId);
    List<ChatMeta> findStarredChatMetasByUserId(Integer userId);
    Integer findUserIdById(Integer id);
    void insertChatInfo(ChatInfo chatInfo);
    void updateChatInfo(ChatInfo chatInfo);
    void deleteChatInfo(Integer id);
}
