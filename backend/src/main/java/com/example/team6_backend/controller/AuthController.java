package com.example.team6_backend.controller;

import com.example.team6_backend.common.ApiResponse;
import com.example.team6_backend.dto.LoginRequest;
import com.example.team6_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// 登录接口，接收用户名和密码，再增加一个状态码data
// 如果找不到用户名或者密码错误，data=-1
// 如果登录成功，data=0
// 前端根据data值来决定显示“用户名或密码错误”还是登录成功
// 另外，没有采用ApiResponse（有点没搞懂逻辑）而是选择返回一个Map
// Map里有两个字段，token和data
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        String token = userService.authenticate(request.getUserId(), request.getPassword());
//        if (token == null) {
//            return ApiResponse.fail(401, "登录异常"); // -1 indicates failure
//        }
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("token", token);
        if(token == null) {
            retMap.put("code", -1);
            retMap.put("message", "IDまたはパスワードが間違っています");
        } else {
            retMap.put("code", 0);
            retMap.put("message", "ログイン成功");
        }
        return retMap;
        //return ApiResponse.success(token);
    }
}
