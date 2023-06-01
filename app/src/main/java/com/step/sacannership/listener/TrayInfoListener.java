package com.step.sacannership.listener;

import com.step.sacannership.model.bean.TrayInfoBean;

public interface TrayInfoListener extends BasePresenter {
    void getTraySuccess(TrayInfoBean infoBean);
    void getTrayFailed(String message);
}
