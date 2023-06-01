package com.step.sacannership.activity;

import android.app.Application;

public class App extends Application {
    private static App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        MyCrashHandler crashHandler = MyCrashHandler.instance();
//        crashHandler.init(getApplicationContext());
    }

    public static App getInstance() {
        return instance;
    }
}
