package com.raed.twitterclient.filter;

import com.raed.twitterclient.model.tweet.Tweet;

import java.util.ArrayList;
import java.util.List;

public class Filter {

    private List<Condition> mConditions = new ArrayList<>();

    public void setConditions(List<Condition> conditions) {
        mConditions = conditions;
    }

    /**
     * @return true if a tweet pass a list of conditions
     */
    public boolean pass(Tweet tweet){
        for (Condition condition : mConditions) {
            if (!condition.check(tweet))
                return false;
        }
        return true;
    }
}
