package com.example.team6_backend.mapper;

import com.example.team6_backend.entity.Shelter;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShelterMapper {
    int insert(Shelter shelter);
}
