package com.yeo.memo.api;

import com.yeo.memo.model.Memo;
import com.yeo.memo.model.MemoList;
import com.yeo.memo.model.PostRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MemoApi {

    // 메모 생성하는 API
    @POST("/memo")
    Call<PostRes> addMemo(@Header("Authorization") String token,
                          @Body Memo memo);

    // 내 메모 리스트 가져오는 API
    @GET("/memo")
    Call<MemoList> getMemoList(@Header("Authorization") String token,
                               @Query("offset") int offset,
                               @Query("limit") int limit);
    // 메모 수정 API
    @PUT("/memo/{memoId}")
    Call<PostRes> updateMemo(@Header("Authorization") String token,
                             @Path("memoId") int memoId,
                             @Body Memo memo);


}






