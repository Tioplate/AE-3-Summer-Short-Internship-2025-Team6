package com.example.team6_backend.mapper;

import com.example.team6_backend.entity.ShelterGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShelterGoodsMapper {
    int insert(ShelterGoods shelterGoods);

    int deleteById(@Param("goodsId") String goodsId);

    int update(ShelterGoods shelterGoods);

    List<ShelterGoods> selectByShelterId(@Param("shelterId") String shelterId);

    ShelterGoods selectByGoodsId(@Param("goodsId") String goodsId);
}
