package com.step.sacannership.activity;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.step.sacannership.R;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.model.TModel;
import com.step.sacannership.tools.ToastUtils;
import okhttp3.ResponseBody;

public class BackUnBindActivity extends BaseActivity implements TPresenter<ResponseBody> {

    @Override
    protected int contentView() {
        return R.layout.back_un_bind_view;
    }

    private EditText editBarCode;
    private QMUITopBarLayout topBarLayout;
    private QMUIEmptyView emptyView;
    @Override
    protected void initView() {
        emptyView = findViewById(R.id.empty_view);
        topBarLayout = findViewById(R.id.topBar);
        topBarLayout.setTitle("退货机器解绑");
        topBarLayout.addLeftBackImageButton().setOnClickListener(v->onBackPressed());
        editBarCode = findViewById(R.id.editBarCode);
        editBarCode.setOnEditorActionListener((v, actionId, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP){
                QMUIKeyboardHelper.hideKeyboard(editBarCode);
                submitUnBind();
            }
            return true;
        });
    }

    private void submitUnBind(){
        if (tModel == null){
            tModel = new TModel();
        }
        String barCode = editBarCode.getText().toString().trim();
        if (TextUtils.isEmpty(barCode)){
            ToastUtils.showToast(this, "请扫描机器条码");
            return;
        }
        tModel.backUnBind(barCode, this);
    }

    @Override
    public void showDialog(String message) {
        emptyView.show("正在提交", "");
        emptyView.setLoadingShowing(true);
    }

    @Override
    public void dismissDialog() {
        emptyView.hide();
    }

    @Override
    public void getSuccess(ResponseBody responseBody) {
        emptyView.show("成功", "");
        requestFocus(editBarCode);
    }

    @Override
    public void getFailed(String message) {
        emptyView.show("", message);
    }
}