package com.step.sacannership.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.step.sacannership.R;
import com.step.sacannership.activity.App;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ToastUtils {
    public final static String apkFile = Environment.getExternalStorageDirectory().getAbsolutePath()+"/电机Mes/";
    // Toast对象
    private static Toast toast;
    // 文字显示的颜色 <color name="white">#FFFFFFFF</color>
    private static int messageColor = R.color.white;

    /**
     * 发送Toast,默认LENGTH_SHORT
     *
     * @param resId
     */
    public static void show(Context context, int resId) {
        showToast(context, context.getString(resId));
    }

    public static void showToast(Context context, String massage) {
        // 设置显示文字的颜色
        SpannableString spannableString = new SpannableString(massage);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, messageColor));
        spannableString.setSpan(colorSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (toast == null) {
            toast = Toast.makeText(context, spannableString, Toast.LENGTH_SHORT);
        } else {
            toast.setText(spannableString);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        // 设置显示的背景
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.toast_frame_style);
        // 设置Toast要显示的位置，水平居中并在底部，X轴偏移0个单位，Y轴偏移200个单位，
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 200);
        toast.show();
    }

    /**
     * 在UI界面隐藏或者销毁前取消Toast显示
     */
    public static void cancel() {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
    }

    /** 安装一个apk文件 */
    public static void installApp(String filePath) {
        Context context = App.getInstance().getApplicationContext();
        File file = new File(filePath);
        if(Build.VERSION.SDK_INT>=24) {//判读版本是否在7.0以上
            Uri apkUri = FileProvider.getUriForFile(context, "com.step.sacannership.provider", file);//在AndroidManifest中的android:authorities值
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
            context.startActivity(install);
        } else {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(install);
        }
    }

    /**获取当前版本号*/
    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return packInfo.versionName;
    }

    public static String getTimes(){
        long millions = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(millions));
    }
}
