package com.example.myapp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.ArrayList;


public class UsbMonitor {

	private static final String TAG = UsbMonitor.class.getName();

	private final Context mContext;
	private final UsbManager mUsbManager;
	private final ArrayList<Listener> mListeners = new ArrayList<Listener>();

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			UsbDevice usbDevice = (UsbDevice) intent
					.getParcelableExtra(UsbManager.EXTRA_DEVICE);
			String deviceName = usbDevice.getDeviceName();

			if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
				if (isCamera(usbDevice)) {
					Log.d(TAG, "it's camera device,maybe mtp device");
				}

				for (Listener listener : mListeners) {
					listener.deviceAdded(usbDevice);
				}
			} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				Log.d(TAG, "adb interface removed");
				for (Listener listener : mListeners) {
					listener.deviceRemoved(usbDevice);
				}
			}
		}
	};

	/**
	 * An interface for being notified when devices are attached or
	 * removed. In the current implementation
	 */
	public interface Listener {
		public void deviceAdded(UsbDevice device);
		public void deviceRemoved(UsbDevice device);
	}

	static private boolean isCamera(UsbDevice device) {
		int count = device.getInterfaceCount();
		for (int i = 0; i < count; i++) {
			UsbInterface intf = device.getInterface(i);
			if (intf.getInterfaceClass() == UsbConstants.USB_CLASS_STILL_IMAGE
					&& intf.getInterfaceSubclass() == 1
					&& intf.getInterfaceProtocol() == 1) {
				return true;
			}
		}
		return false;
	}

	/**
	 *  constructor
	 */
	public UsbMonitor(Context context) {
		mContext = context;
		mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

		// check for existing devices
		for (UsbDevice device : mUsbManager.getDeviceList().values()) {
			Log.d(TAG, "get adb in initial,possible get wrong device:" + device);
		}
	}

	public void resume() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

		mContext.registerReceiver(mUsbReceiver, filter);
	}
	/**
	 * Closes all resources
	 */
	public void pause() {
		mContext.unregisterReceiver(mUsbReceiver);
	}

	public void addListener(Listener listener) {
		if (!mListeners.contains(listener)) {
			mListeners.add(listener);
		}
	}

	public void removeListener(Listener listener) {
		mListeners.remove(listener);
	}

}
