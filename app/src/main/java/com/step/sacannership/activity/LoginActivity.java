package com.step.sacannership.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljd.retrofit.progress.ProgressBean;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.QMUILoadingView;
import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.step.sacannership.BuildConfig;
import com.step.sacannership.MainActivity;
import com.step.sacannership.R;
import com.step.sacannership.api.ApiManager;
import com.step.sacannership.api.GlideApp;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.listener.UpdateListener;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.UpdateInfo;
import com.step.sacannership.model.bean.UserBean;
import com.step.sacannership.tools.FileUtil;
import com.step.sacannership.tools.SPTool;
import com.step.sacannership.update.DownLoadDialog;
import com.step.sacannership.update.DownLoadInfo;
import com.step.sacannership.update.DownloadManager;
import com.step.sacannership.update.UpdateBean;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LoginActivity extends BaseActivity implements TPresenter<UserBean>{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edit_account)
    EditText editAccount;
    @BindView(R.id.edit_password)
    EditText editPassword;
    @BindView(R.id.loading)
    QMUILoadingView loadingView;
    @BindView(R.id.img_logo)
    ImageView imgLogo;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.progress_bar)
    QMUIProgressBar progressBar;

    @Override
    protected int contentView() {
        return R.layout.login_view;
    }

    @Override
    protected void initView() {
        setOnclick(editAccount);
        setOnclick(editPassword);

        requestFocus(editAccount);
        initToolBar(toolbar);
        GlideApp.with(this).asGif().load(R.mipmap.logo).into(imgLogo);
        editAccount.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP){
                requestFocus(editPassword);
            }
            return true;
        });
        editPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP){
                login();
            }
            QMUIKeyboardHelper.hideKeyboard(editPassword);
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

    @OnClick({R.id.btn_login, R.id.tv_server, R.id.btn_update})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_server:
                QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
                builder.setTitle("配置服务器地址").setPlaceholder(SPTool.getServer());
                builder.addAction("取消", (dialog, index) -> dialog.dismiss());
                builder.addAction("确定", (dialog, index) -> {
                    StringBuilder sb = new StringBuilder();
                    EditText editText = builder.getEditText();
                    String text = editText.getText().toString().trim();
                    if (!TextUtils.isEmpty(text)){
                        if (!text.startsWith("http://")){
                            sb.append("http://");
                        }
                        sb.append(text);
                        if (!text.endsWith("/")){
                            sb.append("/");
                        }
                    }else {
                        sb.append("http://192.168.50.3:8080/");
                    }
                    SPTool.put(LoginActivity.this, SPTool.Server, sb.toString().trim());
                    ApiManager.refreshRetrofit();
                    dialog.dismiss();
                });
                builder.create().show();
                break;
            case R.id.btn_update:
                checkUpdate();
                break;
        }
    }

    private void login(){
        if (tModel == null) tModel = new TModel();
        String name = editAccount.getText().toString();
        String password = editPassword.getText().toString();
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

        Log.e("TAGGG", "token="+userBean.getToken());

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
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissDialog() {
        loadingView.setVisibility(View.GONE);
    }


//    @Override
//    public void getUpdateSuccess(UpdateInfo updateInfo) {
//        int appVersion = FileUtil.getVersionCodeName(this);
//        int serverVersion = 1;
//        try {
//            serverVersion = Integer.parseInt(updateInfo.getVersion());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        if (serverVersion > appVersion){
//            showMessage("发现新版本,版本号："+updateInfo.getVersion(), true, updateInfo.getFilepath(), updateInfo.getVersion());
//        }else {
//            showMessage("已是最新版本，无需更新", false);
//        }
//    }
//
//    @Override
//    public void getUpdateFailed(String message) {
//        showMessage("获取版本失败："+message, false);
//    }

    private void showMessage(String message, boolean showDownload, String ...filePath){
        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(this).setMessage(message);
//        if (showDownload){
//            builder.addAction("取消", (dialog, index) -> dialog.dismiss())
//                .addAction("下载", (dialog, index) -> {
//                    dialog.dismiss();
//                    if (filePath.length > 0){
//
//                        if (tModel == null){
//                            tModel = new TModel();
//                        }
//                        tModel.downLoadFile(filePath[0], filePath[1], LoginActivity.this);
//
//                    }else {
//                        showMessage("找不到下载链接，请联系相关人员确认！", false);
//                    }
//
//                });
//        }else {
//            builder.addAction("确定", (dialog, index) -> dialog.dismiss());
//        }
        builder.addAction("确定", (dialog, index) -> dialog.dismiss());
        builder.create().show();
    }
//    QMUITipDialog tipDialog;
//    @Override
//    public void show() {
//        if (tipDialog == null){
//            tipDialog = new QMUITipDialog.Builder(this)
//                    .setTipWord("正在获取最新版本")
//                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
//                    .create();
//        }
//        tipDialog.show();
//    }
//
//    @Override
//    public void dismiss() {
//        if (tipDialog != null){
//            tipDialog.dismiss();
//        }
//    }
//
//    @Override
//    public void downSuccess(ProgressBean progressBean) {
//        if (progressBar.getVisibility() != View.VISIBLE){
//            progressBar.setVisibility(View.VISIBLE);
//        }
//        if (tvProgress.getVisibility() != View.VISIBLE){
//            tvProgress.setVisibility(View.VISIBLE);
//        }
//        if (progressBean.isDone()){
//            tvProgress.setText("下载进度：100%");
//            progressBar.setProgress(100, true);
//        }else {
//            long downByte = progressBean.getBytesRead();
//            long countByte = progressBean.getContentLength();
//            float progress = downByte * 100.0f / countByte;
//            progressBar.setProgress((int)progress);
//            DecimalFormat decimalFormat = new  DecimalFormat( ".00" );
//            tvProgress.setText("下载进度："+decimalFormat.format(progress)+"%");
//        }
//    }
//
//    @Override
//    public void downLoadFailed(String message) {
//        showMessage("下载失败:"+message+"；请联系相关人员确认！", false);
//    }
}
