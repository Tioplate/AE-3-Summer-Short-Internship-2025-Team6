package com.example.team6_backend.controller;

import com.example.team6_backend.entity.Shelter;
import com.example.team6_backend.service.ShelterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shelter")
public class ShelterController {
    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @PostMapping("/insert")
    public int insert(@RequestBody Shelter shelter) {
        return shelterService.insert(shelter);
    }

    @DeleteMapping("/delete")
    public int deleteById(@RequestParam String shelterId) {
        return shelterService.deleteById(shelterId);
    }

    @PostMapping("/update")
    public int update(@RequestBody Shelter shelter) {
        return shelterService.update(shelter);
    }

    @GetMapping("/list")
    public List<Shelter> selectAll() {
        return shelterService.selectAll();
    }

    @GetMapping("/getById")
    public Shelter selectById(@RequestParam String shelterId) {
        return shelterService.selectById(shelterId);
    }
}
