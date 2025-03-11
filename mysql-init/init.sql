CREATE TABLE IF NOT EXISTS `user` (
    `id` int NOT NULL AUTO_INCREMENT,
    `account` varchar(250) NOT NULL,
    `password` varchar(250) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_account` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `chatinfo` (
    `id` int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL,
    `content` longtext NOT NULL,
    `topic` varchar(250) DEFAULT NULL COMMENT '会话主题',
    `model` varchar(250) DEFAULT 'deepseek-r1',
    PRIMARY KEY (`id`),
    KEY `fk_chatinfo_user` (`user_id`),
    CONSTRAINT `fk_chatinfo_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='聊天信息表';

CREATE TABLE IF NOT EXISTS `star` (
    `id` int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL,
    `chatinfo_id` int NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_chatinfo` (`user_id`,`chatinfo_id`),
    KEY `fk_star_chatinfo` (`chatinfo_id`),
    CONSTRAINT `fk_star_chatinfo` FOREIGN KEY (`chatinfo_id`) REFERENCES `chatinfo` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_star_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户收藏';