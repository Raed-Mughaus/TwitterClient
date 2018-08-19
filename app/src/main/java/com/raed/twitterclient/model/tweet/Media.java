package com.raed.twitterclient.model.tweet;

import com.google.gson.annotations.SerializedName;

public class Media {

    public static class Type {
        public static final String PHOTO = "photo";
        public static final String VIDEO = "video";
        public static final String GIF = "gif";
    }

    @SerializedName("media_url_https")
    private String url;
    private String type;


    public Media(String url, String type) {
        this.url = url;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }


}
