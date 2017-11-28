package com.example.laptor.rxcoaster.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.laptor.rxcoaster.Blueteeth.BlueteethManager;
import com.example.laptor.rxcoaster.Blueteeth.BlueteethResponse;
import com.example.laptor.rxcoaster.R;
import com.example.laptor.rxcoaster.data.actions.CrudActions;
import com.example.laptor.rxcoaster.debugActivities.SamplePeripheral;


import java.util.Arrays;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by laptor on 11/24/17.
 */

public class BlueteethDeviceActivity extends Activity {
    private static final String TAG = BlueteethDeviceActivity.class.getSimpleName();
    private SamplePeripheral mSamplePeripheral;
    private CoasterInfo coasterInfo;
    private PreviousData previousData = new PreviousData();
    private SendData sendData;
    private boolean mIsConnected;

    @BindView(R.id.scrollview)
    ScrollView mScrollView;

    @BindView(R.id.textview_console)
    TextView mConsoleTextView;

    @BindView(R.id.button_connect)
    Button mConnectionButton;

    @BindView(R.id.button_read_counter)
    Button mReadCounterButton;

    @BindView(R.id.button_set_empty)
    Button mSetEmptyButton;

    @BindView(R.id.button_set_half_full)
    Button mSetHalfFullButton;

    @BindView(R.id.button_set_full)
    Button mSetFullButton;


    @OnClick(R.id.button_clear)
    void clearConsole() {
        mConsoleTextView.setText("");
    }

    @OnClick(R.id.button_connect)
    void connect() {
        if (mIsConnected) {
            updateReceivedData(String.format("Attempting to disconnect from %s - %s...", mSamplePeripheral.getName(), mSamplePeripheral.getMacAddress()));
            mSamplePeripheral.disconnect(isConnected -> {
                updateReceivedData("Connection Status: " + Boolean.toString(isConnected) + "\n");
                mIsConnected = isConnected;
                runOnUiThread(mConnectionRunnable);
            });
        } else {
            updateReceivedData(String.format("Attempting to connect to  %s - %s...", mSamplePeripheral.getName(), mSamplePeripheral.getMacAddress()));
            mSamplePeripheral.connect(true, isConnected -> {
                updateReceivedData("Connection Status: " + Boolean.toString(isConnected));
                mIsConnected = isConnected;
                readCounter();
                runOnUiThread(mConnectionRunnable);
            });


        }
    }

    Runnable mConnectionRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsConnected) {
                mConnectionButton.setText(R.string.disconnect);
                mReadCounterButton.setEnabled(true);
                mSetEmptyButton.setEnabled(true);
                mSetHalfFullButton.setEnabled(true);
                mSetFullButton.setEnabled(true);
            } else {
                mConnectionButton.setText(R.string.connect);
                mReadCounterButton.setEnabled(false);
                mSetEmptyButton.setEnabled(false);
                mSetHalfFullButton.setEnabled(false);
                mSetFullButton.setEnabled(false);
            }
        }
    };

    @OnClick(R.id.button_read_counter)
    void readCounter() {

        updateReceivedData("Attempting to Read Counter ...");
        mSamplePeripheral.readCounter((response, data) -> {
            if (response != BlueteethResponse.NO_ERROR) {
                updateReceivedData("Read error... " + response.name());
                return;
            }


            updateReceivedData(Arrays.toString(data));

            mSamplePeripheral.enableNotification(true, (notifyResponse, notifyData) -> {

                if (notifyResponse != BlueteethResponse.NO_ERROR) {
                    updateReceivedData("Notification error... " + notifyResponse.name());
                    return;
                }

                if(notifyData[0] != previousData.getPreviousData()) {
                    previousData.setPreviousData(notifyData[0]);
                    sendData.sendPutRequest(data, coasterInfo);
                    updateReceivedData(Arrays.toString(notifyData));
                }
            });
        });
    }

    @OnClick(R.id.button_set_empty)
    void writeSetEmpty() {
        updateReceivedData("Attempting to set this weight as empty ...");
        mSamplePeripheral.writeCounter((byte) 1, response -> {
            if (response != BlueteethResponse.NO_ERROR) {
                updateReceivedData("Write error... " + response.name());
                return;
            }
            updateReceivedData("Set current weight as empty");
        });
    }

    @OnClick(R.id.button_set_half_full)
    void writeSetHalfFull() {
        updateReceivedData("Attempting to set this weight as half full ...");
        mSamplePeripheral.writeCounter((byte) 2, response -> {
            if (response != BlueteethResponse.NO_ERROR) {
                updateReceivedData("Write error... " + response.name());
                return;
            }
            updateReceivedData("Set current weight as half full"); //Or half empty heheh
        });
    }
    @OnClick(R.id.button_set_full)
    void writeSetFull() {
        updateReceivedData("Attempting to set this weight as full ...");
        mSamplePeripheral.writeCounter((byte) 3, response -> {
            if (response != BlueteethResponse.NO_ERROR) {
                updateReceivedData("Write error... " + response.name());
                return;
            }
            updateReceivedData("Set current weight as full");
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        ButterKnife.bind(this);

        final Intent intent = getIntent();
        coasterInfo = (CoasterInfo)intent.getSerializableExtra("coasterInfo");
        sendData = new SendData(coasterInfo);
        String macAddress = coasterInfo.getBtDeviceAddress();
        mSamplePeripheral = new SamplePeripheral(BlueteethManager.with(this).getPeripheral(macAddress));
        mSamplePeripheral.setServiceTest(UUID.fromString(coasterInfo.getGattService()));
        mSamplePeripheral.setCharacteristicRead(UUID.fromString(coasterInfo.getGattCharacteristic()));


        connect();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSamplePeripheral.disconnect();
        mSamplePeripheral.close();
    }

    private void updateReceivedData(String message) {
        runOnUiThread(() -> {
            mConsoleTextView.append(message + "\n");
            mScrollView.smoothScrollTo(0, mConsoleTextView.getBottom());
        });
    }
}
