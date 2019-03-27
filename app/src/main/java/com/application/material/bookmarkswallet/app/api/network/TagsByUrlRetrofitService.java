package com.application.material.bookmarkswallet.app.api.network;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TagsByUrlRetrofitService {
    @GET("result.php")
    Observable<ResponseBody> getTagsByUrl(@Query("url") String url);

}
