package com.raed.twitterclient.model.tweet;


import com.google.gson.annotations.SerializedName;
import com.raed.twitterclient.model.User;

import java.util.Date;

public class RetweetedTweet {

    @SerializedName("created_at")
    private Date time;

    private long id;

    private String fullText;
    private int[] displayTextRange;
    private Entities entities;
    private long inReplyToStatusId;
    private long inReplyToUserId;

    private User user;

    private ExtendedEntities extendedEntities;

    public String getText() {
        return fullText.substring(displayTextRange[0], displayTextRange[1]);
    }

    public User getUser() {
        return user;
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

}
