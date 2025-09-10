package com.example.team6_backend.mapper;

import com.example.team6_backend.entity.UserGoods;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserGoodsMapper {
    int insert(UserGoods userGoods);
    List<UserGoods> selectByUserId(String userId);
    int deleteByReqId(String reqId); // 根据主键删除
    int updateNumber(UserGoods userGoods);
}
