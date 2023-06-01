package com.step.sacannership.model.bean;

public class UpdateInfo {
    private Integer pkid;
    private String name;
    private String version;
    private String filepath;
    private boolean isDeleted;
    private boolean createTime;

    public Integer getPkid() {
        return pkid;
    }

    public void setPkid(Integer pkid) {
        this.pkid = pkid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isCreateTime() {
        return createTime;
    }

    public void setCreateTime(boolean createTime) {
        this.createTime = createTime;
    }
}
