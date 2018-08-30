package com.raed.twitterclient.settings;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.raed.twitterclient.SingleFragmentActivity;

public class PagesActivity extends SingleFragmentActivity {


    public static Intent newIntent(Context context) {
        return new Intent(context, PagesActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return PagesFragment.newInstance();
    }
}
