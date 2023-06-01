package com.step.sacannership.fragment;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.QMUILoadingView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogBuilder;
import com.step.sacannership.R;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.model.TModel;
import com.step.sacannership.tools.ToastUtils;

public class ExportSnDialog extends QMUIDialogBuilder implements View.OnClickListener, TPresenter {

    private String materialNo;
    private Activity activity;
    private ImportListener listener;

    public void setListener(ImportListener listener) {
        this.listener = listener;
    }

    public ExportSnDialog(Activity activity, String materialNo) {
        super(activity);
        this.materialNo = materialNo;
        this.activity = activity;
    }

    private TextView tvMaterialNo;
    private EditText editSnNo;
    private TextView tvCancel, tvSure;
    private QMUILoadingView loadingView;

    private TextView tvProgress;
    @Override
    protected void onCreateContent(QMUIDialog dialog, ViewGroup parent, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.export_sn_view, parent, false);
        parent.addView(view);
        tvMaterialNo = view.findViewById(R.id.tv_material);
        tvMaterialNo.setText("物料SN："+materialNo);
        editSnNo = view.findViewById(R.id.edit_sn);
        tvCancel = view.findViewById(R.id.tv_cancel);
        tvSure = view.findViewById(R.id.tv_sure);
        tvSure.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvProgress = view.findViewById(R.id.tv_progress);
        editSnNo.setOnEditorActionListener((v, actionId, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP){
                saveMaterial();
                QMUIKeyboardHelper.hideKeyboard(editSnNo);
            }
            return true;
        });
        loadingView = view.findViewById(R.id.loading);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_sure:
                saveMaterial();
                break;
            case R.id.tv_cancel:
                listener.importFail();
                mDialog.dismiss();
                break;
        }
    }

    private TModel tModel;
    private void saveMaterial(){
        String materialSn = editSnNo.getText().toString().trim();
        if (TextUtils.isEmpty(materialSn)){
            ToastUtils.showToast(activity, "请扫描物料sn");
            return;
        }
        if (tModel == null){
            tModel = new TModel();
        }
        tModel.exportMaterialSn(materialNo, materialSn, this);
    }

    @Override
    public void getSuccess(Object o) {
        editSnNo.setText("");
        tvProgress.setText("成功");
        listener.importSuccess();
        mDialog.dismiss();
    }

    @Override
    public void getFailed(String message) {
        tvProgress.setText("失败："+message);
    }

    @Override
    public void showDialog(String message) {
        loadingView.setVisibility(View.VISIBLE);
        tvProgress.setText("正在保存");
    }

    @Override
    public void dismissDialog() {
        loadingView.setVisibility(View.GONE);
        tvProgress.setText("");
    }

    public void dismiss(){
        mDialog.dismiss();
    }

    public interface ImportListener{
        void importSuccess();
        void importFail();
    }
}
