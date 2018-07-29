package com.raed.twitterclient.profile;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.raed.twitterclient.retrofitservices.RetrofitServices;
import com.raed.twitterclient.retrofitservices.UserService;
import com.raed.twitterclient.userdata.AuthorizedUser;
import com.raed.twitterclient.userdata.CurrentUser;

import io.reactivex.Observable;

public class ProfileViewModel extends ViewModel {


    private LiveData<AuthorizedUser> mUser;
    private UserService mUserService;

    public ProfileViewModel() {
        mUser = CurrentUser.getInstance().getCurrentUser();
        mUserService = RetrofitServices.getInstance().getUserService();
    }

    public Observable<UserProfile> getUser() {
        return mUserService.show(mUser.getValue().getUserId());
    }

}
