package com.step.sacannership.fragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogBuilder;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogView;
import com.step.sacannership.R;
import com.step.sacannership.api.ApiManager;
import com.step.sacannership.tools.SPTool;

public class ConfigServerDialog extends QMUIDialogBuilder<ConfigServerDialog> {
    public ConfigServerDialog(Context context) {
        super(context);
    }

    private RadioGroup rgServer;
    private EditText editServer;
    private TextView tvSure, tvCancel;
    @Nullable
    @Override
    protected View onCreateContent(@NonNull QMUIDialog dialog, @NonNull QMUIDialogView parent, @NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.config_server_view, parent, false);

        rgServer = view.findViewById(R.id.rgIps);
        rgServer.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbIp1){
                editServer.setText("http://dj-mes.stepelectric.com/");
            }else if (checkedId == R.id.rbIp2){
                editServer.setText("http://192.168.50.3:8080/");
            }else if (checkedId == R.id.rbTest){
                editServer.setText("http://192.168.99.50:7000/");
            }
        });

        editServer = view.findViewById(R.id.editServer);
        String server = SPTool.getServer();
        editServer.setText(server);
        tvSure = view.findViewById(R.id.tvSure);
        tvCancel = view.findViewById(R.id.tvCancel);

        tvSure.setOnClickListener(v -> {
            String url = editServer.getText().toString().trim();
            if (TextUtils.isEmpty(url)){
                url = "http://dj-mes.stepelectric.com/";
            }

            if (!url.startsWith("http://")){
                url = "http://" + url;
            }
            if (!url.endsWith("/")){
                url = url + "/";
            }
            SPTool.put(context, SPTool.Server, url.trim());
            ApiManager.refreshRetrofit();
            mDialog.dismiss();
        });
        tvCancel.setOnClickListener(v -> mDialog.dismiss());

        this.setCancelable(false);
        setCanceledOnTouchOutside(false);
        return view;
    }

}
