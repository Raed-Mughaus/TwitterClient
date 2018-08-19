package com.raed.twitterclient.userlist;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raed.twitterclient.R;
import com.raed.twitterclient.model.User;

class UserAdapter extends PagedListAdapter<User, UserHolder> {

    public UserAdapter() {
        super(sDiffCallback);
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_user, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        holder.bindUser(getItem(position));
    }

    private static final DiffUtil.ItemCallback<User> sDiffCallback = new DiffUtil.ItemCallback<User>() {
        @Override
        public boolean areItemsTheSame(User oldItem, User newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(User oldItem, User newItem) {
            //check only these, since we are only displaying them
            return oldItem.getScreenName().equals(newItem.getScreenName()) &&
                    oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getUrl().equals(newItem.getUrl());
        }
    };
}
