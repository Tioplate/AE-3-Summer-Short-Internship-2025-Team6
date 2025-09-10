package com.example.team6_backend.mapper;

import com.example.team6_backend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    int update(User user);
    int insert(User user);
    User selectById(String userId);
}
