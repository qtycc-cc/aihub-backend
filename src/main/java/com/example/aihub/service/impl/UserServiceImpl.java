package com.example.aihub.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.aihub.annotation.CheckDataOwner;
import com.example.aihub.exception.AccountHasBeenUsedException;
import com.example.aihub.exception.InvalidCredentialsException;
import com.example.aihub.exception.MyIllegalArgumentException;
import com.example.aihub.mapper.ChatInfoMapper;
import com.example.aihub.mapper.UserMapper;
import com.example.aihub.pojo.ChatInfo;
import com.example.aihub.pojo.Star;
import com.example.aihub.pojo.User;
import com.example.aihub.pojo.UserInfoChangeRequest;
import com.example.aihub.pojo.UserLoginResponse;
import com.example.aihub.pojo.UserRequest;
import com.example.aihub.pojo.UserResponse;
import com.example.aihub.service.UserService;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ChatInfoMapper chatInfoMapper;

    @Override
    public ResponseEntity<UserResponse> getUserInfo() {
        Integer userId = StpUtil.getLoginIdAsInt();
        User user = userMapper.findUserById(userId);
        List<ChatInfo> userChatInfos = chatInfoMapper.findChatInfosByUserId(user.getId());
        List<ChatInfo> userStars = chatInfoMapper.findStarredChatInfosByUserId(user.getId());
        UserResponse userResponse = UserResponse.builder()
                                            .id(user.getId())
                                            .account(user.getAccount())
                                            .apiKey(user.getApiKey())
                                            .userChatInfos(userChatInfos)
                                            .userStars(userStars)
                                            .build();
        return ResponseEntity.ok().body(userResponse);
    }

    @Override
    public ResponseEntity<UserLoginResponse> login(UserRequest userRequest) {
        if (userRequest == null
                || StrUtil.isBlank(userRequest.getAccount())
                || StrUtil.isBlank(userRequest.getPassword())) {
            throw new MyIllegalArgumentException("Account or password can not be empty!");
        }
        User user = userMapper.findUserByAccount(userRequest.getAccount());
        if (user == null || !SaSecureUtil.md5(userRequest.getPassword()).equals(user.getPassword())) {
            throw new InvalidCredentialsException("Account or password wrong!");
        }
        List<ChatInfo> userChatInfos = chatInfoMapper.findChatInfosByUserId(user.getId());
        List<ChatInfo> userStars = chatInfoMapper.findStarredChatInfosByUserId(user.getId());

        StpUtil.login(user.getId());
        StpUtil.getSession().set("currentUser", user);
        // some setting
        UserLoginResponse userResponse = new UserLoginResponse();
        userResponse.setId(user.getId());
        userResponse.setAccount(user.getAccount());
        userResponse.setApiKey(user.getApiKey());
        userResponse.setUserChatInfos(userChatInfos);
        userResponse.setUserStars(userStars);
        userResponse.setToken(StpUtil.getTokenInfo());
        return ResponseEntity.ok().body(userResponse);
    }

    @Override
    public ResponseEntity<UserResponse> register(UserRequest userRequest) {
        if (userRequest == null
                || StrUtil.isBlank(userRequest.getAccount())
                || StrUtil.isBlank(userRequest.getPassword())) {
            throw new MyIllegalArgumentException("Account or password can not be empty!");
        }
        if (userMapper.findUserByAccount(userRequest.getAccount()) != null) {
            throw new AccountHasBeenUsedException("Account has been registered!");
        }
        User user = new User();
        BeanUtils.copyProperties(userRequest, user);
        user.setPassword(SaSecureUtil.md5(userRequest.getPassword()));
        userMapper.insertUser(user);
        UserResponse userResponse = UserResponse.builder()
                                            .id(user.getId())
                                            .account(user.getAccount())
                                            .userChatInfos(new ArrayList<>())
                                            .userStars(new ArrayList<>())
                                            .build();
        return ResponseEntity.ok().body(userResponse);
    }

    @Override
    @CheckDataOwner(serviceClass = ChatServiceImpl.class)
    public ResponseEntity<UserResponse> star(Integer chatInfoId) {
        if (chatInfoId == null) {
            throw new MyIllegalArgumentException("Account or password can not be empty!");
        }
        Star star = Star.builder()
                        .userId(StpUtil.getLoginIdAsInt())
                        .chatinfoId(chatInfoId)
                        .build();
        userMapper.insertUserStar(star);
        User user = userMapper.findUserById(star.getUserId());
        List<ChatInfo> userChatInfos = chatInfoMapper.findChatInfosByUserId(user.getId());
        List<ChatInfo> userStars = chatInfoMapper.findStarredChatInfosByUserId(user.getId());
        UserResponse userResponse = UserResponse.builder()
                                            .id(user.getId())
                                            .account(user.getAccount())
                                            .apiKey(user.getApiKey())
                                            .userChatInfos(userChatInfos)
                                            .userStars(userStars)
                                            .build();
        return ResponseEntity.ok().body(userResponse);
    }

    @Override
    @CheckDataOwner(serviceClass = ChatServiceImpl.class)
    public ResponseEntity<UserResponse> unstar(Integer chatInfoId) {
        if (chatInfoId == null) {
            throw new MyIllegalArgumentException("Account or password can not be empty!");
        }
        Star star = Star.builder()
                        .userId(StpUtil.getLoginIdAsInt())
                        .chatinfoId(chatInfoId)
                        .build();
        userMapper.deleteUserStar(star);
        User user = userMapper.findUserById(star.getUserId());
        List<ChatInfo> userChatInfos = chatInfoMapper.findChatInfosByUserId(user.getId());
        List<ChatInfo> userStars = chatInfoMapper.findStarredChatInfosByUserId(user.getId());
        UserResponse userResponse = UserResponse.builder()
                                            .id(user.getId())
                                            .account(user.getAccount())
                                            .apiKey(user.getApiKey())
                                            .userChatInfos(userChatInfos)
                                            .userStars(userStars)
                                            .build();
        return ResponseEntity.ok().body(userResponse);
    }

    @Override
    public ResponseEntity<UserResponse> updateUserInfo(UserInfoChangeRequest userInfoChangeRequest) {
        User user = (User) StpUtil.getSession().get("currentUser");
        if (userInfoChangeRequest == null) {
            throw new MyIllegalArgumentException("Request not be empty!");
        }
        user.setPassword(userInfoChangeRequest.getPassword());
        user.setApiKey(userInfoChangeRequest.getApiKey());
        userMapper.updateUserInfo(user);
        UserResponse userResponse = UserResponse.builder()
                                            .id(user.getId())
                                            .account(user.getAccount())
                                            .apiKey(user.getApiKey())
                                            .build();
        return ResponseEntity.ok().body(userResponse);
    }
}
