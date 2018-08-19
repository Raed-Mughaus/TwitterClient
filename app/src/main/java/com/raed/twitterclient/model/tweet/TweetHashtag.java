package com.raed.twitterclient.model.tweet;

public class TweetHashtag {

    private String text;
    private int[] indices;

    public TweetHashtag(String text, int[] indices) {
        this.text = text;
        this.indices = indices;
    }

    public String getText() {
        return text;
    }

    public int[] getIndices() {
        return indices;
    }

}
