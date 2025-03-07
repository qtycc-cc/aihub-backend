package com.example.aihub.service.impl;

import org.springframework.http.ResponseEntity;

import com.example.aihub.exception.InvalidCredentialsException;
import com.example.aihub.pojo.RefreshTokenRequest;
import com.example.aihub.pojo.RefreshTokenResponse;
import com.example.aihub.service.TokenService;
import com.example.aihub.utils.JWTUtils;

import cn.hutool.core.util.StrUtil;

public class TokenServiceImpl implements TokenService {

    @Override
    public ResponseEntity<RefreshTokenResponse> refreshToken(RefreshTokenRequest refreshTokenReq) {
        if (refreshTokenReq == null || StrUtil.isBlank(refreshTokenReq.getRefreshToken())) {
            throw new IllegalArgumentException("Refresh token can not be empty!");
        }
        String refreshToken = refreshTokenReq.getRefreshToken();
        if (JWTUtils.isTokenExpired(refreshToken)) {
            throw new InvalidCredentialsException("Invalid refresh token!");
        }
        String account = JWTUtils.extractKey(refreshToken);
        String newAccessToken = JWTUtils.generateToken(account);
        return ResponseEntity.ok().body(
            RefreshTokenResponse.builder()
                            .newAccessToken(newAccessToken)
                            .build()
        );
    }
}
