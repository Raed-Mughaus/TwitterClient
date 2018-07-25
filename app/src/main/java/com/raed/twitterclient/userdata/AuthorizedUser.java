package com.raed.twitterclient.userdata;

public class AuthorizedUser {

    private String mUserId;
    private String mToken;
    private String mSecret;
    private String mScreenName;

    public AuthorizedUser(String token, String secret, String userId, String screenName) {
        mToken = token;
        mSecret = secret;
        mUserId = userId;
        mScreenName = screenName;
    }

    public AuthorizedUser() {

    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public void setSecret(String secret) {
        mSecret = secret;
    }

    public void setScreenName(String screenName) {
        mScreenName = screenName;
    }

    public String getToken() {
        return mToken;
    }

    public String getSecret() {
        return mSecret;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getScreenName() {
        return mScreenName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizedUser authorizedUser = (AuthorizedUser) o;
        return mUserId.equals(authorizedUser.mUserId);
    }

    @Override
    public String toString() {
        return "AuthorizedUser{" +
                "mUserId='" + mUserId + '\'' +
                ", mToken='" + mToken + '\'' +
                ", mSecret='" + mSecret + '\'' +
                ", mScreenName='" + mScreenName + '\'' +
                '}';
    }
}
