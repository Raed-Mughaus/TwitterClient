package com.raed.twitterclient.timeline;

import com.raed.twitterclient.TLViewModel;
import com.raed.twitterclient.retrofitservices.RetrofitServices;
import com.raed.twitterclient.retrofitservices.TLService;

public class HomeTLViewModel extends TLViewModel {

    private TLService mTLService;

    public HomeTLViewModel() {
        mTLService = RetrofitServices.getInstance().getTLService();
    }
}
