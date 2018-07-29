package com.raed.twitterclient.retrofitservices;

import android.util.Log;

import com.raed.twitterclient.AuthInterceptor;
import com.raed.twitterclient.BuildConfig;
import com.raed.twitterclient.userdata.CurrentUser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;


import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitServices {

    private static final String TAG = "RetrofitServices";

    private static RetrofitServices sRetrofitServices;
    private Retrofit mRetrofit;

    public static RetrofitServices getInstance() {
        return sRetrofitServices;
    }

    public static void initializeInstance() {
        sRetrofitServices = new RetrofitServices();
    }

    public RetrofitServices() {
        CurrentUser currentUser = CurrentUser.getInstance();
        AuthInterceptor authInterceptor = new AuthInterceptor(currentUser);
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor);
        if (!BuildConfig.DEBUG)
            okHttpClientBuilder.addInterceptor(new MyLogInterceptor());
        Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(10));//todo should I stick with .io() instead
        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.twitter.com/1.1/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(scheduler))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build())
                .build();
    }

    public UserService getUserService(){
        return mRetrofit.create(UserService.class);
    }

    public AuthService getAuthService(){
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        if (!BuildConfig.DEBUG)
            okHttpClientBuilder.addInterceptor(new MyLogInterceptor());
        return new Retrofit.Builder()
                .baseUrl("https://api.twitter.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(okHttpClientBuilder.build())
                .build()
                .create(AuthService.class);
    }


    private static class MyLogInterceptor implements Interceptor{

        @Override
        public Response intercept(Chain chain) throws IOException {
            if (!BuildConfig.DEBUG)
                throw new RuntimeException("Trying to log on a non-debug version");
            Log.d(TAG, "intercept(chain): headers = " + chain.request().headers());
            Log.d(TAG, "intercept(chain): url" + chain.request().url());
            Response response = chain.proceed(chain.request());
            BufferedSource source = response.body().source();
            source.request(Long.MAX_VALUE); // request the entire body.
            Buffer buffer = source.buffer();
            String responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"));
            Log.d(TAG, "intercept(chain): " + responseBodyString);
            return response;
        }
    }
}
