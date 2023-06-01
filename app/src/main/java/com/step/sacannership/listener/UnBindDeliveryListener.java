package com.step.sacannership.listener;

public interface UnBindDeliveryListener extends BasePresenter {
    void unBindSuccess();
    void unBindFail(String message);

}
