package com.raed.twitterclient;

import android.app.Application;

import com.raed.twitterclient.retrofitservices.RetrofitServices;
import com.raed.twitterclient.userdata.CurrentUser;
import com.raed.twitterclient.userdata.Users;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Users.initializeInstance(this);
        CurrentUser.initializeInstance(this);
        RetrofitServices.initializeInstance();
    }

}

