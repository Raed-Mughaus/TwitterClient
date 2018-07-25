package com.raed.twitterclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.raed.twitterclient.auth.AuthActivity;
import com.raed.twitterclient.retrofitservices.UserService;
import com.raed.twitterclient.userdata.CurrentUser;

import javax.inject.Inject;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Inject UserService mUserService;
    @Inject CurrentUser mCurrentUser;
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MyApplication) getApplication()).getAppComponent().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mCurrentUser.hasAnyUserSignedIn())
            return;
        mDisposable = mUserService
                .show(mCurrentUser.getCurrentUser().getValue().getUserId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> Log.d(TAG, "onResume: " + user.getName()),
                        throwable -> Log.e(TAG, "onResume: an error happen while trying to get the user info", throwable)
                );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
    }

    @Override
    public void onBackPressed() {
        startActivity(AuthActivity.newIntent(this));
    }
}
