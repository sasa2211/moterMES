package com.step.sacannership.model.bean;

public class ScanDeliveryMaterialBean {
    private String createTime;
    private int creatorID;
    private int deliveryBillID;
    private String billNO;
    private String materialBarcode;
    private String materialNo;
    private int palletID;
    private int palletStatus;
    private int pkid;
    private String rowIndex;

    public void setBillNO(String billNO) {
        this.billNO = billNO;
    }

    public String getBillNO() {
        return billNO;
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

    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
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

    public int getPalletID() {
        return palletID;
    }

    public void setPalletID(int palletID) {
        this.palletID = palletID;
    }

    public int getPalletStatus() {
        return palletStatus;
    }

    public void setPalletStatus(int palletStatus) {
        this.palletStatus = palletStatus;
    }

    public int getPkid() {
        return pkid;
    }

    public void setPkid(int pkid) {
        this.pkid = pkid;
    }

    public String getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(String rowIndex) {
        this.rowIndex = rowIndex;
    }
}
