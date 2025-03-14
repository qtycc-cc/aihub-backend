package com.example.aihub.service;

import org.springframework.http.ResponseEntity;

import com.example.aihub.pojo.SimpleResponse;
import com.example.aihub.pojo.UserInfoChangeRequest;
import com.example.aihub.pojo.UserLoginResponse;
import com.example.aihub.pojo.UserRequest;
import com.example.aihub.pojo.UserResponse;

public interface UserService {
    ResponseEntity<UserResponse> getUserInfo();
    ResponseEntity<SimpleResponse> updateUserInfo(UserInfoChangeRequest userInfoChangeRequest);
    ResponseEntity<UserLoginResponse> login(UserRequest userRequest);
    ResponseEntity<SimpleResponse> register(UserRequest userRequest);
    ResponseEntity<SimpleResponse> star(Integer chatInfoId);
    ResponseEntity<SimpleResponse> unstar(Integer chatInfoId);
}
