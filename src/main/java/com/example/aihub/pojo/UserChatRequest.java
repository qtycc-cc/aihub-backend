package com.example.aihub.pojo;

import io.micrometer.common.lang.Nullable;
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
     * 用户id
     */
    private Integer userId;
    /**
     * 聊天id
     * {@code null} when the chat is new
     */
    @Nullable
    private Integer chatInfoId;
    /**
     * 是否为新的聊天
     */
    // private boolean newChat;
    /**
     * 提示词
     */
    private String prompt;
    /**
     * 用户消息
     */
    private String message;
}
