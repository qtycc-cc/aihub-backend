package com.example.aihub.service;

import org.springframework.http.ResponseEntity;

import com.example.aihub.pojo.UserLoginResponse;
import com.example.aihub.pojo.UserRequest;
import com.example.aihub.pojo.UserResponse;
import com.example.aihub.pojo.UserStarRequest;

public interface UserService {
    ResponseEntity<UserLoginResponse> login(UserRequest userRequest);
    ResponseEntity<UserResponse> register(UserRequest userRequest);
    ResponseEntity<UserResponse> star(UserStarRequest userStarRequest);
    ResponseEntity<UserResponse> unstar(UserStarRequest userStarRequest);
}
