package com.raed.twitterclient;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import com.raed.twitterclient.timeline.TLFragment;
import com.raed.twitterclient.timeline.TLViewModel;

public class HomeTLFragment extends TLFragment {

    public static Fragment newInstance() {
        return new HomeTLFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_timeline;
    }

    @Override
    protected TLViewModel getViewModel(){
        return ViewModelProviders.of(this).get(HomeTLViewModel.class);
    }
}
