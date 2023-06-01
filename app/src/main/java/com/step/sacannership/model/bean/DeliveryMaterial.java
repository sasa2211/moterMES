package com.step.sacannership.model.bean;

import java.util.List;

public class DeliveryMaterial {

    private String billNO;
    private int billId;
    private List<MaterialBean> sdDeliveryBillMaterialBarcodes;

    public String getBillNO() {
        return billNO;
    }

    public void setBillNO(String billNO) {
        this.billNO = billNO;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public List<MaterialBean> getSdDeliveryBillMaterialBarcodes() {
        return sdDeliveryBillMaterialBarcodes;
    }

    public void setSdDeliveryBillMaterialBarcodes(List<MaterialBean> sdDeliveryBillMaterialBarcodes) {
        this.sdDeliveryBillMaterialBarcodes = sdDeliveryBillMaterialBarcodes;
    }
}
