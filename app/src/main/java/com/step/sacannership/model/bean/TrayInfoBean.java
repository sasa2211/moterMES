package com.step.sacannership.model.bean;

import java.util.ArrayList;
import java.util.List;

public class TrayInfoBean {
    /**
     * deliveryBills : [{"deliveryBillId":0,"deliveryBillNO":"string","deliveryBillPalletDetails":[{"deliveryBillID":0,"masterID":0,"materialName":"string","materialNo":"string","pkid":0,"quantity":0,"rowIndex":"string"}]}]
     * palletId : 0
     * palletNO : string
     */

    private int palletId;
    private String palletNO;

    private List<TrayDeliveryBean> deliveryBills;

    public int getPalletId() {
        return palletId;
    }

    public void setPalletId(int palletId) {
        this.palletId = palletId;
    }

    public String getPalletNO() {
        return palletNO;
    }

    public void setPalletNO(String palletNO) {
        this.palletNO = palletNO;
    }

    public List<TrayDeliveryBean> getDeliveryBills() {
        return deliveryBills == null ? new ArrayList<>() : deliveryBills;
    }

    public void setDeliveryBills(List<TrayDeliveryBean> deliveryBills) {
        this.deliveryBills = deliveryBills;
    }
}
