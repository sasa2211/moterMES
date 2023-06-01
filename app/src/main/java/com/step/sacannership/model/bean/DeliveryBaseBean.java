package com.step.sacannership.model.bean;

import java.util.ArrayList;
import java.util.List;

public class DeliveryBaseBean {

    private int pkId;
    private String billNo;
    private String salesOrderNo;
    private List<DeliveryDetailBean> sapDeliveryBillDetails;
    private List<NoPalletDetails> sdDeliveryBillNoPalletDetails;

    public int getPkId() {
        return pkId;
    }

    public void setPkId(int pkId) {
        this.pkId = pkId;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getSalesOrderNo() {
        return salesOrderNo;
    }

    public void setSalesOrderNo(String salesOrderNo) {
        this.salesOrderNo = salesOrderNo;
    }

    public List<DeliveryDetailBean> getSapDeliveryBillDetails() {
        return sapDeliveryBillDetails;
    }

    public void setSapDeliveryBillDetails(List<DeliveryDetailBean> sapDeliveryBillDetails) {
        this.sapDeliveryBillDetails = sapDeliveryBillDetails;
    }

    public List<NoPalletDetails> getSdDeliveryBillNoPalletDetails() {
        return sdDeliveryBillNoPalletDetails == null ? new ArrayList<NoPalletDetails>() : sdDeliveryBillNoPalletDetails;
    }

    public void setSdDeliveryBillNoPalletDetails(List<NoPalletDetails> sdDeliveryBillNoPalletDetails) {
        this.sdDeliveryBillNoPalletDetails = sdDeliveryBillNoPalletDetails;
    }

//    public List<SapBillDetailBean> getDeliveryBillPalletResultVOs() {
//        return deliveryBillPalletResultVOs;
//    }
//
//    public void setDeliveryBillPalletResultVOs(List<SapBillDetailBean> deliveryBillPalletResultVOs) {
//        this.deliveryBillPalletResultVOs = deliveryBillPalletResultVOs;
//    }
}
