package com.step.sacannership.listener;

import com.ljd.retrofit.progress.ProgressBean;
import com.step.sacannership.model.bean.UpdateInfo;

public interface UpdateListener{
    void getUpdateSuccess(UpdateInfo updateInfo);
    void getUpdateFailed(String message);
    void show();
    void dismiss();
    void downSuccess(ProgressBean progressBean);
    void downLoadFailed(String errorMessage);
}
