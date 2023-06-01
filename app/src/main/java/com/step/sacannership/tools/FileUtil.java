package com.step.sacannership.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;


/**
 * Created by step on 2018-12-24.
 */

public class FileUtil {

    public static final String filePath = Environment.getExternalStorageDirectory().toString() +"/crash_collect/";
    public static final String fileName = "crash_collect.txt";

    /**写入file*/
    public static void saveFile(String content){
        BufferedWriter out = null;
        //获取SD卡状态
        String state = Environment.getExternalStorageState();
        //判断SD卡是否就绪
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        StringBuilder sb = new StringBuilder(ToastUtils.getTimes());
        sb.append(content);
        content = sb.toString();
        Log.e("TAGGG", "content="+content);
        File file_directory = new File(filePath);
        File file = new File(file_directory.getPath(), fileName);
//
//        if (file.exists()){
//            file.delete();
//        }

        if (!file_directory.exists()) {
            file_directory.mkdirs(); // 创建文件夹，程序安装在该文件夹下面
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
         /*
            输出流的构造参数1：可以是File对象  也可以是文件路径
            输出流的构造参数2：默认为False=>覆盖内容； true=>追加内容
             */
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath + fileName,true)));
            out.newLine();
            out.write(content);
//            ToastUtils.showShort("保存成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**获取当前版本号*/
    public static int getVersionCodeName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return packInfo.versionCode;
    }
}