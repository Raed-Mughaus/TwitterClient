package com.raed.twitterclient.timeline.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.raed.twitterclient.MyApplication;

//todo each user should have a folder that contains their cached tweets and

public class TLPreferences {

    private static final String SHARED_PREFERENCES_NAME = "timeline";
    private static final String KEY_SCROLLED_TO_ID = "scrolled_to_id";

    private SharedPreferences mSharedPreferences;

    public TLPreferences(String userID) {
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
