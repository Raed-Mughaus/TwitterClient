package com.raed.twitterclient.retrofitservices;

import android.util.Log;

import com.raed.twitterclient.AuthInterceptor;

import java.nio.charset.Charset;
import java.util.concurrent.Executors;


import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RetrofitModule {

    private static final String TAG = "RetrofitModule";

    private Retrofit mRetrofit;

    @Provides
    public UserService provideUserService(AuthInterceptor authInterceptor){
        Retrofit retrofit = getRetrofit(authInterceptor);
        return retrofit.create(UserService.class);
    }

    @Provides
    public AuthService provideAuthService(){

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(chain -> {
                    Log.d(TAG, "provideAuthService: " + chain.request().headers());
                    Response response = chain.proceed(chain.request());
                    Log.d(TAG, "intercept: " + response.toString());
                    Log.d(TAG, "intercept: " + response.body().toString());
                    return response;
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl("https://api.twitter.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(okHttpClient)
                .build()
                .create(AuthService.class);
    }

    private Retrofit getRetrofit(AuthInterceptor authInterceptor){
        if (mRetrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(chain -> {
                        Log.d(TAG, "intercept: headers = " + chain.request().headers());
                        Log.d(TAG, "intercept: url" + chain.request().url());
                        Response response = chain.proceed(chain.request());
                        BufferedSource source = response.body().source();
                        source.request(Long.MAX_VALUE); // request the entire body.
                        Buffer buffer = source.buffer();
                        String responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"));
                        Log.d(TAG, "Response: " + responseBodyString);
                        return response;
                    })
                    .build();
            Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(10));//todo should I stick with .io() instead
            mRetrofit = new Retrofit.Builder()
                    .baseUrl("https://api.twitter.com/1.1/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(scheduler))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return mRetrofit;
    }

}
