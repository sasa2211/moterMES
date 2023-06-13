package com.step.sacannership.model.bean;

public class AssembleDeleteBean {

    private String sn;
    private int departmentType = 1;
    private int productionOrderId;
    private int productionStationId;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(int departmentType) {
        this.departmentType = departmentType;
    }

    public int getProductionOrderId() {
        return productionOrderId;
    }

    public void setProductionOrderId(int productionOrderId) {
        this.productionOrderId = productionOrderId;
    }

    public int getProductionStationId() {
        return productionStationId;
    }

    public void setProductionStationId(int productionStationId) {
        this.productionStationId = productionStationId;
    }
}
