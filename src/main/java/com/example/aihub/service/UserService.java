package com.example.aihub.service;

import org.springframework.http.ResponseEntity;

import com.example.aihub.pojo.UserLoginResponse;
import com.example.aihub.pojo.UserRequest;
import com.example.aihub.pojo.UserResponse;

public interface UserService {
    ResponseEntity<UserLoginResponse> login(UserRequest userRequest);
    ResponseEntity<UserResponse> register(UserRequest userRequest);
}
