package com.example.aihub.service;

import org.springframework.http.ResponseEntity;

import com.example.aihub.pojo.RefreshTokenRequest;
import com.example.aihub.pojo.RefreshTokenResponse;

public interface TokenService {
    ResponseEntity<RefreshTokenResponse> refreshToken(RefreshTokenRequest refreshTokenReq);
}
