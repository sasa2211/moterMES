package com.step.sacannership.model.bean;

public class BindResultBean {

    private String materialNo;
    private int num;
    private int resultCode;
    private int deliveryBillID;
    private String rowIndex;
    private String errorMsg;

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getDeliveryBillID() {
        return deliveryBillID;
    }

    public void setDeliveryBillID(int deliveryBillID) {
        this.deliveryBillID = deliveryBillID;
    }

    public String getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(String rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
