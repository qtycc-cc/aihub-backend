package com.example.aihub.pojo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseVII {
    private Integer id;
    private String account;
    private String apiKey;
    private List<ChatMeta> userChatMetas;
    private List<ChatMeta> userStars;
}
