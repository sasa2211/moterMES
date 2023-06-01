package com.step.sacannership.model.bean;

public class DeliveryBillPalletDetailsBean {

    private int deliveryBillID;
    private int masterID;
    private String materialName;
    private String materialNo;
    private int pkid;
    private double quantity;
    private double billQuantity;
    private double outQuantity;
    private double bindQuantity;
    private double qualifiedQuantity;
    private double snQuantity;
    private String rowIndex;
    private String statue;

    public int getDeliveryBillID() {
        return deliveryBillID;
    }

    public void setDeliveryBillID(int deliveryBillID) {
        this.deliveryBillID = deliveryBillID;
    }

    public int getMasterID() {
        return masterID;
    }

    public void setMasterID(int masterID) {
        this.masterID = masterID;
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

    public int getPkid() {
        return pkid;
    }

    public void setPkid(int pkid) {
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

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public double getBillQuantity() {
        return billQuantity;
    }

    public void setBillQuantity(double billQuantity) {
        this.billQuantity = billQuantity;
    }

    public double getOutQuantity() {
        return outQuantity;
    }

    public void setOutQuantity(double outQuantity) {
        this.outQuantity = outQuantity;
    }

    public double getBindQuantity() {
        return bindQuantity;
    }

    public void setBindQuantity(double bindQuantity) {
        this.bindQuantity = bindQuantity;
    }

    public double getQualifiedQuantity() {
        return qualifiedQuantity;
    }

    public void setQualifiedQuantity(double qualifiedQuantity) {
        this.qualifiedQuantity = qualifiedQuantity;
    }

    public double getSnQuantity() {
        return snQuantity;
    }

    public void setSnQuantity(double snQuantity) {
        this.snQuantity = snQuantity;
    }
}
