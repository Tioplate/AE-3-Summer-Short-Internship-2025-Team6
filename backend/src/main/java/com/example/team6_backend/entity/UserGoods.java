package com.example.team6_backend.entity;

public class UserGoods {
    private String reqId;
    private String userId;
    private String shelterId;
    private String url;
    private Integer number;

    // Getters and Setters

    public String getReqId() { return reqId; }
    public void setReqId(String reqId) { this.reqId = reqId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getShelterId() { return shelterId; }
    public void setShelterId(String shelterId) { this.shelterId = shelterId; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }
}
