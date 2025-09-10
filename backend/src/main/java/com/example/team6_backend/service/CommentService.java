package com.example.team6_backend.service;

import com.example.team6_backend.entity.Comment;
import com.example.team6_backend.mapper.CommentMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {
    private final CommentMapper commentMapper;

    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public int addComment(Comment comment) {
        comment.setCommentId(UUID.randomUUID().toString());
        return commentMapper.insert(comment);
    }

    public List<Comment> listByShelter(String shelterId) {
        return commentMapper.selectByShelterId(shelterId);
    }
}
