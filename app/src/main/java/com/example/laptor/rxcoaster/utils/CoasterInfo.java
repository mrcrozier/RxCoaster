package com.example.laptor.rxcoaster.utils;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.io.Serializable;

/**
 * Created by laptor on 11/1/17.
 */

public class CoasterInfo implements Serializable {
    private String id;
    private String coasterId;
    private String tableId;
    private boolean isConnected;
    private boolean needsRefill;
    private boolean cupPresent;
    //    private  String btDeviceName;
    private String btDeviceAddress;
    private String gattService;
    private String gattCharacteristic;


    public CoasterInfo() {
    }

    public CoasterInfo(String coasterId, String tableId, boolean isConnected, boolean needsRefill,
                       boolean cupPresent, String btDeviceAddress, String gattService, String gattCharacteristic) {
        this.coasterId = coasterId;
        this.tableId = tableId;
        this.isConnected = isConnected;
        this.needsRefill = needsRefill;
        this.cupPresent = cupPresent;
        this.btDeviceAddress = btDeviceAddress;
        this.gattService = gattService;
        this.gattCharacteristic = gattCharacteristic;

    }

    public String getId() {
        return id;
    }

    public boolean isCupPresent() {
        return cupPresent;
    }

    public void setCupPresent(boolean cupPresent) {
        this.cupPresent = cupPresent;
    }

    public String getGattService() {
        return gattService;
    }

    public void setGattService(String gattService) {
        this.gattService = gattService;
    }

    public String getGattCharacteristic() {
        return gattCharacteristic;
    }

    public void setGattCharacteristic(String gattCharacteristic) {
        this.gattCharacteristic = gattCharacteristic;
    }

    public String getBtDeviceAddress() {

        return btDeviceAddress;
    }

    public void setBtDeviceAddress(String btDeviceAddress) {
        this.btDeviceAddress = btDeviceAddress;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoasterId() {
        return coasterId;
    }

    public void setCoasterId(String coasterId) {
        this.coasterId = coasterId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public boolean isIsConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isNeedsRefill() {
        return needsRefill;
    }

    public void setNeedsRefill(boolean needsRefill) {
        this.needsRefill = needsRefill;
    }

    @Override
    public String toString() {
        return "CoasterInfo{" +
                "coasterId='" + coasterId + '\'' +
                ", tableId='" + tableId + '\'' +
                ", isConnected=" + isConnected +
                ", needsRefill=" + needsRefill +
                ", cupPresent=" + cupPresent +
                ", btDeviceAddress=" + btDeviceAddress +
                ", gattService=" + gattService +
                ", gattCharacteristic=" + gattCharacteristic +
                '}';
    }


}
