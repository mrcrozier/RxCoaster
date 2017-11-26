package com.example.laptor.rxcoaster.debugActivities;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.support.annotation.NonNull;


import com.example.laptor.rxcoaster.Blueteeth.BlueteethDevice;
import com.example.laptor.rxcoaster.Blueteeth.BlueteethUtils;
import com.example.laptor.rxcoaster.listeners.OnCharacteristicReadListener;
import com.example.laptor.rxcoaster.listeners.OnCharacteristicWriteListener;

import java.util.UUID;

import timber.log.Timber;

/**
 * Created by laptor on 11/24/17.
 */

public class SamplePeripheral extends BaseBluetoothPeripheral {

    // Custom Service
    private UUID SERVICE_TEST = UUID.fromString("f000ba55-0451-4000-b000-000000000000");

    private UUID CHARACTERISTIC_WRITE = UUID.fromString("01726f62-6f74-7061-6a61-6d61732e6361");
    private static final UUID CHARACTERISTIC_WRITE_NO_RESPONSE = UUID.fromString("02726f62-6f74-7061-6a61-6d61732e6361");

    private UUID CHARACTERISTIC_READ = UUID.fromString("00002cab-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_NOTIFY = UUID.fromString("00002cab-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_INDICATE = UUID.fromString("05726f62-6f74-7061-6a61-6d61732e6361");

    private static final UUID CHARACTERISTIC_WRITE_ECHO = UUID.fromString("06726f62-6f74-7061-6a61-6d61732e6361");
    private static final UUID CHARACTERISTIC_READ_ECHO = UUID.fromString("07726f62-6f74-7061-6a61-6d61732e6361");

    public SamplePeripheral(BlueteethDevice device){//,UUID serviceId,UUID charRead, UUID charWrite) {
        super(device);
//        this.SERVICE_TEST = serviceId;
//        this.CHARACTERISTIC_READ = charRead;
//        this
    }

    public void writeCounter(byte value, OnCharacteristicWriteListener writeListener) {
        byte[] data = new byte[]{value};
        BlueteethUtils.writeData(data, CHARACTERISTIC_WRITE, SERVICE_TEST, mPeripheral, writeListener);
    }

    public void readCounter(OnCharacteristicReadListener readListener) {
        BlueteethUtils.read(CHARACTERISTIC_READ, SERVICE_TEST, mPeripheral, readListener);
    }
    public void enableNotification(boolean isEnabled, OnCharacteristicReadListener readListener) {
        if (isEnabled) {
            mPeripheral.addNotification(CHARACTERISTIC_NOTIFY, SERVICE_TEST, readListener);
        } else {
//            mPeripheral.removeNotifications(CHARACTERISTIC_NOTIFY, SERVICE_TEST);
        }
    }


    public void writeEcho(byte[] dataToWrite, OnCharacteristicWriteListener writeListener) {
        BlueteethUtils.writeData(dataToWrite, CHARACTERISTIC_WRITE_ECHO, SERVICE_TEST, mPeripheral, writeListener);
        mPeripheral.writeCharacteristic(dataToWrite, CHARACTERISTIC_WRITE_ECHO, SERVICE_TEST, writeListener);
    }

//    public void writeNoResponseEcho(byte[] dataToWrite) {
//        mPeripheral.writeCharacteristic(dataToWrite, CHARACTERISTIC_WRITE_ECHO, SERVICE_TEST, null);
//    }

    public void readEcho(OnCharacteristicReadListener readListener) {
        BlueteethUtils.read(CHARACTERISTIC_READ_ECHO, SERVICE_TEST, mPeripheral, readListener);
    }

    public UUID getServiceTest() {
        return SERVICE_TEST;
    }

    public void setServiceTest(UUID serviceTest) {
        SERVICE_TEST = serviceTest;
    }

    public  UUID getCharacteristicWrite() {
        return CHARACTERISTIC_WRITE;
    }

    public void setCharacteristicWrite(UUID characteristicWrite) {
        CHARACTERISTIC_WRITE = characteristicWrite;
    }

    public UUID getCharacteristicRead() {
        return CHARACTERISTIC_READ;
    }

    public void setCharacteristicRead(UUID characteristicRead) {
        this.CHARACTERISTIC_READ = characteristicRead;
    }

    public static UUID getCharacteristicNotify() {
        return CHARACTERISTIC_NOTIFY;
    }

    //    public void notifyEcho()
//    public void indicateEcho()

}
