package com.step.sacannership.activity;

import android.app.Dialog;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.step.sacannership.R;
import com.step.sacannership.databinding.DeliveryCancelViewBinding;
import com.step.sacannership.listener.CancelListener;
import com.step.sacannership.model.TModel;
import com.step.sacannership.tools.LoadingDialog;
import com.step.sacannership.tools.ToastUtils;
import butterknife.BindView;
import butterknife.OnClick;

public class DeliverySapCancel extends BaseTActivity<DeliveryCancelViewBinding> implements CancelListener {
//
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
//    @BindView(R.id.delivery_no)
//    EditText deliveryNo;
//    @BindView(R.id.tv_name)
//    TextView tvName;
private boolean hasPallet;
    @Override
    protected int contentView() {
        return R.layout.delivery_cancel_view;
    }

    @Override
    protected void initView() {
        hasPallet = getIntent().getBooleanExtra("hasPallet", true);
        binding.topBar.setTitle(hasPallet ? "托盘号" : "发运单编号");
        binding.topBar.addLeftBackImageButton().setOnClickListener(v->onBackPressed());
//        initToolBar(toolbar);
        binding.deliveryNo.setOnEditorActionListener((v, actionId, event) -> {
//            if (event.getAction() == KeyEvent.ACTION_UP) {
//                cancelDeliverySubmit();
//            }
            if (isTwice()){
                cancelDeliverySubmit();
            }
            QMUIKeyboardHelper.hideKeyboard(binding.deliveryNo);
            return true;
        });
        binding.btnSubmit.setOnClickListener(v -> cancelDeliverySubmit());
    }

    private void cancelDeliverySubmit(){
        String deliveryText = binding.deliveryNo.getText().toString();
        if (TextUtils.isEmpty(deliveryText)){
            ToastUtils.showToast(this, "编号不能为空");
            return;
        }
        if (tModel == null){
            tModel = new TModel();
        }
        tModel.cancelDelivery(deliveryText, hasPallet, this);
        binding.deliveryNo.setText("");
    }

//    @OnClick(R.id.btn_submit)
//    public void onViewClicked() {
//        cancelDeliverySubmit();
//    }

    @Override
    public void cancelSuccess(String message) {
        ToastUtils.showToast(this, "取消成功");
    }

    @Override
    public void cancelFailed(String message) {
        ToastUtils.showToast(this, "取消失败："+message);
    }

    Dialog dialog;
    @Override
    public void showDialog(String message) {
        dialog = LoadingDialog.createLoadingDialog(this, "正在请求");
        LoadingDialog.showLoadingDialog(dialog);
    }

    @Override
    public void dismissDialog() {
        if (dialog != null){
            LoadingDialog.hideLoadingDialog(dialog);
            dialog = null;
        }
    }
}