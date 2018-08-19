package com.raed.twitterclient.userlist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.raed.twitterclient.R;
import com.raed.twitterclient.model.User;
import com.raed.twitterclient.profile.ProfileActivity;

class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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
        mScreenNameView.setText(mUser.getScreenName());
        Glide.with(mProfileImage)
                .load(mUser.getProfileImage())
                .into(mProfileImage);
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        Intent intent = ProfileActivity.newIntent(context, mUser.getId());
        context.startActivity(intent);
    }
}