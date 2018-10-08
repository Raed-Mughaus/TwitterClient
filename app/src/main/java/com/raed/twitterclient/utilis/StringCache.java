package com.raed.twitterclient.utilis;


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

public class StringCache {

    private File mCacheFolder;

    public StringCache(File cacheFolder) {
        mCacheFolder = cacheFolder;
        mCacheFolder.mkdirs();//in case it is not created
    }

    public void addString(long id, String string){
        File file = new File(mCacheFolder, id + "");
        IOUtils.writeString(file, string);
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
        return IOUtils.readString(file);
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
        return IOUtils.readString(file);
    }

    public String getNewestString() {
        Long maxID = Observable
                .fromArray( getFileIds() )
                .reduce((max, fileID) -> max > fileID ? max : fileID)
                .blockingGet();
        if (maxID == null)
            return null;
        File file = new File(mCacheFolder, maxID + "");
        return IOUtils.readString(file);
    }

    public void clearCache(){
        //todo to be faster consider moving files to trash folder and then deleting them using another thread
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
        private List<CacheString> mCacheStrings = new ArrayList<>();

        Transaction(StringCache StringCache) {
            mStringCache = StringCache;
        }

        public void addString(long maxId, String tweets){
            mCacheStrings.add(new CacheString(maxId, tweets));
        }

        public void commit(){
            for (CacheString cacheString : mCacheStrings)
                mStringCache.addString(cacheString.id, cacheString.string);
        }
    }

    private static class CacheString {
        long id;
        String string;

        CacheString(long id, String string) {
            this.id = id;
            this.string = string;
        }
    }
}
