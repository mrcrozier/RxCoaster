package com.example.laptor.rxcoaster.data.remote;

/**
 * Created by laptor on 10/31/17.
 */

import com.example.laptor.rxcoaster.data.model.Post;
import com.example.laptor.rxcoaster.utils.CoasterInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface APIService {

    @POST("/coasters")
    @FormUrlEncoded
    Observable<Post> savePost(@Field("coasterId") String coasterId,
                              @Field("tableId") String tableId,
                              @Field("connected") boolean connected,
                              @Field("needsRefill") boolean needsRefill);

    @PUT("/api/coasters/update/{coasterId}")
    Observable<Post> updatePost(@Path("coasterId") String coasterId,
                                @Body Post post);
}
