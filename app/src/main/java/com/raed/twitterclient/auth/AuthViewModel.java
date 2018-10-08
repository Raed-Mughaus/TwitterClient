package com.raed.twitterclient.auth;


import android.arch.lifecycle.ViewModel;

import com.raed.twitterclient.auth.authorized_user.AuthUser;
import com.raed.twitterclient.api.RetrofitServices;
import com.raed.twitterclient.auth.authorized_user.CurrentAuthUser;
import com.raed.twitterclient.auth.auth_header.AuthHeaderGenerator;
import com.raed.twitterclient.auth.auth_header.SignatureGenerator;
import com.raed.twitterclient.api.AuthService;

import java.util.HashMap;
import java.util.Map;



import io.reactivex.Single;

public class AuthViewModel extends ViewModel {

    private static final String TAG = "AuthViewModel";

    private AuthService mAuthService;
    private String mConsumerKey;
    private String mConsumerSecret;

    public AuthViewModel() {
        super();
        mAuthService = RetrofitServices.getInstance().getAuthService();

        mConsumerKey = APIKeys.API_KEY;
        mConsumerSecret = APIKeys.API_SECRET_KEY;
    }

    Single<AuthUser> onUserRedirected(String oauthToken, String oauthVerifier){

        SignatureGenerator signatureGenerator = new SignatureGenerator(null);
        AuthHeaderGenerator headerGenerator = new AuthHeaderGenerator(oauthToken, signatureGenerator);

        Map<String, String> additionalParams = new HashMap<>();
        additionalParams.put("oauth_verifier", oauthVerifier);

        String authorizationHeader =
                headerGenerator.generateAuthHeader("POST", "https://api.twitter.com/oauth/access_token", null, additionalParams);

        return mAuthService
                .accessToken(authorizationHeader, oauthVerifier)
                .map(responseBody -> {
                    String[] params = responseBody.string().split("&");
                    AuthUser authUser = new AuthUser();
                    for (String param : params){
                        if (param.contains("oauth_token=")) authUser.setToken(param.substring(param.indexOf('=') + 1));
                        else if (param.contains("oauth_token_secret=")) authUser.setSecret(param.substring(param.indexOf("=") + 1));
                        else if (param.contains("user_id=")) authUser.setUserId(param.substring(param.indexOf("=") + 1));
                        else if (param.contains("screen_name=")) authUser.setScreenName(param.substring(param.indexOf("=") + 1));
                    }
                    return authUser;
                });
    }

    Single<String[]> requestToken() {
        SignatureGenerator signatureGenerator = new SignatureGenerator(null);
        AuthHeaderGenerator headerGenerator = new AuthHeaderGenerator(null, signatureGenerator);
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

    void onNewUser(AuthUser authUser){
        CurrentAuthUser.getInstance().set(authUser);
    }
}
