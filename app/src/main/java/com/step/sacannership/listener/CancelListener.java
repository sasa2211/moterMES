package com.step.sacannership.listener;
/**
 * 撤销出库
 * 撤销发运
 * */
public interface CancelListener extends BasePresenter {
    void cancelSuccess(String message);
    void cancelFailed(String message);
}
