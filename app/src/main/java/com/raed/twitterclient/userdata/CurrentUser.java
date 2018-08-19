package com.raed.twitterclient.userdata;

import com.raed.twitterclient.MyApplication;
import com.raed.twitterclient.io.StringFile;

import java.io.File;


public class CurrentUser {

    private static final String FILE_NAME = "current_user";
    private static CurrentUser sCurrentUser = new CurrentUser();

    private AuthorizedUser mAuthorizedUser;
    private StringFile mCurrentUserFile;

    public static CurrentUser getInstance(){
        return sCurrentUser;
    }

    private CurrentUser() {
        File file = new File(MyApplication.getApp().getFilesDir(), FILE_NAME);
        mCurrentUserFile = new StringFile(file);

        //load the current user
        String userId = mCurrentUserFile.read();
        if (userId != null)
            mAuthorizedUser = Users.getInstance().getUserById(userId);
    }

    public void setCurrentUser(AuthorizedUser authorizedUser){
        mAuthorizedUser = authorizedUser;
        mCurrentUserFile.write(mAuthorizedUser.getUserId());
    }

    public AuthorizedUser getCurrentUser() {
        return mAuthorizedUser;
    }

    public boolean hasAnyUserSignedIn(){
        return mAuthorizedUser != null;
    }

}
