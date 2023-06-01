package com.step.sacannership.listener;

import com.step.sacannership.model.bean.BindResultBean;

public interface BindListener {
    void bindMaterialSuccess(BindResultBean result);
    void bindMaterialFail(String message);
    void bindSuccess(String materialNo, boolean isExit);
    void bindFailed(String message);
    void deleteSuccess();
    void deleteFailed(String message);

}
