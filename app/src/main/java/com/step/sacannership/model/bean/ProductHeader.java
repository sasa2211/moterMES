package com.step.sacannership.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductHeader implements Parcelable{
    private String batchStr;//生产批次
    private String productOrder;//生产订单
    private String materialNo;//物料编号
    private String lineNo;//线别
    private String stationPort;//站点
    private int statueId;//站点id
    private int lineId;
    private String materialDesc;//物料描述
    private int count;//批次总量
    private int scanNo;//批次总量
    private int unScanNo;//待扫数量
    private String stationNo = "";

    private int headrId;

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


    protected ProductHeader(Parcel in) {
        batchStr = in.readString();
        productOrder = in.readString();
        materialNo = in.readString();
        lineNo = in.readString();
        stationPort = in.readString();
        statueId = in.readInt();
        lineId = in.readInt();
        materialDesc = in.readString();
        count = in.readInt();
        scanNo = in.readInt();
        unScanNo = in.readInt();
        stationNo = in.readString();
        headrId = in.readInt();
    }

    public static final Creator<ProductHeader> CREATOR = new Creator<ProductHeader>() {
        @Override
        public ProductHeader createFromParcel(Parcel in) {
            return new ProductHeader(in);
        }

        @Override
        public ProductHeader[] newArray(int size) {
            return new ProductHeader[size];
        }
    };

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

    public int getStatueId() {
        return statueId;
    }

    public void setStatueId(int statueId) {
        this.statueId = statueId;
    }

    public String getStationNo() {
        return stationNo;
    }

    public void setStationNo(String stationNo) {
        this.stationNo = stationNo;
    }

    public void copyDate(ProductHeader header){
        if (header == null){
            return;
        }
        batchStr = header.getBatchStr();
        productOrder =  header.getProductOrder();
        materialNo =  header.getMaterialNo();
        lineNo =  header.getLineNo();
        lineId = header.getLineId();
        stationPort =  header.getStationPort();
        statueId = header.getStatueId();
        materialDesc = header.getMaterialDesc();
        count = header.getCount();
        scanNo = header.getScanNo();
        unScanNo = header.getUnScanNo();
        headrId = header.getHeadrId();
        stationNo = header.getStationNo();
    }

    public int getHeadrId() {
        return headrId;
    }

    public void setHeadrId(int headrId) {
        this.headrId = headrId;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(batchStr);
        dest.writeString(productOrder);
        dest.writeString(materialNo);
        dest.writeString(lineNo);
        dest.writeString(stationPort);
        dest.writeInt(statueId);
        dest.writeInt(lineId);
        dest.writeString(materialDesc);
        dest.writeInt(count);
        dest.writeInt(scanNo);
        dest.writeInt(unScanNo);
        dest.writeString(stationNo);
        dest.writeInt(headrId);
    }
}
