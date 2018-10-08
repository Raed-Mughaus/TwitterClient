package com.raed.twitterclient.timeline.home_timeline;

import com.raed.twitterclient.auth.authorized_user.AuthUser;
import com.raed.twitterclient.auth.authorized_user.CurrentAuthUser;
import com.raed.twitterclient.timeline.TweetViewModel;
import com.raed.twitterclient.timeline.home_timeline.data.HomeTLDataSource;
import com.raed.twitterclient.timeline.home_timeline.data.HomeTLPreferences;
import com.raed.twitterclient.timeline.home_timeline.data.HomeTLRepository;
import com.raed.twitterclient.timeline.user_timeline.UserTweetViewModel;

public class HomeTLViewModel extends TweetViewModel {

    private CurrentAuthUser mCurrentAuthUser = CurrentAuthUser.getInstance();

    private static final String TAG = UserTweetViewModel.class.getSimpleName();

    public HomeTLViewModel() {
        super(createDataSrcFactory());
    }

    boolean shouldStartAuthActivity(){
        return mCurrentAuthUser.get() == null;
    }

    String getCurrentUserID() {
        return mCurrentAuthUser.get().getUserId();
    }

    void signOut() {
        mCurrentAuthUser.remove();
    }

    void saveScrolledToID(long tweetID){
        AuthUser authUser = getCurrentUser();
        HomeTLPreferences preferences = new HomeTLPreferences(authUser.getUserId());
        preferences.saveScrollPosition(tweetID);
    }

    private static HomeTLDataSource.Factory createDataSrcFactory(){
       if (getCurrentUser() == null)
           return null;
        Long initialLoadKey = getInitialLoadKey();
        HomeTLRepository homeTLRepository = getRepository();
        return new HomeTLDataSource.Factory(homeTLRepository, initialLoadKey);
    }

    public static Long getInitialLoadKey() {
        AuthUser authUser = getCurrentUser();
        HomeTLPreferences preferences = new HomeTLPreferences(authUser.getUserId());
        Long initialID = preferences.getScrolledToId(0);//assuming twitter does not have a tweet with id 0
        if (initialID == 0)
            initialID = null;
        return initialID;
    }

    private static AuthUser getCurrentUser() {
        return CurrentAuthUser.getInstance().get();
    }

    private static HomeTLRepository getRepository() {
        AuthUser user = getCurrentUser();
        return new HomeTLRepository(user);
    }

}
