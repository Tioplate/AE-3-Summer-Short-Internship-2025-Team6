package com.example.team6_backend.entity;

public class Shelter {
    private String shelterId;
    private String shelterName;
    private String address;
    private String adminId;
    private Integer shelterCap;
    private Integer shelterCur;
    private Float moneyCur;
    private Float moneyReq;

    // Getters and Setters

    public String getShelterId() { return shelterId; }
    public void setShelterId(String shelterId) { this.shelterId = shelterId; }

    public String getShelterName() { return shelterName; }
    public void setShelterName(String shelterName) { this.shelterName = shelterName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public Integer getShelterCap() { return shelterCap; }
    public void setShelterCap(Integer shelterCap) { this.shelterCap = shelterCap; }

    public Integer getShelterCur() { return shelterCur; }
    public void setShelterCur(Integer shelterCur) { this.shelterCur = shelterCur; }

    public Float getMoneyCur() { return moneyCur; }
    public void setMoneyCur(Float moneyCur) { this.moneyCur = moneyCur; }

    public Float getMoneyReq() { return moneyReq; }
    public void setMoneyReq(Float moneyReq) { this.moneyReq = moneyReq; }
}
