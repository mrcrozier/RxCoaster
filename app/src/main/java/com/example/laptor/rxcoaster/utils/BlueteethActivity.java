package com.example.laptor.rxcoaster.utils;

import android.app.Activity;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.laptor.rxcoaster.Blueteeth.BlueteethDevice;
import com.example.laptor.rxcoaster.Blueteeth.BlueteethManager;
import com.example.laptor.rxcoaster.R;
import com.example.laptor.rxcoaster.debugActivities.DeviceControlActivity;
import com.example.laptor.rxcoaster.debugActivities.DeviceScanActivity;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by laptor on 11/24/17.
 */

public class BlueteethActivity  extends ListActivity {
    private static final String TAG = BlueteethDeviceActivity.class.getSimpleName();
    private static final int REQ_BLUETOOTH_ENABLE = 1000;
    private static final int DEVICE_SCAN_MILLISECONDS = 1000;
    public static final String MIME_TEXT_PLAIN = "text/plain";

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefresh;
    private DeviceScanListAdapter mDeviceAdapter;

    private NfcAdapter mNfcAdapter;
    private CoasterInfo coasterInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.blueteeth);
        ButterKnife.bind(this);

        Timber.plant(new Timber.DebugTree());

        // If BLE support isn't there, quit the app
        checkBluetoothSupport();

        mSwipeRefresh.setOnRefreshListener(this::startScanning);
        mDeviceAdapter = new DeviceScanListAdapter(this);
        setListAdapter(mDeviceAdapter);
        // Start automatic scan
        mSwipeRefresh.setRefreshing(true);
        startScanning();

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this,"NFC is disabled.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"NFC is enabled.", Toast.LENGTH_LONG).show();
        }

        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDeviceAdapter.clear();

        // Start automatic scan
        mSwipeRefresh.setRefreshing(true);
        startScanning();
        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
        stopScanning();
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        stopScanning();

        BlueteethDevice blueteethDevice = mDeviceAdapter.getItem(position);
        final Intent intent = new Intent(this, BlueteethDeviceActivity.class);
        intent.putExtra(getString(R.string.extra_mac_address), blueteethDevice.getMacAddress());
        startActivity(intent);
    }

    private void startScanning() {
        // Clear existing devices (assumes none are connected)
        Timber.d("Start scanning");
        mDeviceAdapter.clear();
        BlueteethManager.with(this).scanForPeripherals(DEVICE_SCAN_MILLISECONDS, bleDevices -> {
            Timber.d("On Scan completed");
            mSwipeRefresh.setRefreshing(false);
            for (BlueteethDevice device : bleDevices) {
                if (!TextUtils.isEmpty(device.getBluetoothDevice().getName())) {
                    Timber.d("%s - %s", device.getName(), device.getMacAddress());
                    mDeviceAdapter.add(device);
                }
            }
        });
    }

    private void stopScanning() {
        // Update the button, and shut off the progress bar
        mSwipeRefresh.setRefreshing(false);
        BlueteethManager.with(this).stopScanForPeripherals();
    }

    private void checkBluetoothSupport() {
        // Check for BLE support - also checked from Android manifest.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            exitApp("No BLE Support...");
        }

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            exitApp("No BLE Support...");
        }

        //noinspection ConstantConditions
        if (!btAdapter.isEnabled()) {
            enableBluetooth();
        }
    }

    private void exitApp(String reason) {
        // Something failed, exit the app and send a toast as to why
        Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_LONG).show();
        finish();
    }

    private void enableBluetooth() {
        // Ask user to enable bluetooth if it is currently disabled
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQ_BLUETOOTH_ENABLE);
    }
    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        //reached
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new BlueteethActivity.NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        }
    }

    /**
     * @param activity The corresponding Activity requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author Ralf Wondratschek
     *
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        String testNFC = readText(ndefRecord);
                        String[] parsedNFC = testNFC.split(",");
                        coasterInfo = new CoasterInfo(parsedNFC[0], parsedNFC[1], Boolean.valueOf(parsedNFC[2]),Boolean.valueOf(parsedNFC[3])
                                ,Boolean.valueOf(parsedNFC[4]),parsedNFC[5],parsedNFC[6],parsedNFC[7]);

                        final Intent intent = new Intent(BlueteethActivity.this, BlueteethDeviceActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        //intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, coasterInfo.getCoasterId());
                        //intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, coasterInfo.getBtDeviceAddress());
                        intent.putExtra("coasterInfo", coasterInfo);

                        startActivity(intent);


                        return testNFC;
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(BlueteethActivity.this, result, Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Read content: " + result);
            }
        }
    }
}
