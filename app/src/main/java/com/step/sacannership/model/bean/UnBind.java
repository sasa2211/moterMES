package com.step.sacannership.model.bean;

import java.util.List;

public class UnBind {

    private int palletId;
    private List<Integer> deliveryBillIds;

    public int getPalletId() {
        return palletId;
    }

    public void setPalletId(int palletId) {
        this.palletId = palletId;
    }

    public List<Integer> getDeliveryBillIds() {
        return deliveryBillIds;
    }

    public void setDeliveryBillIds(List<Integer> deliveryBillIds) {
        this.deliveryBillIds = deliveryBillIds;
    }
}
