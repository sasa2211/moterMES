package com.step.sacannership.model.bean;

import java.util.List;

public class NpBindSaveBean {
    private List<NoPalletDetails> sdDeliveryBillNoPalletDetails;

    public NpBindSaveBean(List<NoPalletDetails> deliveryBillNoPalletVO) {
        this.sdDeliveryBillNoPalletDetails = deliveryBillNoPalletVO;
    }
}
