package com.raed.twitterclient.utilis;

import android.util.Log;

/**
 * Created by Raed on 11/07/2018.
 */

public class Crashlytics {

    private static final String TAG = "Crashlytics";

    public static void log(String s){
        Log.d(TAG, s);
    }

    public static void logException(Exception e) {
        Log.e(TAG, "logException: ", e);
    }
}
