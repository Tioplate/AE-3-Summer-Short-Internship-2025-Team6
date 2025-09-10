package com.example.team6_backend.service;

import com.example.team6_backend.entity.ShelterGoods;
import com.example.team6_backend.mapper.ShelterGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShelterGoodsService {
    @Autowired
    ShelterGoodsMapper shelterGoodsMapper;

    public int insert(ShelterGoods shelterGoods) {
        return shelterGoodsMapper.insert(shelterGoods);
    }
    public int deleteById(String goodsId) {
        return shelterGoodsMapper.deleteById(goodsId);
    }

    public int update(ShelterGoods shelterGoods) {
        return shelterGoodsMapper.update(shelterGoods);
    }

    public List<ShelterGoods> selectByShelterId(String shelterId) {
        return shelterGoodsMapper.selectByShelterId(shelterId);
    }

    public ShelterGoods selectByGoodsId(String goodsId) {
        return shelterGoodsMapper.selectByGoodsId(goodsId);
    }
}
