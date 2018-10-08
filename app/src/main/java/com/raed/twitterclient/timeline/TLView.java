package com.raed.twitterclient.timeline;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.raed.twitterclient.R;
import com.raed.twitterclient.api.TwitterErrors;

import com.raed.twitterclient.utilis.Utils;

import retrofit2.HttpException;

import static android.support.v7.widget.RecyclerView.NO_POSITION;
import static com.raed.twitterclient.timeline.TLEvent.FAIL_TO_LOAD_TWEETS;
import static com.raed.twitterclient.timeline.TLEvent.NO_TWEETS_AVAILABLE_FOR_NOW;
import static com.raed.twitterclient.timeline.TLEvent.START_LOADING_TWEETS;

public class TLView implements LifecycleObserver{
    
    private static final String TAG = TLView.class.getSimpleName();

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private TweetAdapter mTweetAdapter;
    
    private TweetViewModel mViewModel;
    
    private Context mContext;

    private int mCurrentPlayingVideoPosition = NO_POSITION;

    //todo I should show a retry button at the bottom if an error occur
    //todo What about the time
    //todo I need to know when an error happen
    //todo the data source needs to keep the callback in load after and when the data is available
    // try to load it
    //The view model act as a bridge between this fragment and the data src,

    //when an error occur show a retry button,
    // when the data is loaded show loading widget(maybe)
    //when the user click tell the data src to try to load data on the referenced callback



    public TLView(LifecycleOwner lifecycleOwner, TweetViewModel viewModel, SwipeRefreshLayout refreshLayout, RecyclerView recyclerView) {
        mRefreshLayout = refreshLayout;
        mRecyclerView = recyclerView;
        
        mContext = refreshLayout.getContext();
        
        mViewModel = viewModel;

        mRefreshLayout.setEnabled(true);
        mRefreshLayout.setOnRefreshListener(this::onRefreshLayoutSwap);

        setUpRecyclerView(lifecycleOwner);

        mViewModel.getError().observe(lifecycleOwner, this::handleError);
        observeTLEvent(lifecycleOwner);

        lifecycleOwner.getLifecycle().addObserver(this);

    }

    protected TweetAdapter getTweetAdapter() {
        return mTweetAdapter;
    }

    private void setUpRecyclerView(LifecycleOwner lifecycleOwner){
        mTweetAdapter = new TweetAdapter();
        mTweetAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mTweetAdapter.unregisterAdapterDataObserver(this);
                restoreScrollPosition();
            }
        });
        mViewModel.getPagedList().observe(lifecycleOwner, mTweetAdapter::submitList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mTweetAdapter);

    }

    private void onRefreshLayoutSwap() {
        TLEvent event = mViewModel.getInitialTweetsTLEvent().getValue();
        if (event == FAIL_TO_LOAD_TWEETS || event == NO_TWEETS_AVAILABLE_FOR_NOW){
            mViewModel.reloadInitialTweets();
            return;
        }
        event = mViewModel.getNewTweetsTLEvent().getValue();
        if (event == null)
            return;
        if (event == FAIL_TO_LOAD_TWEETS || event == NO_TWEETS_AVAILABLE_FOR_NOW)
            mViewModel.reloadNewTweets();
    }
    
    private void observeTLEvent(LifecycleOwner lifecycleOwner) {
        mViewModel.getInitialTweetsTLEvent().observe(lifecycleOwner,
                tlEvent -> {
                    Log.d(TAG, "initialTweetEvent: " + tlEvent);
                    if(tlEvent != START_LOADING_TWEETS)
                        mRefreshLayout.setRefreshing(false);
                });
        mViewModel.getNewTweetsTLEvent().observe(lifecycleOwner,
                tlEvent -> {
                    Log.d(TAG, "newTweetEvent: " + tlEvent);
                    if(tlEvent != START_LOADING_TWEETS)
                        mRefreshLayout.setRefreshing(false);
                });
        mViewModel.getOldTweetsTLEvent().observe(lifecycleOwner,
                tlEvent -> {
                    Log.d(TAG, "oldTweetEvent: " + tlEvent);
                    if(tlEvent != START_LOADING_TWEETS)
                        mRefreshLayout.setRefreshing(false);
                    if(tlEvent == FAIL_TO_LOAD_TWEETS){
                        //todo show a button at the end of the list
                    }
                });
    }

    private void handleError(Exception e) {
        if (!Utils.isOnline(mContext)){
            //todo add a new string to the resource file and use it
            Toast.makeText(mContext, "You are offline", Toast.LENGTH_LONG).show();
            return;
        }
        Log.e(TAG, "handleError: " + e.getMessage(), e);
        Log.d(TAG, "handleError: type of e is "  + e.getClass().getSimpleName());
        e.printStackTrace();
        if (!(e instanceof HttpException)){
            Toast.makeText(mContext, "Error type " + e.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
            Throwable throwable = e.getCause();
            if(throwable != null)
                Toast.makeText(mContext, "Error type " + throwable.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
            return;
        }
        TwitterErrors.TwitterError[] errors = Utils.extractTwitterErrors((HttpException) e);
        for (TwitterErrors.TwitterError error : errors){
            switch (error.code){
                case 32: //Could not authenticate you
                    //todo check the time
                    //todo add a new string to the resource file and use it
                    Toast.makeText(mContext, "Could not authenticate you", Toast.LENGTH_LONG).show();
                    break;
                case 88:
                    Toast.makeText(mContext, R.string.rate_limit_reached, Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(mContext, "code = " + error.code, Toast.LENGTH_LONG).show();
                    Toast.makeText(mContext, "message = " + error.message, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    protected void restoreScrollPosition() { }

    public Long getCurrentShownID() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int index = layoutManager.findFirstVisibleItemPosition();
        if (index == NO_POSITION)
            return null;
        return mTweetAdapter.getItem(index).getId();

    }
}
