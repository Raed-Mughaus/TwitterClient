package com.raed.twitterclient.timeline.user_timeline;

import com.raed.twitterclient.timeline.TweetViewModel;


public class UserTweetViewModel extends TweetViewModel {

    private static final String TAG = UserTweetViewModel.class.getSimpleName();

    public UserTweetViewModel(String userID) {
        super(new UserTLDataSource.Factory(userID));
    }
}
