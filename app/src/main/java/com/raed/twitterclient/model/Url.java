package com.raed.twitterclient.model;


public class Url {

    private String url;
    private String expandedUrl;
    private String displayUrl;
    private int[] indices;

    public Url(String url, String expandedUrl, String displayUrl, int[] indices) {
        this.url = url;
        this.expandedUrl = expandedUrl;
        this.displayUrl = displayUrl;
        this.indices = indices;
    }

    public String getUrl() {
        return url;
    }

    public String getExpandedUrl() {
        return expandedUrl;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public int[] getIndices() {
        return indices;
    }
}
