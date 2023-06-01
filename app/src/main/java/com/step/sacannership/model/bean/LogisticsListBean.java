package com.step.sacannership.model.bean;

public class LogisticsListBean {

    private int pkid;
    private String name;

    public LogisticsListBean(int pkid, String name) {
        this.pkid = pkid;
        this.name = name;
    }

    public int getPkid() {
        return pkid;
    }

    public void setPkid(int pkid) {
        this.pkid = pkid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
