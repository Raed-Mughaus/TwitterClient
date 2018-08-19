package com.raed.twitterclient;

import android.app.Application;

import com.raed.twitterclient.retrofitservices.RetrofitServices;
import com.raed.twitterclient.userdata.CurrentUser;
import com.raed.twitterclient.userdata.Users;

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

