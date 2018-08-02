package com.raed.twitterclient.retrofitservices;

import com.raed.twitterclient.Tweet;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TLService {

    @GET("statuses/home_timeline.json?tweet_mode=extended")
    Single<List<Tweet>> homeTimeline(@Query("count") int count);

}
