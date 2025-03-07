package com.example.aihub.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.aihub.interceptor.LoginInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(
                        "/api/v1/login/**",
                        "/api/v1/register/**",
                        "/api/v1/refresh/**",
                        "/doc.html",
                        "/webjars/**",
                        "/v3/api-docs/swagger-config",
                        "/v3/api-docs/**",
                        "/swagger-ui/index.html",
                        "swagger-ui.html"
                );
    }
}
