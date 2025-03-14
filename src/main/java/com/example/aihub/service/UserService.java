package com.example.aihub.service;

import org.springframework.http.ResponseEntity;

import com.example.aihub.pojo.UserLoginResponse;
import com.example.aihub.pojo.UserRequest;
import com.example.aihub.pojo.UserResponse;
import com.example.aihub.pojo.UserSetApiKeyRequest;

public interface UserService {
    ResponseEntity<UserResponse> getUserInfo();
    ResponseEntity<UserResponse> updateUserApiKey(UserSetApiKeyRequest userSetApiKeyRequest);
    ResponseEntity<UserLoginResponse> login(UserRequest userRequest);
    ResponseEntity<UserResponse> register(UserRequest userRequest);
    ResponseEntity<UserResponse> star(Integer chatInfoId);
    ResponseEntity<UserResponse> unstar(Integer chatInfoId);
}
