package com.step.sacannership.model.bean;

public class UnBindBean {
    private int deliveryBillId;
    private String deliveryBillNO;
    private String materialName;
    private String materialNo;
    private double quantity;

    public int getDeliveryBillId() {
        return deliveryBillId;
    }

    public void setDeliveryBillId(int deliveryBillId) {
        this.deliveryBillId = deliveryBillId;
    }

    public String getDeliveryBillNO() {
        return deliveryBillNO;
    }

    public void setDeliveryBillNO(String deliveryBillNO) {
        this.deliveryBillNO = deliveryBillNO;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
