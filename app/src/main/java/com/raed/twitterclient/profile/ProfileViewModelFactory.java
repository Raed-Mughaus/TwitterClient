package com.raed.twitterclient.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.raed.twitterclient.di.AppComponent;
import com.raed.twitterclient.retrofitservices.UserService;
import com.raed.twitterclient.userdata.CurrentUser;
import com.raed.twitterclient.utilis.Crashlytics;

import javax.inject.Inject;

public class ProfileViewModelFactory implements ViewModelProvider.Factory {

    @Inject UserService mUserService;
    @Inject CurrentUser mCurrentUser;

    public ProfileViewModelFactory(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (ProfileViewModel.class.equals(modelClass)) {
            try {
                return modelClass
                        .getConstructor(UserService.class, LiveData.class)
                        .newInstance(mUserService, mCurrentUser.getCurrentUser());
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
        throw new IllegalArgumentException("Unknown model class");
    }
}
