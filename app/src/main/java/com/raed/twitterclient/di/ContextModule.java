package com.raed.twitterclient.di;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    private Context mContext;

    public ContextModule(Application application) {
        mContext = application;
    }

    @Provides
    Context provideContext(){
        return mContext;
    }
}
