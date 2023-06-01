package com.step.sacannership.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogBuilder;
import com.step.sacannership.R;
import com.step.sacannership.tools.FileUtil;

public class ProgressDialog extends QMUIDialogBuilder {
    QMUIProgressBar progressBar;
    TextView tvMessage;
    public ProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreateContent(QMUIDialog dialog, ViewGroup parent, Context context) {

        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));

        View view = LayoutInflater.from(context).inflate(R.layout.down_progress_view, parent, false);
        parent.addView(view);
        progressBar = view.findViewById(R.id.progress_bar);
        tvMessage = view.findViewById(R.id.tv_message);
        tvMessage.setText("系统检测到新版本,正在为您下载更新！");
    }

    public void setProgress(int progress){
        if (progressBar != null){
            progressBar.setProgress(progress, false);
        }
    }
    public boolean isShow(){
        if (mDialog != null){
            return mDialog.isShowing();
        }else {
            return false;
        }
    }
    public void dismiss(){
        mDialog.dismiss();
    }
}
