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

    @SerializedName("video_info")
    private VideoInfo mVideoInfo;

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

    public VideoInfo getVideoInfo() {
        return mVideoInfo;
    }

    public static class VideoInfo {
        @SerializedName("variants")
        private Variant mVariants[];

        public Variant[] getVariants() {
            return mVariants;
        }

        public static class Variant {
            @SerializedName("content_type")
            private String mContentType;

            @SerializedName("url")
            private String mUrl;

            @SerializedName("bitrate")
            private Long mBitrate;

            public String getContentType() {
                return mContentType;
            }

            public String getUrl() {
                return mUrl;
            }

            public Long getBitrate() {
                return mBitrate;
            }
        }
    }
}
