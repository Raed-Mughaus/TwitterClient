package com.raed.twitterclient.timeline;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.raed.twitterclient.R;
import com.raed.twitterclient.TLViewModel;
import com.raed.twitterclient.Tweet;
import com.raed.twitterclient.profile.User;
import com.raed.twitterclient.utilis.Crashlytics;


import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class TLFragment extends Fragment{

    private static final String TAG = TLFragment.class.getSimpleName();

    private TLViewModel mViewModel;
    private CompositeDisposable mDisposables = new CompositeDisposable();

    public static Fragment newInstance() {
        return new TLFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(HomeTLViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.view_recyler_view, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Disposable disposable = mViewModel.getTweets()
                .observeOn(AndroidSchedulers.mainThread())
                .map(TweetAdapter::new)
                .subscribe(
                        recyclerView::setAdapter,
                        throwable -> Crashlytics.logException(new Exception(throwable))
                );
        mDisposables.add(disposable);
        return recyclerView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposables.dispose();
    }

    private class TweetViewHolder extends ViewHolder{

        private TextView mTweetView;
        private TextView mNameView;
        private TextView mScreenNameView;
        private ImageView mUserProfileView;

        TweetViewHolder(View itemView) {
            super(itemView);

            mTweetView = itemView.findViewById(R.id.tweet_text_view);
            mNameView = itemView.findViewById(R.id.name_text_view);
            mScreenNameView = itemView.findViewById(R.id.screen_name_text_view);
            mUserProfileView = itemView.findViewById(R.id.user_profile_image);
        }

        void bindTweet(Tweet tweet){
            mTweetView.setText(tweet.getText());

            User user = tweet.getUser();
            mNameView.setText(user.getName());
            mScreenNameView.setText(getString(R.string.username_prefix, user.getScreenName()));

            Glide.with(TLFragment.this)
                    .load(user.getProfileImage().replace("_normal", ""))
                    .into(mUserProfileView);

        }
    }

    private class TweetAdapter extends RecyclerView.Adapter<TweetViewHolder>{

        private List<Tweet> mTweets;

        TweetAdapter(List<Tweet> tweets) {
            mTweets = tweets;
        }

        @NonNull
        @Override
        public TweetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.item_tweet, parent, false);
            return new TweetViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TweetViewHolder holder, int position) {
            holder.bindTweet(mTweets.get(position));
        }

        @Override
        public int getItemCount() {
            return 1000;
        }
    }
}
