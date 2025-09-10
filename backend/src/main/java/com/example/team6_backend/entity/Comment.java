package com.example.team6_backend.entity;

public class Comment {
    private String commentId;
    private String comment;
    private String shelterId;

    // Getters and Setters

    public String getCommentId() { return commentId; }
    public void setCommentId(String commentId) { this.commentId = commentId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getShelterId() { return shelterId; }
    public void setShelterId(String shelterId) { this.shelterId = shelterId; }
}
