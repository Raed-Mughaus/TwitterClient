package com.raed.twitterclient;

import android.app.Application;

import com.raed.twitterclient.authusers.AuthUser;
import com.raed.twitterclient.timeline.TLViewModel;
import com.raed.twitterclient.timeline.data.TweetsRepository;

public class HomeTLViewModel extends TLViewModel{

    public HomeTLViewModel(Application application) {
        super(application);
    }

    @Override
    protected TweetsRepository getRepository(AuthUser user) {
        return new TweetsRepository(user);
    }
}
