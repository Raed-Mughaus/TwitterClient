package com.raed.twitterclient;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.raed.twitterclient.auth.AuthActivity;
import com.raed.twitterclient.model.User;
import com.raed.twitterclient.utilis.Utils;

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

    private NavigationViewModel mViewModel;

    private CompositeDisposable mDisposables = new CompositeDisposable();

    private ImageView mBannerImageView;
    private ImageView mProfileImageView;
    private TextView mNameView;
    private TextView mScreenNameView;
    private LinearLayout mAccountsContainer;
    private NavigationView mNavigationView;

    NavigationViewHolder(NavigationView navigationView, NavigationViewModel navigationViewModel) {
        mNavigationView = navigationView;
        View header = mNavigationView.getHeaderView(0);
        mBannerImageView = header.findViewById(R.id.banner_image_view);
        mProfileImageView = header.findViewById(R.id.profile_image_view);
        mNameView = header.findViewById(R.id.name);
        mScreenNameView = header.findViewById(R.id.screen_name);

        header.findViewById(R.id.add_account).setOnClickListener(
                ignore -> {
                    Context context = mNavigationView.getContext();
                    Intent intent = AuthActivity.newIntent(context);
                    context.startActivity(intent);
                });

        mViewModel = navigationViewModel;
        mDisposables.add(
                mViewModel.getCurrentUser()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::updateUI)
        );

        mAccountsContainer = header.findViewById(R.id.accounts_container);

        header.findViewById(R.id.expand)
                .setOnClickListener(this::onExpandClicked);
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
        int count =  mAccountsContainer.getChildCount() - 1;
        mAccountsContainer.removeViews(0, count);
        Context context = mAccountsContainer.getContext();
        for (int i = users.size() - 1 ; i >= 0 ; i--){
            User user = users.get(i);
            View userView = LayoutInflater.from(context)
                    .inflate(R.layout.item_account, mAccountsContainer, false);
            userView.setOnClickListener(this::onItemClicked);
            userView.setTag(user);
            mAccountsContainer.addView(userView, 0);
            ((TextView) userView.findViewById(R.id.name)).setText(user.getName());
            String screenName = context.getString(R.string.username_prefix, user.getScreenName());
            ((TextView) userView.findViewById(R.id.name)).setText(screenName);
            userView.findViewById(R.id.remove_account)
                    .setOnClickListener(this::onRemoveAccountClicked);
            ImageView profileImageView = userView.findViewById(R.id.user_profile_image);
            Glide.with(profileImageView)
                    .load(user.getProfileImage().replace("_normal", ""))
                    .into(profileImageView);
        }
    }

    private void onItemClicked(View view){
        User user = (User) view.getTag();
        mViewModel.setCurrentUser(user);
        Utils.restartApp(view.getContext());
    }

    private void onRemoveAccountClicked(View v){
        User user = (User) v.getTag();
        mViewModel.removeUserAccount(user);
        mAccountsContainer.removeView((View) v.getParent());
        mDisposables.add(
                mViewModel.getCurrentUser()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::updateUI)
        );
    }

    private void onExpandClicked(View view){
        ImageView expandButton = (ImageView) view;
        if (mAccountsContainer.getVisibility() == View.VISIBLE){
            mAccountsContainer.setVisibility(View.GONE);
            expandButton.setImageResource(R.drawable.ic_expand_more_24);
            mNavigationView.inflateMenu(R.menu.activity_main);
            return;
        }
        mAccountsContainer.setVisibility(View.VISIBLE);
        mDisposables.add(
                mViewModel
                        .getAuthUsers()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::updateUsersAccountContainer));
        expandButton.setImageResource(R.drawable.ic_expand_less_24);
        mNavigationView.getMenu().clear();
    }

}
