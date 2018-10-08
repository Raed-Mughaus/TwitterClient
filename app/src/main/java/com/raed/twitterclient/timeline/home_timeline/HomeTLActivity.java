package com.raed.twitterclient.timeline.home_timeline;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.raed.twitterclient.R;
import com.raed.twitterclient.auth.AuthActivity;
import com.raed.twitterclient.model.tweet.Tweet;
import com.raed.twitterclient.profile.ProfileActivity;
import com.raed.twitterclient.timeline.TLView;

public class HomeTLActivity extends AppCompatActivity {

    private static final String TAG = "HomeTLActivity";

    private Toolbar mToolbar;

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;

    private HomeTLViewModel mViewModel;
    private TLView mTlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = ViewModelProviders.of(this).get(HomeTLViewModel.class);
        if (mViewModel.shouldStartAuthActivity()){
            Intent intent = AuthActivity.newIntent(this);
            startActivity(intent);
            finish();
            return;
        }

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setOnClickListener(ignore -> Toast.makeText(this, "Scroll To Top", Toast.LENGTH_SHORT).show());//todo show a view asking the user to click if he want to scroll
        setSupportActionBar(mToolbar);

        mRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = mRefreshLayout.findViewById(R.id.recycler_view);
        mTlView = new TLView(this, mViewModel, mRefreshLayout, mRecyclerView){
            protected void restoreScrollPosition(){
                Long id = mViewModel.getInitialLoadKey();
                if (id == null)
                    return;
                int index = getTweetAdapter().getCurrentList().indexOf(new Tweet(id));
                mRecyclerView.scrollToPosition(index);
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();

        mViewModel = ViewModelProviders.of(this).get(HomeTLViewModel.class);
        if (mViewModel.shouldStartAuthActivity()){
            return;
        }

        Long currentShownID = mTlView.getCurrentShownID();
        if (currentShownID != null)
            mViewModel.saveScrolledToID(currentShownID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.my_profile:
                startActivity(ProfileActivity.newIntent(this, mViewModel.getCurrentUserID()));
                return true;
            case R.id.sign_out:
                mViewModel.signOut();
                Intent intent = ProfileActivity.newIntent(this, mViewModel.getCurrentUserID());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
