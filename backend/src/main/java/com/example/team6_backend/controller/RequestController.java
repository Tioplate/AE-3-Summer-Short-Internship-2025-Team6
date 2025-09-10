package com.example.team6_backend.controller;

import com.example.team6_backend.common.ApiResponse;
import com.example.team6_backend.entity.UserGoods;
import com.example.team6_backend.service.UserGoodsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {
    private final UserGoodsService userGoodsService;

    public RequestController(UserGoodsService userGoodsService) {
        this.userGoodsService = userGoodsService;
    }

    @PostMapping
    public ApiResponse<String> create(@RequestBody UserGoods userGoods) {
        int affected = userGoodsService.create(userGoods);
        if (affected > 0) {
            return ApiResponse.successMessage("created");
        }
        return ApiResponse.fail(500, "创建失败");
    }

    @GetMapping
    public ApiResponse<List<UserGoods>> list(@RequestParam("userId") String userId) {
        return ApiResponse.success(userGoodsService.listByUser(userId));
    }
}
