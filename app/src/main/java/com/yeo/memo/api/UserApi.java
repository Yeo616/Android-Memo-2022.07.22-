package com.yeo.memo.api;

import com.yeo.memo.model.UserRes;
import com.yeo.memo.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {

    @POST("/users/register")
    Call<UserRes> register(@Body User user);

    @POST("/users/login")
    Call<UserRes> login(@Body User user);

}



