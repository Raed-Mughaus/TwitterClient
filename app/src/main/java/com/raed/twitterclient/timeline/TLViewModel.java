package com.raed.twitterclient.timeline;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.arch.paging.PagedList.Config;

import com.raed.twitterclient.model.tweet.Tweet;
import com.raed.twitterclient.timeline.data.TLDataSource;
import com.raed.twitterclient.timeline.data.TLPreferences;

public class TLViewModel extends AndroidViewModel {

    private LiveData<PagedList<Tweet>> mListLiveData;
    private MutableLiveData<Exception> mErrorLiveData = new MutableLiveData<>();
    private MutableLiveData<>;
    private TLPreferences mTLPreferences;

    public TLViewModel(Application application) {
        super(application);

        mTLPreferences = new TLPreferences();
        TLDataSource.Factory dataSrcFactory =
                new TLDataSource.Factory(getInitialLoadKey(), mErrorLiveData::postValue, eventListener);
        Config config = new Config.Builder()
                .setPageSize(100)//I think this is safer
                .build();
        mListLiveData = new LivePagedListBuilder<>(dataSrcFactory, config)
                .build();
    }

    //todo you need to merge cache

    public LiveData<PagedList<Tweet>> getPagedList() {
        return mListLiveData;
    }

    public void saveScrolledToID(long tweetID, int offset){
        mTLPreferences.saveScrollPosition(tweetID, offset);
    }

    public Long getInitialLoadKey() {
        Long initialID = mTLPreferences.getScrolledToId(0);//assuming twitter does not have a tweet with id 0
        if (initialID == 0)
            initialID = null;
        return initialID;
    }

    public int getOffset(){
        return mTLPreferences.getOffset();
    }

    public MutableLiveData<Exception> getErrorLiveData() {
        return mErrorLiveData;
    }
}
