package com.step.sacannership.listener;

public interface TPresenter<T> extends BasePresenter{
    void getSuccess(T t);
    void getFailed(String message);
}
