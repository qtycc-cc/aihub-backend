package com.example.aihub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.aihub.utils.Console;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()

                // 指定 [拦截路由] 与 [放行路由]
                .addInclude("/**").addExclude(
                    "/api/v1/login/**",
                        "/api/v1/register/**",
                        "/api/v1/logout/**",
                        "/doc.html",
                        "/webjars/**",
                        "/v3/api-docs/swagger-config",
                        "/v3/api-docs/**",
                        "/swagger-ui/index.html",
                        "swagger-ui.html")

                // 认证函数: 每次请求执行
                .setAuth(obj -> {
                    SaManager.getLog().debug("----- 请求path={}  提交token={}", SaHolder.getRequest().getRequestPath(),
                            StpUtil.getTokenValue());
                })

                // 异常处理函数：每次认证函数发生异常时执行此函数
                .setError(e -> {
                    return SaResult.error(e.getMessage());
                })

                // 前置函数：在每次认证函数之前执行
                .setBeforeAuth(obj -> {

                    // 获得客户端domain
                    SaRequest request = SaHolder.getRequest();
                    String origin = request.getHeader("Origin");
                    if (origin == null) {
                        origin = request.getHeader("Referer");
                    }
                    // ---------- 设置跨域响应头 ----------
                    SaHolder.getResponse()
                            // 允许第三方 Cookie
                            // .setHeader("Access-Control-Allow-Credentials", "true")
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", origin)
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "*")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600");

                    // 如果是预检请求，则立即返回到前端
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            .free(r -> Console.log("Option method can be passed!"))
                            .back();
                });
    }
}
