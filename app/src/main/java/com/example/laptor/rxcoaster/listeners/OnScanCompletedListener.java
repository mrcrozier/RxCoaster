package com.example.laptor.rxcoaster.listeners;



import com.example.laptor.rxcoaster.Blueteeth.BlueteethDevice;

import java.util.List;

public interface OnScanCompletedListener {
    void call(List<BlueteethDevice> blueteethDevices);
}
