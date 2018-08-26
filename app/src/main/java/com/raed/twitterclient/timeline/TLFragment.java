package com.raed.twitterclient.timeline;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.raed.twitterclient.R;
import com.raed.twitterclient.TwitterErrors.TwitterError;
import com.raed.twitterclient.model.tweet.Tweet;
import com.raed.twitterclient.utilis.Crashlytics;
import com.raed.twitterclient.utilis.Utils;


import retrofit2.HttpException;

import static com.raed.twitterclient.timeline.TLEvent.FAIL_TO_LOAD_TWEETS;
import static com.raed.twitterclient.timeline.TLEvent.NO_TWEETS_AVAILABLE_FOR_NOW;
import static com.raed.twitterclient.timeline.TLEvent.START_LOADING_TWEETS;
import static com.raed.twitterclient.timeline.TLEvent.TWEETS_LOADED_SUCCESSFULLY;


public class TLFragment extends Fragment{

    private static final String TAG = TLFragment.class.getSimpleName();

    private TLViewModel mViewModel;

    private SwipeRefreshLayout mRefreshLayout;
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
        mRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_timeline, container, false);
        mRefreshLayout.setEnabled(true);
        mRefreshLayout.setOnRefreshListener(this::onRefreshLayoutSwap);
        //todo I should show a retry button at the bottom if an error occur
        //todo What about the time
        //todo I need to know when an error happen
        //todo the data source needs to keep the callback in load after and when the data is available
        // try to load it
        //The view model act as a bridge between this fragment and the data src,

        //when an error occur show a retry button,
        // when the data is loaded show loading widget(maybe)
        //when the user click tell the data src to try to load data on the referenced callback

        mRecyclerView = mRefreshLayout.findViewById(R.id.recycler_view);
        setUpRecyclerView();

        mViewModel.getError().observe(this, TLFragment.this::handleError);
        observeTLEvent();

        return mRefreshLayout;
    }

    @Override
    public void onPause() {
        super.onPause();

        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int index = layoutManager.findFirstVisibleItemPosition();
        if (index == RecyclerView.NO_POSITION)
            return;
        long currentShownID = mTweetAdapter.getItem(index).getId();
        int offset = mRecyclerView.computeVerticalScrollOffset() % mRecyclerView.getHeight();
        mViewModel.saveScrolledToID(currentShownID, offset);

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
        if (event == TWEETS_LOADED_SUCCESSFULLY) {
            Crashlytics.logException(new IllegalStateException("User can swap RefreshLayout in illegal state"));
        }
    }

    private void setUpRecyclerView(){
        mTweetAdapter = new TweetAdapter();
        mTweetAdapter.registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mTweetAdapter.unregisterAdapterDataObserver(this);
                restoreScrollPosition();
            }
        });
        mViewModel.getPagedList().observe(this, mTweetAdapter::submitList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mTweetAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                int leftTweets = layoutManager.findFirstVisibleItemPosition();
                String subtitle = getResources().getQuantityString(R.plurals.tweet_or_tweets, leftTweets, leftTweets);
                actionBar.setSubtitle(subtitle);
            }
        });
    }

    private void observeTLEvent() {
        mViewModel.getInitialTweetsTLEvent().observe(this,
                tlEvent -> {
                    Log.d(TAG, "initialTweetEvent: " + tlEvent);
                    if(tlEvent != START_LOADING_TWEETS)
                        mRefreshLayout.setRefreshing(false);
                });
        mViewModel.getNewTweetsTLEvent().observe(this,
                tlEvent -> {
                    Log.d(TAG, "newTweetEvent: " + tlEvent);
                    if(tlEvent != START_LOADING_TWEETS)
                        mRefreshLayout.setRefreshing(false);
                });
        mViewModel.getOldTweetsTLEvent().observe(this,
                tlEvent -> {
                    Log.d(TAG, "oldTweetEvent: " + tlEvent);
                    if(tlEvent != START_LOADING_TWEETS)
                        mRefreshLayout.setRefreshing(false);
                    if(tlEvent == FAIL_TO_LOAD_TWEETS){
                        //todo show a button at the end of the list
                        Toast.makeText(getContext(), "There is a button but you cannot see it :)", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleError(Exception e) {
        if (!Utils.isOnline(getContext())){
            //todo add a new string to the resource file and use it
            Toast.makeText(getContext(), "You are offline", Toast.LENGTH_LONG).show();
            return;
        }
        Log.e(TAG, "handleError: " + e.getMessage(), e);
        Log.d(TAG, "handleError: type of e is "  + e.getClass().getSimpleName());
        e.printStackTrace();
        if (!(e instanceof HttpException)){
            Toast.makeText(getContext(), "Error type " + e.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
            return;
        }
        TwitterError[] errors = Utils.extractTwitterErrors((HttpException) e);
        for (TwitterError error : errors){
            switch (error.code){
                case 32: //Could not authenticate you
                    //todo check the time
                    //todo add a new string to the resource file and use it
                    Toast.makeText(getContext(), "Could not authenticate you", Toast.LENGTH_LONG).show();
                    break;
                case 88:
                    Toast.makeText(getContext(), R.string.rate_limit_reached, Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(getContext(), "code = " + error.code, Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(), "message = " + error.message, Toast.LENGTH_LONG).show();
                    break;

            }
        }
    }

    private void restoreScrollPosition(){
        Long id = mViewModel.getInitialLoadKey();
        if (id == null)
            return;
        int index = mTweetAdapter.getCurrentList().indexOf(new Tweet(id));
        mRecyclerView.scrollToPosition(index);
        //mRecyclerView.scrollBy(0, mViewModel.getOffset());
    }

}
