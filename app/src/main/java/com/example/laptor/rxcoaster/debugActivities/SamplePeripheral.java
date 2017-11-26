package com.example.laptor.rxcoaster.debugActivities;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.support.annotation.NonNull;

import com.robotpajamas.blueteeth.BlueteethDevice;
import com.robotpajamas.blueteeth.BlueteethUtils;
import com.robotpajamas.blueteeth.listeners.OnCharacteristicReadListener;
import com.robotpajamas.blueteeth.listeners.OnCharacteristicWriteListener;

import java.util.UUID;

import timber.log.Timber;

/**
 * Created by laptor on 11/24/17.
 */

public class SamplePeripheral extends BaseBluetoothPeripheral {

    // Custom Service
    private static final UUID SERVICE_TEST = UUID.fromString("f000ba55-0451-4000-b000-000000000000");

    private static final UUID CHARACTERISTIC_WRITE = UUID.fromString("01726f62-6f74-7061-6a61-6d61732e6361");
    private static final UUID CHARACTERISTIC_WRITE_NO_RESPONSE = UUID.fromString("02726f62-6f74-7061-6a61-6d61732e6361");

    private static final UUID CHARACTERISTIC_READ = UUID.fromString("00002cab-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_NOTIFY = UUID.fromString("04726f62-6f74-7061-6a61-6d61732e6361");
    private static final UUID CHARACTERISTIC_INDICATE = UUID.fromString("05726f62-6f74-7061-6a61-6d61732e6361");

    private static final UUID CHARACTERISTIC_WRITE_ECHO = UUID.fromString("06726f62-6f74-7061-6a61-6d61732e6361");
    private static final UUID CHARACTERISTIC_READ_ECHO = UUID.fromString("07726f62-6f74-7061-6a61-6d61732e6361");

    public SamplePeripheral(com.example.laptor.rxcoaster.debugActivities.BlueteethDevice device) {
        super(device);
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
    public boolean addNotification(@NonNull UUID characteristic, @NonNull UUID service, OnCharacteristicReadListener characteristicReadListener) {
        Timber.d("addNotification: Adding Notification listener to %s", characteristic.toString());

        if (mPeripheral.mBluetoothGatt == null) {
            Timber.e("addNotification: GATT is null");
            return false;
        }

        BluetoothGattService gattService = mPeripheral.mBluetoothGatt.getService(service);
        if (gattService == null) {
            Timber.e("addNotification: Service not available - %s", service.toString());
            return false;
        }

        BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(characteristic);
        if (gattCharacteristic == null) {
            Timber.e("addNotification: Characteristic not available - %s", characteristic.toString());
            return false;
        }

        BluetoothGattDescriptor gattDescriptor = gattCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        if (gattDescriptor == null) {
            Timber.e("addNotification: Descriptor not available - %s", characteristic.toString());
            return false;
        }

        mNotificationMap.put(characteristic.toString(), characteristicReadListener);
        mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
        gattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(gattDescriptor);
        return true;
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

//    public void notifyEcho()
//    public void indicateEcho()

}
