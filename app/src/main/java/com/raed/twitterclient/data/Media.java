package com.raed.twitterclient.data;

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

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    /*

    {
          "id": 1024874805294710800,
          "id_str": "1024874805294710784",
          "indices": [
            138,
            161
          ],
          "media_url": "http://pbs.twimg.com/media/DjkWNJTWwAAEBzk.jpg",
          "media_url_https": "https://pbs.twimg.com/media/DjkWNJTWwAAEBzk.jpg",
          "url": "https://t.co/MnR36bK4dC",
          "display_url": "pic.twitter.com/MnR36bK4dC",
          "expanded_url": "https://twitter.com/SaudiNews50/status/1024874807907823616/photo/1",
          "type": "photo",
          "sizes": {
            "small": {
              "w": 680,
              "h": 453,
              "resize": "fit"
            },
            "thumb": {
              "w": 150,
              "h": 150,
              "resize": "crop"
            },
            "medium": {
              "w": 1200,
              "h": 800,
              "resize": "fit"
            },
            "large": {
              "w": 1280,
              "h": 853,
              "resize": "fit"
            }
          }
        }

     */


}
