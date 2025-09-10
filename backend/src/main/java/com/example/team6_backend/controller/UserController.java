package com.example.team6_backend.controller;

import com.example.team6_backend.common.ApiResponse;
import com.example.team6_backend.entity.User;
import com.example.team6_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ApiResponse<Integer> update(@RequestBody User user) {
        int affected = userService.updateUser(user);
        return ApiResponse.success(affected);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ApiResponse<String> add(@RequestBody User user) {
        user.setPermission(0); // デフォルトの権限を設定
        int result = userService.addUser(user);
        if (result == -1) {
            return ApiResponse.fail(40001, "userIdは既に存在しています");
        }
        return ApiResponse.successMessage("ユーザー登録成功");
    }

}
