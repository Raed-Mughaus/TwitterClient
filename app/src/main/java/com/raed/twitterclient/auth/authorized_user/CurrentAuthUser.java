package com.raed.twitterclient.auth.authorized_user;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raed.twitterclient.MyApplication;
import com.raed.twitterclient.utilis.IOUtils;

import java.io.File;


public class CurrentAuthUser {

    private static final String TAG = CurrentAuthUser.class.getSimpleName();

    private static final String FOLDER_NAME = "users";
    private static final String AUTH_USERS_LIST_FILE_NAME = "auth_users_list";

    private static CurrentAuthUser sCurrentAuthUser;

    private final File mCurrentUserFile;

    private final Gson mGson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat("EEE MMM dd HH:mm:ss Z yyyy")
            .create();

    public static CurrentAuthUser getInstance(){
        if (sCurrentAuthUser == null){
            sCurrentAuthUser = new CurrentAuthUser(MyApplication.getApp());
        }
        return sCurrentAuthUser;
    }

    private CurrentAuthUser(Context context) {
        File folder = new File(context.getFilesDir(), FOLDER_NAME);
        if (!folder.exists())
            folder.mkdirs();

        mCurrentUserFile = new File(folder, AUTH_USERS_LIST_FILE_NAME);
    }

    @MainThread
    public void set(AuthUser authUser){
        String authUserJSON = mGson.toJson(authUser);
        String encryptedAuthUser = encrypt(authUserJSON);
        IOUtils.writeString(mCurrentUserFile, encryptedAuthUser);
    }

    public AuthUser get() {
        String authUserJSON = IOUtils.readString(mCurrentUserFile);
        if (authUserJSON == null)
            return null;
        String decryptedJSON = decrypt(authUserJSON);
        return mGson.fromJson(decryptedJSON, AuthUser.class);
    }

    @MainThread
    public void remove(){
        mCurrentUserFile.delete();
    }

    @NonNull
    private static String encrypt(String str){
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            chars[i] = (char) ((chars[i] + i) % 65536);
        }
        return new String(chars);
    }

    @NonNull
    private static String decrypt(String str){
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            chars[i] = (char) ((chars[i] - i) % 65536);
        }
        return new String(chars);
    }
}
