package com.raed.twitterclient.retrofitservices;

import com.raed.twitterclient.profile.UserProfile;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserService {

    @GET("users/show.json")
    Observable<UserProfile> show(@Query("user_id") String userId);

}
