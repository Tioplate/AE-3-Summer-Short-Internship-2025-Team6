package com.example.team6_backend.mapper;

import com.example.team6_backend.entity.Shelter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShelterMapper {
    int insert(Shelter shelter);
    int deleteById(String shelterId);
    int update(Shelter shelter);
    List<Shelter> selectAll();
    Shelter selectById(String shelterId);
}
