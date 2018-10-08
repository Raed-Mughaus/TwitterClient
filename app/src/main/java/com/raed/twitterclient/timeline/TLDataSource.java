package com.raed.twitterclient.timeline;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.raed.twitterclient.model.tweet.Tweet;
import com.raed.twitterclient.timeline.home_timeline.data.HomeTLDataSource;

import java.util.ArrayList;

public abstract class TLDataSource extends PageKeyedDataSource<Long, Tweet> {

    private static final String TAG = HomeTLDataSource.class.getSimpleName();

    private MutableLiveData<TLEvent> mInitialTweetsTLEvent;
    private MutableLiveData<TLEvent> mNewTweetsTLEvent;
    private MutableLiveData<TLEvent> mOldTweetsTLEvent;
    private MutableLiveData<Exception> mError;

    private LoadInitialCallback<Long, Tweet> mInitialCallback;
    private LoadInitialParams<Long> mInitialParams;

    private LoadCallback<Long, Tweet> mBeforeCallback;
    private LoadParams<Long> mBeforeParams;

    private LoadCallback<Long, Tweet> mAfterCallback;
    private LoadParams<Long> mAfterParams;

    protected TLDataSource(MutableLiveData<TLEvent> initialTweetsTLEvent,
                             MutableLiveData<TLEvent> newTweetsTLEvent,
                             MutableLiveData<TLEvent> oldTweetsTLEvent,
                             MutableLiveData<Exception> error) {
        mInitialTweetsTLEvent = initialTweetsTLEvent;
        mNewTweetsTLEvent = newTweetsTLEvent;
        mOldTweetsTLEvent = oldTweetsTLEvent;
        mError = error;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params,
                            @NonNull LoadInitialCallback<Long, Tweet> callback) {
        Log.d(TAG, "loadInitial: ");
        TweetList tweetList;
        try {
            mInitialTweetsTLEvent.postValue(TLEvent.START_LOADING_TWEETS);
            tweetList = loadInitialTweetList();
        }
        catch (Exception e){
            mInitialTweetsTLEvent.postValue(TLEvent.FAIL_TO_LOAD_TWEETS);
            mError.postValue(e);
            mInitialCallback = callback;
            mInitialParams = params;
            return;
        }
        if (tweetList == null){
            mInitialTweetsTLEvent.postValue(TLEvent.NO_TWEETS_AVAILABLE_FOR_NOW);
            mInitialCallback = callback;
            mInitialParams = params;
        } else {
            mInitialTweetsTLEvent.postValue(TLEvent.TWEETS_LOADED_SUCCESSFULLY);
            callback.onResult(tweetList.tweets, tweetList.newerTweetsKey, tweetList.olderTweetsKey);
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params,
                           @NonNull LoadCallback<Long, Tweet> callback) {
        Log.d(TAG, "loadBefore: ");
        TweetList tweetList;
        try {
            mNewTweetsTLEvent.postValue(TLEvent.START_LOADING_TWEETS);
            tweetList = loadNewerTweetList(params);
        }
        catch (Exception e){
            mBeforeParams = params;
            mBeforeCallback = callback;
            mNewTweetsTLEvent.postValue(TLEvent.FAIL_TO_LOAD_TWEETS);
            mError.postValue(e);
            return;
        }
        if(tweetList == null) {
            mBeforeParams = params;
            mBeforeCallback = callback;
            mNewTweetsTLEvent.postValue(TLEvent.NO_TWEETS_AVAILABLE_FOR_NOW);
        } else {
            callback.onResult(tweetList.tweets, tweetList.newerTweetsKey);
            mNewTweetsTLEvent.postValue(TLEvent.TWEETS_LOADED_SUCCESSFULLY);
        }
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params,
                          @NonNull LoadCallback<Long, Tweet> callback) {
        Log.d(TAG, "loadAfter: " + params.key);
        TweetList tweetList;
        try {
            mOldTweetsTLEvent.postValue(TLEvent.START_LOADING_TWEETS);
            tweetList = loadOlderTweetList(params);
        }
        catch (Exception e){
            mAfterParams = params;
            mAfterCallback = callback;
            mOldTweetsTLEvent.postValue(TLEvent.FAIL_TO_LOAD_TWEETS);
            mError.postValue(e);
            return;
        }
        if(tweetList == null) {
            callback.onResult(new ArrayList<>(),null);
            mOldTweetsTLEvent.postValue(TLEvent.NO_MORE_TWEETS_AVAILABLE);
        } else {
            callback.onResult(tweetList.tweets, tweetList.olderTweetsKey);
            mOldTweetsTLEvent.postValue(TLEvent.TWEETS_LOADED_SUCCESSFULLY);
        }
    }

    @WorkerThread
    public void retryToLoadInitialTweets(){
        Log.d(TAG, "retryToLoadInitialTweets:");
        if (mInitialCallback == null){
            throw new IllegalStateException("Trying to load data in inappropriate state");
        }
        LoadInitialCallback<Long, Tweet> loadCallback = mInitialCallback;
        LoadInitialParams<Long> loadParams = mInitialParams;
        mInitialCallback = null;
        mInitialParams = null;
        loadInitial(loadParams, loadCallback);
    }

    @WorkerThread
    public void retryToLoadNewTweets(){
        Log.d(TAG, "retryToLoadNewTweets: ");
        if (mBeforeCallback == null){
            throw new IllegalStateException("Trying to load in inappropriate state");
        }
        LoadCallback<Long, Tweet> loadCallback = mBeforeCallback;
        LoadParams<Long> loadParams = mBeforeParams;
        mBeforeCallback = null;
        mBeforeParams = null;
        loadBefore(loadParams, loadCallback);
    }

    @WorkerThread
    public void retryToLoadOldTweets(){
        Log.d(TAG, "retryToLoadOldTweets: ");
        if (mAfterCallback == null){
            throw new IllegalStateException("Trying to load in inappropriate state");
        }
        LoadCallback<Long, Tweet> loadCallback = mAfterCallback;
        LoadParams<Long> loadParams = mAfterParams;
        mAfterCallback = null;
        mAfterParams = null;
        loadAfter(loadParams, loadCallback);
    }

    protected abstract TweetList loadInitialTweetList();
    protected abstract TweetList loadNewerTweetList(LoadParams<Long> params);
    protected abstract TweetList loadOlderTweetList(LoadParams<Long> params);

    public abstract static class Factory extends DataSource.Factory<Long, Tweet>{

        public MutableLiveData<TLEvent> mInitialTweetsTLEvent;
        public MutableLiveData<TLEvent> mNewTweetsTLEvent;
        public MutableLiveData<TLEvent> mOlderTweetsTLEvent;
        public MutableLiveData<Exception> mError;

    }
}
