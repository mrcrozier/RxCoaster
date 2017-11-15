package com.example.laptor.rxcoaster.debugActivities;

/**
 * Created by laptor on 10/31/17.
 */
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.laptor.rxcoaster.R;

public class InteractActivity extends AppCompatActivity {

    public static final String EXTRA_DEVICE_ADDRESS = "mAddress";

    private final GattClient mGattClient = new GattClient();
    private Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interact_activity);
        mButton = (Button) findViewById(R.id.interact_button);
        mButton.setEnabled(false);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGattClient.writeInteractor();
            }
        });

        String address = getIntent().getStringExtra(EXTRA_DEVICE_ADDRESS);
        mGattClient.onCreate(this, address, new GattClient.OnCounterReadListener() {
            @Override
            public void onCounterRead(final int value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mButton.setText(Integer.toString(value));
                    }
                });
            }

            @Override
            public void onConnected(final boolean success) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mButton.setEnabled(success);
                        if (!success) {
                            Toast.makeText(InteractActivity.this, "Connection error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGattClient.onDestroy();
    }
}