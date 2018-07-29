package com.raed.twitterclient.userdata;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.raed.twitterclient.MyApplication;
import com.raed.twitterclient.utilis.StringFile;

import java.io.File;


public class CurrentUser {

    private static final String FILE_NAME = "current_user";
    private static CurrentUser sCurrentUser;

    private MutableLiveData<AuthorizedUser> mAuthorizedUser = new MutableLiveData<>();
    private StringFile mCurrentUserFile;

    public static CurrentUser getInstance(){
        return sCurrentUser;
    }

    public static void initializeInstance(Context context) {
        sCurrentUser = new CurrentUser(context);
    }

    private CurrentUser(Context context) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        mCurrentUserFile = new StringFile(file);

        //load the current user
        String userId = mCurrentUserFile.read();
        if (userId != null)
            mAuthorizedUser.setValue(Users.getInstance().getUserById(userId));
    }

    public void setCurrentUser(AuthorizedUser authorizedUser){
        mAuthorizedUser.setValue(authorizedUser);
        mCurrentUserFile.write(mAuthorizedUser.getValue().getUserId());
    }

    public LiveData<AuthorizedUser> getCurrentUser() {
        return mAuthorizedUser;
    }

    public boolean hasAnyUserSignedIn(){
        return mAuthorizedUser.getValue() != null;
    }

}
