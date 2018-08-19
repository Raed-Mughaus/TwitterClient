package com.raed.twitterclient.timeline.data;

import android.arch.paging.DataSource;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.raed.twitterclient.model.tweet.Tweet;

import java.io.IOException;
import java.util.List;


/*
    Ignore the params.requestedLoadSize
 */
public class TLDataSource extends PageKeyedDataSource<Long, Tweet>{

    private static final String TAG = TLDataSource.class.getSimpleName();

    private TweetsRepository mRepository;
    private Long mInitialKey;

    public TLDataSource(Long initialKey) {
        mInitialKey = initialKey;
        mRepository = new TweetsRepository();
    }

    //todo no complex filters

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Long, Tweet> callback) {
        Log.d(TAG, "loadInitial:");
        TweetsRepository.TweetsSubset tweetsSubset = null;
        try {
            if (mInitialKey == null)
                tweetsSubset = mRepository.getTweets();
            else
                tweetsSubset = mRepository.getTweetsWithMaxID(mInitialKey);//this should be in the cache?
        } catch (IOException ignore) {}
        callback.onResult(tweetsSubset.tweets, tweetsSubset.newerTweetsKey, tweetsSubset.olderTweetsKey);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Tweet> callback) {
        Log.d(TAG, "loadBefore: ");
        TweetsRepository.TweetsSubset tweetsSubset = null;
        try { tweetsSubset = mRepository.getTweetsNewerThan(params.key); } catch (IOException ignore){}
        callback.onResult(tweetsSubset.tweets, tweetsSubset.adjacentKey);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Tweet> callback) {
        Log.d(TAG, "loadAfter:");
        TweetsRepository.TweetsSubset tweetsSubset = null;
        try { tweetsSubset = mRepository.getTweetsOlderThan(params.key); } catch (IOException ignore){}
        callback.onResult(tweetsSubset.tweets, tweetsSubset.adjacentKey);
    }

    public static class Factory extends DataSource.Factory<Long, Tweet>{

        private Long mInitialKey;

        public Factory(Long initialKey) {
            mInitialKey = initialKey;
        }

        @Override
        public DataSource<Long, Tweet> create() {
            return new TLDataSource(mInitialKey);
        }
    }
}
