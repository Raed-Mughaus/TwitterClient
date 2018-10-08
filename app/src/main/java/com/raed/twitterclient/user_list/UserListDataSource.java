package com.raed.twitterclient.user_list;

import android.arch.paging.DataSource;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.raed.twitterclient.model.User;
import com.raed.twitterclient.api.RetrofitServices;
import com.raed.twitterclient.api.UserService;

class UserListDataSource extends PageKeyedDataSource<String, User>{

    private static final String TAG = UserListDataSource.class.getSimpleName();

    private final UserService mUserService = RetrofitServices.getInstance().getUserService();
    private String mUserRelation;
    private String mUserId;

    public UserListDataSource(String userRelation, String userId) {
        mUserRelation = userRelation;
        mUserId = userId;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull LoadInitialCallback<String, User> callback) {
        Log.d(TAG, "loadInitial: params.requestedLoadSize = " + params.requestedLoadSize);
        //todo: param.requestLoadSize in being doubled to 600, so instead of 200 it is 600, look at the config
        UserList userList = mUserService.listUsers(mUserRelation, params.requestedLoadSize, mUserId).blockingGet();
        String nextCursor = userList.getNextCursor();
        if (nextCursor.equals("0"))//this means we have requested the last page.
            nextCursor = null;//pass null key to callback.onResult since there are no more pages to load
        callback.onResult(userList.getUsers(), null, nextCursor);
    }

    //todo use Crashlytics to know if loadBefore would ever be called,
    //todo take your time to write your code
    //todo Download the sample and see what they are doing in there

    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, User> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, User> callback) {
        Log.d(TAG, "loadBefore: params.requestedLoadSize = " + params.requestedLoadSize);
        UserList userList = mUserService.listUsers(mUserRelation, params.requestedLoadSize, mUserId, params.key).blockingGet();
        String nextCursor = userList.getNextCursor();
        if (nextCursor.equals("0"))//this means we have requested the last page.
            nextCursor = null;//pass null key to callback.onResult since there are no more pages to load

        callback.onResult(userList.getUsers(), nextCursor);
    }

    static class Factory extends DataSource.Factory<String, User>{

        private String mUserRelation;
        private String mUserId;

        Factory(String userRelation, String userId) {
            mUserRelation = userRelation;
            mUserId = userId;
        }

        @Override
        public DataSource<String, User> create() {
            return new UserListDataSource(mUserRelation, mUserId);
        }
    }
}
