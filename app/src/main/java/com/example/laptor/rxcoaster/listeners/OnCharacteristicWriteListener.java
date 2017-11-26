package com.example.laptor.rxcoaster.listeners;


import com.example.laptor.rxcoaster.Blueteeth.BlueteethResponse;

public interface OnCharacteristicWriteListener {
    void call(BlueteethResponse response);
}
