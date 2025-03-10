package com.example.aihub.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * 用户收藏
 */
public class Star {
    private Integer id;
    private Integer userId;
    private Integer chatinfoId;
}
