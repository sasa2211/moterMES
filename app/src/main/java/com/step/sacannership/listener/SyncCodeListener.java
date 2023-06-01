package com.step.sacannership.listener;

public interface SyncCodeListener {
    void syncSuccess();
    void syncFail(String message);
    void getSnNumSuccess(Integer snNum);
}
