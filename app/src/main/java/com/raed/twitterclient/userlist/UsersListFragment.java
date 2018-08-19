package com.raed.twitterclient.userlist;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raed.twitterclient.retrofitservices.UserService.UserRelation;

public class UsersListFragment extends Fragment{

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_RELATION_TYPE = "relation";

    private UserListViewModel mViewModel;

    /**
     * @param usersRelation {@link UserRelation}
     */
    public static Fragment newInstance(String userID, String usersRelation){
        if (!usersRelation.equals(UserRelation.FOLLOWERS) && !usersRelation.equals(UserRelation.FOLLOWING))
            throw new IllegalArgumentException("Unknown user relation relation, have a look at class UserService.UserRelation");
        Bundle args = new Bundle();
        args.putString(KEY_USER_ID, userID);
        args.putString(KEY_USER_RELATION_TYPE, usersRelation);
        Fragment fragment = new UsersListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(UserListViewModel.class);

        Bundle args = getArguments();
        String key = args.getString(KEY_USER_ID);
        String relation = args.getString(KEY_USER_RELATION_TYPE);
        mViewModel.initialize(key, relation);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        UserAdapter userAdapter = new UserAdapter();
        mViewModel.getUsersList().observe(this, userAdapter::submitList);

        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return recyclerView;
    }

}
