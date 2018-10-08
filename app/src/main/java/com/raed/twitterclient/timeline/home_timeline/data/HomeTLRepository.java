package com.raed.twitterclient.timeline.home_timeline.data;


import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raed.twitterclient.MyApplication;
import com.raed.twitterclient.api.TwitterErrors.TwitterError;
import com.raed.twitterclient.auth.authorized_user.AuthUser;
import com.raed.twitterclient.utilis.StringCache;
import com.raed.twitterclient.utilis.StringCache.Transaction;
import com.raed.twitterclient.model.tweet.Tweet;
import com.raed.twitterclient.api.RetrofitServices;
import com.raed.twitterclient.api.TLService;
import com.raed.twitterclient.timeline.TweetList;
import com.raed.twitterclient.utilis.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.HttpException;

/**
 * The max_id in a list of tweets is used as an id for the cache.
 * This repository is responsible for:
 *  * Requesting tweets from Twitter API
 *  * Caching tweets
 *  * Removing old cache
 */
public class HomeTLRepository {

    private final static String TAG = HomeTLRepository.class.getSimpleName();
    private final static String CACHE_FOLDER = "tweets";

    private static final int MAX_CACHE_SIZE = 1024 * 1024  * 15; // 15 MB

    private final TLService mTLService = RetrofitServices.getInstance().getTLService();
    private final StringCache mCache;
    private final Gson mGson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat("EEE MMM dd HH:mm:ss Z yyyy")
            .create();

    public HomeTLRepository(AuthUser user) {
        File file = new File(MyApplication.getApp().getCacheDir(), user.getUserId() + "/" + CACHE_FOLDER);
        mCache = new StringCache(file);
    }

    /**
     * @return tweets starting from the top of the timeline, null if there is no any tweet.
     */
    public TweetList getTweets() throws IOException {
        Log.d(TAG, "getTweets: ");
        mCache.clearCache();//clear cache so the cached timeline does not contain any gap.
        List<Tweet> tweets = requestTweetsAndCache(null);
        if(tweets == null)//maybe the user follow no one
            return null;
        return new TweetList(tweets);
    }

    /**
     * return a tweetList that contains a tweet with the id, or null if the tweet is not available.
     */
    public TweetList getTweetsThatInclude(long id) throws IOException {
        Log.d(TAG, "getTweetsThatInclude: ");
        String jsonTweets = mCache.getStringNewerThan(id - 1);
        if (jsonTweets != null){ //if exists in the cache just return it
            List<Tweet> tweets = jsonToTweets(jsonTweets);
            return new TweetList(tweets);
        }

        mCache.clearCache();//it is safer to clear the cache here, we do not want any gaps
        List<Tweet> tweets = requestTweetsAndCache(id);
        if (tweets == null)//this might happen if the id is associated with an old tweet
            return null;
        return new TweetList(tweets);
    }

    /**
     * Here, if the cache does not contain any tweet we will try to load all of the tweets from the 
     * top of the timeline until the tweet before the tweet with newerKey.
     * The cache will be cleared since we only store adjacent tweet lists, gaps between tweets are 
     * not supported.
     * @param newerKey pass the newerKey you received from a previous call.
     * @return null if there is no new tweets.
     */
    public TweetList getTweetsNewerThan(long newerKey) throws IOException{
        Log.d(TAG, "getTweetsNewerThan: " + newerKey);
        String jsonTweets = mCache.getStringNewerThan(newerKey);
        if (jsonTweets != null){ //if exists in the cache, just return it
            List<Tweet> tweets = jsonToTweets(jsonTweets);
            return new TweetList(tweets);
        }

        //use a transaction since we may need to clear the cache before storing multiple files
        Transaction transaction = mCache.beginTransaction();
        List<Tweet> allTweets = requestTweetsAndCache(transaction, null, newerKey);
        if (allTweets == null) //no new tweets
            return null;

        try{
            while (true){
                long maxID = allTweets.get(allTweets.size() - 1).getId() - 1;
                List<Tweet> tweets = requestTweetsAndCache(transaction, maxID, newerKey);
                if (tweets == null)//this means there are no more tweets, or twitter does not allow us to load more tweets(not limit reached)
                    break;
                allTweets.addAll(tweets);
            }
        } catch (HttpException e){
            TwitterError[] errors = Utils.extractTwitterErrors(e);
            if (errors.length != 1 || errors[0].code != 88)//weather rate limit reached or not
                throw e;
            mCache.clearCache();//in case an error occur, clear the old cache
        }

        transaction.commit();
        return new TweetList(allTweets);
    }

    /**
     * @param olderKey pass the key you received from a previous call
     * @return a list of tweets or null if there is no more tweets.
     */
    public TweetList getTweetsOlderThan(long olderKey) throws IOException {
        Log.d(TAG, "getTweetsOlderThan: ");
        String jsonTweets = mCache.getStringOlderThan(olderKey);
        if (jsonTweets != null) {
            List<Tweet> tweets = jsonToTweets(jsonTweets);
            return new TweetList(tweets);
        }
        List<Tweet> tweets = requestTweetsAndCache(olderKey - 1);
        if (tweets == null)//maybe because twitter does not allow us to load old tweets
            return null;
        return new TweetList(tweets);
    }

    public void cacheNewTweets() throws IOException {
        String tweetsJson = mCache.getNewestString();
        if (tweetsJson == null){
            requestTweetsAndCache(null);
            return;
        }

        long newerKey = jsonToTweets(tweetsJson).get(0).getId();

        //use a transaction since we may need to clear the cache before storing multiple files
        List<Tweet> tweets = requestTweetsAndCache(null, newerKey);
        if (tweets == null) //no new tweets
            return;

        while (true){
            long maxID = tweets.get(tweets.size() - 1).getId() - 1;
            tweets = requestTweetsAndCache(maxID, newerKey);
            if (tweets == null)//this means there are no more tweets, or twitter does not allow us to load more tweets(not limit reached)
                break;
        }
    }

    @Nullable
    private List<Tweet> requestTweetsAndCache(Long maxID) throws IOException {
        return requestTweetsAndCache(maxID, null);
    }

    @Nullable
    private List<Tweet> requestTweetsAndCache(Long maxID, Long sinceID) throws IOException {
        String tweetJSON = mTLService.homeTimeline(maxID, sinceID)
                .blockingGet()
                .string();//todo should I remove the default scheduler
        List<Tweet> tweets = jsonToTweets(tweetJSON);
        if (tweets.size() == 0)
            return null;
        mCache.addString(tweets.get(0).getId(), tweetJSON);
        return tweets;
    }

    @Nullable
    private List<Tweet> requestTweetsAndCache(Transaction transaction, Long maxID, Long sinceID) throws IOException {
        String tweetJSON = mTLService.homeTimeline(maxID, sinceID).blockingGet().string();
        List<Tweet> tweets = jsonToTweets(tweetJSON);
        if (tweets.size() == 0)
            return null;
        transaction.addString(tweets.get(0).getId(), tweetJSON);
        return tweets;
    }

    private List<Tweet> jsonToTweets(String tweetsJSON){
        Type tweetsType = new TypeToken<ArrayList<Tweet>>(){}.getType();
        return mGson.fromJson(tweetsJSON, tweetsType);
    }

}
