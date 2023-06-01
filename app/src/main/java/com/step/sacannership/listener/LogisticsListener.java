package com.step.sacannership.listener;

import com.step.sacannership.model.bean.LogisticsListBean;

import java.util.List;

public interface LogisticsListener {
    void getLogisticsSuccess(List<LogisticsListBean> datas);
    void getLogisticsFailed(String errorMessage);
}
