package com.step.sacannership.model;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.step.sacannership.activity.App;
import com.step.sacannership.tools.FileUtil;
import com.step.sacannership.tools.ToastUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by step on 2019-1-16.
 */

public class MyCrashHandler implements Thread.UncaughtExceptionHandler {
    private static MyCrashHandler crashHandler;
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //程序的Context对象
    private Context mContext;
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);

        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {

            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();//准备发消息的MessageQueue
                Toast.makeText(mContext, "很抱歉,程序出现异常,即将重启.", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();

        //保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }


    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {
        AppCrashLog log = new AppCrashLog();
        log.setSerialNo(Build.SERIAL);
        log.setSystemVersion(ToastUtils.getVersionName(App.getInstance().getApplicationContext()));
        String trace = Log.getStackTraceString(ex);

        log.setStackTrace(trace);
        String errorLog = new Gson().toJson(log);
        FileUtil.saveFile(errorLog);
        return null;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private MyCrashHandler() {
    }

    //单例
    public static MyCrashHandler instance() {
        if (crashHandler == null) {
            crashHandler = new MyCrashHandler();
        }
        return crashHandler;
    }
}
