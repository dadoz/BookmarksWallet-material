package com.application.material.bookmarkswallet.app.api.network;

import com.application.material.bookmarkswallet.app.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookmarksService {
    private final FaviconFinderRetrofitService service;
    private final TagsByUrlRetrofitService service2;

    public BookmarksService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.OPENGRAPHCHECK_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        service = retrofit.create(FaviconFinderRetrofitService.class);
        service2 = retrofit.create(TagsByUrlRetrofitService.class);
    }

    public FaviconFinderRetrofitService getService() {
        return service;
    }

    public TagsByUrlRetrofitService getTagsByUrlService() {
        return service2;
    }
}
