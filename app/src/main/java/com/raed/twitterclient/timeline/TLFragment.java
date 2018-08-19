package com.raed.twitterclient.timeline;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.raed.twitterclient.R;
import com.raed.twitterclient.model.tweet.Tweet;


public class TLFragment extends Fragment{

    private static final String TAG = TLFragment.class.getSimpleName();

    private TLViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private TweetAdapter mTweetAdapter;

    public static Fragment newInstance() {
        return new TLFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TLViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mTweetAdapter = new TweetAdapter();
        mViewModel.getPagedList().observe(this, mTweetAdapter::submitList);

        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.view_recyler_view, container, false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mTweetAdapter);

        return mRecyclerView;
    }

    @Override
    public void onPause() {
        super.onPause();

        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int index = layoutManager.findFirstVisibleItemPosition();
        if (index == RecyclerView.NO_POSITION)
            return;
        long currentShownID = mTweetAdapter.getItem(index).getId();
        mViewModel.saveScrolledToID(currentShownID);
    }

}
