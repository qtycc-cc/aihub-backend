package com.example.aihub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.aihub.pojo.SimpleResponse;
import com.example.aihub.pojo.UserInfoChangeRequest;
import com.example.aihub.pojo.UserLoginResponse;
import com.example.aihub.pojo.UserLoginResponseVII;
import com.example.aihub.pojo.UserRequest;
import com.example.aihub.pojo.UserResponse;
import com.example.aihub.pojo.UserResponseVII;
import com.example.aihub.service.UserService;

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
    @GetMapping("/api/v2/user")
    public ResponseEntity<UserResponseVII> getUserInfoVII() {
        return userService.getUserInfoVII();
    }

    @SaCheckLogin
    @PostMapping("/api/v1/user")
    public ResponseEntity<SimpleResponse> updateUserInfo(@RequestBody UserInfoChangeRequest userInfoChangeRequest) {
        return userService.updateUserInfo(userInfoChangeRequest);
    }

    @GetMapping("/api/v1/logout")
    public ResponseEntity<SimpleResponse> logout() {
        StpUtil.logout();
        return ResponseEntity.ok().body(new SimpleResponse("Logout success!"));
    }

    @PostMapping("/api/v1/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserRequest userRequest) {
        return userService.login(userRequest);
    }

    @PostMapping("/api/v2/login")
    public ResponseEntity<UserLoginResponseVII> loginVII(@RequestBody UserRequest userRequest) {
        return userService.loginVII(userRequest);
    }

    @PostMapping("/api/v1/register")
    public ResponseEntity<SimpleResponse> register(@RequestBody UserRequest userRequest) {
        return userService.register(userRequest);
    }

    @SaCheckLogin
    @PostMapping("/api/v1/star/{chatInfoId}")
    public ResponseEntity<SimpleResponse> star(@PathVariable Integer chatInfoId) {
        return userService.star(chatInfoId);
    }

    @SaCheckLogin
    @DeleteMapping("/api/v1/star/{chatInfoId}")
    public ResponseEntity<SimpleResponse> unstar(@PathVariable Integer chatInfoId) {
        return userService.unstar(chatInfoId);
    }
}
