package com.raed.twitterclient.timeline.home_timeline.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.raed.twitterclient.MyApplication;

public class HomeTLPreferences {

    private static final String SHARED_PREFERENCES_NAME = "timeline";
    private static final String KEY_SCROLLED_TO_ID = "scrolled_to_id";

    private SharedPreferences mSharedPreferences;

    public HomeTLPreferences(String userID) {
        mSharedPreferences = MyApplication.getApp()
                .getSharedPreferences(userID + "_" + SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void saveScrollPosition(long tweetID){
        mSharedPreferences.edit()
                .putLong(KEY_SCROLLED_TO_ID, tweetID)
                .apply();
    }

    public long getScrolledToId(long defValue) {
        return mSharedPreferences.getLong(KEY_SCROLLED_TO_ID, defValue);
    }
}
