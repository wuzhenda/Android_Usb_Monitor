package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.example.myapp.util.UsbMonitor;

public class MyActivity extends Activity implements UsbMonitor.Listener {

    static final String TAG = MyActivity.class.getName();

    private Context mContext;

    private UsbMonitor mUsbMonitor;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mContext = getApplicationContext();

        mUsbMonitor = new UsbMonitor(mContext.getApplicationContext());
        mUsbMonitor.addListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUsbMonitor.resume();
    }

    @Override
    public void onPause() {
        super.onStop();
        mUsbMonitor.pause();
    }

    private void notifyDeviceDetect(boolean online) {
        if (online) {
            Toast.makeText(mContext,"attach",Toast.LENGTH_SHORT ).show();
        } else {
            Toast.makeText(mContext,"detach",Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    public void deviceAdded(UsbDevice device) {
        Log.d(TAG, "Adb device Added:" + device.toString());
        notifyDeviceDetect(true);
    }

    @Override
    public void deviceRemoved(UsbDevice device) {
        Log.d(TAG,"Adb device removeded:" + device.toString());
        notifyDeviceDetect(false);
    }

}
