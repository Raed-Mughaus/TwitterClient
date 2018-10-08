package com.raed.twitterclient.timeline.home_timeline.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.raed.twitterclient.model.tweet.Tweet;
import com.raed.twitterclient.timeline.TLDataSource;
import com.raed.twitterclient.timeline.TLEvent;
import com.raed.twitterclient.timeline.TweetList;

import java.io.IOException;

/*
    params.requestedLoadSize is ignored
*/
public class HomeTLDataSource extends TLDataSource{

    private HomeTLRepository mRepository;
    private Long mInitialKey;//the tweet with this id needs to be included in the list of the 1st load

    private HomeTLDataSource(HomeTLRepository homeTLRepository,
                             Long initialKey,
                             MutableLiveData<TLEvent> initialTweetsTLEvent,
                             MutableLiveData<TLEvent> newTweetsTLEvent,
                             MutableLiveData<TLEvent> oldTweetsTLEvent,
                             MutableLiveData<Exception> error) {
        super(initialTweetsTLEvent, newTweetsTLEvent, oldTweetsTLEvent, error);
        mRepository = homeTLRepository;
        mInitialKey = initialKey;
    }

    protected TweetList loadInitialTweetList(){
        try {
            if (mInitialKey != null)
                return mRepository.getTweetsThatInclude(mInitialKey);
            return mRepository.getTweets();
        } catch (IOException ignore) { }
        return null;
    }

    protected TweetList loadNewerTweetList(LoadParams<Long> params) {
        try {
            return mRepository.getTweetsNewerThan(params.key);
        } catch (IOException ignore) { }
        return null;
    }

    protected TweetList loadOlderTweetList(LoadParams<Long> params){
        try {
            return mRepository.getTweetsOlderThan(params.key);
        } catch (IOException ignore) { }
        return null;
    }

    public static class Factory extends TLDataSource.Factory{

        private HomeTLRepository mRepository;
        private Long mInitialKey;

        public Factory(HomeTLRepository repository, Long initialKey) {
            mRepository = repository;
            mInitialKey = initialKey;
        }

        @Override
        public DataSource<Long, Tweet> create() {
            return new HomeTLDataSource(mRepository, mInitialKey, mInitialTweetsTLEvent,
                    mNewTweetsTLEvent, mOlderTweetsTLEvent, mError);
        }
    }
}
