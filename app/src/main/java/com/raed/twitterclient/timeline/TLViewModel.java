package com.raed.twitterclient.timeline;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.arch.paging.PagedList.Config;

import com.raed.twitterclient.model.tweet.Tweet;
import com.raed.twitterclient.timeline.data.TLDataSource;

public class TLViewModel extends AndroidViewModel {

    private LiveData<PagedList<Tweet>> mListLiveData;
    private TLPreferences mTLPreferences;

    public TLViewModel(Application application) {
        super(application);

        mTLPreferences = new TLPreferences();
        Long id = mTLPreferences.getScrolledToId(0);//assuming twitter does not have a tweet with id 0
        if (id == 0)
            id = null;
        TLDataSource.Factory dataSrcFactory = new TLDataSource.Factory(id);
        Config config = new Config.Builder()
                .setPageSize(100)//I think this is safer
                .build();
        mListLiveData = new LivePagedListBuilder<>(dataSrcFactory, config)
                .build();
    }

    public LiveData<PagedList<Tweet>> getPagedList() {
        return mListLiveData;
    }

    public void saveScrolledToID(long tweetID){
        mTLPreferences.saveScrollPosition(tweetID);
    }

}
