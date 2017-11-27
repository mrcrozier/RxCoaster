package com.example.laptor.rxcoaster.data.actions;

import android.util.Log;

import com.example.laptor.rxcoaster.data.model.Post;
import com.example.laptor.rxcoaster.data.remote.APIService;
import com.example.laptor.rxcoaster.data.remote.ApiUtils;
import com.example.laptor.rxcoaster.utils.CoasterInfo;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by laptor on 11/1/17.
 */

public class CrudActions {

    private static final String TAG = CrudActions.class.getSimpleName();

    private static APIService mAPIService = ApiUtils.getAPIService();

    public static void sendPost(String coasterId, String tableId, boolean connected, boolean needsRefill) {

        // RxJava
        mAPIService.savePost(coasterId, tableId, connected, needsRefill).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Post>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Post post) {
                        Log.w(TAG, post.toString());
                    }
                });
    }

    public static void sendPut(CoasterInfo coasterInfo) {

        // RxJava
        Post post = new Post();
        post.setCoasterId(coasterInfo.getCoasterId());
        post.setConnected(coasterInfo.isIsConnected());
        post.setTableId(coasterInfo.getTableId());
        post.setNeedsRefill(coasterInfo.isNeedsRefill());
        post.setCupPresent(coasterInfo.isCupPresent());
        mAPIService.updatePost(coasterInfo.getCoasterId(), post).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Post>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "Successfully posted to the db");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Error updating the db, INVESTIGATE");
                    }

                    @Override
                    public void onNext(Post postR) {
                        Log.w(TAG, postR.toString());
                    }
                });
    }


}
