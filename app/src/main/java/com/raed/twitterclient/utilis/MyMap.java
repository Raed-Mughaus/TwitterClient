package com.raed.twitterclient.utilis;

import java.util.HashMap;

public class MyMap<T, K> {

    private HashMap<T, K> mHashMap = new HashMap<>();

    public static <T, K> MyMap<T, K> create() {
        return new MyMap<T, K>();
    }

    public HashMap<T, K> put(T key, K value){
        mHashMap.put(key, value);
        return mHashMap;
    }

}
