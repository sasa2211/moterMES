package com.step.sacannership;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.step.sacannership.activity.BaseActivity;
import com.step.sacannership.activity.LoginActivity;
import com.step.sacannership.fragment.MenuPalletFragment;
import com.step.sacannership.listener.TPresenter;
import com.step.sacannership.model.TModel;
import com.step.sacannership.tools.SPTool;
import com.step.sacannership.tools.ToastUtils;
import com.step.sacannership.update.DownLoadDialog;
import com.step.sacannership.update.DownLoadInfo;
import com.step.sacannership.update.DownloadManager;
import com.step.sacannership.update.UpdateBean;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    QMUITopBarLayout topBarLayout;
    @BindView(R.id.fragment)
    FrameLayout fragment;

    @BindView(R.id.rb_pallet)
    RadioButton rbPallet;
    @BindView(R.id.rb_un_pallet)
    RadioButton rbUnPallet;
    @BindView(R.id.rg_bottom)
    RadioGroup radioGroup;

    MenuPalletFragment palletFragment;
    MenuPalletFragment unPalletFragment;

    private FragmentManager manager;

//    private MyBroadCastReceiver receiver;

    @Override
    protected int contentView() {
        return R.layout.activity_main;
    }

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Intent bind = new Intent(MainActivity.this, UpdateService.class);
//        bindService(bind, sconnection, Context.BIND_AUTO_CREATE);
//        receiver = new MyBroadCastReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("start");
//        filter.addAction("progress");
//        filter.addAction("error");
//        registerReceiver(receiver, filter);
//    }

    @Override
    protected void initView() {
        topBarLayout.addLeftBackImageButton().setOnClickListener(v -> onBackPressed());
        topBarLayout.setTitle("扫描菜单");
        topBarLayout.setSubTitle(ToastUtils.getVersionName(this));

        palletFragment = MenuPalletFragment.newInstance(true);
        unPalletFragment = MenuPalletFragment.newInstance(false);

        manager = getSupportFragmentManager();
        replaceFrg(palletFragment);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> replaceFragment(checkedId));

        checkUpdate();
    }

    private void replaceFragment(int checkID){
        switch (checkID){
            case R.id.rb_pallet:
                replaceFrg(palletFragment);
                break;
            case R.id.rb_un_pallet:
                replaceFrg(unPalletFragment);
                break;
        }
    }
    private Fragment curFrg = new Fragment();
    public void replaceFrg(Fragment fragment) {
        if (fragment == null) {
            return;
        }

        FragmentTransaction transaction = manager.beginTransaction();
        if (!fragment.isAdded()) {
            transaction.hide(curFrg).add(R.id.fragment, fragment, fragment.getClass().getName());
        } else {
            transaction.hide(curFrg).show(fragment);
        }
        transaction.commit();
        curFrg = fragment;
    }

    @OnClick(R.id.tv_exit)
    public void onViewClicked() {
        SPTool.remove(MainActivity.this, SPTool.Token);
        SPTool.remove(MainActivity.this, SPTool.Authority);
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
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
//                new QMUIDialog.MessageDialogBuilder(this)
//                        .setMessage("当前已是最新版本，无需更新！")
//                        .addAction("确定", (dialog, index) -> dialog.dismiss()).create().show();
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
                downloadDialog = new DownLoadDialog(MainActivity.this, url);
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
                new QMUIDialog.MessageDialogBuilder(MainActivity.this)
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
}
