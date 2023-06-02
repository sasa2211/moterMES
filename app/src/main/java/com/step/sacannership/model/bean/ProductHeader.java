package com.step.sacannership.model.bean;

public class ProductHeader {
    private String batchStr;//生产批次
    private String productOrder;//生产订单
    private String materialNo;//物料编号
    private String lineNo;//线别
    private String stationPort;//站点
    private String materialDesc;//物料描述
    private int count;//批次总量
    private int scanNo;//批次总量
    private int unScanNo;//待扫数量

    public ProductHeader() {
        this.batchStr = "";
        this.productOrder = "";
        this.materialNo = "";
        this.lineNo = "";
        this.stationPort = "";
        this.materialDesc = "";
        this.count = 0;
        this.scanNo = 0;
        this.unScanNo = 0;
    }

    public String getBatchStr() {
        return batchStr;
    }

    public void setBatchStr(String batchStr) {
        this.batchStr = batchStr;
    }

    public String getProductOrder() {
        return productOrder;
    }

    public void setProductOrder(String productOrder) {
        this.productOrder = productOrder;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public String getStationPort() {
        return stationPort;
    }

    public void setStationPort(String stationPort) {
        this.stationPort = stationPort;
    }

    public String getMaterialDesc() {
        return materialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        this.materialDesc = materialDesc;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getScanNo() {
        return scanNo;
    }

    public void setScanNo(int scanNo) {
        this.scanNo = scanNo;
    }

    public int getUnScanNo() {
        return unScanNo;
    }

    public void setUnScanNo(int unScanNo) {
        this.unScanNo = unScanNo;
    }
}
