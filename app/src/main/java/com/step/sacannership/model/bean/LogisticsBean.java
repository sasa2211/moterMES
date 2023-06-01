package com.step.sacannership.model.bean;

public class LogisticsBean {
    private String logisticsName;
    private String logisticsOrderNO;
    private String palletNO;
    private String billNo;

    public LogisticsBean(String logisticsName, String logisticsOrderNO, String palletNO, boolean hasPallet) {
        this.logisticsName = logisticsName;
        this.logisticsOrderNO = logisticsOrderNO;
        if (hasPallet){
            this.palletNO = palletNO;
        }else {
            this.billNo = palletNO;
        }

    }

}
