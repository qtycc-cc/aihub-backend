package com.example.aihub.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.aihub.exception.InvalidCredentialsException;
import com.example.aihub.mapper.ChatInfoMapper;
import com.example.aihub.mapper.UserMapper;
import com.example.aihub.pojo.ChatInfo;
import com.example.aihub.pojo.User;
import com.example.aihub.pojo.UserRequest;
import com.example.aihub.pojo.UserResponse;
import com.example.aihub.service.UserService;
import com.example.aihub.utils.MD5Utils;

import cn.hutool.core.util.StrUtil;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ChatInfoMapper chatInfoMapper;

    @Override
    public ResponseEntity<UserResponse> login(UserRequest userRequest) {
        if (userRequest == null
                || StrUtil.isBlank(userRequest.getAccount())
                || StrUtil.isBlank(userRequest.getPassword())) {
            throw new IllegalArgumentException("Account or password can not be empty!");
        }
        User user = userMapper.findUserByAccount(userRequest.getAccount());
        if (user == null || !MD5Utils.checkPassword(userRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Account or password wrong!");
        }
        List<ChatInfo> userChatInfos = chatInfoMapper.findChatInfosByUserId(user.getId());
        UserResponse userResponse = UserResponse.builder()
                                            .id(user.getId())
                                            .account(user.getAccount())
                                            .password(user.getPassword())
                                            .userChatInfos(userChatInfos)
                                            .build();
        return ResponseEntity.ok().body(userResponse);
    }

    @Override
    public ResponseEntity<UserResponse> register(UserRequest userRequest) {
        if (userRequest == null
                || StrUtil.isBlank(userRequest.getAccount())
                || StrUtil.isBlank(userRequest.getPassword())) {
            throw new IllegalArgumentException("Account or password can not be empty!");
        }
        User user = userMapper.findUserByAccount(userRequest.getAccount());
        if (user != null) {
            throw new DuplicateKeyException("Account has been registered!");
        }
        userMapper.insertUser(userRequest.getAccount(), MD5Utils.getMD5String(userRequest.getPassword()));
        user = userMapper.findUserByAccount(userRequest.getAccount());
        UserResponse userResponse = UserResponse.builder()
                                            .id(user.getId())
                                            .account(user.getAccount())
                                            .password(user.getPassword())
                                            .userChatInfos(new ArrayList<>())
                                            .build();
        return ResponseEntity.ok().body(userResponse);
    }
}
