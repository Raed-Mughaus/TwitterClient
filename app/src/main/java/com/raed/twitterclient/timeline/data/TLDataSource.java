package com.raed.twitterclient.timeline.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.raed.twitterclient.model.tweet.Tweet;
import com.raed.twitterclient.timeline.TLEvent;
import com.raed.twitterclient.timeline.data.TweetsRepository.TweetsSubset;
import com.raed.twitterclient.utilis.Crashlytics;

import java.io.IOException;
import java.util.ArrayList;

/*
    params.requestedLoadSize is ignored
*/
public class TLDataSource extends PageKeyedDataSource<Long, Tweet>{

    private static final String TAG = TLDataSource.class.getSimpleName();

    private TweetsRepository mRepository = new TweetsRepository();
    private Long mInitialKey;//the tweet with this id needs to be included in the list of the 1st load

    private MutableLiveData<TLEvent> mTLEvent;
    private MutableLiveData<Exception> mError;

    private TLDataSource(Long initialKey, MutableLiveData<TLEvent> tlEvent, MutableLiveData<Exception> error) {
        mInitialKey = initialKey;
        mTLEvent = tlEvent;
        mError = error;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Long, Tweet> callback) {
        Log.d(TAG, "loadInitial:");
        TweetsSubset tweetsSubset;
        try {
            if (mInitialKey == null) {
                tweetsSubset = mRepository.getTweets();
            } else {
                tweetsSubset = mRepository.getTweetsThatInclude(mInitialKey);
                if (tweetsSubset == null){
                    tweetsSubset = mRepository.getTweets();
                }
            }
            callback.onResult(tweetsSubset.tweets, tweetsSubset.newerTweetsKey, tweetsSubset.olderTweetsKey);
        }
        catch (IOException e) { Crashlytics.logException(e);}
        catch (Exception e){ mError.postValue(e); }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Tweet> callback) {
        Log.d(TAG, "loadBefore: ");
        TweetsSubset tweetsSubset = null;
        try { tweetsSubset = mRepository.getTweetsNewerThan(params.key); }
        catch (IOException e) { Crashlytics.logException(e);}
        catch (Exception e){ mError.postValue(e); }
        if(tweetsSubset == null)
            callback.onResult(new ArrayList<>(),null);
        else
            callback.onResult(tweetsSubset.tweets, tweetsSubset.newerTweetsKey);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Tweet> callback) {
        Log.d(TAG, "loadAfter: " + params.key);
        TweetsSubset tweetsSubset = null;
        try { tweetsSubset = mRepository.getTweetsOlderThan(params.key); }
        catch (IOException e) { Crashlytics.logException(e);}
        catch (Exception e){ mError.postValue(e); }
        if(tweetsSubset == null)
            callback.onResult(new ArrayList<>(),null);
        else
            callback.onResult(tweetsSubset.tweets, tweetsSubset.olderTweetsKey);
    }

    public static class Factory extends DataSource.Factory<Long, Tweet>{

        private Long mInitialKey;
        private MutableLiveData<TLEvent> mTLEvent;
        private MutableLiveData<Exception> mError;

        public Factory(Long initialKey, MutableLiveData<TLEvent> tlEvent, MutableLiveData<Exception> error) {
            mInitialKey = initialKey;
            mTLEvent = tlEvent;
            mError = error;
        }

        @Override
        public DataSource<Long, Tweet> create() {
            return new TLDataSource(mInitialKey, mTLEvent, mError);
        }
    }
}
