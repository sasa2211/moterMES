package com.step.sacannership.listener;

import com.step.sacannership.model.bean.BindResultBean;


public interface MaterialBindListener extends BasePresenter{
    void bindMaterialNoPalletSuccess(BindResultBean map);
    void bindMaterialNoPalletFail(String errorMessage);
}
