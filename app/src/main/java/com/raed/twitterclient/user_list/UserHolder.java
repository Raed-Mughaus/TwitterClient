package com.raed.twitterclient.user_list;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.raed.twitterclient.R;
import com.raed.twitterclient.model.User;
import com.raed.twitterclient.profile.ProfileActivity;

class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private final static RequestOptions CIRCULAR_REQUEST_OPTIONS = new RequestOptions().circleCrop();

    private User mUser;

    private TextView mNameView;
    private TextView mScreenNameView;
    private ImageView mProfileImage;

    UserHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mNameView = itemView.findViewById(R.id.name);
        mScreenNameView = itemView.findViewById(R.id.screen_name);
        mProfileImage = itemView.findViewById(R.id.user_profile_image);
    }

    void bindUser(User user){
        mUser = user;
        mNameView.setText(mUser.getName());
        mScreenNameView.setText(mScreenNameView.getContext().getString(R.string.username_prefix, mUser.getScreenName()));
        Glide.with(mProfileImage)
                .load(mUser.getProfileImage().replace("_normal", ""))
                .apply(CIRCULAR_REQUEST_OPTIONS)
                .into(mProfileImage);
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        Intent intent = ProfileActivity.newIntent(context, mUser.getId());
        context.startActivity(intent);
    }
}