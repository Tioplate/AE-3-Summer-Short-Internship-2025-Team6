package com.example.team6_backend.mapper;

import com.example.team6_backend.entity.Shelter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShelterMapper {
    int insert(Shelter shelter);
    int deleteById(String shelterId);
    int update(Shelter shelter);
    int updateCurrentCapacity(@Param("shelterId") String shelterId, @Param("shelterCur") Integer shelterCur);
    int updateCurrentMoney(@Param("shelterId") String shelterId, @Param("moneyCur") Integer moneyCur);
    int updateRequestedMoney(@Param("shelterId") String shelterId, @Param("moneyReq") Integer moneyReq);
    int updateStatus(@Param("shelterId") String shelterId, @Param("status") String status);
    List<Shelter> selectAll();
    Shelter selectById(String shelterId);
    int countAll();
    int countByStatus(@Param("status") String status);
}
