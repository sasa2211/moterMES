package com.step.sacannership.api;

import android.content.Context;
import android.content.Intent;

import com.step.sacannership.activity.App;
import com.step.sacannership.activity.LoginActivity;
import com.step.sacannership.tools.ToastUtils;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class MyIntercepter implements Interceptor{
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        int code = response.code();
        String message = response.message();

        if (401 == code || "Unauthorized".equals(message)){
            //重新登录
            Context context = App.getInstance().getApplicationContext();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("clear", true);
            context.startActivity(intent);
            ToastUtils.showToast(context, "登录过期，请重新登录");
        }
        return response;
    }
}