package com.example.team6_backend.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenreFinder {
    public static String findGenreName(JsonNode root, int genreId) {
        // 1. 先查找current字段
        JsonNode current = root.get("current");
        if (current != null && current.has("genreId") && current.get("genreId").asInt() == genreId) {
            return current.get("genreName").asText();
        }

        // 2. 查找brothers数组
        JsonNode brothers = root.get("brothers");
        if (brothers != null && brothers.isArray()) {
            for (JsonNode brotherObj : brothers) {
                JsonNode brother = brotherObj.get("brother");
                if (brother != null && brother.has("genreId") && brother.get("genreId").asInt() == genreId) {
                    return brother.get("genreName").asText();
                }
            }
        }

        // 3. 查找parents数组
        JsonNode parents = root.get("parents");
        if (parents != null && parents.isArray()) {
            for (JsonNode parentObj : parents) {
                JsonNode parent = parentObj.get("parent");
                if (parent != null && parent.has("genreId") && parent.get("genreId").asInt() == genreId) {
                    return parent.get("genreName").asText();
                }
            }
        }
        // 如果没找到
        return null;
    }
}
