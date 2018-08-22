package com.raed.twitterclient.timeline.data;


import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raed.twitterclient.io.StringCache;
import com.raed.twitterclient.io.StringCache.Transaction;
import com.raed.twitterclient.model.tweet.Tweet;
import com.raed.twitterclient.retrofitservices.RetrofitServices;
import com.raed.twitterclient.retrofitservices.TLService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * The max_id in a list of tweets is used as an id for the cache.
 */
public class TweetsRepository {

    private final static String TAG = TweetsRepository.class.getSimpleName();

    private TLService mTLService = RetrofitServices.getInstance().getTLService();
    private StringCache mStringCache = new StringCache();

    private final Gson mGson;

    TweetsRepository() {
        mGson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("EEE MMM dd HH:mm:ss Z yyyy")
                .create();
    }

    /**
     * Here, if the cache does not contain any tweet we will try to load all of the tweets from the 
     * top of the timeline until the tweet before the tweet with id. If there are too much tweets 
     * or rate limit reached the old cache will be cleared.
     * The cache will be cleared since we only store adjacent tweet lists, gaps between tweets are 
     * not supported.
     * @param id have a look at the twitter API docs for more info.
     * @return null if there is no new tweets.
     */
    public TweetsSubset getTweetsNewerThan(long id) throws IOException{
        Log.d(TAG, "getStringNewerThan:");
        String jsonTweets = mStringCache.getStringNewerThan(id);
        if (jsonTweets != null){ //if exists in the cache just return it
            List<Tweet> tweets = jsonToTweets(jsonTweets);
            long newerTweetsKey = tweets.get(0).getId();
            long olderTweetsKey = tweets.get(tweets.size() - 1).getId();
            return new TweetsSubset(tweets, newerTweetsKey, olderTweetsKey);
        }
        
        Transaction transaction = mStringCache.beginTransaction();
        List<Tweet> allTweets = requestTweetsAndCache(transaction, null, id);
        if (allTweets == null) //no new tweets
            return null;
        if (allTweets.size() < 200){
            transaction.commit();
            long olderTweetsKey = allTweets.get(allTweets.size() - 1).getId();
            return new TweetsSubset(allTweets, null, olderTweetsKey);
        }
        while (true){
            long maxID = allTweets.get(allTweets.size() - 1).getId() - 1;
            List<Tweet> tweets = requestTweetsAndCache(transaction, maxID, id);
            if (tweets == null)//this means there are no more tweets
                break;
            allTweets.addAll(tweets);
            if (tweets.size() < 200) {
                mStringCache.clearCache();
                break;
            }
        }
        transaction.commit();
        long olderTweetsKey = allTweets.get(allTweets.size() - 1).getId();
        return new TweetsSubset(allTweets, null, olderTweetsKey);
    }
    
    public TweetsSubset getTweetsOlderThan(long key) throws IOException {
        Log.d(TAG, "getStringOlderThan: ");
        String jsonTweets = mStringCache.getStringOlderThan(key);
        if (jsonTweets != null) {
            List<Tweet> tweets = jsonToTweets(jsonTweets);
            long newerKey = tweets.get(0).getId();
            long olderKey = tweets.get(tweets.size() - 1).getId();
            return new TweetsSubset(tweets, newerKey, olderKey);
        }
        List<Tweet> tweets = requestTweetsAndCache(key - 1);
        if (tweets == null)
            return null;
        long olderKey = tweets.get(tweets.size() - 1).getId();
        long newerKey = tweets.get(0).getId();
        return new TweetsSubset(tweets, newerKey, olderKey);
    }

    /**
     * @return tweets starting from the top of the timeline
     */
    public TweetsSubset getTweets() throws IOException {
        Log.d(TAG, "getTweets: ");
        mStringCache.clearCache();//clear cache so the cached timeline does not include any gaps.
        List<Tweet> tweets = requestTweetsAndCache(null);//tweets cannot not be null here
        long olderKey = tweets.get(tweets.size() - 1).getId();
        //there is no newerTweetsKey since we are at the start of the timeline
        return new TweetsSubset(tweets, null, olderKey);
    }

    /**
     * return a tweet subset that contains a tweet with the passed id, or null if the data is not
     * available.
     */
    public TweetsSubset getTweetsThatInclude(long id) throws IOException {
        Log.d(TAG, "getTweetsThatInclude: ");
        String jsonTweets = mStringCache.getStringNewerThan(id - 1);
        if (jsonTweets != null){ //if exists in the cache just return it
            List<Tweet> tweets = jsonToTweets(jsonTweets);
            long newerTweetsKey = tweets.get(0).getId();
            long olderTweetsKey = tweets.get(tweets.size() - 1).getId();
            return new TweetsSubset(tweets, newerTweetsKey, olderTweetsKey);
        }

        mStringCache.clearCache();//it is safer to clear the cache
        List<Tweet> tweets = requestTweetsAndCache(id);
        if (tweets == null)//this might happen if the id associated with an old tweet
            return null;
        long olderKey = tweets.get(tweets.size() - 1).getId();
        long newerKey = tweets.get(0).getId();
        return new TweetsSubset(tweets, newerKey, olderKey);
    }

    //todo merge cache

    @Nullable
    private List<Tweet> requestTweetsAndCache(Long maxID) throws IOException {
        String tweetJSON = mTLService.homeTimeline(maxID, null)
                .blockingGet()
                .string();//todo should I remove the default scheduler
        List<Tweet> tweets = jsonToTweets(tweetJSON);
        if (tweetJSON.length() == 0)
            return null;
        mStringCache.addString(tweets.get(0).getId(), tweetJSON);
        return tweets;
    }

    //todo there is a limit to the number of tweets you can retrive
    @Nullable
    private List<Tweet> requestTweetsAndCache(Transaction transaction, Long maxID, Long sinceID) throws IOException {
        String tweetJSON = mTLService.homeTimeline(maxID, sinceID).blockingGet().string();
        List<Tweet> tweets = jsonToTweets(tweetJSON);
        if (tweets.size() == 0)
            return null;
        transaction.addTweets(tweets.get(0).getId(), tweetJSON);
        return tweets;
    }

    private List<Tweet> jsonToTweets(String tweetsJSON){
        Type tweetsType = new TypeToken<ArrayList<Tweet>>(){}.getType();
        return mGson.fromJson(tweetsJSON, tweetsType);
    }

    public static class TweetsSubset {
        public List<Tweet> tweets;
        public Long newerTweetsKey;
        public Long olderTweetsKey;

        TweetsSubset(List<Tweet> tweets, Long newerTweetsKey, Long olderTweetsKey) {
            this.tweets = tweets;
            this.newerTweetsKey = newerTweetsKey;
            this.olderTweetsKey = olderTweetsKey;
        }
    }
}
