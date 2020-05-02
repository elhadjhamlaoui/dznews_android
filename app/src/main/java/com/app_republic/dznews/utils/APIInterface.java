package com.app_republic.dznews.utils;

import com.app_republic.dznews.pojo.ArticlesApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.app_republic.dznews.utils.StaticConfig.API_GET_ARTICLES;
import static com.app_republic.dznews.utils.StaticConfig.API_GET_BREAKING;
import static com.app_republic.dznews.utils.StaticConfig.API_GET_CORONA;


public interface APIInterface {

    @GET(API_GET_ARTICLES)
    Call<ArticlesApiResponse> getArticles(
            @Query("accessToken") String accessToken,
            @Query("page") int page);

    @GET(API_GET_BREAKING)
    Call<ArticlesApiResponse> getBreaking(
            @Query("accessToken") String accessToken,
            @Query("page") int page);

    @GET(API_GET_CORONA)
    Call<ArticlesApiResponse> getCorona(
            @Query("accessToken") String accessToken,
            @Query("page") int page);


}