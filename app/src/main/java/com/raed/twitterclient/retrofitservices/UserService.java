package com.raed.twitterclient.retrofitservices;

import com.raed.twitterclient.model.User;
import com.raed.twitterclient.userlist.UserList;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    interface UserRelation{
        String FOLLOWING = "friends";
        String FOLLOWERS = "followers";
    }

    //todo do we have extended mode for user

    //todo do you need the entities here
    @GET("users/show.json?skip_status=true")
    Single<User> show(@Query("user_id") String userId);

    @GET("{relation}/list.json?skip_status=true&include_user_entities=false")
    Single<UserList> listUsers(@Path("relation") String relation, @Query("count") int count, @Query("user_id") String userId, @Query("cursor") String cursor);

    @GET("{relation}/list.json?skip_status=true&include_user_entities=false")
    Single<UserList> listUsers(@Path("relation") String relation, @Query("count") int count, @Query("user_id") String userId);
}
