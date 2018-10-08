package com.raed.twitterclient.timeline;

import com.raed.twitterclient.model.tweet.Tweet;

import java.util.List;

public class TweetList {
    public List<Tweet> tweets;
    public Long newerTweetsKey;
    public Long olderTweetsKey;

    public TweetList(List<Tweet> tweets) {
        this.tweets = tweets;
        this.newerTweetsKey = tweets.get(0).getId();
        this.olderTweetsKey = tweets.get(tweets.size() - 1).getId();
    }
}
