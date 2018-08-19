package com.raed.twitterclient.model.tweet;

import com.google.gson.annotations.SerializedName;

public class UserMention {

    @SerializedName("id")
    private String userID;
    private String name;
    private int[] indices;

    public UserMention(String userID, String name, int[] indices) {
        this.userID = userID;
        this.name = name;
        this.indices = indices;
    }

    public String getName() {
        return name;
    }

    public int[] getIndices() {
        return indices;
    }
}
