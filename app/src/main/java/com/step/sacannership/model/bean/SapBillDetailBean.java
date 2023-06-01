package com.step.sacannership.model.bean;

public class SapBillDetailBean {

    private int pkid;
    private String workingDay;
    private int workingDayType;

    public int getPkid() {
        return pkid;
    }

    public void setPkid(int pkid) {
        this.pkid = pkid;
    }

    public String getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(String workingDay) {
        this.workingDay = workingDay;
    }

    public int getWorkingDayType() {
        return workingDayType;
    }

    public void setWorkingDayType(int workingDayType) {
        this.workingDayType = workingDayType;
    }
}
