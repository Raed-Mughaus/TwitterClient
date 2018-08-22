package com.raed.twitterclient.model.tweet;

import com.google.gson.annotations.SerializedName;
import com.raed.twitterclient.model.User;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

//todo what about an index for fast retrival
public class Tweet {

    //The null tweet is used to indicate a set of tweets that has not been loaded yet.
    public static Tweet createNullTweet(long id){
        Tweet tweet = new Tweet();
        tweet.id = id;
        tweet.nullTweet = true;
        return tweet;
    }

    private boolean nullTweet = false;

    @SerializedName("created_at")
    private Date time;

    private long id;

    private String fullText;
    private int[] displayTextRange;
    
    private Entities entities;
    private long inReplyToStatusId;
    private long inReplyToUserId;

    
    private User user;

    public Tweet() {
    }

    public Tweet(long id) {
        this.id = id;
    }

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

    public Date getTime() {
        return time;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tweet tweet = (Tweet) o;
        return id == tweet.id;
    }
}
