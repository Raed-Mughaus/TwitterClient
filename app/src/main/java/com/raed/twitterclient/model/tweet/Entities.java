package com.raed.twitterclient.model.tweet;

import com.raed.twitterclient.model.Url;

public class Entities {

    private TweetHashtag[] mTweetHashtags;
    private UserMention[] userMentions;
    private Url[] urls;

    public Entities(TweetHashtag[] tweetHashtags, UserMention[] userMentions, Url[] urls) {
        this.mTweetHashtags = tweetHashtags;
        this.userMentions = userMentions;
        this.urls = urls;
    }

    public TweetHashtag[] getTweetHashtags() {
        return mTweetHashtags;
    }

    public UserMention[] getUserMentions() {
        return userMentions;
    }

    public Url[] getUrls() {
        return urls;
    }
}
