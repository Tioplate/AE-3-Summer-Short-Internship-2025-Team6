package com.example.team6_backend.controller;

import com.example.team6_backend.common.ApiResponse;
import com.example.team6_backend.entity.UserGoods;
import com.example.team6_backend.service.UserGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {
    private final UserGoodsService userGoodsService;

    public RequestController(UserGoodsService userGoodsService) {
        this.userGoodsService = userGoodsService;
    }

    @PostMapping("/create")
    public ApiResponse<String> create(@RequestBody UserGoods userGoods) {
        // 静默处理：数量<=0 直接返回成功，不插入
        if (userGoods == null || userGoods.getNumber() == null || userGoods.getNumber() <= 0) {
            return ApiResponse.successMessage("created");
        }
        int affected = userGoodsService.create(userGoods);
        if (affected > 0) {
            return ApiResponse.successMessage("created");
        }
        return ApiResponse.fail(500, "Failed to create");
    }

    @GetMapping("/list")
    public ApiResponse<List<UserGoods>> list(@RequestParam("userId") String userId) {
        return ApiResponse.success(userGoodsService.listByUser(userId));
    }

    @PostMapping("/updateNumber")
    public ApiResponse<Integer> updateNumber(String reqId, int number) {
        // 若数量<=0，视为删除该请求
        if (number <= 0) {
            int affected = userGoodsService.delete(reqId);
            return ApiResponse.success(affected);
        }
        UserGoods userGoods = new UserGoods();
        userGoods.setReqId(reqId);
        userGoods.setNumber(number);
        int affected = userGoodsService.updateNumber(userGoods);
        return ApiResponse.success(affected);
    }
    @GetMapping("/delete")
    public ApiResponse<Integer> delete(@RequestParam("reqId") String reqId) {
        int affected = userGoodsService.delete(reqId);
        return ApiResponse.success(affected);
    }
    @PostMapping("/batchCreate")
    public ApiResponse<String> batchCreate(@RequestBody List<UserGoods> list) {
        if (list == null || list.isEmpty()) {
            return ApiResponse.successMessage("batch created");
        }
        // 过滤掉数量<=0的项
        List<UserGoods> validList = list.stream()
            .filter(ug -> ug != null && ug.getNumber() != null && ug.getNumber() > 0)
            .toList();
        if (validList.isEmpty()) {
            return ApiResponse.successMessage("batch created");
        }
        int affected = userGoodsService.batchCreate(validList);
        if (affected > 0) {
            return ApiResponse.successMessage("batch created");
        }
        return ApiResponse.fail(500, "Failed to batch create");
    }
}
