package com.raed.twitterclient.settings.pages;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Page {

    private String type;
    private Map<String, String> attributes;

    @NonNull
    public static Page homeTimeline() {
        return new Page(PagesConstants.HOME_TL, null);
    }

    @NonNull
    public static Page listTimeline(String listID, String filter) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(PagesConstants.LIST_ID, listID);
        if (filter != null)
            attributes.put(PagesConstants.FILTER, filter);
        return new Page(PagesConstants.LIST_TL, attributes);
    }

    @NonNull
    public static Page mentionTimeline(String filter) {
        Map<String, String> attributes = new HashMap<>();
        if (filter != null)
            attributes.put(PagesConstants.FILTER, filter);
        return new Page(PagesConstants.MENTION_TL, attributes);
    }

    @NonNull
    public static Page directMessages() {
        return new Page(PagesConstants.DIRECT_MESSAGES);
    }

    @NonNull
    public static Page favoritesTL(String filter) {
        Map<String, String> attributes = new HashMap<>();
        if (filter != null)
            attributes.put(PagesConstants.FILTER, filter);
        return new Page(PagesConstants.FAVORITES_TL, attributes);
    }

    private Page(String type, Map<String, String> attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    private Page(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getAttributeValue(String name) {
        return attributes.get(name);
    }
}
