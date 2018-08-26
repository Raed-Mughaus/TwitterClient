package com.raed.twitterclient.timeline.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.raed.twitterclient.MyApplication;

public class TLPreferences {

    private static final String SHARED_PREFERENCES_NAME = "tl_view_model";
    private static final String KEY_SCROLLED_TO_ID = "scrolled_to_id";
    private static final String KEY_OFFSET = "offset";

    private SharedPreferences mSharedPreferences;

    public TLPreferences(String userID) {
        mSharedPreferences = MyApplication.getApp()
                .getSharedPreferences(userID + SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void saveScrollPosition(long tweetID, int offset){
        mSharedPreferences.edit()
                .putLong(KEY_SCROLLED_TO_ID, tweetID)
                .putInt(KEY_OFFSET, offset)
                .apply();
    }

    public long getScrolledToId(long defValue) {
        return mSharedPreferences.getLong(KEY_SCROLLED_TO_ID, defValue);
    }

    public int getOffset() {
        return mSharedPreferences.getInt(KEY_OFFSET, 0);
    }
}
