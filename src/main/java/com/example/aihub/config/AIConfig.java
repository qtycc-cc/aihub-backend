package com.example.aihub.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.volcengine.ark.runtime.service.ArkService;

@Component
@Configuration
@PropertySource("application.yml")
public class AIConfig {
    @Value("${deepseek.api_key}")
    private String apiKey;

    @Bean(destroyMethod = "shutdownExecutor")
    public ArkService arkService() {
        return ArkService.builder().apiKey(apiKey)
                .timeout(Duration.ofMinutes(30))
                .build();
    }
}
