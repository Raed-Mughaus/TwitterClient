package com.raed.twitterclient;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.raed.twitterclient.auth.AuthActivity;
import com.raed.twitterclient.timeline.TLFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ViewPager mViewPager;
    private Toolbar mToolbar;

    private MainActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        if (mViewModel.shouldStartAuthActivity()){
            Intent intent = AuthActivity.newIntent(this);
            startActivity(intent);
        }

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(mPageChangeListener);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this::onItemClicked);

        NavigationViewModel navigationViewModel = ViewModelProviders
                .of(this)
                .get(NavigationViewModel.class);
        NavigationViewHolder navigationViewHolder =
                new NavigationViewHolder(navigationView, navigationViewModel);
        getLifecycle().addObserver(navigationViewHolder);
    }

    private boolean onItemClicked(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.home:
                mViewPager.setCurrentItem(0);//edit this to be customizable
                break;
        }
        return true;
    }

    private final OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {
        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
        @Override public void onPageScrollStateChanged(int state) { }

        @Override
        public void onPageSelected(int position) {
            mToolbar.setTitle("Page " + position);
        }
    };

    private final FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            return TLFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 1;
        }

        //todo show tabs in settings
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page #" + position;
        }
    };

}
