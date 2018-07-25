package com.raed.twitterclient.auth;


import android.arch.lifecycle.ViewModel;

import com.raed.twitterclient.userdata.AuthorizedUser;
import com.raed.twitterclient.userdata.CurrentUser;
import com.raed.twitterclient.utilis.MyMap;
import com.raed.twitterclient.userdata.Users;
import com.raed.twitterclient.authheader.AuthHeaderGenerator;
import com.raed.twitterclient.authheader.SignatureGenerator;
import com.raed.twitterclient.retrofitservices.AuthService;

import java.util.Map;

import javax.inject.Inject;


import io.reactivex.Single;

public class AuthViewModel extends ViewModel {

    private static final String TAG = "AuthViewModel";

    private AuthService mAuthService;
    private Users mUsers;
    private String mConsumerKey;
    private String mConsumerSecret;
    private CurrentUser mCurrentUser;

    @Inject
    public AuthViewModel(AuthService authService, Users users, CurrentUser currentUser, String consumerKey, String consumerSecret) {
        super();
        mAuthService = authService;
        mUsers = users;
        mConsumerKey = consumerKey;
        mConsumerSecret = consumerSecret;
        mCurrentUser = currentUser;
    }

    Single<AuthorizedUser> onUserRedirected(String oauthToken, String oauthVerifier){

        SignatureGenerator signatureGenerator = new SignatureGenerator(mConsumerSecret, null);
        AuthHeaderGenerator headerGenerator = new AuthHeaderGenerator(mConsumerKey, oauthToken, signatureGenerator);

        Map<String, String> additionalParams = MyMap.<String, String>create()
                .put("oauth_verifier", oauthVerifier);

        String authorizationHeader =
                headerGenerator.generateAuthHeader("POST", "https://api.twitter.com/oauth/access_token", null, additionalParams);

        return mAuthService
                .accessToken(authorizationHeader, oauthVerifier)
                .map(responseBody -> {
                    String[] params = responseBody.string().split("&");
                    AuthorizedUser authorizedUser = new AuthorizedUser();
                    for (String param : params){
                        if (param.contains("oauth_token=")) authorizedUser.setToken(param.substring(param.indexOf('=') + 1));
                        else if (param.contains("oauth_token_secret=")) authorizedUser.setSecret(param.substring(param.indexOf("=") + 1));
                        else if (param.contains("user_id=")) authorizedUser.setUserId(param.substring(param.indexOf("=") + 1));
                        else if (param.contains("screen_name=")) authorizedUser.setScreenName(param.substring(param.indexOf("=") + 1));
                    }
                    return authorizedUser;
                });
    }

    Single<String[]> requestToken() {
        SignatureGenerator signatureGenerator = new SignatureGenerator(mConsumerSecret, null);
        AuthHeaderGenerator headerGenerator = new AuthHeaderGenerator(mConsumerKey, null, signatureGenerator);
        String authorizationHeader =
                headerGenerator.generateAuthHeader("POST", "https://api.twitter.com/oauth/request_token", null, null);
        return mAuthService
                .requestToken(authorizationHeader)
                .map(responseBody -> {
                    String[] keys = responseBody.string().split("&");
                    keys[0] = keys[0].substring(keys[0].indexOf('=') + 1);
                    keys[1] = keys[1].substring(keys[1].indexOf('=') + 1);
                    return keys;
                });
    }

    void onNewUser(AuthorizedUser authorizedUser){
        mUsers.addUser(authorizedUser);
        mCurrentUser.setCurrentUser(authorizedUser);
    }
}
