package com.example.laptor.rxcoaster.debugActivities;

/**
 * Created by laptor on 10/31/17.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.laptor.rxcoaster.data.actions.CrudActions;
import com.example.laptor.rxcoaster.data.model.Post;
import com.example.laptor.rxcoaster.data.remote.APIService;
import com.example.laptor.rxcoaster.data.remote.ApiUtils;
import com.example.laptor.rxcoaster.utils.BluetoothInfo;
import com.example.laptor.rxcoaster.utils.CoasterInfo;
import com.example.laptor.rxcoaster.utils.Ints;


import java.util.UUID;

import static android.content.Context.BLUETOOTH_SERVICE;


public class GattClient {

    private static final String TAG = GattClient.class.getSimpleName();
    private static UUID DESCRIPTOR_CONFIG;
    private static UUID DESCRIPTOR_USER_DESC;

    private static UUID SERVICE_UUID = UUID.fromString("f000ba55-0451-4000-b000-000000000000");;
    private static UUID CHARACTERISTIC_REFILL_UUID = UUID.fromString("00002cab-0000-1000-8000-00805f9b34fb");
    //private static UUID CHARACTERISTIC_INTERACTOR_UUID = UUID.fromString("00002bad-0000-1000-8000-8000-00805f9b34fb");
    public interface OnCounterReadListener {
        void onCounterRead(int value);

        void onConnected(boolean success);
    }

    private Context mContext;
    private OnCounterReadListener mListener;
    private String mDeviceAddress;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;

    private static BluetoothInfo mBluetoothInfo;
    private boolean btRefillStatus;
    private CrudActions crud = new CrudActions();
    private APIService mAPIService = ApiUtils.getAPIService();
    private String coasterID = "59f94d58572b89369081e712";

    //private CoasterInfo coasterInfo = new CoasterInfo("Thirty five" , "Six" , true, true, true, "test");

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT client. Attempting to start service discovery");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT client");
                mListener.onConnected(false);
            }
        }
    public void setBluetoothInfo(BluetoothInfo bluetoothInfo) {
            mBluetoothInfo = bluetoothInfo;
    }



        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                boolean connected = false;

                BluetoothGattService service = gatt.getService(mBluetoothInfo.getServiceUuid());
                if (service != null) {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(mBluetoothInfo.getCharacteristicRefillUuid());
                    if (characteristic != null) {
                        gatt.setCharacteristicNotification(characteristic, true);

                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(mBluetoothInfo.getDescriptorConfig());
                        if (descriptor != null) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            connected = gatt.writeDescriptor(descriptor);
                        }
                    }
                }
                mListener.onConnected(connected);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            readRefillCharacteristic(characteristic);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            readRefillCharacteristic(characteristic);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (mBluetoothInfo.getDescriptorConfig().equals(descriptor.getUuid())) {
                BluetoothGattCharacteristic characteristic = gatt.getService(mBluetoothInfo.getServiceUuid()).getCharacteristic(mBluetoothInfo.getCharacteristicRefillUuid());
                gatt.readCharacteristic(characteristic);
            }
        }

        private void readRefillCharacteristic(BluetoothGattCharacteristic characteristic) {
            if (mBluetoothInfo.getCharacteristicRefillUuid().equals(characteristic.getUuid())) {
                byte[] data = characteristic.getValue();
                int value = Ints.fromByteArray(data);
                mListener.onCounterRead(value);
                //value of 0 or 1 indicates refill flag is set
//                if(value  == 0 || value == 1){
//                    //check if this coaster id is in the database
//                    //TODO: Add route handler to search for a coaster based on coaster id
//                    //if its not, post with the correct values
//
//                    //if it is, update with the current flag and table id's
//                    coasterInfo.setNeedsRefill(true);
//                    crud.sendPut(coasterInfo.getTableId(),
//                            coasterInfo.isIsConnected(), coasterInfo.isNeedsRefill(), mAPIService);
//                } else {
//                    coasterInfo.setNeedsRefill(false);
//                    crud.sendPut(coasterInfo.getTableId(),
//                            coasterInfo.isIsConnected(), coasterInfo.isNeedsRefill(), mAPIService);
//                }

            }
        }
    };

    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);

            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    startClient();
                    break;
                case BluetoothAdapter.STATE_OFF:
                    stopClient();
                    break;
                default:
                    // Do nothing
                    break;
            }
        }
    };

    public void onCreate(Context context, String deviceAddress, OnCounterReadListener listener) throws RuntimeException {
        mContext = context;
        mListener = listener;
        mDeviceAddress = deviceAddress;

        mBluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (!checkBluetoothSupport(mBluetoothAdapter)) {
            throw new RuntimeException("GATT client requires Bluetooth support");
        }

        // Register for system Bluetooth events
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mBluetoothReceiver, filter);
        if (!mBluetoothAdapter.isEnabled()) {
            Log.w(TAG, "Bluetooth is currently disabled... enabling");
            mBluetoothAdapter.enable();
        } else {
            Log.i(TAG, "Bluetooth enabled... starting client");
            startClient();
        }
    }

    public void onDestroy() {
        mListener = null;

        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        if (bluetoothAdapter.isEnabled()) {
            stopClient();
        }

        mContext.unregisterReceiver(mBluetoothReceiver);
    }

    public void writeInteractor() {
        BluetoothGattCharacteristic interactor = mBluetoothGatt
                .getService(mBluetoothInfo.getServiceUuid())
                .getCharacteristic(mBluetoothInfo.getCharacteristicInteractorUuid());
        interactor.setValue("!");
        mBluetoothGatt.writeCharacteristic(interactor);
    }

    private boolean checkBluetoothSupport(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter == null) {
            Log.w(TAG, "Bluetooth is not supported");
            return false;
        }

        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.w(TAG, "Bluetooth LE is not supported");
            return false;
        }

        return true;
    }

    private void startClient() {
        BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
        mBluetoothGatt = bluetoothDevice.connectGatt(mContext, false, mGattCallback);

        if (mBluetoothGatt == null) {
            Log.w(TAG, "Unable to create GATT client");
            return;
        }
    }

    private void stopClient() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }

        if (mBluetoothAdapter != null) {
            mBluetoothAdapter = null;
        }
    }
}

