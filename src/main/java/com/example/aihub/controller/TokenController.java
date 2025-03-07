package com.example.aihub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.aihub.pojo.RefreshTokenRequest;
import com.example.aihub.pojo.RefreshTokenResponse;
import com.example.aihub.service.TokenService;

@CrossOrigin
@RestController
public class TokenController {
    @Autowired
    private TokenService tokenService;

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return tokenService.refreshToken(refreshTokenRequest);
    }
}
