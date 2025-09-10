package com.example.team6_backend.service;

import com.example.team6_backend.entity.Shelter;
import com.example.team6_backend.mapper.ShelterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShelterService {
    @Autowired
    private ShelterMapper shelterMapper;

    public int insert(Shelter shelter) {
        return shelterMapper.insert(shelter);
    }

    public int deleteById(String shelterId) {
        return shelterMapper.deleteById(shelterId);
    }

    public int update(Shelter shelter) {
        return shelterMapper.update(shelter);
    }

    public List<Shelter> selectAll() {
        return shelterMapper.selectAll();
    }

    public Shelter selectById(String shelterId) {
        return shelterMapper.selectById(shelterId);
    }
}
