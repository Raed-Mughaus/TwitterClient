package com.raed.twitterclient.api;

public class TwitterErrors {

    public TwitterError[] errors;

    public static class TwitterError {
        public String message;
        public int code;
    }


}
