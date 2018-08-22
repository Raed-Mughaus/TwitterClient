package com.raed.twitterclient.filter;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raed.twitterclient.MyApplication;
import com.raed.twitterclient.io.IOUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to store and retrieve custom conditions.
 */
public class CustomConditionsRepo {

    //todo GSON and proguard

    private static final String CONDITIONS_FILE_NAME = "conditions.json";
    private static final String TAG = CustomConditionsRepo.class.getName();

    private static CustomConditionsRepo sCustomConditionsRepo;

    private List<Condition> mConditions;
    private File mFile;

    public static CustomConditionsRepo getInstance(){
        if (sCustomConditionsRepo == null)
            sCustomConditionsRepo = new CustomConditionsRepo(MyApplication.getApp());
        return sCustomConditionsRepo;
    }

    private CustomConditionsRepo(Application app) {
        mFile = new File(app.getFilesDir(), CONDITIONS_FILE_NAME);

        retrieveConditions();
    }

    public List<Condition> getConditions() {
        return mConditions;
    }

    public void setConditions(List<Condition> conditions) {
        mConditions = conditions;
        storeConditions();
    }

    private void storeConditions(){
        Gson gson = new GsonBuilder().create();
        Type userListType = new TypeToken<ArrayList<Condition>>(){}.getType();
        String filterString = gson.toJson(mConditions, userListType);
        IOUtils.writeString(mFile, filterString);
    }

    private void retrieveConditions(){
        Gson gson = new GsonBuilder().create();
        Type userListType = new TypeToken<ArrayList<Condition>>(){}.getType();
        mConditions = gson.fromJson(IOUtils.readString(mFile), userListType);//todo what if the file does not exists
    }

}
