package com.raed.twitterclient.timeline.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.raed.twitterclient.authusers.AuthUser;
import com.raed.twitterclient.model.tweet.Tweet;
import com.raed.twitterclient.timeline.TLEvent;
import com.raed.twitterclient.timeline.data.TweetsRepository.TweetList;
import com.raed.twitterclient.utilis.Crashlytics;

import java.io.IOException;
import java.util.ArrayList;

/*
    params.requestedLoadSize is ignored
*/
public class TLDataSource extends PageKeyedDataSource<Long, Tweet>{

    private static final String TAG = TLDataSource.class.getSimpleName();

    private TweetsRepository mRepository;
    private Long mInitialKey;//the tweet with this id needs to be included in the list of the 1st load

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

    private TLDataSource(TweetsRepository tweetsRepository,
                         Long initialKey,
                         MutableLiveData<TLEvent> initialTweetsTLEvent,
                         MutableLiveData<TLEvent> newTweetsTLEvent,
                         MutableLiveData<TLEvent> oldTweetsTLEvent,
                         MutableLiveData<Exception> error) {
        mRepository = tweetsRepository;
        mInitialKey = initialKey;
        mInitialTweetsTLEvent = initialTweetsTLEvent;
        mNewTweetsTLEvent = newTweetsTLEvent;
        mOldTweetsTLEvent = oldTweetsTLEvent;
        mError = error;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params,
                            @NonNull LoadInitialCallback<Long, Tweet> callback) {
        Log.d(TAG, "loadInitial: ");
        TweetList tweetList = null;
        try {
            mInitialTweetsTLEvent.postValue(TLEvent.START_LOADING_TWEETS);
            if (mInitialKey != null)
                tweetList = mRepository.getTweetsThatInclude(mInitialKey);
            if (tweetList == null)
                tweetList = mRepository.getTweets();
        }
        catch (IOException e) { Crashlytics.logException(e);}
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
        TweetList tweetList = null;
        try {
            mNewTweetsTLEvent.postValue(TLEvent.START_LOADING_TWEETS);
            tweetList = mRepository.getTweetsNewerThan(params.key);
        }
        catch (IOException e) { Crashlytics.logException(e);}
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
        TweetList tweetList = null;
        try {
            mOldTweetsTLEvent.postValue(TLEvent.START_LOADING_TWEETS);
            tweetList = mRepository.getTweetsOlderThan(params.key);
        } catch (IOException e) { Crashlytics.logException(e);}
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

    public static class Factory extends DataSource.Factory<Long, Tweet>{

        private TweetsRepository mRepository;
        private Long mInitialKey;

        private MutableLiveData<TLEvent> mInitialTweetsTLEvent;
        private MutableLiveData<TLEvent> mNewTweetsTLEvent;
        private MutableLiveData<TLEvent> mOlderTweetsTLEvent;
        private MutableLiveData<Exception> mError;

        public Factory(TweetsRepository repository,
                       Long initialKey,
                       MutableLiveData<TLEvent> initialTweetsTLEvent,
                       MutableLiveData<TLEvent> newTweetsTLEvent,
                       MutableLiveData<TLEvent> olderTweetsTLEvent,
                       MutableLiveData<Exception> error) {
            mRepository = repository;
            mInitialKey = initialKey;
            mInitialTweetsTLEvent = initialTweetsTLEvent;
            mNewTweetsTLEvent = newTweetsTLEvent;
            mOlderTweetsTLEvent = olderTweetsTLEvent;
            mError = error;

        }

        @Override
        public DataSource<Long, Tweet> create() {
            return new TLDataSource(mRepository, mInitialKey, mInitialTweetsTLEvent,
                    mNewTweetsTLEvent, mOlderTweetsTLEvent, mError);
        }
    }
}
