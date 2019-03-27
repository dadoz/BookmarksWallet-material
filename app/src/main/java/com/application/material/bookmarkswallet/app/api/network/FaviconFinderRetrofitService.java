package com.application.material.bookmarkswallet.app.api.network;

import android.graphics.Bitmap;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FaviconFinderRetrofitService {
    @GET("{url}")
    Observable<Bitmap> getFaviconByUrl(@Query("url") String url);
}
