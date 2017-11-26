package com.example.laptor.rxcoaster.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.laptor.rxcoaster.R;
import com.example.laptor.rxcoaster.data.actions.CrudActions;
import com.example.laptor.rxcoaster.debugActivities.SamplePeripheral;
import com.robotpajamas.blueteeth.BlueteethManager;
import com.robotpajamas.blueteeth.BlueteethResponse;

import java.util.Arrays;

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
    private boolean mIsConnected;

    @BindView(R.id.scrollview)
    ScrollView mScrollView;

    @BindView(R.id.textview_console)
    TextView mConsoleTextView;

    @BindView(R.id.button_connect)
    Button mConnectionButton;

    @BindView(R.id.button_read_counter)
    Button mReadCounterButton;

    @BindView(R.id.button_write_counter)
    Button mWriteCounterButton;

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
                mWriteCounterButton.setEnabled(true);
            } else {
                mConnectionButton.setText(R.string.connect);
                mReadCounterButton.setEnabled(false);
                mWriteCounterButton.setEnabled(false);
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
            if(data[0] == 0){
                coasterInfo.setCupPresent(false);
                CrudActions.sendPut(coasterInfo);
            }
            if(data[0] == 1){
                coasterInfo.setNeedsRefill(true);
                CrudActions.sendPut(coasterInfo);
            }
            if(data[0] == 2){
                coasterInfo.setNeedsRefill(false);
                CrudActions.sendPut(coasterInfo);
            }
            if(data[0]  == 3){
                coasterInfo.setNeedsRefill(false);
                Log.w(TAG, "Coaster " + coasterInfo.getCoasterId() + "is reporting three");
                CrudActions.sendPut(coasterInfo);
            }
            if(data[0] == 4){
                coasterInfo.setNeedsRefill(false);
                Log.w(TAG, "Coaster " + coasterInfo.getCoasterId() + " is reporting four. BT Device address is ");
                CrudActions.sendPut(coasterInfo);
            }

            updateReceivedData(Arrays.toString(data));
        });
    }

    @OnClick(R.id.button_write_counter)
    void writeCharacteristic() {
        updateReceivedData("Attempting to Reset Counter ...");
        mSamplePeripheral.writeCounter((byte) 42, response -> {
            if (response != BlueteethResponse.NO_ERROR) {
                updateReceivedData("Write error... " + response.name());
                return;
            }
            updateReceivedData("Counter characteristic reset to 42");
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        ButterKnife.bind(this);

        final Intent intent = getIntent();
        coasterInfo = (CoasterInfo)intent.getSerializableExtra("coasterInfo");
        String macAddress = coasterInfo.getBtDeviceAddress();
        mSamplePeripheral = new SamplePeripheral(BlueteethManager.with(this).getPeripheral(macAddress));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSamplePeripheral.close();
    }

    private void updateReceivedData(String message) {
        runOnUiThread(() -> {
            mConsoleTextView.append(message + "\n");
            mScrollView.smoothScrollTo(0, mConsoleTextView.getBottom());
        });
    }
}
