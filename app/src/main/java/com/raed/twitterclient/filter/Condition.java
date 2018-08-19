package com.raed.twitterclient.filter;

import com.raed.twitterclient.model.tweet.Tweet;

/*
 * A filter is created from a group of conditions.
 */
public abstract class Condition{

    private String name;

    public Condition(String  name) {
        this.name = name;
    }

    abstract boolean check(Tweet tweet);
}
