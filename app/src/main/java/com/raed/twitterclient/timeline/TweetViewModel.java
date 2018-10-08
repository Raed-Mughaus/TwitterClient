package com.raed.twitterclient.timeline;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.util.Log;

import com.raed.twitterclient.auth.authorized_user.AuthUser;
import com.raed.twitterclient.auth.authorized_user.CurrentAuthUser;
import com.raed.twitterclient.model.tweet.Tweet;
import com.raed.twitterclient.timeline.home_timeline.data.HomeTLDataSource;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public abstract class TweetViewModel extends ViewModel {

    private static final String TAG = TweetViewModel.class.getSimpleName();

    private LiveData<PagedList<Tweet>> mTweetsPagedList;

    private MutableLiveData<TLEvent> mInitialTweetsTLEvent = new MutableLiveData<>();
    private MutableLiveData<TLEvent> mNewTweetsTLEvent = new MutableLiveData<>();
    private MutableLiveData<TLEvent> mOldTweetsTLEvent = new MutableLiveData<>();
    private MutableLiveData<Exception> mError = new MutableLiveData<>();


    public TweetViewModel(TLDataSource.Factory dataSrcFactory) {
        if (dataSrcFactory == null)
            return;
        dataSrcFactory.mInitialTweetsTLEvent = mInitialTweetsTLEvent;
        dataSrcFactory.mNewTweetsTLEvent = mNewTweetsTLEvent;
        dataSrcFactory.mOlderTweetsTLEvent = mOldTweetsTLEvent;
        dataSrcFactory.mError = mError;
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(100)//I think this is safer//todo is it ok
                .build();
        mTweetsPagedList = new LivePagedListBuilder<>(dataSrcFactory, config)
                .build();
    }

    public MutableLiveData<Exception> getError() {
        return mError;
    }

    public MutableLiveData<TLEvent> getInitialTweetsTLEvent() {
        return mInitialTweetsTLEvent;
    }

    public MutableLiveData<TLEvent> getNewTweetsTLEvent() {
        return mNewTweetsTLEvent;
    }

    public MutableLiveData<TLEvent> getOldTweetsTLEvent() {
        return mOldTweetsTLEvent;
    }

    public void reloadNewTweets() {
        Log.d(TAG, "reloadNewTweets: ");
        if (mTweetsPagedList == null)
            return;
        Observable
                .create(emitter -> {
                    getCurrentDataSource().retryToLoadNewTweets();
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void reloadOldTweets(){
        Log.d(TAG, "reloadOldTweets: ");
        if (mTweetsPagedList == null)
            return;
        Observable
                .create(emitter -> {
                    getCurrentDataSource().retryToLoadOldTweets();
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void reloadInitialTweets(){
        Log.d(TAG, "reloadInitialTweets: ");
        if (mTweetsPagedList == null)
            return;
        Observable
                .create(emitter -> {
                    getCurrentDataSource().retryToLoadInitialTweets();
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public LiveData<PagedList<Tweet>> getPagedList() {
        return mTweetsPagedList;
    }

    private HomeTLDataSource getCurrentDataSource(){
        return (HomeTLDataSource) mTweetsPagedList.getValue().getDataSource();
    }
}
