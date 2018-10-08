package com.raed.twitterclient.api;

import com.raed.twitterclient.model.tweet.Tweet;

import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TLService {

    @GET("statuses/home_timeline.json?tweet_mode=extended&count=200")
    Single<ResponseBody> homeTimeline(@Query("max_id") Long maxId, @Query("since_id") Long sinceID);

    @GET("statuses/user_timeline.json?tweet_mode=extended&count=200&exclude_replies=true")
    Single<List<Tweet>> userTimeline(@Query("user_id") String userID, @Query("max_id") Long maxId, @Query("since_id") Long sinceID);
}
