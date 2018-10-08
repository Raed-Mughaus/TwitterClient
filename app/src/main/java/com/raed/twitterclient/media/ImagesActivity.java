package com.raed.twitterclient.media;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.raed.twitterclient.R;

public class ImagesActivity extends AppCompatActivity {

    private static final String KEY_IMAGE_URLS = "image_urls";
    private static final String KEY_SELECTED_IMAGE = "selected_image";

    public static Intent newIntent(Context context, String[] imageUrls, int selectedImage){
        Intent intent = new Intent(context, ImagesActivity.class);
        intent.putExtra(KEY_IMAGE_URLS, imageUrls);
        intent.putExtra(KEY_SELECTED_IMAGE, selectedImage);
        return intent;
    }

    private String mImageUrls[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        mImageUrls = getIntent().getStringArrayExtra(KEY_IMAGE_URLS);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return ImageFragment.newInstance(mImageUrls[position]);
            }

            @Override
            public int getCount() {
                return mImageUrls.length;
            }
        });

        int currentPage = getIntent().getIntExtra(KEY_SELECTED_IMAGE, 0);
        viewPager.setCurrentItem(currentPage);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
    }
}
