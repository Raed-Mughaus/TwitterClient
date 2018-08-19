package com.raed.twitterclient.filter;

import java.util.ArrayList;
import java.util.List;

public class DefaultCondtionsRepo {

    private static List<Condition> sDefaultConditions = new ArrayList<Condition>(){
        {
            //todo create and add the default conditions
        }
    };

    public static List<Condition> getDefaultConditions(){
        return sDefaultConditions;
    }

}
