package com.raed.twitterclient.io;


import com.raed.twitterclient.MyApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * This cache stores a set of strings.
 * Each cache file is named with an ID that is provided when adding a string through {@link StringCache#addString(long, String)}
 * The files are sorted according to the IDs, so when you call {@link StringCache#getStringNewerThan(long)}
 * you will get the nearest string newer than the id you passed as an argument. The same thing goes
 * for {@link StringCache#getStringOlderThan(long)}.
 */

//todo you need to test this file
public class StringCache {

    private final static String CACHE_FOLDER = "tweets";

    private File mCacheFolder;

    public StringCache() {
        mCacheFolder = new File(MyApplication.getApp().getCacheDir(), CACHE_FOLDER);
        mCacheFolder.mkdirs();//in case it is not created
    }

    public void addString(long id, String string){
        File file = new File(mCacheFolder, id + "");
        new StringFile(file).write(string);
    }

    /**
     * @return null if there is no newer string
     */
    public String getStringNewerThan(long id){
        Long nearestID = Observable
                .fromArray( getFileIds() )
                .filter(fileID -> fileID > id) //we are only interested in the newer tweets
                .reduce((minID, fileID) -> minID < fileID ? minID : fileID) // the min is the nearest
                .blockingGet();
        if (nearestID == null)
            return null;
        File file = new File(mCacheFolder, nearestID + "");
        return new StringFile(file).read();
    }

    /**
     * @return null if there is no older string
     */
    public String getStringOlderThan(long id){
        Long nearestID = Observable
                .fromArray( getFileIds() )
                .filter(fileID -> fileID < id)  //we are only interested in the older tweets
                .reduce((maxID, fileID) -> maxID > fileID ? maxID : fileID) // the max is the nearest
                .blockingGet();
        if (nearestID == null)
            return null;
        File file = new File(mCacheFolder, nearestID + "");
        return new StringFile(file).read();
    }

    public void clearCache(){
        //todo consider moving file to be faster
        for (File file : mCacheFolder.listFiles())
            file.delete();
    }

    public Long[] getFileIds() {
        String fileNames[] = mCacheFolder.list();
        Long[] ids = new Long[fileNames.length];
        for (int i = 0; i < ids.length; i++)
            ids[i] = Long.parseLong(fileNames[i]);
        return ids;
    }

    public Transaction beginTransaction(){
        return new Transaction(this);
    }

    private boolean hasCacheWithId(long id){
        return new File(mCacheFolder, id + "").exists();
    }

    public static class Transaction{

        private StringCache mStringCache;
        private List<CachePair> mPairs = new ArrayList<>();

        public Transaction(StringCache StringCache) {
            mStringCache = StringCache;
        }

        public void addTweets(long maxId, String tweets){
            mPairs.add(new CachePair(maxId, tweets));
        }

        public void commit(){
            for (CachePair cachePair : mPairs)
                mStringCache.addString(cachePair.id, cachePair.json);
        }
    }

    private static class CachePair{
        long id;
        String json;

        CachePair(long id, String json) {
            this.id = id;
            this.json = json;
        }
    }
}
