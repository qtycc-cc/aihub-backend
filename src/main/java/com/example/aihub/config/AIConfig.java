package com.example.aihub.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.volcengine.ark.runtime.service.ArkService;

@Configuration
@PropertySource("application.yml")
public class AIConfig {
    @Value("${ai.deepseek.api_key}")
    private String deepseekApiKey;
    @Value("${ai.doubao.api_key}")
    private String doubaoApikey;

    @Bean(name = "deepseekService", destroyMethod = "shutdownExecutor")
    public ArkService deepseekService() {
        return ArkService.builder().apiKey(deepseekApiKey)
                .timeout(Duration.ofMinutes(30))
                .build();
    }

    @Bean(name = "doubaoService", destroyMethod = "shutdownExecutor")
    public ArkService doubaoService() {
        return ArkService.builder().apiKey(doubaoApikey)
                .timeout(Duration.ofMinutes(30))
                .build();
    }
}
