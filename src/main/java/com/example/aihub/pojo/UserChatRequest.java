package com.example.aihub.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserChatRequest {
    /**
     * 是否为新的聊天
     */
    private boolean newChat;
    /**
     * 提示词
     */
    private String prompt;
    /**
     * 用户消息
     */
    private String message;
}
