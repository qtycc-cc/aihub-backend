package com.example.aihub.interceptor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.example.aihub.utils.JWTUtils;

/**
 * 拦截器类，用于拦截请求并验证 Token
 * @version 1.0
 * @author 漆廷煜
 * @since 1.0
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final String AUTHORIZATION_PREFIX = "Authorization";
    private final String REFRESH_TOKEN_PREFIX = "Refresh-Token";
    private final String JWT_PREFIX = "Bearer ";
    /**
     * 在请求处理之前进行拦截
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // **注意排除预检请求**
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }

        String accessToken = request.getHeader(AUTHORIZATION_PREFIX);
        String refreshToken = request.getHeader(REFRESH_TOKEN_PREFIX);

        boolean accessTokenValid = false;
        boolean refreshTokenValid = false;

        // 验证 Access Token
        if (accessToken != null && accessToken.startsWith(JWT_PREFIX)) {
            accessToken = accessToken.substring(JWT_PREFIX.length());
            accessTokenValid = !JWTUtils.isTokenExpired(accessToken);
        }

        // 验证 Refresh Token
        if (refreshToken != null && !refreshToken.isEmpty()) {
            refreshTokenValid = !JWTUtils.isTokenExpired(refreshToken);
        }

        // 如果 Access Token 有效，继续请求
        if (accessTokenValid) {
            return true;
        }

        // 如果 Access Token 无效但 Refresh Token 有效，尝试刷新 Access Token
        if (!accessTokenValid && refreshTokenValid) {
            String key = JWTUtils.extractKey(refreshToken); // 从 refreshToken 中提取用户信息
            String newAccessToken = JWTUtils.generateToken(key); // 生成新的 Access Token

            response.setHeader("New-Access-Token", newAccessToken);
            return true;
        }

        // 两个 Token 都无效，返回401状态
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"reason\": \"Invalid token!\"}");
        return false;
    }
}
