package com.step.sacannership.model.bean;

public class AssembleSubmitBean {

    private String sn;
    private String customerSerialNumber = "";
    private String customer = "";
    private final int departmentType = 1;
    private final boolean smtWorkshop = false;
    private int productionOrderId;
    private String productionOrderNo;
    private int productionLineId;
    private int productionStationId;
    private String materialNo;
    private boolean withstandTestCheck;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getCustomerSerialNumber() {
        return customerSerialNumber;
    }

    public void setCustomerSerialNumber(String customerSerialNumber) {
        this.customerSerialNumber = customerSerialNumber;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public int getProductionOrderId() {
        return productionOrderId;
    }

    public void setProductionOrderId(int productionOrderId) {
        this.productionOrderId = productionOrderId;
    }

    public String getProductionOrderNo() {
        return productionOrderNo;
    }

    public void setProductionOrderNo(String productionOrderNo) {
        this.productionOrderNo = productionOrderNo;
    }

    public int getProductionLineId() {
        return productionLineId;
    }

    public void setProductionLineId(int productionLineId) {
        this.productionLineId = productionLineId;
    }

    public int getProductionStationId() {
        return productionStationId;
    }

    public void setProductionStationId(int productionStationId) {
        this.productionStationId = productionStationId;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public boolean isWithstandTestCheck() {
        return withstandTestCheck;
    }

    public void setWithstandTestCheck(boolean withstandTestCheck) {
        this.withstandTestCheck = withstandTestCheck;
    }
}
