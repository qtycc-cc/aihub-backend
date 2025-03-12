package com.example.aihub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
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
