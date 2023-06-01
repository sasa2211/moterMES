package com.step.sacannership.model.bean;


public class DeliveryBean extends DeliveryBaseBean{

    private int parentID;
    private String deliveryDate;
    private String deliveryAddress;
    private String status;
    private String createTime;
    private String updateTime;
    private String outStockTime;
    private String palletBindCompletedTime;
    private String inspectCompletedTime;
    private String deliveryCompletedTime;

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOutStockTime() {
        return outStockTime;
    }

    public void setOutStockTime(String outStockTime) {
        this.outStockTime = outStockTime;
    }

    public String getPalletBindCompletedTime() {
        return palletBindCompletedTime;
    }

    public void setPalletBindCompletedTime(String palletBindCompletedTime) {
        this.palletBindCompletedTime = palletBindCompletedTime;
    }

    public String getInspectCompletedTime() {
        return inspectCompletedTime;
    }

    public void setInspectCompletedTime(String inspectCompletedTime) {
        this.inspectCompletedTime = inspectCompletedTime;
    }

    public String getDeliveryCompletedTime() {
        return deliveryCompletedTime;
    }

    public void setDeliveryCompletedTime(String deliveryCompletedTime) {
        this.deliveryCompletedTime = deliveryCompletedTime;
    }
}
