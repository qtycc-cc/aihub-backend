package com.example.aihub.mapper;

import java.util.List;

import com.example.aihub.pojo.User;

public interface UserMapper {
    List<User> findAllUsers();
    User findUserById(Integer id);
    User findUserByAccount(String account);
    void insertUser(User user);
}
