package com.example.team6_backend.entity;

public class UserGoods {
    private String reqId;
    private String userId;
    private String shelterId;
    private String goodsId;
    private Integer number;
    private String status;

    // Getters and Setters

    public String getReqId() { return reqId; }
    public void setReqId(String reqId) { this.reqId = reqId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getShelterId() { return shelterId; }
    public void setShelterId(String shelterId) { this.shelterId = shelterId; }

    public String getGoodsId() { return goodsId; }
    public void setGoodsId(String goodsId) { this.goodsId = goodsId; }

    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
