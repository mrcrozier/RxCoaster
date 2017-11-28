package com.example.laptor.rxcoaster.utils;

import android.util.Log;

import com.example.laptor.rxcoaster.data.actions.CrudActions;

/**
 * Created by laptor on 11/27/17.
 */

public class SendData {
    private CoasterInfo coasterInfo;


    public SendData(CoasterInfo coasterInfo){
        this.coasterInfo = coasterInfo;
    }

    public void sendPutRequest(byte[] data, CoasterInfo coasterInfo){
        if(data[0] == 0){
            coasterInfo.setCupPresent(false);
            coasterInfo.setNeedsRefill("grey");
            CrudActions.sendPut(coasterInfo);
        }
        //red
        if(data[0] == 1){
            coasterInfo.setCupPresent(true);
            coasterInfo.setNeedsRefill("red");
            CrudActions.sendPut(coasterInfo);
        }
        //yellow
        if(data[0] == 2){
            coasterInfo.setCupPresent(true);
            coasterInfo.setNeedsRefill("yellow");
            CrudActions.sendPut(coasterInfo);
        }
        //green
        if(data[0]  == 3){
            coasterInfo.setCupPresent(true);
            coasterInfo.setNeedsRefill("green");
            CrudActions.sendPut(coasterInfo);
        }
        //dark
        if(data[0] == 4){
            coasterInfo.setCupPresent(true);
            coasterInfo.setNeedsRefill("dark");
            CrudActions.sendPut(coasterInfo);
        }
    }
}
