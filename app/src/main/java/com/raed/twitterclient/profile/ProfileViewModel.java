package com.raed.twitterclient.profile;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.raed.twitterclient.model.User;
import com.raed.twitterclient.retrofitservices.RetrofitServices;
import com.raed.twitterclient.retrofitservices.UserService;
import com.raed.twitterclient.userdata.AuthorizedUser;
import com.raed.twitterclient.userdata.CurrentUser;

import io.reactivex.Single;

public class ProfileViewModel extends ViewModel {


    private AuthorizedUser mUser;
    private UserService mUserService;

    public ProfileViewModel() {
        mUser = CurrentUser.getInstance().getCurrentUser();
        mUserService = RetrofitServices.getInstance().getUserService();
    }

    public Single<User> getUser(String userID) {
        if (userID == null)
            return mUserService.show(mUser.getUserId());
        return mUserService.show(userID);
    }

}
