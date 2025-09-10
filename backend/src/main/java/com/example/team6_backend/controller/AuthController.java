package com.example.team6_backend.controller;

import com.example.team6_backend.common.ApiResponse;
import com.example.team6_backend.dto.LoginRequest;
import com.example.team6_backend.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody LoginRequest request) {
        String token = userService.authenticate(request.getUserId(), request.getPassword());
        if (token == null) {
            return ApiResponse.fail(401, "用户名或密码错误");
        }
        return ApiResponse.success(token);
    }
}
