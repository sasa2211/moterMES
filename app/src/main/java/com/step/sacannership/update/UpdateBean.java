package com.step.sacannership.update;


public class UpdateBean {

    private int id;
    private int appId;
    private String name;
    private String versionName;
    private int versionCode;
    private int size;
    private Object icon;
    private String downloadUrl;
    private int downloadCount;
    private String changeLog;
    private String createTime;
    private boolean forceUpdate;
    private boolean startDown;
    private boolean proActive;//是否主动更新

    public void setProActive(boolean proActive) {
        this.proActive = proActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersionName() {
        return versionName;
    }


    public int getVersionCode() {
        return versionCode;
    }


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }


    public String getDownloadUrl() {
        return downloadUrl;
    }


    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
