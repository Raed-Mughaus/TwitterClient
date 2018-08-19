package com.raed.twitterclient.userlist;

import com.google.gson.annotations.SerializedName;
import com.raed.twitterclient.model.User;

import java.util.List;

public class UserList {

    @SerializedName("next_cursor_str")
    private String nextCursor;

    @SerializedName("previous_cursor_str")
    private String previousCursor;

    private List<User> users;


    public List<User> getUsers() {
        return users;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public String getPreviousCursor() {
        return previousCursor;
    }
}
