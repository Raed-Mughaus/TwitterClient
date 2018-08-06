package com.raed.twitterclient.data;

import com.google.gson.annotations.SerializedName;

public class UserMention {

    @SerializedName("id")
    private String userID;
    private String name;
    private int[] indices;

    public String getName() {
        return name;
    }

    public int[] getIndices() {
        return indices;
    }
}
