package com.raed.twitterclient.data;

import com.google.gson.annotations.SerializedName;
import com.raed.twitterclient.profile.User;

public class Tweet {

    private String fullText;
    private int[] displayTextRange;
    private User user;

    @SerializedName("created_at")
    private String time;

    private ExtendedEntities extendedEntities;

    @SerializedName("retweeted_status")
    private Tweet retweetedTweet;

    public String getText() {
        return fullText.substring(displayTextRange[0], displayTextRange[1]);
    }

    public User getUser() {
        return user;
    }

    public Tweet getRetweetedTweet() {
        return retweetedTweet;
    }

    public ExtendedEntities getExtendedEntities() {
        return extendedEntities;
    }

    public String getTime() {
        return time;
    }
}
