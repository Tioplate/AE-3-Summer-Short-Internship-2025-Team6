package com.example.team6_backend.service;

import com.example.team6_backend.entity.UserGoods;
import com.example.team6_backend.mapper.UserGoodsMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserGoodsService {
    private final UserGoodsMapper userGoodsMapper;

    public UserGoodsService(UserGoodsMapper userGoodsMapper) {
        this.userGoodsMapper = userGoodsMapper;
    }

    public int create(UserGoods userGoods) {
        userGoods.setReqId(UUID.randomUUID().toString());
        return userGoodsMapper.insert(userGoods);
    }

    public List<UserGoods> listByUser(String userId) {
        return userGoodsMapper.selectByUserId(userId);
    }
}
