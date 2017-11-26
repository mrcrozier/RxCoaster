package com.example.laptor.rxcoaster.listeners;


import com.example.laptor.rxcoaster.Blueteeth.BlueteethResponse;

public interface OnCharacteristicReadListener {
    void call(BlueteethResponse response, byte[] data);
}
