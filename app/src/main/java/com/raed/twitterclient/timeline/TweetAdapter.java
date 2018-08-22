package com.raed.twitterclient.timeline;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raed.twitterclient.R;
import com.raed.twitterclient.model.tweet.Tweet;

class TweetAdapter extends PagedListAdapter<Tweet, TweetViewHolder> {

    TweetAdapter() {
        super(new MyDiffCallback());
    }

    @NonNull
    @Override
    public TweetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_tweet, parent, false);
        return new TweetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TweetViewHolder holder, int position) {
        holder.bindTweet(getItem(position));
    }

    @Nullable
    @Override
    //make it public
    public Tweet getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    private static class MyDiffCallback extends DiffUtil.ItemCallback<Tweet> {
        @Override
        public boolean areItemsTheSame(Tweet oldItem, Tweet newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(Tweet oldItem, Tweet newItem) {
            return oldItem.getId() == newItem.getId();
        }
    }
}