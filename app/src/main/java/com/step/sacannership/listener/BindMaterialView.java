package com.step.sacannership.listener;

import java.util.Map;

public interface BindMaterialView {
    void bindSuccess(Map<String, Object> map);
    void bindFailed(String message);
    void syncBarcodeSuccess();
    void deleteSuccess();
    void deleteFailed(String message);
}
