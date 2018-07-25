package com.raed.twitterclient.userdata;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.raed.twitterclient.utilis.StringFile;

import javax.inject.Inject;
import javax.inject.Named;


public class CurrentUser {

    static final String FILE_NAME = "current_user";

    private MutableLiveData<AuthorizedUser> mAuthorizedUser = new MutableLiveData<>();
    private StringFile mCurrentUserFile;

    @Inject
    public CurrentUser(@Named(FILE_NAME) StringFile stringFile, Users users) {
        mCurrentUserFile = stringFile;

        //load the current user
        String userId = mCurrentUserFile.read();
        if (userId != null)
            mAuthorizedUser.setValue(users.getUserById(userId));
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
