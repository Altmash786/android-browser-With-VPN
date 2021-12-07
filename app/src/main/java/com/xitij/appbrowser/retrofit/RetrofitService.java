package com.xitij.appbrowser.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("/api/clientpackage")
    Call<ProductKRoot> getProducts(@Query("key") String key);
}
