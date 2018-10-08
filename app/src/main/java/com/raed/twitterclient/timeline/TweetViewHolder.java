package com.raed.twitterclient.timeline;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.raed.twitterclient.R;
import com.raed.twitterclient.media.ImagesActivity;
import com.raed.twitterclient.media.VideoActivity;
import com.raed.twitterclient.model.Url;
import com.raed.twitterclient.model.User;
import com.raed.twitterclient.model.tweet.ExtendedEntities;
import com.raed.twitterclient.model.tweet.Media;
import com.raed.twitterclient.model.tweet.Media.VideoInfo.Variant;
import com.raed.twitterclient.model.tweet.Tweet;
import com.raed.twitterclient.profile.ProfileActivity;
import com.raed.twitterclient.utilis.Utils;

import java.util.Date;

class TweetViewHolder extends RecyclerView.ViewHolder {

    private final static RequestOptions CIRCULAR_REQUEST_OPTIONS = new RequestOptions().circleCrop();
    private static final String TAG = TweetViewHolder.class.getSimpleName();

    private Tweet mTweet;

    private View mMediaContainer;

    private TextView mTweetView;
    private TextView mNameView;
    private TextView mScreenNameView;
    private ImageView mUserProfileView;

    private ImageView mRetweetIconView;
    private ImageView mRetweeterProfileView;
    private TextView mRetweeterTextView;

    private ImageView[] mMediaImageViews = new ImageView[4];

    private TextView mTimelineView;

    private View mPlayIcon;

    TweetViewHolder(View itemView) {
        super(itemView);

        mMediaContainer = itemView.findViewById(R.id.media_container);

        mTweetView = itemView.findViewById(R.id.tweet_text_view);

        View.OnClickListener profileActivityClickListener = ignore -> startProfileActivity();
        mNameView = itemView.findViewById(R.id.name_text_view);
        mNameView.setOnClickListener(profileActivityClickListener);
        mScreenNameView = itemView.findViewById(R.id.screen_name_text_view);
        mScreenNameView.setOnClickListener(profileActivityClickListener);
        mUserProfileView = itemView.findViewById(R.id.user_profile_image);
        mUserProfileView.setOnClickListener(profileActivityClickListener);

        mPlayIcon = itemView.findViewById(R.id.play_icon);

        mMediaImageViews[0] = itemView.findViewById(R.id.image_view_1);
        mMediaImageViews[1] = itemView.findViewById(R.id.image_view_2);
        mMediaImageViews[2] = itemView.findViewById(R.id.image_view_3);
        mMediaImageViews[3] = itemView.findViewById(R.id.image_view_4);

        mRetweetIconView = itemView.findViewById(R.id.retweet_icon);
        mRetweeterProfileView = itemView.findViewById(R.id.retweeter_profile_image);
        mRetweeterTextView = itemView.findViewById(R.id.retweeter_name);

        mTimelineView = itemView.findViewById(R.id.time_view);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mTweet != null){
                    Date date = mTweet.getTime();
                    mTimelineView.setText(Utils.getHappenXTimeAgoString(mTweetView.getResources(), date.getTime()));
                }
                handler.postDelayed(this, 1000 * 10);
            }
        };
        handler.postDelayed(runnable, 1000 * 10);
    }

    void bindTweet(Tweet tweet){
        mTweet = tweet;
        Tweet retweetedTweet = tweet.getRetweetedTweet();

        Date date = mTweet.getTime();
        mTimelineView.setText(Utils.getHappenXTimeAgoString(mTweetView.getResources(), date.getTime()));

        int visibility = retweetedTweet == null ? View.GONE : View.VISIBLE;
        mRetweeterTextView.setVisibility(visibility);
        mRetweeterProfileView.setVisibility(visibility);
        mRetweetIconView.setVisibility(visibility);
        if (retweetedTweet != null){
            mRetweeterTextView.setText(tweet.getUser().getName());
            //todo maybe you want make sure its is immediately updated, you may want to use a place holder
            Glide.with(mRetweeterProfileView.getContext())
                    .load(tweet.getUser().getProfileImage())
                    .apply(CIRCULAR_REQUEST_OPTIONS)
                    .into(mRetweeterProfileView);
        }

        if (retweetedTweet == null)
            updateTweet(tweet);
        else
            updateTweet(retweetedTweet);

        updateExtendedEntities(tweet.getExtendedEntities());
    }

    private void startProfileActivity(){
        Context context = mNameView.getContext();
        String userID;
        if (mTweet.getRetweetedTweet() == null)
            userID = mTweet.getUser().getId();
        else
            userID = mTweet.getRetweetedTweet().getUser().getId();
        Intent intent = ProfileActivity.newIntent(context, userID);
        context.startActivity(intent);
    }

    private void updateTweet(@NonNull Tweet tweet){
        mTweetView.setText(tweet.getText());

        User user = tweet.getUser();
        mNameView.setText(user.getName());
        String text = mScreenNameView.getContext().getString(R.string.username_prefix, user.getScreenName());
        mScreenNameView.setText(text);

        Glide.with(mUserProfileView)
                .load(user.getProfileImage().replace("_normal", ""))
                .apply(CIRCULAR_REQUEST_OPTIONS)
                .into(mUserProfileView);
    }

    private void updateExtendedEntities(ExtendedEntities extendedEntities){
        if (extendedEntities == null) {
            mMediaContainer.setVisibility(View.GONE);
            return;
        }
        mMediaContainer.setVisibility(View.VISIBLE);
        for (View view : mMediaImageViews) {
            view.setVisibility(View.GONE);
            view.setOnClickListener(null);
        }
        mPlayIcon.setVisibility(View.GONE);
        Media media[] = extendedEntities.getMedia();
        if (media == null) return;
        if (media[0].getType().equals(Media.Type.VIDEO)){
            mMediaImageViews[0].setVisibility(View.VISIBLE);
            mMediaImageViews[0].setOnClickListener(view -> {
                Context context = view.getContext();
                String url = getVideoUrl();
                if (url == null)
                    Toast.makeText(context, "A problem happen while trying to play the Video", Toast.LENGTH_LONG).show();
                context.startActivity(VideoActivity.newIntent(context, url));
            });
            mPlayIcon.setVisibility(View.VISIBLE);
            Glide.with(mMediaImageViews[0])
                    .load(media[0].getUrl())
                    .into(mMediaImageViews[0]);
            return;
        }
        for (int i = 0 ; i < media.length ; i++) {
            mMediaImageViews[i].setVisibility(View.VISIBLE);
            int finalI = i;
            mMediaImageViews[i].setOnClickListener(view -> {
                String[] urls = getImageUrls();
                Context context = view.getContext();
                context.startActivity(ImagesActivity.newIntent(context, urls, finalI));
            });
            Glide.with(mMediaImageViews[i])
                    .load(media[i].getUrl())
                    .into(mMediaImageViews[i]);
        }
    }

    private String getVideoUrl(){
        Tweet tweet = mTweet.getRetweetedTweet() == null ? mTweet : mTweet.getRetweetedTweet();
        Variant variants[] = tweet.getExtendedEntities()
                .getMedia()[0]
                .getVideoInfo()
                .getVariants();
        Variant variantWithMaxBitrate = null;
        for (Variant variant : variants) {
            if (variant.getBitrate() == null)
                continue;
            if (variantWithMaxBitrate == null)
                variantWithMaxBitrate = variant;
            else if (variantWithMaxBitrate.getBitrate() < variant.getBitrate())
                variantWithMaxBitrate = variant;
        }
        return variantWithMaxBitrate.getUrl();
    }

    private String[] getImageUrls(){
        Tweet tweet = mTweet.getRetweetedTweet() == null ? mTweet : mTweet.getRetweetedTweet();
        Media media[] = tweet.getExtendedEntities().getMedia();
        String urls[] = new String[media.length];
        for (int i = 0 ; i < media.length ; i++)
            urls[i] = media[i].getUrl();
        return urls;
    }

}
