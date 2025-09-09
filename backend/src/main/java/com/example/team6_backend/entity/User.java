package com.example.team6_backend.entity;

public class User {
    private String userId;
    private String password;
    private int permission;

    public User(String userId, String password, int permission) {
        this.userId = userId;
        this.password = password;
        this.permission = permission;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }
}
