package com.raed.twitterclient.timeline.user_timeline;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.raed.twitterclient.model.tweet.Tweet;
import com.raed.twitterclient.api.RetrofitServices;
import com.raed.twitterclient.api.TLService;
import com.raed.twitterclient.timeline.TLDataSource;
import com.raed.twitterclient.timeline.TLEvent;
import com.raed.twitterclient.timeline.TweetList;

import java.util.List;

public class UserTLDataSource extends TLDataSource{

    private static final String TAG = UserTLDataSource.class.getSimpleName();

    private TLService mTLService = RetrofitServices.getInstance().getTLService();

    private String mUserID;

    UserTLDataSource(
            String userID,
            MutableLiveData<TLEvent> initialTweetsTLEvent,
            MutableLiveData<TLEvent> newTweetsTLEvent,
            MutableLiveData<TLEvent> oldTweetsTLEvent,
            MutableLiveData<Exception> error) {
        super(initialTweetsTLEvent, newTweetsTLEvent, oldTweetsTLEvent, error);
        mUserID = userID;
    }

    @Override
    protected TweetList loadInitialTweetList() {
        return getTweetList(null, null);
    }

    @Override
    protected TweetList loadNewerTweetList(LoadParams<Long> params) {
        return getTweetList(null, params.key);
    }

    @Override
    protected TweetList loadOlderTweetList(LoadParams<Long> params) {
        return getTweetList(params.key - 1, null);
    }

    private TweetList getTweetList(Long maxID, Long sinceID){
        List<Tweet> tweets = mTLService.userTimeline(mUserID, maxID, sinceID).blockingGet();
        return new TweetList(tweets);
    }

    static class Factory extends TLDataSource.Factory{

        private final String mUserID;

        Factory(String userID) {
            mUserID = userID;
        }

        @Override
        public DataSource<Long, Tweet> create() {
            return new UserTLDataSource(mUserID, mInitialTweetsTLEvent, mNewTweetsTLEvent,
                    mOlderTweetsTLEvent, mError);
        }
    }
}
