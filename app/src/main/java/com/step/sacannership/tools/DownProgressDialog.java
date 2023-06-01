package com.step.sacannership.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogBuilder;
import com.step.sacannership.R;

public class DownProgressDialog extends QMUIDialogBuilder {
    private QMUIProgressBar progressBar;
    private TextView tvProgress;
    public DownProgressDialog(Context context) {
        super(context);
    }
    @Override
    protected void onCreateContent(QMUIDialog dialog, ViewGroup parent, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.down_progress, parent, false);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setProgress(0);
        progressBar.setMaxValue(100);
        tvProgress = view.findViewById(R.id.tv_progress);
        parent.addView(view);
    }
    public void setProgress(int progress){
        if (progressBar != null){
            progressBar.setProgress(progress, false);
        }

        if (tvProgress != null){
            tvProgress.setText(progress + "%");
        }
    }
}
