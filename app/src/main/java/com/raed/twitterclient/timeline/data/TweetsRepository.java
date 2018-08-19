package com.raed.twitterclient.timeline.data;


import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raed.twitterclient.io.StringCache;
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

    /**
     * Here, if the cache does not contain any tweet we will try to load all of the tweets from the 
     * top of the timeline until the tweet before the tweet with id. If there are too much tweets 
     * or rate limit reached the old cache will be cleared and a null item will be included at the 
     * end of the returned list.
     * The cache will be cleared since we only store adjacent tweet lists, gaps between tweets are 
     * not supported.
     * @param id have a look at the twitter API docs for more info.
     * @return null if there is no new tweets.
     */
    public TweetsSubset getTweetsNewerThan(long id) throws IOException{
        Log.d(TAG, "getStringNewerThan:");
        String jsonTweets = mStringCache.getStringNewerThan(id);
        if (jsonTweets != null){ //if exists in the cache just return it
            List<Tweet> tweets = jsonToList(jsonTweets);
            long adjacentID = tweets.get(0).getId();//max id in this list, you may receive this again to load more data
            return new TweetsSubset(tweets, adjacentID);
        }
        
        StringCache.Transaction transaction = mStringCache.beginTransaction();
        List<Tweet> allTweets = requestTweetsAndCahce(transaction, null, id);
        if (allTweets == null) //no new tweets
            return null;
        if (allTweets.size() < 200){
            transaction.commit();
            return new TweetsSubset(allTweets);
        }
        while (true){
            long maxID = allTweets.get(allTweets.size() - 1).getId() - 1;
            List<Tweet> tweets = requestTweetsAndCahce(transaction, maxID, id);
            if (tweets == null)//this means there are no more tweets
                break;
            allTweets.addAll(tweets);
            if (tweets.size() < 200 || allTweets.size() >= 1000) {
                mStringCache.clearCache();
                break;
            }
        }
        transaction.commit();
        return new TweetsSubset(allTweets);
    }
    
    public TweetsSubset getTweetsOlderThan(long maxID) throws IOException {
        Log.d(TAG, "getStringOlderThan: ");
        String jsonTweets = mStringCache.getStringOlderThan(maxID);
        if (jsonTweets != null) {
            List<Tweet> tweets = jsonToList(jsonTweets);
            long adjacentKey = tweets.get(tweets.size() - 1).getId();//min id in the list, you may receive this again to load more data
            return new TweetsSubset(tweets, adjacentKey);
        }
        List<Tweet> tweets = requestTweetsAndCache(maxID - 1);
        long adjacentKey = tweets.get(tweets.size() - 1).getId();//min id in the list, you may receive this again to load more data
        return new TweetsSubset(tweets, adjacentKey);
    }
    
    public TweetsSubset getTweets() throws IOException {
        Log.d(TAG, "getTweets:");
        mStringCache.clearCache();
        List<Tweet> tweets = requestTweetsAndCache(null);
        long olderKey = tweets.get(tweets.size() - 1).getId();
        return new TweetsSubset(tweets, null, olderKey);
    }

    /**
     * If the cache does not contain any tweets we clear the cache(if any) then request tweets.
     *
     * @param id
     * @return
     */
    public TweetsSubset getTweetsWithMaxID(long id) throws IOException {
        Log.d(TAG, "getTweetsWithMaxID: " + id);
        String jsonTweets = mStringCache.getStringNewerThan(id - 1);
        if (jsonTweets != null) {
            List<Tweet> tweets = jsonToList(jsonTweets);
            int tweetWithMaxIdIndex = tweets.indexOf(new Tweet(id));
            for (int i = 0; i < tweetWithMaxIdIndex; i++)
                tweets.remove(0);
            if (tweets.size() < 10) {
                jsonTweets = mStringCache.getStringOlderThan(id);
                if (jsonTweets != null){
                    tweets.addAll(jsonToList(jsonTweets));
                } else {
                    long maxID = tweets.get(tweets.size() - 1).getId();
                    tweets.addAll(requestTweetsAndCache(maxID));//what if an error occur here
                }
            }
            long newerTweetsKey = tweets.get(0).getId();
            long olderTweetsKey = tweets.get(tweets.size() - 1).getId();
            return new TweetsSubset(tweets, newerTweetsKey, olderTweetsKey);
        }
        //this id is supposes to be in the cache, if it is not there clear the cache
        mStringCache.clearCache();
        List<Tweet> tweets = requestTweetsAndCache(id);
        long newerTweetsKey = tweets.get(0).getId();
        long olderTweetsKey = tweets.get(tweets.size() - 1).getId();
        return new TweetsSubset(tweets, newerTweetsKey, olderTweetsKey);
    }

    //todo you may want to stop using deafualt thread when creatg rxcalladapter
    private List<Tweet> requestTweetsAndCache(Long maxID) throws IOException {
        String tweetJSON = mTLService.homeTimeline(maxID, null)
                .blockingGet()
                .string();
        List<Tweet> tweets = jsonToList(tweetJSON);
        mStringCache.addString(tweets.get(0).getId(), tweetJSON);
        return tweets;
    }

    private List<Tweet> requestTweetsAndCahce(StringCache.Transaction transaction, Long maxID, Long sinceID) throws IOException {
        String tweetJSON = mTLService.homeTimeline(maxID, sinceID).blockingGet().string();
        List<Tweet> tweets = jsonToList(tweetJSON);
        if (tweets.size() == 0)
            return null;
        transaction.addTweets(tweets.get(0).getId(), tweetJSON);
        return tweets;
    }

    private static ArrayList<Tweet> jsonToList(String tweetsJSON){
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("EEE MMM dd HH:mm:ss Z yyyy")
                .create();//todo no need to initialize each time
        Type tweetsType = new TypeToken<ArrayList<Tweet>>(){}.getType();
        return gson.fromJson(tweetsJSON, tweetsType);
    }

    //todo see what is wrong with the threading and currant user
    //todo see what is wrong with type of

    public static class TweetsSubset {
        public Long adjacentKey;
        public List<Tweet> tweets;
        public Long newerTweetsKey;
        public Long olderTweetsKey;

        public TweetsSubset(List<Tweet> tweets, Long newerTweetsKey, Long olderTweetsKey) {
            this.tweets = tweets;
            this.newerTweetsKey = newerTweetsKey;
            this.olderTweetsKey = olderTweetsKey;
        }

        public TweetsSubset(List<Tweet> tweets, Long adjacentKey) {
            this.adjacentKey = adjacentKey;
            this.tweets = tweets;
        }

        public TweetsSubset(List<Tweet> tweets) {
            this.tweets = tweets;
        }
    }
}
