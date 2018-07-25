package com.raed.twitterclient;

import android.app.Application;

import com.raed.twitterclient.di.AppComponent;
import com.raed.twitterclient.di.ContextModule;
import com.raed.twitterclient.di.DaggerAppComponent;

public class MyApplication extends Application {

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent
                .builder()
                .contextModule(new ContextModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
