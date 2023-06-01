package com.step.sacannership.model.bean;

public class DeliveryDetailBean {
    /**
     * pkId : 1
     * masterId : 10210
     * deliveryBillNo : 8100974326
     * rowIndex : 20
     * salesOrderRowIndex : 20
     * materialNo : F38001808
     * materialName : 印度蒂森400V级18安培MC4系统CPIC-I-18/不含PG卡
     * quantity : 3.0
     * unit : PC
     */

    private int pkId;
    private int masterId;
    private String deliveryBillNo;
    private String rowIndex;
    private String salesOrderRowIndex;
    private String materialNo;
    private String materialName;
    private double quantity;
    private double bindQuantity;
    private double outQuantity;
    private double qualifiedQuantity;
    private String unit;

    public double getQualifiedQuantity() {
        return qualifiedQuantity;
    }

    public void setQualifiedQuantity(double qualifiedQuantity) {
        this.qualifiedQuantity = qualifiedQuantity;
    }

    public double getBindQuantity() {
        return bindQuantity;
    }

    public void setBindQuantity(double bindQuantity) {
        this.bindQuantity = bindQuantity;
    }

    public double getOutQuantity() {
        return outQuantity;
    }

    public void setOutQuantity(double outQuantity) {
        this.outQuantity = outQuantity;
    }

    public int getPkId() {
        return pkId;
    }

    public void setPkId(int pkId) {
        this.pkId = pkId;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public String getDeliveryBillNo() {
        return deliveryBillNo;
    }

    public void setDeliveryBillNo(String deliveryBillNo) {
        this.deliveryBillNo = deliveryBillNo;
    }

    public String getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(String rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getSalesOrderRowIndex() {
        return salesOrderRowIndex;
    }

    public void setSalesOrderRowIndex(String salesOrderRowIndex) {
        this.salesOrderRowIndex = salesOrderRowIndex;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
