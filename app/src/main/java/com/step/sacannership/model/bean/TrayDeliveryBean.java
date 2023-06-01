package com.step.sacannership.model.bean;

import com.hgdendi.expandablerecycleradapter.BaseExpandableRecyclerViewAdapter;

import java.util.List;

public class TrayDeliveryBean implements BaseExpandableRecyclerViewAdapter.BaseGroupBean<DeliveryBillPalletDetailsBean> {

    private int deliveryBillId;
    private String deliveryBillNO;
    private String createTime;
    private String outStockTime;
    private String palletBindCompletedTime;
    private String inspectCompletedTime;
    private String deliveryCompletedTime;
    private boolean isNewBind;

    private List<DeliveryBillPalletDetailsBean> deliveryBillPalletDetails;

    public String getCreateTime() {
        return createTime == null ? "" : createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOutStockTime() {
        return outStockTime == null ? "" : outStockTime;
    }

    public void setOutStockTime(String outStockTime) {
        this.outStockTime = outStockTime;
    }

    public String getPalletBindCompletedTime() {
        return palletBindCompletedTime == null ? "" : palletBindCompletedTime;
    }

    public void setPalletBindCompletedTime(String palletBindCompletedTime) {
        this.palletBindCompletedTime = palletBindCompletedTime;
    }

    public String getInspectCompletedTime() {
        return inspectCompletedTime == null ? "" : inspectCompletedTime;
    }

    public void setInspectCompletedTime(String inspectCompletedTime) {
        this.inspectCompletedTime = inspectCompletedTime;
    }

    public String getDeliveryCompletedTime() {
        return deliveryCompletedTime == null ? "" : deliveryCompletedTime;
    }

    public void setDeliveryCompletedTime(String deliveryCompletedTime) {
        this.deliveryCompletedTime = deliveryCompletedTime;
    }

    public int getDeliveryBillId() {
        return deliveryBillId;
    }

    public void setDeliveryBillId(int deliveryBillId) {
        this.deliveryBillId = deliveryBillId;
    }

    public String getDeliveryBillNO() {
        return deliveryBillNO;
    }

    public void setDeliveryBillNO(String deliveryBillNO) {
        this.deliveryBillNO = deliveryBillNO;
    }

    public List<DeliveryBillPalletDetailsBean> getDeliveryBillPalletDetails() {
        return deliveryBillPalletDetails;
    }

    public void setDeliveryBillPalletDetails(List<DeliveryBillPalletDetailsBean> deliveryBillPalletDetails) {
        this.deliveryBillPalletDetails = deliveryBillPalletDetails;
    }

    @Override
    public int getChildCount() {
        return deliveryBillPalletDetails.size();
    }

    @Override
    public DeliveryBillPalletDetailsBean getChildAt(int childIndex) {
        return deliveryBillPalletDetails.get(childIndex);
    }
    @Override
    public boolean isExpandable() {
        return getChildCount() > 0;
    }
    public boolean isNewBind() {
        return isNewBind;
    }

    public void setNewBind(boolean newBind) {
        isNewBind = newBind;
    }
}
