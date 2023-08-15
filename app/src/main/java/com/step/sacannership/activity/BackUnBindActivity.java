package com.step.sacannership.activity;

import android.text.TextUtils;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.step.sacannership.R;
import com.step.sacannership.databinding.BackUnBindViewBinding;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.model.TModel;
import com.step.sacannership.tools.ToastUtils;
import okhttp3.ResponseBody;

public class BackUnBindActivity extends BaseTActivity<BackUnBindViewBinding> implements TPresenter<ResponseBody> {

    @Override
    protected int contentView() {
        return R.layout.back_un_bind_view;
    }

    @Override
    protected void initView() {
        binding.topBar.setTitle("退货机器解绑");
        binding.topBar.addLeftBackImageButton().setOnClickListener(v->onBackPressed());
        binding.editBarCode.setOnEditorActionListener((v, actionId, event) -> {
            if (!isTwice()){
                QMUIKeyboardHelper.hideKeyboard(v);
                submitUnBind();
            }
            return true;
        });
    }

    private void submitUnBind(){
        if (tModel == null){
            tModel = new TModel();
        }
        String barCode = binding.editBarCode.getText().toString().trim();
        if (TextUtils.isEmpty(barCode)){
            ToastUtils.showToast(this, "请扫描机器条码");
            return;
        }
        tModel.backUnBind(barCode, this);
    }

    @Override
    public void showDialog(String message) {
        binding.emptyView.show("正在提交", "");
        binding.emptyView.setLoadingShowing(true);
    }

    @Override
    public void dismissDialog() {
        binding.emptyView.hide();
    }

    @Override
    public void getSuccess(ResponseBody responseBody) {
        binding.emptyView.show("成功", "");
        requestFocus(binding.editBarCode);
    }

    @Override
    public void getFailed(String message) {
        binding.emptyView.show("", message);
    }
}