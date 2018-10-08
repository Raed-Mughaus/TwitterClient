package com.raed.twitterclient.user_list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.raed.twitterclient.model.User;

public class UserListViewModel extends ViewModel {

    private static final int USER_LIST_PAGE_SIZE = 200;

    private LiveData<PagedList<User>> mUserListLiveData;

    public void initialize(String userID, String userRelation) {
        if (mUserListLiveData != null) // in case it is already initialized
            return;

        //todo what about prefetch distance
        PagedList.Config config = new PagedList.Config
                .Builder()
                .setEnablePlaceholders(false)
                .setPageSize(USER_LIST_PAGE_SIZE)
                //todo .setPrefetchDistance()
                .build();
        mUserListLiveData =
                new LivePagedListBuilder<>(new UserListDataSource.Factory(userRelation, userID), config)
                        .build();
    }

    LiveData<PagedList<User>> getUsersList(){
        return mUserListLiveData;
    }

}
