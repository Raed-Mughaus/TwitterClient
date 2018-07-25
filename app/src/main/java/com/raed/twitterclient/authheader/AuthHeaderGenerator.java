package com.raed.twitterclient.authheader;

import java.util.HashMap;
import java.util.Map;

public class AuthHeaderGenerator {

    private static final String POSSIBLE_CHARS_SET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    private static final String OAUTH_VERSION = "1.0";
    private static final String OAUTH_SIGNATURE_METHOD = "HMAC-SHA1";

    //this is associated with the app
    private String mConsumerKey;

    //this is associated with the user
    private String mToken;

    private SignatureGenerator mSignatureGenerator;

    //todo what if the time in the user device is incorrect

    public AuthHeaderGenerator(String consumerKey, String token, SignatureGenerator signatureGenerator) {
        mConsumerKey = consumerKey;
        mToken = token;
        mSignatureGenerator = signatureGenerator;
    }

    public String generateAuthHeader(
            String method,
            String requestUrl,
            String body,
            Map<String, String> additionalParameters
    ){
        String timestamp = System.currentTimeMillis()/1000L + "";
        String nonce = random32CharString();

        Map<String, String> signatureParams;
        if (additionalParameters != null)
            signatureParams = new HashMap<>(additionalParameters);
        else
            signatureParams = new HashMap<>();
        signatureParams.put("oauth_consumer_key", mConsumerKey);
        signatureParams.put("oauth_nonce", nonce);
        signatureParams.put("oauth_signature_method", OAUTH_SIGNATURE_METHOD);
        signatureParams.put("oauth_timestamp", timestamp);
        signatureParams.put("oauth_version", OAUTH_VERSION);
        if (mToken != null) signatureParams.put("oauth_token", mToken);

        String signature = mSignatureGenerator.generateSignature(method, requestUrl, signatureParams, body);

        return "OAuth " +
                "oauth_consumer_key=" + "\"" + PercentEncoder.percentEncode(mConsumerKey) + "\", " +
                "oauth_nonce=" + "\"" + nonce + " \", " +
                "oauth_signature=\"" + PercentEncoder.percentEncode(signature) + "\", " +
                "oauth_signature_method=\"" + OAUTH_SIGNATURE_METHOD + "\", " +
                "oauth_timestamp=\"" + timestamp + "\", " +
                ((mToken != null) ? "oauth_token=\"" + PercentEncoder.percentEncode(mToken) + "\", " : "" )+
                "oauth_version=\"" + PercentEncoder.percentEncode(OAUTH_VERSION) + "\"";
    }

    private String random32CharString(){
        int n = POSSIBLE_CHARS_SET.length();
        StringBuilder randomString = new StringBuilder(32);
        for (int i = 0; i < 32; i++)
            randomString.append(POSSIBLE_CHARS_SET.charAt((int) (n * Math.random())));
        return randomString.toString();
    }

}
