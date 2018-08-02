package com.raed.twitterclient;

import com.google.gson.annotations.SerializedName;
import com.raed.twitterclient.profile.User;

public class Tweet {

    private String fullText;
    private int[] displayTextRange;
    private User user;

    public String getText() {
        return fullText.substring(displayTextRange[0], displayTextRange[1]);
    }

    public User getUser() {
        return user;
    }
}
