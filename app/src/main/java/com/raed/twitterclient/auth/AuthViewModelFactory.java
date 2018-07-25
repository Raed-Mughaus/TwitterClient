package com.raed.twitterclient.auth;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.raed.twitterclient.AppTokenAndSecret;
import com.raed.twitterclient.di.AppComponent;
import com.raed.twitterclient.userdata.CurrentUser;
import com.raed.twitterclient.utilis.Crashlytics;
import com.raed.twitterclient.userdata.Users;
import com.raed.twitterclient.retrofitservices.AuthService;


import javax.inject.Inject;



public class AuthViewModelFactory implements ViewModelProvider.Factory{

    @Inject AuthService mAuthService;
    @Inject Users mUsers;
    @Inject CurrentUser mCurrentUser;

    AuthViewModelFactory(AppComponent component){
        component.inject(this);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        AppTokenAndSecret tokenAndSecret = new AppTokenAndSecret();
        if (AuthViewModel.class.equals(modelClass)) {
            try {
                return modelClass
                        .getConstructor(AuthService.class, Users.class, CurrentUser.class, String.class, String.class)//todo fix
                        .newInstance(mAuthService, mUsers, mCurrentUser, tokenAndSecret.getAppKey(), tokenAndSecret.getAppSecret());
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
        throw new IllegalArgumentException("Unknown model class");
    }

}
