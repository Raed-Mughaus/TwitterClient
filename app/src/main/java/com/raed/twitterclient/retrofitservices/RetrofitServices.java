package com.raed.twitterclient.retrofitservices;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raed.twitterclient.x.AuthInterceptor;
import com.raed.twitterclient.BuildConfig;
import com.raed.twitterclient.authusers.AuthUser;
import com.raed.twitterclient.authusers.AuthUsersRepository;

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

    private static RetrofitServices sRetrofitServices = new RetrofitServices();
    private Retrofit mRetrofit;

    public static RetrofitServices getInstance() {
        return sRetrofitServices;
    }

    private RetrofitServices() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("EEE MMM dd HH:mm:ss Z yyyy")
                .create();
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        AuthUser user = AuthUsersRepository.getInstance().getCurrentUser();
        if (user != null){
            AuthInterceptor authInterceptor = new AuthInterceptor(user);
            okHttpClientBuilder.addInterceptor(authInterceptor);
        }

        if (BuildConfig.DEBUG)
            okHttpClientBuilder.addInterceptor(new MyLogInterceptor());
        Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(10));//todo should I stick with .io() instead
        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.twitter.com/1.1/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(scheduler))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClientBuilder.build())
                .build();
    }

    public UserService getUserService(){
        return mRetrofit.create(UserService.class);
    }

    public TLService getTLService(){
        return mRetrofit.create(TLService.class);
    }

    public AuthService getAuthService(){
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder().addInterceptor(new MyLogInterceptor());
        if (BuildConfig.DEBUG)
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
        public Response intercept(Chain chain) throws IOException, RuntimeException {
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
