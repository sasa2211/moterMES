package com.step.sacannership.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.step.sacannership.activity.App;
import com.step.sacannership.api.ApiManager;
import com.step.sacannership.api.MyIntercepter;
import com.step.sacannership.api.NullOnEmptyConverterFactory;
import com.step.sacannership.model.bean.ErrorBean;
import com.step.sacannership.tools.SPTool;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;

import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by step on 2018-2-8.
 */

public class BaseModuel {
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void onDestory(){
        compositeDisposable.clear();
    }

    public static <T> FlowableTransformer<T, T> toMain() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
    protected String getErrorMsg(String errorMsg){
        String result;
        if (errorMsg.contains("400")){
            result = "网络异常";
        }else if (errorMsg.contains("401")){
            result = "未登录或者登录过期";
        }else if (errorMsg.contains("403")){
            result = "当前账号没有操作权限";
        }else if (errorMsg.contains("404")){
            result = "服务端无此接口";
        }else if (errorMsg.contains("405")){
            result = "请求方式不正确";
        }else if (errorMsg.contains("500")){
            result = "内部服务器错误";
        }else {
            result = errorMsg;
        }
        return result;
    }
    public String getErrorMessage(Throwable throwable) throws Exception{
        try {
            if (throwable instanceof HttpException){
                String json = ((HttpException) throwable).response().errorBody().string();
                try {
                    ErrorBean errorBean = new Gson().fromJson(json, ErrorBean.class);
                    json = errorBean.getMessage();
                }catch (Exception e){
                    e.printStackTrace();
                }
                return json;
            }else if (throwable instanceof ConnectException){
                return "网络异常";
            }else {
                return getErrorMsg(throwable.getMessage());
            }
        }catch (Exception e){
            e.printStackTrace();
            return "连接异常";
        }

    }

    /**
     * 项目中使用的服务器有多个
     * 不想添加太多APIManager类
     * 非常用服务器使用该方法获取一个retrofit对象
     * */
    protected Retrofit getRetrofit(String url){
        //开启Log
        HttpLoggingInterceptor httpLog = new HttpLoggingInterceptor(message -> Log.e("TAGGG", "message="+message));
        httpLog.setLevel(HttpLoggingInterceptor.Level.BODY);
        //缓存
        File cacheFile = new File(App.getInstance().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 50 * 1024 * 1024);
        //增加头部信息
        //增加头部信息
        Interceptor header = chain -> {
            Request build = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", SPTool.getToken())
                    .build();
            return chain.proceed(build);
        };

        OkHttpClient okHttp = new OkHttpClient.Builder()
//        OkHttpClient okHttpClient = ProgressHelper.addProgress(null)
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                //.addNetworkInterceptor(new NetworkBaseInterceptor())
                .addInterceptor(header)
                .addInterceptor(httpLog)
                .addInterceptor(new MyIntercepter())
                .cache(cache)
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .serializeNulls()
                .create();
        return new Retrofit.Builder()
                .client(okHttp)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .baseUrl(url)
                .build();
    }

}
