package com.raed.twitterclient.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.raed.twitterclient.R;
import com.raed.twitterclient.model.User;
import com.raed.twitterclient.api.UserService;
import com.raed.twitterclient.timeline.user_timeline.UserTLFragment;
import com.raed.twitterclient.user_list.UsersListFragment;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileFragment";

    private static final String KEY_USER_ID = "user_id";

    private ProfileViewModel mViewModel;

    private ImageView mProfileImageView;
    private ImageView mBannerImageView;
    private TextView mNameView;
    private TextView mScreenNameView;
    private TextView mDescriptionView;
    private TextView mLocationView;
    private View mLocationIcon;
    private TextView mUrlView;
    private View mUrlIcon;
    private Disposable mDisposable;
    private Button mFollowButton;

    public static Intent newIntent(Context context, String userID){
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(KEY_USER_ID, userID);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_tool_bar);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(
                (barLayout, verticalOffset) -> {
                    float visibleRange = barLayout.getTotalScrollRange()/3f;//the range in which the text is visible
                    collapsingToolbarLayout.setTitleEnabled(-verticalOffset > 2f * visibleRange);
                }
        );

        mViewModel = ViewModelProviders
                .of(this)
                .get(ProfileViewModel.class);

        mNameView = findViewById(R.id.name_text_view);
        mScreenNameView = findViewById(R.id.screen_name_text_view);
        mDescriptionView = findViewById(R.id.bio_text_view);

        mProfileImageView = findViewById(R.id.profile_image_view);
        mLocationView = findViewById(R.id.location_text_view);
        mLocationIcon = findViewById(R.id.location_icon);
        mUrlView = findViewById(R.id.website_text_view);
        mUrlIcon = findViewById(R.id.website_icon);

        mBannerImageView = findViewById(R.id.banner_image_view);

        String userID = getIntent().getStringExtra(KEY_USER_ID);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return UserTLFragment.newInstance(userID);
                    case 1:
                        return UsersListFragment.newInstance(getIntent().getStringExtra(KEY_USER_ID), UserService.UserRelation.FOLLOWING);
                    case 2:
                        return UsersListFragment.newInstance(getIntent().getStringExtra(KEY_USER_ID), UserService.UserRelation.FOLLOWERS);
                }
                throw new IllegalArgumentException("Position is out of range");
            }

            @Override
            public int getCount() {
                return 3;
            }

        });

        mDisposable = mViewModel.getUser(userID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::updateUI,
                        throwable -> Log.e(TAG, "onCreateView: " + throwable.getMessage(), throwable)
                );

    }

    private void setupTapLayout(User user){

        ViewPager viewPager = findViewById(R.id.view_pager);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);
        setupTap(tabLayout.getTabAt(0), R.string.tweets, user.getStatusesCount());
        setupTap(tabLayout.getTabAt(1), R.string.following, user.getFriendsCount());
        setupTap(tabLayout.getTabAt(2), R.string.followers, user.getFollowersCount());
    }

    private void setupTap(TabLayout.Tab tab, @StringRes int title, long number){
        tab.setCustomView(R.layout.tab_profile);
        View view = tab.getCustomView();
        ((TextView) view.findViewById(R.id.number_view)).setText("" + number);
        ((TextView) view.findViewById(R.id.title_view)).setText(title);
    }

    //todo have a look at Glide page to see how to use it with RecyclerView
    private void updateUI(User user){

        setupTapLayout(user);

        Glide.with(Objects.requireNonNull(this))
                .load(user.getBannerImage())
                .into(mBannerImageView);

        Glide.with(Objects.requireNonNull(this))
                .load(user.getProfileImage().replace("_normal", ""))
                .into(mProfileImageView);

        mNameView.setText(user.getName());
        mScreenNameView.setText(getString(R.string.username_prefix, user.getScreenName()));

        if (user.getDescription() != null) {
            mDescriptionView.setText(user.getDescription());
        }

        if (user.getLocation() != null && user.getLocation().length() > 0) {
            mLocationView.setText(user.getLocation());
        } else {
            mLocationView.setVisibility(View.GONE);
            mLocationIcon.setVisibility(View.GONE);
        }

        if (user.getUrl() != null) {
            mUrlView.setText(user.getUrl());
        } else{
            mUrlIcon.setVisibility(View.GONE);
            mUrlView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null)
            mDisposable.dispose();
    }


}
