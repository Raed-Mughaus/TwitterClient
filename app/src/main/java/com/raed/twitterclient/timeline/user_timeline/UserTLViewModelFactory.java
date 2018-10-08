package com.raed.twitterclient.timeline.user_timeline;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

class UserTLViewModelFactory implements ViewModelProvider.Factory {

    private String mUserID;

    UserTLViewModelFactory(String userID) {
        mUserID = userID;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (UserTweetViewModel.class.equals(modelClass)) {
            return (T) new UserTweetViewModel(mUserID);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
