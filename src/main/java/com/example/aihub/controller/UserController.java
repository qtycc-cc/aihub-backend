package com.example.aihub.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.aihub.pojo.UserRequest;
import com.example.aihub.pojo.UserResponse;
import com.example.aihub.pojo.UserSetApiKeyRequest;
import com.example.aihub.service.UserService;
import com.example.aihub.utils.JsonUtils;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @SaCheckLogin
    @GetMapping("/api/v1/user")
    public ResponseEntity<UserResponse> getUserInfo() {
        return userService.getUserInfo();
    }

    @SaCheckLogin
    @PostMapping("/api/v1/apikey")
    public ResponseEntity<UserResponse> updateUserInfo(@RequestBody UserSetApiKeyRequest userSetApiKeyRequest) {
        return userService.updateUserApiKey(userSetApiKeyRequest);
    }

    @GetMapping("/api/v1/logout")
    public ResponseEntity<String> logout() {
        StpUtil.logout();
        return ResponseEntity.ok().body(JsonUtils.toJson(Map.of("message", "Logout success!")));
    }

    @PostMapping("/api/v1/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserRequest userRequest) {
        return userService.login(userRequest);
    }

    @PostMapping("/api/v1/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest userRequest) {
        return userService.register(userRequest);
    }

    @SaCheckLogin
    @PostMapping("/api/v1/star/{chatInfoId}")
    public ResponseEntity<UserResponse> star(@PathVariable Integer chatInfoId) {
        return userService.star(chatInfoId);
    }

    @SaCheckLogin
    @DeleteMapping("/api/v1/star/{chatInfoId}")
    public ResponseEntity<UserResponse> unstar(@PathVariable Integer chatInfoId) {
        return userService.unstar(chatInfoId);
    }
}
