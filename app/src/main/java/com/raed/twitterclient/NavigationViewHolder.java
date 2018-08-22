package com.raed.twitterclient;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.raed.twitterclient.auth.AuthActivity;
import com.raed.twitterclient.model.User;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Since we do not want MainActivity to be too complex, this class was created to help the
 *  MainActivity.
 * This class is responsible for:
 *  * The navigationView header.
 *  * Displaying the current user & the list of authorized users.
 *  * Communicating with the ViewModel to add, remove or change the current user.
 *
 * The MainView is responsible for handling navigationView item click.
 */
public class NavigationViewHolder implements LifecycleObserver{

    //TODO consider caching the images yourself in user repository
    //todo clear the cache when removing a user

    private ImageView mBannerImageView;
    private ImageView mProfileImageView;
    private TextView mNameView;
    private TextView mScreenNameView;

    private LinearLayout mAccountsContainer;

    private NavigationViewModel mViewModel;

    private CompositeDisposable mDisposables = new CompositeDisposable();

    private NavigationView mNavigationView;

    NavigationViewHolder(NavigationView navigationView, NavigationViewModel navigationViewModel) {
        mNavigationView = navigationView;
        View parent = mNavigationView.getHeaderView(0);
        mBannerImageView = parent.findViewById(R.id.banner_image_view);
        mProfileImageView = parent.findViewById(R.id.profile_image_view);
        mNameView = parent.findViewById(R.id.name);
        mScreenNameView = parent.findViewById(R.id.screen_name);

        parent.findViewById(R.id.add_account).setOnClickListener(
                ignore -> {
                    Context context = mNavigationView.getContext();
                    Intent intent = AuthActivity.newIntent(context);
                    context.startActivity(intent);
                });

        mViewModel = navigationViewModel;
        mDisposables.add(
                mViewModel.getCurrentUser()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::updateUI, throwable -> throwable.printStackTrace())
        );

        mAccountsContainer = parent.findViewById(R.id.accounts_container);

        parent.findViewById(R.id.expand).setOnClickListener(
                view -> {
                    ImageView expandButton = (ImageView) view;
                    if (mAccountsContainer.getVisibility() == View.VISIBLE){
                        mAccountsContainer.setVisibility(View.GONE);
                        expandButton.setImageResource(R.drawable.ic_expand_more_24);
                        mNavigationView.inflateMenu(R.menu.activity_main);
                    }else {
                        mAccountsContainer.setVisibility(View.VISIBLE);
                        mDisposables.add(
                                mViewModel
                                        .getAuthUsers()
                                        .subscribe(this::updateUsersAccountContainer)
                        );
                        expandButton.setImageResource(R.drawable.ic_expand_less_24);
                        mNavigationView.getMenu().clear();
                    }
                }
        );
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    public void disposeObservables(){
        mDisposables.dispose();
    }

    private void updateUI(User user) {
        Glide.with(mBannerImageView)
                .load(user.getBannerImage())
                .into(mBannerImageView);
        Glide.with(mProfileImageView)
                .load(user.getProfileImage().replace("_normal", ""))
                .into(mProfileImageView);
        mNameView.setText(user.getName());
        mScreenNameView.setText(user.getScreenName());
    }

    private void updateUsersAccountContainer(List<User> users){
        mAccountsContainer.removeAllViews();
        Context context = mAccountsContainer.getContext();
        for (User user : users){
            View userView = LayoutInflater.from(context)
                    .inflate(R.layout.item_user, mAccountsContainer, false);
            mAccountsContainer.addView(userView);
            ((TextView) userView.findViewById(R.id.name)).setText(user.getName());
            String screenName = context.getString(R.string.username_prefix, user.getScreenName());
            ((TextView) userView.findViewById(R.id.name)).setText(screenName);
            ImageView profileImageView = userView.findViewById(R.id.user_profile_image);
            Glide.with(profileImageView)
                    .load(user.getProfileImage().replace("_normal", ""))
                    .into(profileImageView);
        }
    }

}
