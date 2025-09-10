package com.example.team6_backend.service;

import com.example.team6_backend.entity.User;
import com.example.team6_backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public int updateUser(User user) {
        return userMapper.update(user);
    }

    public int addUser(User user) {
        // Uniqueness check
        User existing = userMapper.selectById(user.getUserId());
        if (existing != null) {
            return -1; // indicate duplicate
        }
        // MD5 hash password
        user.setPassword(md5Hex(user.getPassword()));
        return userMapper.insert(user);
    }

    public String authenticate(String userId, String passwordPlain) {
        User existing = userMapper.selectById(userId);
        if (existing == null) return null;
        String inputHash = md5Hex(passwordPlain);
        if (inputHash == null || !inputHash.equalsIgnoreCase(existing.getPassword())) {
            return null;
        }
        String raw = userId + "|" + Instant.now().toEpochMilli();
        return Base64.getEncoder().encodeToString(raw.getBytes());
    }

    private String md5Hex(String input) {
        if (input == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }
}
