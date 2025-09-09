package com.example.team6_backend.service;

import com.example.team6_backend.entity.User;
import com.example.team6_backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public int updateUser(User user) {
        return userMapper.update(user);
    }
}
