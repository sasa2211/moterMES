package com.step.sacannership.update;

public class DownLoadInfo {

    private String url;
    private long total;
    private long progress;
    private String fileName;

    private String filePath;

    public DownLoadInfo(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
