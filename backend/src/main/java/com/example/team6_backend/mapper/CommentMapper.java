package com.example.team6_backend.mapper;

import com.example.team6_backend.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CommentMapper {
    int insert(Comment comment);
    List<Comment> selectByShelterId(String shelterId);
}
