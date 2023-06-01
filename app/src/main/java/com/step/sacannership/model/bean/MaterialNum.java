package com.step.sacannership.model.bean;

import java.io.Serializable;

public class MaterialNum implements Serializable {
    private String materialNo;
    private int materialNum;
    private String billNo;
    private String rowIndex;

    public MaterialNum(String rowIndex, String billNo, String materialNo, int materialNum) {
        this.rowIndex = rowIndex;
        this.billNo = billNo;
        this.materialNo = materialNo;
        this.materialNum = materialNum;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public int getMaterialNum() {
        return materialNum;
    }

    public void setMaterialNum(int materialNum) {
        this.materialNum = materialNum;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(String rowIndex) {
        this.rowIndex = rowIndex;
    }

    public void add(){
        materialNum ++;
    }
}
