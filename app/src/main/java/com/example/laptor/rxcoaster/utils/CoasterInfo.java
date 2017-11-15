package com.example.laptor.rxcoaster.utils;

import java.io.Serializable;

/**
 * Created by laptor on 11/1/17.
 */

public class CoasterInfo implements Serializable {
    private static String id;
    private static String coasterId;
    private static String tableId;
    private static boolean isConnected;
    private static boolean needsRefill;
    private static boolean cupPresent;
//    private static String btDeviceName;
    private static String btDeviceAddress;
//    private static

    public CoasterInfo() {
    }

    public CoasterInfo(String coasterId, String tableId, boolean isConnected, boolean needsRefill, boolean cupPresent, String btDeviceAddress) {
        this.coasterId = coasterId;
        this.tableId = tableId;
        this.isConnected = isConnected;
        this.needsRefill = needsRefill;
        this.cupPresent = cupPresent;
        this.btDeviceAddress = btDeviceAddress;

    }

    public static String getId() {
        return id;
    }

    public static boolean isCupPresent() {
        return cupPresent;
    }

    public static void setCupPresent(boolean cupPresent) {
        CoasterInfo.cupPresent = cupPresent;
    }

    public static String getBtDeviceAddress() {
        return btDeviceAddress;
    }

    public static void setBtDeviceAddress(String btDeviceAddress) {
        CoasterInfo.btDeviceAddress = btDeviceAddress;
    }

    public static void setId(String id) {
        CoasterInfo.id = id;
    }

    public static String getCoasterId() {
        return coasterId;
    }

    public static void setCoasterId(String coasterId) {
        CoasterInfo.coasterId = coasterId;
    }

    public static String getTableId() {
        return tableId;
    }

    public static void setTableId(String tableId) {
        CoasterInfo.tableId = tableId;
    }

    public static boolean isIsConnected() {
        return isConnected;
    }

    public static void setIsConnected(boolean isConnected) {
        CoasterInfo.isConnected = isConnected;
    }

    public static boolean isNeedsRefill() {
        return needsRefill;
    }

    public static void setNeedsRefill(boolean needsRefill) {
        CoasterInfo.needsRefill = needsRefill;
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
                '}';
    }


}
