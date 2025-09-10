package com.example.team6_backend.entity;

public class ShelterGoods {
    private String goodsId;
    private String goodsName;
    private String shelterId;
    private Integer numberNow;
    private Integer numberReq;
    private String comment;

    // Getters and Setters

    public String getGoodsId() { return goodsId; }
    public void setGoodsId(String goodsId) { this.goodsId = goodsId; }

    public String getGoodsName() { return goodsName; }
    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }

    public String getShelterId() { return shelterId; }
    public void setShelterId(String shelterId) { this.shelterId = shelterId; }

    public Integer getNumberNow() { return numberNow; }
    public void setNumberNow(Integer numberNow) { this.numberNow = numberNow; }

    public Integer getNumberReq() { return numberReq; }
    public void setNumberReq(Integer numberReq) { this.numberReq = numberReq; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}