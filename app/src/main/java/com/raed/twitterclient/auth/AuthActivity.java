package com.raed.twitterclient.auth;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.raed.twitterclient.MyApplication;
import com.raed.twitterclient.utilis.Crashlytics;
import com.raed.twitterclient.R;
import com.raed.twitterclient.utilis.Utilis;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private AuthViewModel mViewModel;

    private WebView mWebView;

    public static Intent newIntent(Context context) {
        return new Intent(context, AuthActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Utilis.isOnline(this)){
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mWebView = new WebView(this);
        setContentView(mWebView);

        mViewModel = ViewModelProviders
                .of(this)
                .get(AuthViewModel.class);

        Disposable disposable = mViewModel
                .requestToken()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        requestTokenAndSecret -> setupWebView(requestTokenAndSecret[0], requestTokenAndSecret[1]),
                        this::handleAuthError);

        mCompositeDisposable.add(disposable);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }

    private void handleAuthError(Throwable throwable){
        if (Utilis.isOnline(AuthActivity.this)) {
            Crashlytics.logException(new Exception(throwable));
            Toast.makeText(AuthActivity.this, R.string.error_happened_in_authentication, Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(AuthActivity.this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
        finish();
    }

    private void setupWebView(String requestToken, String requestTokenSecret){
        mWebView.loadUrl("https://api.twitter.com/oauth/authenticate?oauth_token=" + requestToken + "&force_login=true");
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String oauthToken = request.getUrl().getQueryParameter("oauth_token");
                String oauthVerifier = request.getUrl().getQueryParameter("oauth_verifier");

                Disposable disposable = mViewModel
                        .onUserRedirected(oauthToken, oauthVerifier)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                user -> {
                                    mViewModel.onNewUser(user);
                                    finish();
                                },
                                AuthActivity.this::handleAuthError
                        );

                mCompositeDisposable.add(disposable);
                return false;
            }
        });
    }
}
