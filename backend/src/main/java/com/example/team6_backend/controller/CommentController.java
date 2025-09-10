package com.example.team6_backend.controller;

import com.example.team6_backend.common.ApiResponse;
import com.example.team6_backend.entity.Comment;
import com.example.team6_backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ApiResponse<String> add(@RequestBody Comment comment) {
        int affected = commentService.addComment(comment);
        if (affected > 0) {
            return ApiResponse.successMessage("created");
        }
        return ApiResponse.fail(500, "创建失败");
    }

    @GetMapping
    public ApiResponse<List<Comment>> list(@RequestParam("shelterId") String shelterId) {
        return ApiResponse.success(commentService.listByShelter(shelterId));
    }
}
