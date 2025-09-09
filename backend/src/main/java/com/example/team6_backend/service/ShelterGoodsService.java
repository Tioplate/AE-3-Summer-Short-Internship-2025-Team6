package com.example.team6_backend.service;

import com.example.team6_backend.entity.ShelterGoods;
import com.example.team6_backend.mapper.ShelterGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShelterGoodsService {
    @Autowired
    ShelterGoodsMapper shelterGoodsMapper;

    public int insert(ShelterGoods shelterGoods) {
        return shelterGoodsMapper.insert(shelterGoods);
    }
}
