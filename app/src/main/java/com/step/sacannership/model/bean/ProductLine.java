package com.step.sacannership.model.bean;

public class ProductLine {

    private int pkid;
    private String productionLineNo;
    private String productionLineName;
    private int workCenterId;
    private int creatorId;
    private String createTime;
    private boolean isDeleted;
    private String workCenterNo;
    private String workCenterName;
    private Object creatorName;

    public int getPkid() {
        return pkid;
    }

    public void setPkid(int pkid) {
        this.pkid = pkid;
    }

    public String getProductionLineNo() {
        return productionLineNo;
    }

    public void setProductionLineNo(String productionLineNo) {
        this.productionLineNo = productionLineNo;
    }

    public String getProductionLineName() {
        return productionLineName;
    }

    public void setProductionLineName(String productionLineName) {
        this.productionLineName = productionLineName;
    }

    public int getWorkCenterId() {
        return workCenterId;
    }

    public void setWorkCenterId(int workCenterId) {
        this.workCenterId = workCenterId;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getWorkCenterNo() {
        return workCenterNo;
    }

    public void setWorkCenterNo(String workCenterNo) {
        this.workCenterNo = workCenterNo;
    }

    public String getWorkCenterName() {
        return workCenterName;
    }

    public void setWorkCenterName(String workCenterName) {
        this.workCenterName = workCenterName;
    }

    public Object getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(Object creatorName) {
        this.creatorName = creatorName;
    }
}
