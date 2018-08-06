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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.raed.twitterclient.R;
import com.raed.twitterclient.hashtaghelper.HashTagHelper;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileFragment";

    private static final String KEY_USER_ID = "user_id";

    private ProfileViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private TextView mFollowersView;
    private TextView mFollowingView;
    private TextView mTweetsCountView;
    private ImageView mProfileImageView;
    private ImageView mBannerImageView;
    private TextView mNameView;
    private TextView mScreenNameView;
    private TextView mDescriptionView;
    private TextView mLocationView;
    private TextView mUrlView;
    private ImageView mVerifiedAccountView;
    private Disposable mDisposable;
    private Button mFollowButton;
    private ImageButton mDMButton;

    public static Intent newIntent(Context context, String userID){
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(KEY_USER_ID, userID);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.activity_profile);

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
        /*mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new StringAdapter());*/

        mNameView = findViewById(R.id.name_text_view);
        mScreenNameView = findViewById(R.id.screen_name_text_view);
        mDescriptionView = findViewById(R.id.bio_text_view);

        mProfileImageView = findViewById(R.id.profile_image_view);
        mLocationView = findViewById(R.id.location_text_view);
        mUrlView = findViewById(R.id.website_text_view);

        mBannerImageView = findViewById(R.id.banner_image_view);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return new ListFragment();
            }

            @Override
            public int getCount() {
                return 3;
            }

        });

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);
        setupTap(tabLayout.getTabAt(0), R.string.tweets, 500);
        setupTap(tabLayout.getTabAt(1), R.string.following, 500);
        setupTap(tabLayout.getTabAt(2), R.string.followers, 500);

        mDisposable = mViewModel.getUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::updateUI,
                        throwable -> Log.e(TAG, "onCreateView: " + throwable.getMessage(), throwable)
                );

    }

    private void setupTap(TabLayout.Tab tab, @StringRes int title, int number){
        tab.setCustomView(R.layout.tab_profile);
        View view = tab.getCustomView();
        ((TextView) view.findViewById(R.id.number_view)).setText("" + number);
        ((TextView) view.findViewById(R.id.title_view)).setText(title);
    }

    //todo have a look at Glide page to see how to use it with RecyclerView
    private void updateUI(User user){
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
            HashTagHelper hashTagHelper = HashTagHelper
                    .Creator.create(0xff2187bb,
                            hashTag -> Toast.makeText(ProfileActivity.this, hashTag,
                                    Toast.LENGTH_SHORT).show(), '_');
            hashTagHelper.handle(mDescriptionView);
        }

        if (user.getLocation() != null)
            mLocationView.setText(user.getLocation());
        else {
            mLocationView.setVisibility(View.GONE);
        }

        if (user.getUrl() != null)
            mUrlView.setText(user.getUrl());

       /* mFollowersView.setText(Utils.format(user.getFollowersCount()));
        mFollowingView.setText(Utils.format(user.getFriendsCount()));
        mTweetsCountView.setText(Utils.format(user.getStatusesCount()));*/
        //todo what image library should I use, think of the RecyclerView
        /*mProfileImageView;
        mBannerImageView;
        mUserNameView;
        mScreenNameView;
        mDescriptionView;*/
       /* if (user.getLocation() != null)
            mLocationView.setText(user.getLocation());
        else
           ;// mLocationView.setVisibility();*/
        //   mUrlView.setText(user.getUrl());
        // mVerifiedAccountView.setVisibility(user.isVerified() ? View.VISIBLE : View.GONE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null)
            mDisposable.dispose();
    }


}
