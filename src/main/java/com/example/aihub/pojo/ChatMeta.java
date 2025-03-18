package com.example.aihub.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMeta {
    private Integer id;
    private Integer userId;
    private String topic;
    private ModelType model;
}
