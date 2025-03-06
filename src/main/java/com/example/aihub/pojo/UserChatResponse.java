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
public class UserChatResponse {
    private ChatRespType type;
    @Nullable
    private Integer chatInfoId;
    @Nullable
    private String topic;
    @Nullable
    private String data;
}
