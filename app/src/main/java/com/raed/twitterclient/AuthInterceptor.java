package com.raed.twitterclient;

import com.raed.twitterclient.authheader.AuthHeaderGenerator;
import com.raed.twitterclient.authheader.SignatureGenerator;
import com.raed.twitterclient.userdata.AuthorizedUser;
import com.raed.twitterclient.userdata.CurrentUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private AuthHeaderGenerator mAuthHeaderGenerator;

    public AuthInterceptor(){
        AuthorizedUser user = CurrentUser.getInstance().getCurrentUser();
        AppTokenAndSecret appTokenAndSecret = new AppTokenAndSecret();
        SignatureGenerator signatureGenerator = new SignatureGenerator(appTokenAndSecret.getAppSecret(), user.getSecret());
        mAuthHeaderGenerator = new AuthHeaderGenerator(appTokenAndSecret.getAppKey(), user.getToken(), signatureGenerator);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String authHeader = mAuthHeaderGenerator
                .generateAuthHeader(
                        request.method(),
                        getBaseUrl(request),
                        getBodyAsString(request),
                        generateMap(request)
                );
        Request requestWithAuthHeader = chain.request()
                .newBuilder()//this will create a new builder with the old request properties(headers, url, method, ...)
                .header("Authorization", authHeader)
                .build();
        return chain.proceed(requestWithAuthHeader);
    }


    private static Map<String, String> generateMap(Request request){
        Map<String, String> map = new HashMap<>();
        Headers headers = request.headers();
        for (String headerName : headers.names()) {
            int length = 6;
            //add it only if it start with 'oauth_'
            if (headerName.length() > length && headerName.substring(0, length).equals("oauth_*"))
                map.put(headerName, headers.get(headerName));
        }
        HttpUrl httpUrl = request.url();
        for (String paramName : httpUrl.queryParameterNames())
            map.put(paramName, httpUrl.queryParameter(paramName));
        return map;
    }

    private static String getBaseUrl(Request request){
        String urlString = request.url().toString();
        int index = urlString.indexOf("?");
        if (index == -1)
            return urlString;
        return urlString.substring(0, index);
    }

    private static String getBodyAsString(Request request){
        RequestBody requestBody = request.body();
        String bodyString = null;
        if (requestBody != null)
            bodyString = requestBody.toString();
        return bodyString;
    }

}
