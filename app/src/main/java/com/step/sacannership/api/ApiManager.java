package com.step.sacannership.api;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.step.sacannership.activity.App;
import com.step.sacannership.tools.SPTool;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {
    //读超时长，单位：秒
    private static final int READ_TIME_OUT = 20;
    //连接时长，单位：秒
    private static final int CONNECT_TIME_OUT = 20;
    //缓存大小
    private static final long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MB

    private static Retrofit retrofit;
    private static final Map<String, Object> services = new HashMap<>();

    private ApiManager() {
        //开启Log
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(this::showLog);
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //缓存
        File cacheFile = new File(App.getInstance().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, SIZE_OF_CACHE); //50Mb
        //增加头部信息
        Interceptor headerInterceptor = chain -> {
            Request build = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", SPTool.getToken())
                    .build();
            return chain.proceed(build);
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                //.addNetworkInterceptor(new NetworkBaseInterceptor())
                .addInterceptor(headerInterceptor)
                .addInterceptor(logInterceptor)
                .addInterceptor(new MyIntercepter())

                .cache(cache)
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .serializeNulls()
                .create();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))//MyGsonConverterFactory.create(gson)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
//                .baseUrl("http://192.168.99.126:7002/")
                .baseUrl(SPTool.getServer())
                .build();
    }

    private void showLog(String message){
        if (message.length() > 1000){
            Log.e("TAGGG", message.substring(0, 1000));
            showLog(message.substring(1000));
        } else {
            Log.e("TAGGG", message);
        }
    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            new ApiManager();
        }
        return retrofit;
    }

    public static void refreshRetrofit() {
        services.clear();
        new ApiManager();
    }

    public static <T> T getService(final Class<T> serviceClass) {
        T serviceReal;
        Object service = services.get(serviceClass.getName());
        if (service == null) {
            serviceReal = getRetrofit().create(serviceClass);
            services.put(serviceClass.getName(), serviceReal);
        } else {
            serviceReal = serviceClass.cast(service);
        }
        return serviceReal;
    }
}