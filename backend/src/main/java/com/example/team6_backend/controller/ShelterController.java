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

    @PostMapping("/updateCurrentCapacity")
    public int updateCurrentCapacity(@RequestParam String shelterId, @RequestParam Integer shelterCur) {
        return shelterService.updateCurrentCapacity(shelterId, shelterCur);
    }

    @PostMapping("/updateCurrentMoney")
    public int updateCurrentMoney(@RequestParam String shelterId, @RequestParam Integer moneyCur) {
        return shelterService.updateCurrentMoney(shelterId, moneyCur);
    }

    @PostMapping("/updateRequestedMoney")
    public int updateRequestedMoney(@RequestParam String shelterId, @RequestParam Integer moneyReq) {
        return shelterService.updateRequestedMoney(shelterId, moneyReq);
    }
    @PostMapping("/updateStatus")
    public int updateStatus(@RequestParam String shelterId, @RequestParam String status) {
        return shelterService.updateStatus(shelterId, status);
    }

    @GetMapping("/list")
    public List<Shelter> selectAll() {
        return shelterService.selectAll();
    }

    @GetMapping("/getById")
    public Shelter selectById(@RequestParam String shelterId) {
        return shelterService.selectById(shelterId);
    }

    @GetMapping("/stats")
    public StatsResponse getStats() {
        int total = shelterService.countAll();
        int urgent = shelterService.countByStatus("urgent");
        int needsSupplies = shelterService.countByStatus("needs-supplies");
        int full = shelterService.countByStatus("full");
        return new StatsResponse(total, urgent, needsSupplies, full);
    }

    public static class StatsResponse {
        private final int total;
        private final int urgent;
        private final int needsSupplies;
        private final int full;

        public StatsResponse(int total, int urgent, int needsSupplies, int full) {
            this.total = total;
            this.urgent = urgent;
            this.needsSupplies = needsSupplies;
            this.full = full;
        }

        public int getTotal() {
            return total;
        }

        public int getUrgent() {
            return urgent;
        }

        public int getNeedsSupplies() {
            return needsSupplies;
        }

        public int getFull() {
            return full;
        }
    }
}
