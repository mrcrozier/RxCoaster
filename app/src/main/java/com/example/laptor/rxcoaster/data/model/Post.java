package com.example.laptor.rxcoaster.data.model;

/**
 * Created by laptor on 10/31/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {


    @SerializedName("coasterId")
    @Expose
    private String coasterId;
    @SerializedName("tableId")
    @Expose
    private String tableId;
    @SerializedName("connected")
    @Expose
    private Boolean connected;
    @SerializedName("needsRefill")
    @Expose
    private String needsRefill;
    @SerializedName("cupPresent")
    @Expose
    private Boolean cupPresent;


    public  String getCoasterId() {
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


    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public String getNeedsRefill() {
        return needsRefill;
    }

    public void setNeedsRefill(String needsRefill) {
        this.needsRefill = needsRefill;
    }
    public Boolean getCupPresent() {
        return cupPresent;
    }

    public void setCupPresent(Boolean cupPresent) {
        this.cupPresent = cupPresent;
    }


    @Override
    public String toString() {
        return "Post{" +
                "coasterId='" + coasterId + '\'' +
                ", tableId='" + tableId + '\'' +
                ", connected=" + connected +
                ", needsRefill=" + needsRefill +
                ", cupPresent=" + cupPresent +
                '}';
    }
}


