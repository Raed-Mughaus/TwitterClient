package com.raed.twitterclient.retrofitservices;

import com.raed.twitterclient.profile.User;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserService {

    @GET("users/show.json")
    Observable<User> show(@Query("user_id") String userId);

}
