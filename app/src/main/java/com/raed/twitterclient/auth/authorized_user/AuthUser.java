package com.raed.twitterclient.auth.authorized_user;

public class AuthUser {

    private String userId;
    private String screenName;
    private String token;
    private String secret;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getToken() {
        return token;
    }

    public String getSecret() {
        return secret;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthUser authUser = (AuthUser) o;
        return userId.equals(authUser.userId);
    }

    @Override
    public String toString() {
        return "AuthUser{" +
                "userId='" + userId + '\'' +
                ", token='" + token + '\'' +
                ", secret='" + secret + '\'' +
                ", screenName='" + screenName + '\'' +
                '}';
    }
}
