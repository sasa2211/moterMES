package com.step.sacannership;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.step.sacannership.activity.BaseTActivity;
import com.step.sacannership.activity.LoginActivity;
import com.step.sacannership.databinding.ActivityMainBinding;
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
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseTActivity<ActivityMainBinding> {


    MenuPalletFragment palletFragment;
    MenuPalletFragment unPalletFragment;

    private FragmentManager manager;

    @Override
    protected int contentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        binding.topBar.addLeftBackImageButton().setOnClickListener(v -> onBackPressed());
        binding.topBar.setTitle("扫描菜单");
        binding.topBar.setSubTitle(ToastUtils.getVersionName(this));

        palletFragment = MenuPalletFragment.newInstance(true);
        unPalletFragment = MenuPalletFragment.newInstance(false);

        manager = getSupportFragmentManager();
        replaceFrg(palletFragment);
        binding.rgBottom.setOnCheckedChangeListener((group, checkedId) -> replaceFragment(checkedId));

        checkUpdate();

        binding.tvExit.setOnClickListener(v -> {
            SPTool.remove(MainActivity.this, SPTool.Token);
            SPTool.remove(MainActivity.this, SPTool.Authority);
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
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
