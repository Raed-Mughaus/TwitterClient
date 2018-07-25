package com.raed.twitterclient.profile;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;

import com.raed.twitterclient.retrofitservices.UserService;
import com.raed.twitterclient.userdata.AuthorizedUser;

import io.reactivex.Observable;
import io.reactivex.Single;

public class ProfileViewModel extends ViewModel {


    private LiveData<AuthorizedUser> mUser;
    private UserService mUserService;

    public ProfileViewModel(UserService userService, LiveData<AuthorizedUser> user) {
        mUser = user;
        mUserService = userService;
    }

    public Observable<User> getUser() {
        //return mUserService.show(mUser.getValue().getUserId());
        return mUserService.show("39205746");
    }

}
