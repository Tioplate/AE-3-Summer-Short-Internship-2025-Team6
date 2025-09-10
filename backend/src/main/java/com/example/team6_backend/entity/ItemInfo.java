package com.example.team6_backend.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class ItemInfo {
    private String itemCode;
    private String itemName;
    private int itemPrice;
    private String genreId;
    private String itemUrl;
    private List<String> smallImageUrls;
    public ItemInfo(){
        return;}
    public ItemInfo(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        JsonNode itemNode = root.path("Items").get(0).path("Item");
        setItemCode(itemNode.path("itemCode").asText());
        setItemName(itemNode.path("itemName").asText());
        setItemPrice(itemNode.path("itemPrice").asInt());
        setItemUrl(itemNode.path("itemUrl").asText());
        setGenreId(itemNode.path("genreId").asText());
        List<String> smallImageUrls = new ArrayList<>();
        for (JsonNode img : itemNode.path("smallImageUrls")) {
            smallImageUrls.add(img.path("imageUrl").asText());
        }
        setSmallImageUrls(smallImageUrls);
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public List<String> getSmallImageUrls() {
        return smallImageUrls;
    }

    public void setSmallImageUrls(List<String> smallImageUrls) {
        this.smallImageUrls = smallImageUrls;
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }
}
