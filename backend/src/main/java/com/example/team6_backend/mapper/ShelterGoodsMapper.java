package com.example.team6_backend.mapper;

import com.example.team6_backend.entity.ShelterGoods;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShelterGoodsMapper {
    int insert(ShelterGoods shelterGoods);
}
