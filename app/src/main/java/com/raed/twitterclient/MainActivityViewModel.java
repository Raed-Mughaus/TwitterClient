package com.raed.twitterclient;

import android.arch.lifecycle.ViewModel;

import com.raed.twitterclient.authusers.AuthUsersRepository;

public class MainActivityViewModel extends ViewModel {

    private AuthUsersRepository mAuthUsersRepository = AuthUsersRepository.getInstance();


    boolean shouldStartAuthActivity(){
        return !mAuthUsersRepository.hasAnyUserSignedIn();
    }

}
