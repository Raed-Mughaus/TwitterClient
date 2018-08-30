package com.raed.twitterclient.timeline;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.arch.paging.PagedList.Config;
import android.util.Log;

import com.raed.twitterclient.authusers.AuthUser;
import com.raed.twitterclient.authusers.AuthUsersRepository;
import com.raed.twitterclient.model.tweet.Tweet;
import com.raed.twitterclient.timeline.data.TLDataSource;
import com.raed.twitterclient.timeline.data.TLPreferences;
import com.raed.twitterclient.timeline.data.TweetsRepository;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public abstract class TLViewModel extends AndroidViewModel {

    private static final String TAG = TLViewModel.class.getSimpleName();

    private LiveData<PagedList<Tweet>> mTweetsPagedList;

    private TLPreferences mTLPreferences;

    private MutableLiveData<TLEvent> mInitialTweetsTLEvent = new MutableLiveData<>();
    private MutableLiveData<TLEvent> mNewTweetsTLEvent = new MutableLiveData<>();
    private MutableLiveData<TLEvent> mOldTweetsTLEvent = new MutableLiveData<>();
    private MutableLiveData<Exception> mError = new MutableLiveData<>();

    public TLViewModel(Application application) {
        super(application);
        AuthUser authUser = getCurrentUser();
        if (authUser == null)
            return;

        mTLPreferences = new TLPreferences(authUser.getUserId());

        TweetsRepository tweetsRepository = getRepository(authUser);
        TLDataSource.Factory dataSrcFactory = new TLDataSource.Factory(
                tweetsRepository, getInitialLoadKey(), mInitialTweetsTLEvent,
                mNewTweetsTLEvent, mOldTweetsTLEvent, mError);
        Config config = new Config.Builder()
                .setPageSize(100)//I think this is safer//todo is it ok
                .build();
        mTweetsPagedList = new LivePagedListBuilder<>(dataSrcFactory, config)
                .build();
    }

    public LiveData<PagedList<Tweet>> getPagedList() {
        return mTweetsPagedList;
    }

    public void saveScrolledToID(long tweetID){
        mTLPreferences.saveScrollPosition(tweetID);
    }

    public Long getInitialLoadKey() {
        Long initialID = mTLPreferences.getScrolledToId(0);//assuming twitter does not have a tweet with id 0
        if (initialID == 0)
            initialID = null;
        return initialID;
    }

    public MutableLiveData<Exception> getError() {
        return mError;
    }

    public MutableLiveData<TLEvent> getInitialTweetsTLEvent() {
        return mInitialTweetsTLEvent;
    }

    public MutableLiveData<TLEvent> getNewTweetsTLEvent() {
        return mNewTweetsTLEvent;
    }

    public MutableLiveData<TLEvent> getOldTweetsTLEvent() {
        return mOldTweetsTLEvent;
    }

    public void reloadNewTweets() {
        Log.d(TAG, "reloadNewTweets: ");
        if (mTweetsPagedList == null)
            return;
        Observable
                .create(emitter -> {
                    getCurrentDataSource().retryToLoadNewTweets();
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void reloadOldTweets(){
        Log.d(TAG, "reloadOldTweets: ");
        if (mTweetsPagedList == null)
            return;
        Observable
                .create(emitter -> {
                    getCurrentDataSource().retryToLoadOldTweets();
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void reloadInitialTweets(){
        Log.d(TAG, "reloadInitialTweets: ");
        if (mTweetsPagedList == null)
            return;
        Observable
                .create(emitter -> {
                    getCurrentDataSource().retryToLoadInitialTweets();
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private TLDataSource getCurrentDataSource(){
        return (TLDataSource) mTweetsPagedList.getValue().getDataSource();
    }

    public AuthUser getCurrentUser() {
        return AuthUsersRepository.getInstance().getCurrentUser();
    }

    protected abstract TweetsRepository getRepository(AuthUser user);
}
