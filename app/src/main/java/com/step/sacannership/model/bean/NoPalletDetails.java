package com.step.sacannership.model.bean;

public class NoPalletDetails {
    private int deliveryBillID;
    private String materialNO;
    private Integer pkid;
    private double quantity;
    private String rowIndex;

    public NoPalletDetails(int deliveryBillID, String materialNO, double quantity, String rowIndex) {
        this.deliveryBillID = deliveryBillID;
        this.materialNO = materialNO;
        this.quantity = quantity;
        this.rowIndex = rowIndex;
    }

    public int getDeliveryBillID() {
        return deliveryBillID;
    }

    public void setDeliveryBillID(int deliveryBillID) {
        this.deliveryBillID = deliveryBillID;
    }

    public String getMaterialNO() {
        return materialNO;
    }

    public void setMaterialNO(String materialNO) {
        this.materialNO = materialNO;
    }

    public Integer getPkid() {
        return pkid;
    }

    public void setPkid(Integer pkid) {
        this.pkid = pkid;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(String rowIndex) {
        this.rowIndex = rowIndex;
    }
}
