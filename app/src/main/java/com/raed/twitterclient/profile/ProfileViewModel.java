package com.raed.twitterclient.profile;


import android.arch.lifecycle.ViewModel;

import com.raed.twitterclient.auth.authorized_user.AuthUser;
import com.raed.twitterclient.auth.authorized_user.CurrentAuthUser;
import com.raed.twitterclient.model.User;
import com.raed.twitterclient.api.RetrofitServices;
import com.raed.twitterclient.api.UserService;

import io.reactivex.Single;

public class ProfileViewModel extends ViewModel {


    private AuthUser mUser;
    private UserService mUserService;

    public ProfileViewModel() {
        mUser = CurrentAuthUser.getInstance().get();
        mUserService = RetrofitServices.getInstance().getUserService();
    }

    public Single<User> getUser(String userID) {
        if (userID == null)
            return mUserService.show(mUser.getUserId());
        return mUserService.show(userID);
    }

}
