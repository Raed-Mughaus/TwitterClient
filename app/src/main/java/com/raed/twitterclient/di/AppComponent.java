package com.raed.twitterclient.di;

import com.raed.twitterclient.MainActivity;
import com.raed.twitterclient.auth.AuthViewModelFactory;
import com.raed.twitterclient.profile.ProfileViewModelFactory;
import com.raed.twitterclient.retrofitservices.RetrofitModule;
import com.raed.twitterclient.userdata.CurrentUser;
import com.raed.twitterclient.userdata.UserModule;
import com.raed.twitterclient.userdata.Users;

import dagger.Component;

@Component(modules = {UserModule.class, ContextModule.class, RetrofitModule.class})
public interface AppComponent {
    Users getUsers();
    CurrentUser getCurrentUser();

    void inject(MainActivity mainActivity);

    void inject(AuthViewModelFactory authViewModelFactory);

    void inject(ProfileViewModelFactory profileViewModelFactory);
}
