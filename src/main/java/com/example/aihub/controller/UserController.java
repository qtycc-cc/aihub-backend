package com.example.aihub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aihub.pojo.UserRequest;
import com.example.aihub.pojo.UserResponse;
import com.example.aihub.pojo.UserStarRequest;
import com.example.aihub.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@CrossOrigin
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/api/v1/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserRequest userRequest) {
        return userService.login(userRequest);
    }

    @PostMapping("/api/v1/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest userRequest) {
        return userService.register(userRequest);
    }

    @PostMapping("/api/v1/star")
    public ResponseEntity<UserResponse> star(@RequestBody UserStarRequest userStarRequest) {
        return userService.star(userStarRequest);
    }

    @DeleteMapping("/api/v1/star")
    public ResponseEntity<UserResponse> unstar(@RequestBody UserStarRequest userStarRequest) {
        return userService.unstar(userStarRequest);
    }
}
