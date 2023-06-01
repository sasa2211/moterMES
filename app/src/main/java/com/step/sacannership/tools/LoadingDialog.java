package com.step.sacannership.tools;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.step.sacannership.R;

/**
 * Created by step on 2018-2-26.
 */

public class LoadingDialog {
    public static Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading, null);// 得到加载view
        RelativeLayout layout = v.findViewById(R.id.rl_loding);// 加载布局
        Drawable drawable = context.getResources().getDrawable(R.drawable.dialog_bg);
        layout.setBackground(drawable);
        // main.xml中的ImageView
        ImageView spaceshipImage = v.findViewById(R.id.img_load);
        spaceshipImage.setImageResource(R.mipmap.loaddialog);
        TextView tipTextView = v.findViewById(R.id.tv_load);// 提示文字
        tipTextView.setTextColor(Color.WHITE);
        // 加载动画
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.loadanim);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(animation);
        tipTextView.setText(msg);// 设置加载信息

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        loadingDialog.setCancelable(false);// 不可以用“返回键”取消

        int wid = (int) context.getResources().getDimension(R.dimen.size150);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(wid, wid));// 设置布局
        return loadingDialog;
    }

    //显示dialog的方法
    public static void showLoadingDialog(Dialog mLog) {
        if (mLog != null) {
            //设置点击返回键取消dialog
            mLog.setCancelable(true);
            //设置dialog中点击外面不消失
            mLog.setCanceledOnTouchOutside(false);
            //显示log
            mLog.show();
        }
    }

    //取消dialog方法
    public static void hideLoadingDialog(Dialog mLog) {
        if (mLog != null) {
            mLog.cancel();
        }
    }
}
