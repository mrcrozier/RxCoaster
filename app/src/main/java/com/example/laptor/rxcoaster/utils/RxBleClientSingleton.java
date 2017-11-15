package com.example.laptor.rxcoaster.utils;

import android.content.Context;

import com.polidea.rxandroidble.RxBleClient;

/**
 * Created by laptor on 10/30/17.
 */

public class RxBleClientSingleton {
    private static RxBleClient rxBleClient = null;

    private RxBleClientSingleton(){}

    public static RxBleClient getInstance(Context context){
        if(rxBleClient == null){
            rxBleClient = RxBleClient.create(context);
        }
        return rxBleClient;
    }
}
