package com.example.team6_backend.service;

import com.example.team6_backend.entity.ShelterGoods;
import com.example.team6_backend.mapper.ShelterGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    public int updateNumberNow(String goodsId, Integer numberNow) {
        return shelterGoodsMapper.updateNumberNow(goodsId, numberNow);
    }

    public int updateNumberReq(String goodsId, Integer numberReq) {
        return shelterGoodsMapper.updateNumberReq(goodsId, numberReq);
    }

    public List<ShelterGoods> selectByShelterId(String shelterId) {
        return shelterGoodsMapper.selectByShelterId(shelterId);
    }

    public ShelterGoods selectByGoodsId(String goodsId) {
        return shelterGoodsMapper.selectByGoodsId(goodsId);
    }

    public int batchUpsert(List<ShelterGoods> list) {
        if (list == null || list.isEmpty()) return 0;
        // 1. 提取所有goodsId
        List<String> goodsIds = list.stream().map(ShelterGoods::getGoodsId).toList();
        // 2. 查找已存在的goodsId
        List<ShelterGoods> existList = shelterGoodsMapper.selectByGoodsIds(goodsIds);
        List<String> existIds = existList.stream().map(ShelterGoods::getGoodsId).toList();
        // 3. 分组
        List<ShelterGoods> toUpdate = list.stream().filter(g -> existIds.contains(g.getGoodsId())).toList();
        List<ShelterGoods> toInsert = list.stream().filter(g -> !existIds.contains(g.getGoodsId())).toList();
        int updated = 0, inserted = 0;
        if (!toUpdate.isEmpty()) {
            updated = shelterGoodsMapper.batchUpdateNumberReq(toUpdate);
        }
        if (!toInsert.isEmpty()) {
            inserted = shelterGoodsMapper.batchInsert(toInsert);
        }
        return updated + inserted;
    }

    public Map<String, List<ShelterGoods>> groupByShelterId() {
        List<ShelterGoods> all = shelterGoodsMapper.selectAll();
        Map<String, List<ShelterGoods>> grouped = new java.util.HashMap<>();
        for (ShelterGoods sg : all) {
            grouped.computeIfAbsent(sg.getShelterId(), k -> new java.util.ArrayList<>()).add(sg);
        }
        return grouped;
    }
}
