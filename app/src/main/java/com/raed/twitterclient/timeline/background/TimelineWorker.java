package com.raed.twitterclient.timeline.background;

import android.support.annotation.NonNull;

import com.raed.twitterclient.authusers.AuthUser;
import com.raed.twitterclient.authusers.AuthUsersRepository;
import com.raed.twitterclient.timeline.data.TweetsRepository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;

public class TimelineWorker extends Worker {

    //If I am going to do it with multiple users I need to edit the AuthInterceptor
    //You may want to limit it to 2 account or only load data for the signed in user

    static {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                TimelineWorker.class, 20,
                TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        WorkManager
                .getInstance()
                .enqueue(workRequest);
    }

    @NonNull
    @Override
    public Result doWork() {
        List<AuthUser> users = AuthUsersRepository.getInstance().getUsers();
        try {
            new TweetsRepository(users.get(0)).cacheNewTweets();
            return Result.SUCCESS;
        } catch (IOException ignore) {
            return Result.FAILURE;
        }

    }
}
