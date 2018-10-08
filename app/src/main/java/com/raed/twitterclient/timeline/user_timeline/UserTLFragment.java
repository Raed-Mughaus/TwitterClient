package com.raed.twitterclient.timeline.user_timeline;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raed.twitterclient.R;
import com.raed.twitterclient.timeline.TLView;


public class UserTLFragment extends Fragment{

    private static final String TAG = UserTLFragment.class.getSimpleName();
    private static final String KEY_USER_ID = "user_id";

    private UserTweetViewModel mViewModel;

    public static Fragment newInstance(String userID) {
        Bundle args = new Bundle();
        args.putString(KEY_USER_ID, userID);
        UserTLFragment fragment = new UserTLFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userID = getArguments().getString(KEY_USER_ID);
        mViewModel = ViewModelProviders.of(this, new UserTLViewModelFactory(userID)).get(UserTweetViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_timeline, container, false);
        refreshLayout.setEnabled(true);

        RecyclerView recyclerView = refreshLayout.findViewById(R.id.recycler_view);

        new TLView(this, mViewModel, refreshLayout, recyclerView);

        return refreshLayout;
    }


}
