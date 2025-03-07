package com.example.aihub.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * json-web-token工具类
 * @version 1.0
 * @author chatGPT
 * @since 1.0
 */
@Component
public class JWTUtils {
    private static final String SECRET_KEY = "VYNBHh8jSfQ4ykayVpuS";
    private static final long EXPIRATION_TIME = 1000 * 60 * 15; // 15 minutes
    private static final long REFRESH_TIME = 1000 * 60 * 60 * 24 * 7; // 7 day

    /**
     * 生成token
     * @param key token中包含的信息
     * @return token
     */
    public static String generateToken(String key) {
        return Jwts.builder()
                .setSubject(key)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    /**
     * 生成refreshToken 时间更长 更不容易过期
     * @param key token中包含的信息
     * @return token
     */
    public static String generateRefreshToken(String key) {
        return Jwts.builder()
                .setSubject(key)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    /**
     * 解析token
     * @param token 需要解析的token
     * @return claims
     */
    public static Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            return null; // 过期的token直接返回null
        }
    }

    /**
     * 获取token中包含的信息
     * @param token 需要解析的token
     * @return token中包含的信息
     */
    public static String extractKey(String token) {
        Claims claims = extractClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * 判断token是否过期
     * @param token 待验证的token
     * @return {@code true}表示过期 {@code false}表示未过期
     */
    public static boolean isTokenExpired(String token) {
        Claims claims = extractClaims(token);
        return (claims != null && claims.getExpiration().before(new Date())) || claims == null;
    }

    /**
     * 验证token合法性
     * @param token 待验证的token
     * @param key 用户信息
     * @return {@code true}表示合法 {@code false}表示不合法
     */
    public static boolean validateToken(String token, String key) {
        String extractedKey = extractKey(token);
        return extractedKey != null && extractedKey.equals(key) && !isTokenExpired(token);
    }

}