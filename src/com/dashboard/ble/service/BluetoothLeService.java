/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dashboard.ble.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.dashboard.ble.constants.Constants;
import com.dashboard.ble.constants.GattAttributes;
import com.dashboard.ble.model.DeviceInfo;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
@SuppressLint("NewApi")
public class BluetoothLeService extends Service {
	private final static String TAG = BluetoothLeService.class.getSimpleName();

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothGatt mBluetoothGatt;
	private DeviceInfo deviceInfo;
	
    public final static String ACTION_GATT_CONNECTED           	= "com.antilost.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED        	= "com.antilost.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED 	= "com.antilost.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_READ			 		= "com.antilost.le.ACTION_DATA_READ";
    public final static String ACTION_DATA_NOTIFY           		= "com.antilost.le.ACTION_DATA_NOTIFY";
    public final static String ACTION_DATA_WRITE			 		= "com.antilost.le.ACTION_DATA_WRITE";
    public final static String ACTION_DATA_RSSI           			= "com.antilost.le.ACTION_DATA_RSSI";
    public final static String EXTRA_DATA                      	= "com.antilost.le.EXTRA_DATA";
    public final static String EXTRA_UUID 						 	= "com.antilost.le.EXTRA_UUID";
    public final static String EXTRA_STATUS 					 	= "com.antilost.le.EXTRA_STATUS";
    public final static String EXTRA_ADDRESS 					 	= "com.antilost.le.EXTRA_ADDRESS";
    public final static String EXTRA_RSSI 						 	= "com.antilost.le.EXTRA_RSSI";

    private Map<String,BluetoothGatt> mBluetoothGattMap = new HashMap<String,BluetoothGatt>();
    private Map<String,BluetoothGattService> mCustomBluetoothGattServiceMap = new HashMap<String, BluetoothGattService>();
    
    private static BluetoothLeService This;
    
	// Implements callback methods for GATT events that the app cares about. For
	// example,
	// connection change and services discovered.
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,int newState) {
			BluetoothDevice device = gatt.getDevice();
            String address = device.getAddress();
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				if(status == BluetoothGatt.GATT_SUCCESS) {
					broadcastUpdate(ACTION_GATT_CONNECTED, address, status);
		            addBluetoothGatt2Map(address, gatt);
					// Attempts to discover services after successful connection.
					Log.i(TAG, "Attempting to start service discovery:"
							+ mBluetoothGatt.discoverServices());
				}

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				if(status == BluetoothGatt.GATT_SUCCESS) {
					Log.i(TAG, "Disconnected from GATT server.");
					broadcastUpdate(ACTION_GATT_DISCONNECTED, address, status);
	                removeBluetoothGattFromMap(address);
				}
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				BluetoothDevice device = gatt.getDevice();
	            broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, device.getAddress(), status);
	           BluetoothGattService customService = gatt.getService(UUID.fromString(Constants.UUID_XMAS_TREE_SERVICE));
	           if(customService != null){
	        	   mCustomBluetoothGattServiceMap.put(device.getAddress(), customService);
	           }
				// 获取所有的服务
				List<BluetoothGattService> supportedGattServices = mBluetoothGatt.getServices();
				for (int i = 0; i < supportedGattServices.size(); i++) {
					Log.e("AAAAA", "1:BluetoothGattService UUID=:" + supportedGattServices.get(i).getUuid());
					List<BluetoothGattCharacteristic> listGattCharacteristic = supportedGattServices.get(i).getCharacteristics();
					for (int j = 0; j < listGattCharacteristic.size(); j++) {
						Log.e("a", "2:   BluetoothGattCharacteristic UUID=:" + listGattCharacteristic.get(j).getUuid());
					}
				}
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			System.out.println("onCharacteristicRead");
			if (status == BluetoothGatt.GATT_SUCCESS) {
				BluetoothDevice device = gatt.getDevice();
	        	broadcastUpdate(ACTION_DATA_READ,device.getAddress(),characteristic, status);
			}
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			System.out.println("onDescriptorWriteonDescriptorWrite = " + status
					+ ", descriptor =" + descriptor.getUuid().toString());
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			BluetoothDevice device = gatt.getDevice();
        	broadcastUpdate(ACTION_DATA_NOTIFY,device.getAddress(),characteristic, BluetoothGatt.GATT_SUCCESS);
			if (characteristic.getValue() != null) {

				System.out.println(characteristic.getStringValue(0));
			}
			System.out.println("--------onCharacteristicChanged-----");
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			BluetoothDevice device = gatt.getDevice();
			broadcastUpdate(ACTION_DATA_RSSI,device.getAddress(),rssi,status);
		}

		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			BluetoothDevice device = gatt.getDevice();
        	broadcastUpdate(ACTION_DATA_WRITE,device.getAddress(),characteristic, status);
			System.out.println("--------write success----- status:" + status);

		};
	};
	
	private void broadcastUpdate(String action,String address, int rssi, final int status) {
    	Intent intent = new Intent(action);
    	intent.putExtra(EXTRA_ADDRESS, address);
        intent.putExtra(EXTRA_RSSI, rssi);
        intent.putExtra(EXTRA_STATUS, status);
        sendBroadcast(intent);
    }
    
    private void broadcastUpdate(final String action, final String address, final int status) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_ADDRESS, address);
        intent.putExtra(EXTRA_STATUS, status);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,String address, final BluetoothGattCharacteristic characteristic, final int status) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_ADDRESS, address);
        intent.putExtra(EXTRA_UUID, characteristic.getUuid().toString());
        intent.putExtra(EXTRA_DATA, characteristic.getValue());
        intent.putExtra(EXTRA_STATUS, status);
        sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
    	super.onCreate();
    	This = this;
    	
    	initialize();
    }

	public class LocalBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that
		// BluetoothGatt.close() is called
		// such that resources are cleaned up properly. In this particular
		// example, close() is
		// invoked when the UI is disconnected from the Service.
		// close();
		for(String address : mBluetoothGattMap.keySet()) {
			close(address);
		}
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
    
    private void addBluetoothGatt2Map(String address,BluetoothGatt bluetoothGatt)
    {
    	if(TextUtils.isEmpty(address) ||bluetoothGatt == null)
    	{
    		Log.i(TAG, "address is null or bluetoothGatt is null");
    		return;
    	}
    	mBluetoothGattMap.put(address, bluetoothGatt);
    }
    
    
    private void removeBluetoothGattFromMap(String address)
    {
    	if(TextUtils.isEmpty(address))
    	{
    		Log.i(TAG, "address is null or bluetoothGatt is null");
    		return;
    	}
    	BluetoothGatt bluetoothGatt = mBluetoothGattMap.remove(address);
    	if(bluetoothGatt != null) {
    		bluetoothGatt.close();
    	}
    	mCustomBluetoothGattServiceMap.remove(address);
    }

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG,
					"BluetoothAdapter not initialized or unspecified address.");
			return false;
		}
		
		mBluetoothGatt = mBluetoothGattMap.get(address);

		// Previously connected device. Try to reconnect. (��ǰ���ӵ��豸�� ������������)
		if (mBluetoothGatt != null) {
			Log.d(TAG,"Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {
//				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		addBluetoothGatt2Map(address, mBluetoothGatt);
		Log.d(TAG, "Trying to create a new connection.");
//		mConnectionState = STATE_CONNECTING;
		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect(String address) {
		if (mBluetoothAdapter == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		
		mBluetoothGatt = mBluetoothGattMap.get(address);
		if(mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close(String address) {
		mBluetoothGatt = mBluetoothGattMap.remove(address);
		mBluetoothGattMap.remove(address);
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}
	
	
    public boolean writeCharacteristic(String address,String characterUuid,String data)
    {
    	BluetoothGattService bluetoothGattService = null;
    	bluetoothGattService = mCustomBluetoothGattServiceMap.get(address);
		if(bluetoothGattService == null){
    		return false;
    	}
		BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(characterUuid));
    	mBluetoothGatt = mBluetoothGattMap.get(address);
    	if(mBluetoothGatt == null || bluetoothGattCharacteristic == null)
    		return false;
    	bluetoothGattCharacteristic.setValue(data);
    	boolean ok = wirteCharacteristic(bluetoothGattCharacteristic);
    	return ok;
    }

	public boolean wirteCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return false;
		}
		return mBluetoothGatt.writeCharacteristic(characteristic);
	}
	
	
	public void readCharacteristic(String address,String characterUuid,boolean isCustom)
    {
		BluetoothGattService bluetoothGattService = mCustomBluetoothGattServiceMap.get(address);
		if(bluetoothGattService == null){
    		return;
    	}
		BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(characterUuid));
    	mBluetoothGatt = mBluetoothGattMap.get(address);
    	if(mBluetoothGatt == null || bluetoothGattCharacteristic == null)
    		return;
    	readCharacteristic(bluetoothGattCharacteristic);
    }

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}
	
	/**
	 * Enables or disables notification on a give characteristic.
	 * 
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public boolean setCharacteristicNotification(String address,String characterUuid,boolean enable,boolean isCustom)
    {
		BluetoothGattService bluetoothGattService = mCustomBluetoothGattServiceMap.get(address);
		if(bluetoothGattService == null){
    		return false;
    	}
		BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(characterUuid));
    	mBluetoothGatt = mBluetoothGattMap.get(address);
    	if(mBluetoothGatt == null || bluetoothGattCharacteristic == null)
    		return false;
    	boolean ok =setCharacteristicNotification(bluetoothGattCharacteristic,enable);
    	return ok;
    }
		/**
	     * Enables or disables notification on a give characteristic.
	     * 
	     * @param characteristic
	     *          Characteristic to act on.
	     * @param enabled
	     *          If true, enable notification. False otherwise.
	     */
	    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
	    	if (mBluetoothAdapter == null || mBluetoothGatt == null) {
				Log.w(TAG, "BluetoothAdapter not initialized");
				return false;
			}
	    	
	    	if (!mBluetoothGatt.setCharacteristicNotification(characteristic, enable)) {
	            Log.w(TAG, "setCharacteristicNotification failed");
	            return false;
	        }

	        BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
	        if (clientConfig == null) {
	            return false;
	        }

	        if (enable) {
	            Log.i(TAG, "enable notification");
	            clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
	        } else {
	            Log.i(TAG, "disable notification");
	            clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
	        }
	        return mBluetoothGatt.writeDescriptor(clientConfig);
	    }
		/*
		 * // This is specific to Heart Rate Measurement. if
		 * (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
		 * System
		 * .out.println("characteristic.getUuid() == "+characteristic.getUuid
		 * ()+", "); BluetoothGattDescriptor descriptor =
		 * characteristic.getDescriptor
		 * (UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
		 * descriptor
		 * .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		 * mBluetoothGatt.writeDescriptor(descriptor); }
		 */
	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 * 
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices(String address) {
		mBluetoothGatt = mBluetoothGattMap.get(address);
		if (mBluetoothGatt == null)
			return null;

		return mBluetoothGatt.getServices();
	}

	/**
	 * Read the RSSI for a connected remote device.
	 * */
	public boolean getRssiVal(String address) {
		if(TextUtils.isEmpty(address)) {
			Log.d(TAG, "the address is null when read rssi");
			return false;
		}
		mBluetoothGatt = mBluetoothGattMap.get(address);
		if (mBluetoothGatt == null)
			return false;
		return mBluetoothGatt.readRemoteRssi();
	}
	
	public void waitIdle(int n)
	{
		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy() {
		mCustomBluetoothGattServiceMap.clear();
		super.onDestroy();
	}
	
	public static BluetoothLeService getInstance() {
		return This;
	}
	
	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
}
