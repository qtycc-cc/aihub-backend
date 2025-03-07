package com.example.aihub.service;

import org.springframework.http.ResponseEntity;

import com.example.aihub.pojo.UserRequest;
import com.example.aihub.pojo.UserResponse;

public interface UserService {
    ResponseEntity<UserResponse> login(UserRequest userRequest);
    ResponseEntity<UserResponse> register(UserRequest userRequest);
}
