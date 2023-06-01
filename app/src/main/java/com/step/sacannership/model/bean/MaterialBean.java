package com.step.sacannership.model.bean;

public class MaterialBean {
    private int pkid;
    private int palletID;
    private int deliveryBillID;
    private String materialBarcode;
    private String materialNo;
    private String createTime;
    private int creatorID;

    public int getPkid() {
        return pkid;
    }

    public void setPkid(int pkid) {
        this.pkid = pkid;
    }

    public int getDeliveryBillID() {
        return deliveryBillID;
    }

    public void setDeliveryBillID(int deliveryBillID) {
        this.deliveryBillID = deliveryBillID;
    }

    public String getMaterialBarcode() {
        return materialBarcode;
    }

    public void setMaterialBarcode(String materialBarcode) {
        this.materialBarcode = materialBarcode;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCreatorID() {
        return creatorID;
    }

    public int getPalletID() {
        return palletID;
    }

    public void setPalletID(int palletID) {
        this.palletID = palletID;
    }

    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }
}
