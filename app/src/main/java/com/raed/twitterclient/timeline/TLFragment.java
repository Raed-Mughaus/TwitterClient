package com.raed.twitterclient.timeline;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raed.twitterclient.R;
import com.raed.twitterclient.model.tweet.Tweet;
import com.raed.twitterclient.utilis.Utils;


import io.reactivex.Observable;
import io.reactivex.subjects.Subject;
import retrofit2.HttpException;


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
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.view_recyler_view, container, false);

        //todo Do I need to show the refresh when
        //todo I should not load the data when it is already loading
        //todo I need to show the UI updating when the data is loaded from the top
        //todo show it whenever you are loading data from the top
        //todo I need to load data
        //todo so the data source should notify me when it is loading data
        //todo I should show a retry button at the bottom if an error occur
        //todo What about the time
        //todo I need to know when an error happen
        //todo the data source needs to keep the callback in load after and when the data is available
        // try to load it
        //The view model act as a bridge between this fragment and the data src,

        //when an error occur show a retry button,
        // when the data is loaded show loading widget(maybe)
        //when the user click tell the data src to try to load data on the referenced callback
        mTweetAdapter = new TweetAdapter();
        mTweetAdapter.registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mTweetAdapter.unregisterAdapterDataObserver(this);
                restoreScrollPosition();
            }
        });
        mViewModel.getPagedList().observe(this, mTweetAdapter::submitList);

        mRecyclerView = refreshLayout.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mTweetAdapter);

        mViewModel.getErrorLiveData().observe(this, TLFragment.this::handleError);
        return mRecyclerView;
    }

    private void handleError(Exception e) {
        if (!Utils.isOnline(getContext())){

            return;
        }
        Log.e(TAG, "handleError: " + e.getMessage(), e);
        Log.d(TAG, "handleError: type of e is "  + e.getClass().getSimpleName());
        e.printStackTrace();
        if (e instanceof HttpException){
            int code = Utils.getTwitterErrorCode((HttpException) e);
            switch (code){
                case 32: //Could not authenticate you
                    //todo check the time
                    break;
            }
        }else {

        }
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

    private void restoreScrollPosition(){
        Long id = mViewModel.getInitialLoadKey();
        if (id == null)
            return;
        int index = mTweetAdapter.getCurrentList().indexOf(new Tweet(id));
        mRecyclerView.scrollToPosition(index);
        //mRecyclerView.scrollBy(0, mViewModel.getOffset());
    }

}
