package com.raed.twitterclient;

import android.arch.lifecycle.ViewModel;

import com.raed.twitterclient.data.Tweet;
import com.raed.twitterclient.retrofitservices.RetrofitServices;
import com.raed.twitterclient.retrofitservices.TLService;

import java.util.List;

import io.reactivex.Single;

public abstract class TLViewModel extends ViewModel {

    private TLService mTLService;

    public TLViewModel() {
        mTLService = RetrofitServices.getInstance().getTLService();
    }

    public Single<List<Tweet>> getTweets() {
        return mTLService.homeTimeline(100);
    }
}
