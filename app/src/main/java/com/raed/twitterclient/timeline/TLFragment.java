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
import com.bumptech.glide.request.RequestOptions;
import com.raed.twitterclient.R;
import com.raed.twitterclient.TLViewModel;
import com.raed.twitterclient.data.ExtendedEntities;
import com.raed.twitterclient.data.Media;
import com.raed.twitterclient.data.Tweet;
import com.raed.twitterclient.profile.User;
import com.raed.twitterclient.utilis.Crashlytics;
import com.raed.twitterclient.utilis.Utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class TLFragment extends Fragment{

    private static final String TAG = TLFragment.class.getSimpleName();

    private final RequestOptions mCircularRequestOptions = new RequestOptions().circleCrop();

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

        private View mMediaContainer;

        private TextView mTweetView;
        private TextView mNameView;
        private TextView mScreenNameView;
        private ImageView mUserProfileView;

        private ImageView mRetweetIconView;
        private ImageView mRetweeterProfileView;
        private TextView mRetweeterTextView;

        private ImageView[] mMediaImageViews = new ImageView[4];

        private DateFormat mDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.getDefault());
        private TextView mTimelineView;

        TweetViewHolder(View itemView) {
            super(itemView);

            mMediaContainer = itemView.findViewById(R.id.media_container);

            mTweetView = itemView.findViewById(R.id.tweet_text_view);
            mNameView = itemView.findViewById(R.id.name_text_view);
            mScreenNameView = itemView.findViewById(R.id.screen_name_text_view);
            mUserProfileView = itemView.findViewById(R.id.user_profile_image);

            mMediaImageViews[0] = itemView.findViewById(R.id.image_view_1);
            mMediaImageViews[1] = itemView.findViewById(R.id.image_view_2);
            mMediaImageViews[2] = itemView.findViewById(R.id.image_view_3);
            mMediaImageViews[3] = itemView.findViewById(R.id.image_view_4);

            mRetweetIconView = itemView.findViewById(R.id.retweet_icon);
            mRetweeterProfileView = itemView.findViewById(R.id.retweeter_profile_image);
            mRetweeterTextView = itemView.findViewById(R.id.retweeter_name);

            mTimelineView = itemView.findViewById(R.id.time_view);
        }

        void bindTweet(Tweet tweet){
            Tweet retweetedTweet = tweet.getRetweetedTweet();

            try {
                Date date = mDateFormat.parse(tweet.getTime());
                mTimelineView.setText(Utils.getHappenXTimeAgoString(getResources(), date.getTime()));
            } catch (ParseException e) {
                Crashlytics.logException(e);
            }

            int visibility = retweetedTweet == null ? View.GONE : View.VISIBLE;
            mRetweeterTextView.setVisibility(visibility);
            mRetweeterProfileView.setVisibility(visibility);
            mRetweetIconView.setVisibility(visibility);
            if (retweetedTweet != null){
                mRetweeterTextView.setText(tweet.getUser().getName());

                //todo maybe you want make sure its is immediately updated, you may want to use a place holder
                Glide.with(TLFragment.this)
                        .load(tweet.getUser().getProfileImage())
                        .apply(mCircularRequestOptions)
                        .into(mRetweeterProfileView);

            }

            updateTweet(retweetedTweet == null ? tweet : retweetedTweet);
            updateExtendedEntities(tweet.getExtendedEntities());
        }

        private void updateTweet(@NonNull Tweet tweet){
            mTweetView.setText(tweet.getText());

            User user = tweet.getUser();
            mNameView.setText(user.getName());
            mScreenNameView.setText(getString(R.string.username_prefix, user.getScreenName()));

            Glide.with(TLFragment.this)
                    .load(user.getProfileImage().replace("_normal", ""))
                    .apply(mCircularRequestOptions)
                    .into(mUserProfileView);
        }

        private void updateExtendedEntities(ExtendedEntities extendedEntities){
            if (extendedEntities == null) {
                mMediaContainer.setVisibility(View.GONE);
                return;
            }
            for (View view : mMediaImageViews) view.setVisibility(View.GONE);
            Media media[] = extendedEntities.getMedia();
            if (media == null) return;
            if (media[0].getType().equals(Media.Type.VIDEO))
                return;
            for (int i = 0 ; i < media.length ; i++) {
                mMediaImageViews[i].setVisibility(View.VISIBLE);
                Glide.with(TLFragment.this)
                        .load(media[i].getUrl())
                        .apply(new RequestOptions().centerCrop())
                        .into(mMediaImageViews[i]);
            }
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
