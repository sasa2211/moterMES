package com.step.sacannership.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.ljd.retrofit.progress.ProgressBean;
import com.step.sacannership.listener.UpdateListener;
import com.step.sacannership.model.TModel;
import com.step.sacannership.model.bean.UpdateInfo;
import com.step.sacannership.tools.FileUtil;
import com.step.sacannership.tools.ToastUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateService extends Service implements UpdateListener {

    private TModel tModel;
    Timer timer = new Timer();

    TimerTask task = new TimerTask(){
        @Override
        public void run(){
            if (tModel == null){
                tModel = new TModel();
            }
        }
    };

    private final IBinder binder = new MyBinder();//绑定器
    @Override
    public void getUpdateSuccess(UpdateInfo updateInfo) {
        try {
            int versionServer = Integer.parseInt(updateInfo.getVersion());
            int appVersion = FileUtil.getVersionCodeName(this);
            if (appVersion < versionServer){
                deleteFile();
                //开始下载
                tModel.downLoadFile(updateInfo.getFilepath(), updateInfo.getVersion(), this);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deleteFile(){
        /**
         * 删除旧文件
         * */
        try {
            File parentFile = new File(ToastUtils.apkFile);
            if (parentFile.exists()){
                if (parentFile.isDirectory()){
                    File[] files = parentFile.listFiles();
                    for (File file : files){
                        boolean isSuccess = file.delete();
                        Log.e("TAGGG", "deleteSuccess="+isSuccess);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void getUpdateFailed(String message) {

    }

    @Override
    public void show() {

    }

    @Override
    public void dismiss() {

    }

    @Override
    public void downSuccess(ProgressBean progressBean) {
        Intent intent = new Intent("progress");
        int progress = (int) (progressBean.getBytesRead() * 100 / progressBean.getContentLength());
        Log.e("TAGGG", "progress="+progress);
        intent.putExtra("progress", progress);
        intent.putExtra("isDone", progressBean.isDone());
        sendBroadcast(intent);
    }

    @Override
    public void downLoadFailed(String errorMessage) {
        Intent intent = new Intent("error");
        intent.putExtra("message", "软件版本更新失败："+errorMessage+";请联系相关人员确认！");
        sendBroadcast(intent);
    }

    public class MyBinder extends Binder {
        public UpdateService getService() {
            return UpdateService.this;//返回本服务
        }
    }

    /**绑定时执行*/
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("TAGGG", "onBind()");
        //开启定时器
        timer.schedule(task,0,20 * 60 * 1000);
        return binder;
    }


    /** 断开绑定是执行 */
    @Override
    public boolean onUnbind(Intent intent) {
        //结束定时器
        timer.cancel();
        if (tModel != null){
            tModel.onDestory();
        }
        return super.onUnbind(intent);
    }
}