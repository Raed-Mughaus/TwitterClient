package com.raed.twitterclient.settings;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.raed.twitterclient.R;
import com.raed.twitterclient.SingleFragmentActivity;

public class SettingsActivity extends SingleFragmentActivity
        implements SettingsFragment.SettingsFragmentCallback{

    public static Intent newIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return SettingsFragment.newInstance();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_master_detail;
    }

    @Override
    public void onSettingsItemSelected(int item) {
        if(findViewById(R.id.detail_fragment_container) != null) {
            replaceSettingFragment(item);
            return;
        }
        startAnotherSettingsActivity(item);
    }

    private void startAnotherSettingsActivity(int item){
        Intent intent;
        switch (item){
            case SettingsFragment.PAGES:
                intent = PagesActivity.newIntent(this);
                break;
            default:
                throw new IllegalArgumentException("No item with this id");
        }
        startActivity(intent);
    }

    private void replaceSettingFragment(int item){
        Fragment fragment;
        switch (item){
            case SettingsFragment.PAGES:
                fragment = PagesFragment.newInstance();
                break;
            default:
                throw new IllegalArgumentException("No item with this id");
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
