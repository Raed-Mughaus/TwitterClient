package com.raed.twitterclient;

import android.app.Application;

public class MyApplication extends Application {

    private static MyApplication sMyApplication;

    public static MyApplication getApp() {
        return sMyApplication;
    }

    @Override
    public void onCreate() {
        sMyApplication = this;
        super.onCreate();
    }



}

