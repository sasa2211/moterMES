package com.step.sacannership.update;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogBuilder;
import com.step.sacannership.R;

public class DownLoadDialog extends QMUIDialogBuilder {
    private Activity context;
    private String url;
    public DownLoadDialog(Activity context, String url) {
        super(context);
        this.context = context;
        this.url = url;
    }
    private TextView tvProgress;
    private QMUIProgressBar progressBar;
    private TextView tvCancel;

    @Override
    protected void onCreateContent(QMUIDialog qmuiDialog, ViewGroup parent, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.down_progress_view, parent, false);
        parent.addView(view);

        tvProgress = view.findViewById(R.id.tv_progress);
        progressBar = view.findViewById(R.id.progress);
        tvCancel = view.findViewById(R.id.tv_cancel);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        tvCancel.setOnClickListener(v -> {
            DownloadManager.getInstance().cancel(url);
            mDialog.dismiss();
        });
    }


    int progressBefore = 0;
    public void setProgress(int progress, String progressText){
        context.runOnUiThread(() -> {
            tvProgress.setText(progressText);
            if (progressBefore != progress){
                progressBar.setProgress(progress);
                progressBefore = progress;
            }
        });
    }

    public void dismiss(){
        mDialog.dismiss();
    }
}
