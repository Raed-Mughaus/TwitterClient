package com.raed.twitterclient.retrofitservices;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TLService {

    @GET("statuses/home_timeline.json?tweet_mode=extended&count=200")
    Single<ResponseBody> homeTimeline(@Query("max_id") Long maxId, @Query("since_id") Long sinceID);

}
