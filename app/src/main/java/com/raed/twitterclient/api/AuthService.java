package com.raed.twitterclient.api;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService {

    @POST("oauth/request_token")
    Single<ResponseBody> requestToken(@Header("Authorization") String authorizationHeader);

    @POST("oauth/access_token")
    Single<ResponseBody> accessToken(@Header("Authorization") String authorizationHeader, @Query("oauth_verifier") String oauthVerifier);

}
