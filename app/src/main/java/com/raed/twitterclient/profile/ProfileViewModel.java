package com.raed.twitterclient.profile;


import android.arch.lifecycle.ViewModel;

import com.raed.twitterclient.authusers.AuthUser;
import com.raed.twitterclient.authusers.AuthUsersRepository;
import com.raed.twitterclient.model.User;
import com.raed.twitterclient.retrofitservices.RetrofitServices;
import com.raed.twitterclient.retrofitservices.UserService;

import io.reactivex.Single;

public class ProfileViewModel extends ViewModel {


    private AuthUser mUser;
    private UserService mUserService;

    public ProfileViewModel() {
        mUser = AuthUsersRepository.getInstance().getCurrentUser();
        mUserService = RetrofitServices.getInstance().getUserService();
    }

    public Single<User> getUser(String userID) {
        if (userID == null)
            return mUserService.show(mUser.getUserId());
        return mUserService.show(userID);
    }

}
