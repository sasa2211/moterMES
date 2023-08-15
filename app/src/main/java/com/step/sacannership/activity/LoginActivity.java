package com.step.sacannership.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.step.sacannership.BuildConfig;
import com.step.sacannership.MainActivity;
import com.step.sacannership.R;
import com.step.sacannership.databinding.LoginViewBinding;
import com.step.sacannership.fragment.ConfigServerDialog;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.UserBean;
import com.step.sacannership.tools.SPTool;
import com.step.sacannership.update.DownLoadDialog;
import com.step.sacannership.update.DownLoadInfo;
import com.step.sacannership.update.DownloadManager;
import com.step.sacannership.update.UpdateBean;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LoginActivity extends BaseTActivity<LoginViewBinding> implements TPresenter<UserBean>{

    @Override
    protected int contentView() {
        return R.layout.login_view;
    }

    @Override
    protected void initView() {
        setOnclick(binding.editAccount);
        setOnclick(binding.editPassword);

        requestFocus(binding.editAccount);
        initToolBar(binding.toolbar);

        binding.editAccount.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP){
                requestFocus(binding.editPassword);
            }
            return true;
        });
        binding.editPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP){
                login();
            }
            QMUIKeyboardHelper.hideKeyboard(textView);
            return true;
        });
        boolean isClear = getIntent().getBooleanExtra("clear", false);
        if (isClear){
            SPTool.remove(this, SPTool.Token);
        }
        String token = SPTool.getToken();
        if (!TextUtils.isEmpty(token)){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            requestPermision();
        }

        binding.btnLogin.setOnClickListener(v -> login());
        binding.tvServer.setOnClickListener(v -> {
            ConfigServerDialog serverDialog = new ConfigServerDialog(this);
            serverDialog.create().show();
        });
        binding.btnUpdate.setOnClickListener(v -> checkUpdate());
    }

    public void requestPermision(){
        String[] permsions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        List<String> permisionLists = new ArrayList<>();
        for (String string : permsions) {
            if (ContextCompat.checkSelfPermission(this, string) != PackageManager.PERMISSION_GRANTED) {
                permisionLists.add(string);
            }
        }

        if (!permisionLists.isEmpty()) {
            ActivityCompat.requestPermissions(this, permisionLists.toArray(new String[permisionLists.size()]), 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            List<String> permisionLists = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                String permision = permissions[i];
                int grantResult = grantResults[i];
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    permisionLists.add(permision);
                }
            }

            if (!permisionLists.isEmpty()) {
                requestPermision();
            }
        }
    }

    private void login(){
        if (tModel == null) tModel = new TModel();
        String name = binding.editAccount.getText().toString();
        String password = binding.editPassword.getText().toString();
        tModel.login(name, password, this);
    }

    private void checkUpdate(){
        if (tModel == null) tModel = new TModel();
        tModel.getApkVersion(true, new TPresenter<UpdateBean>() {
            @Override
            public void getSuccess(UpdateBean updateBean) {
                update(updateBean);
            }

            @Override
            public void getFailed(String message) {

            }

            @Override
            public void showDialog(String message) {

            }

            @Override
            public void dismissDialog() {

            }
        });
    }

    private void update(UpdateBean data){
        try{
            int versionCode = BuildConfig.VERSION_CODE;
            if (data.getVersionCode() > versionCode){
                SPTool.put(this, "versionkey", data.getVersionCode());
                String versionName = data.getVersionName();

                new QMUIDialog.MessageDialogBuilder(this)
                        .setMessage("发现新版本："+versionName)
                        .addAction("取消", (dialog, index) -> dialog.dismiss())
                        .addAction("更新", (dialog, index) -> {
                            String downLoadUrl = "http://app.elevatorstar.com:7120/api/" + data.getDownloadUrl();
                            String fileName = "EMES_dj-$versionName.apk";
                            downloadApk(downLoadUrl, fileName);
                            dialog.dismiss();
                        }).create().show();
            }else {
                new QMUIDialog.MessageDialogBuilder(this)
                        .setMessage("当前已是最新版本，无需更新！")
                        .addAction("确定", (dialog, index) -> dialog.dismiss()).create().show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private DownLoadDialog downloadDialog;
    private void downloadApk(String url, String fileName) {
        DownloadManager.getInstance().download(url, fileName, new Observer<DownLoadInfo>() {
            @Override
            public void onSubscribe(Disposable d) {
                downloadDialog = new DownLoadDialog(LoginActivity.this, url);
                downloadDialog.show();
            }

            @Override
            public void onNext(DownLoadInfo downLoadInfo) {
                float curSize = downLoadInfo.getProgress();
                float totalSize = downLoadInfo.getTotal();
                float percentSize = curSize / totalSize * 100;
                DecimalFormat fnum = new DecimalFormat("##0.00");
                String fileStatus = fnum.format(percentSize) + "%";
                downloadDialog.setProgress((int) percentSize, fileStatus);
            }

            @Override
            public void onError(Throwable e) {
                downloadDialog.dismiss();
                new QMUIDialog.MessageDialogBuilder(LoginActivity.this)
                        .setMessage("下载失败：" + e.getMessage())
                        .addAction("确定", (dialog, index) -> dialog.dismiss())
                        .create().show();
            }

            @Override
            public void onComplete() {
                downloadDialog.dismiss();
            }
        });
    }

    @Override
    public void getSuccess(UserBean userBean) {
        SPTool.putToken(userBean.getToken());
        SPTool.putAuthority(userBean.getUserAuthority());
        startActivity(new Intent(this, MainActivity.class));
        SPTool.showToast(this, "登录成功");
        finish();
    }

    @Override
    public void getFailed(String message) {
        showMessage(message, false);
    }

    @Override
    public void showDialog(String message) {
        binding.loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissDialog() {
        binding.loading.setVisibility(View.GONE);
    }

    private void showMessage(String message, boolean showDownload, String ...filePath){
        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(this).setMessage(message);

        builder.addAction("确定", (dialog, index) -> dialog.dismiss());
        builder.create().show();
    }
}