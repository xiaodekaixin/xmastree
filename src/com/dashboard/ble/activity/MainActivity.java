package com.dashboard.ble.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dashboard.ble.R;
import com.dashboard.ble.adapter.DeviceListAdapter;
import com.dashboard.ble.constants.Constants;
import com.dashboard.ble.model.DeviceInfo;
import com.dashboard.ble.service.BluetoothLeService;

public class MainActivity extends BaseActivity implements OnClickListener{
	final String TAG = "MainActivity";
	private ListView lvBleDevice;
	private TextView tvTitle;
	private ImageView imgAddDevice;
	private ImageView imgBackIcon;
	
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothLeService mBluetoothLeService;
	private DeviceInfo deviceInfo;
	private DeviceListAdapter deviceListAdapter;
    private boolean mScanning;
    private static final int REQUEST_ENABLE_BT = 1;
	// 是否报警
	final int DEVICE_CONNECTED = 0x02;
	final int DEVICE_DISCONNECTED = 0x03;
	
	private List<DeviceInfo> mDeviceInfoList = new ArrayList<DeviceInfo>();
	
	private IntentFilter mFilter;
	
	@Override
	protected void onCreate() {
		setContentView(R.layout.device_list_layout);
	}
	
	@Override
	protected void setupView() {
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		imgAddDevice = (ImageView)findViewById(R.id.imgAddDevice);
		imgBackIcon = (ImageView)findViewById(R.id.imgBackIcon);
		lvBleDevice = (ListView)findViewById(R.id.lvBleDevice);
		
		imgBackIcon.setImageResource(R.drawable.ic_company);
		
		imgBackIcon.setVisibility(View.VISIBLE);
		imgAddDevice.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void setViewListner() {
		imgAddDevice.setOnClickListener(this);
		imgBackIcon.setOnClickListener(this);
		lvBleDevice.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterview, View view,int id, long position) {
				deviceInfo = mDeviceInfoList.get((int)position);
				if(mScanning) {
					scanLeDevice(false);
					mScanning = false;
				}
				
				if(!deviceInfo.isConnectState()) {
					if(mBluetoothLeService != null) {
						boolean ok = mBluetoothLeService.connect(deviceInfo.getAddress());
						if(ok) {
							mBluetoothLeService.waitIdle(Constants.GATT_TIMEOUT);
						}
					}
				} else {
					showBluetoothDetail();
				}
			}
		});
	}

	@Override
	protected void initViewData() {
		tvTitle.setText(getString(R.string.app_name));
		
		initBluetoothDevice();
		
	    Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        
        deviceListAdapter = new DeviceListAdapter(this, mDeviceInfoList);
        lvBleDevice.setAdapter(deviceListAdapter);
        
        mFilter = makeGattUpdateIntentFilter();
	    registerReceiver(mGattReceiver, mFilter);
	} 
	
	
	@TargetApi(18)
	private void initBluetoothDevice() {
		// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
	}
	
	
	public void scanLeDevice(boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    Log.d(TAG, "stop scanLeDevice when timeout");
                }
            }, Constants.SCAN_PERIOD);
            mScanning = true;
            boolean ok = mBluetoothAdapter.startLeScan(mLeScanCallback);
            mBluetoothLeService.waitIdle(Constants.GATT_TIMEOUT);
            Log.d(TAG, "start scanLeDevice state : " + ok);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            Log.d(TAG, "stop scanLeDevice!!!");
        }
    }
	
	
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
        	Log.d(TAG, "onServiceConnected");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish(); 
            }
            Log.d(TAG, "start scanLeDevice");
            scanLeDevice(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    
    // Device scan callback.
    @SuppressLint("NewApi")
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	Log.d(TAG, "scan a device address:" + device.getAddress());
                	if (!deviceInfoExists(device.getAddress())) {
                        // New device
                		DeviceInfo deviceInfo = createDeviceInfo(device, rssi);
                        addDevice(deviceInfo);
                        updateDeviceList();
                        Log.d(TAG, "add new device " + device.getAddress());
                        //connectBleDevice(deviceInfo);
                    } else {
                        // Already in list, update RSSI info
                    	DeviceInfo deviceInfo = findDeviceInfo(device.getAddress());
                        deviceInfo.setRssi(rssi);
                    }
                }
            });
        }
    };
    
    private void addDevice(DeviceInfo device) 
    {
        mDeviceInfoList.add(device);
    }

    private boolean deviceInfoExists(String address) {
        for (int i = 0; i < mDeviceInfoList.size(); i++) {
            if (mDeviceInfoList.get(i).getAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }

    private DeviceInfo findDeviceInfo(String address) {
        for (int i = 0; i < mDeviceInfoList.size(); i++) {
            if (mDeviceInfoList.get(i).getAddress().equals(address)) {
                return mDeviceInfoList.get(i);
            }
        }
        return null;
    }
    
    private void deleteDeviceInfoFromList(String address) {
    	int pos = -1;
    	for(int i=0; i<mDeviceInfoList.size(); i++) {
    		if(mDeviceInfoList.get(i).getAddress().equalsIgnoreCase(address)){
    			pos = i;
    			break;
    		}
    	}
    	
    	if(pos != -1) {
    		mDeviceInfoList.remove(pos);
    	}
    }
    
    
    private DeviceInfo createDeviceInfo(BluetoothDevice device, int rssi) {
    	DeviceInfo deviceInfo = new DeviceInfo(device.getAddress(),device.getName(),false,rssi);
        return deviceInfo;
    }
    
	
	 @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBluetoothLeService != null){
            unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }
        unregisterReceiver(mGattReceiver);
        mDeviceInfoList.clear();
        updateDeviceList();
        deviceInfo = null;
    }
	 
//	 private void saveCurrentDeviceInfo() {
//		 if(deviceInfo != null && deviceInfo.isConnectState()) {
//			 SharePreferenceWrapper.getInstance().putStringValue("address", deviceInfo.getAddress());
//		 }else{
//			 SharePreferenceWrapper.getInstance().putStringValue("address", "");
//		 }
//	 }
//	 private void readCurrentDeviceInfo() {
//		 String address =  SharePreferenceWrapper.getInstance().getStringValue("address");
//		 if(!TextUtils.isEmpty(address)) {
//			 deviceInfo = DeviceDao.getInstance().findDeviceInfoByAddress(address);
//		 }
//	 }
//	
//	 public void writeFirewallData(String data)
//	 {
//		 if(deviceInfo != null && deviceInfo.isConnectState()) {
//			 writeDataOnSubThread(data,true);
//		 }else{
//			 Toast.makeText(this, getString(R.string.no_selected_device), Toast.LENGTH_SHORT).show();
//		 }
//	 }
//
//	private void writeDataOnSubThread(final String data,final boolean isCustom) {
//		mHandler.post(new Runnable() {
//			@Override
//			public void run() {
//				if(mBluetoothLeService != null) 
//				 {
//					boolean ok = mBluetoothLeService.writeCharacteristic(deviceInfo.getAddress(), Constants.UUID_WRITE_DATA,data,isCustom);
//					if(ok){
//						mBluetoothLeService.waitIdle(Constants.GATT_TIMEOUT);
//					}
//				 }
//			}
//		});
//	}
	 
	 private IntentFilter makeGattUpdateIntentFilter() {
	        IntentFilter intentFilter = new IntentFilter();
	        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
	        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
	        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
	        return intentFilter;
		 }
	 
		private final BroadcastReceiver mGattReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				 if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
					 String address = intent.getStringExtra(BluetoothLeService.EXTRA_ADDRESS);
					 mHandler.obtainMessage(DEVICE_CONNECTED, address).sendToTarget();
		         } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
		        	 String address = intent.getStringExtra(BluetoothLeService.EXTRA_ADDRESS);
		        	 mHandler.obtainMessage(DEVICE_DISCONNECTED, address).sendToTarget();
		         } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
		        	 Log.d(TAG, "discoverd service : ");
		         }
			}
		};
	
	 private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == DEVICE_CONNECTED){
				String address = (String) msg.obj;
				if(!TextUtils.isEmpty(address)){
					//DeviceDao.getInstance().updateConnectState(address, true);
					if(deviceInfo != null && address.equalsIgnoreCase(deviceInfo.getAddress())){
						 deviceInfo.setConnectState(true);
						 showBluetoothDetail();
					}
					updateDeviceList();
				}
			}else if(msg.what == DEVICE_DISCONNECTED){
				String address = (String) msg.obj;
				if(!TextUtils.isEmpty(address)){
					//DeviceDao.getInstance().updateConnectState(address, false);
					if(deviceInfo != null && address.equalsIgnoreCase(deviceInfo.getAddress())){
						 deviceInfo.setConnectState(false);
					}
					// 已断开的ble设备需要从缓存中删除掉
					deleteDeviceInfoFromList(address);
					updateDeviceList();
				}
			}
		}; 
	 };
	 
	 public void updateDeviceList() {
		if(deviceListAdapter != null) {
			deviceListAdapter.notifyDataSetChanged();
		}
	 }
	 
	 /**
	  * 连接上蓝牙设备后,进入蓝牙控制界面
	  */
	 private void showBluetoothDetail() {
		 Intent intent = new Intent(this,ControlActivity.class);
		 intent.putExtra("deviceInfo", deviceInfo);
		 startActivity(intent);
	 }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgBackIcon:
			showAboutActivity();
			break;
		case R.id.imgAddDevice:
			scanBluetoothDevice();
			break;
		default:
			break;
		}
	}
	
	
	private void showAboutActivity() {
		Intent intent = new Intent(this,AboutActivity.class);
		startActivity(intent);
	}
	
	private void scanBluetoothDevice() {
		Log.d(TAG, "click add new device, scan state :" + mScanning);
		if(!mScanning) {
			scanLeDevice(true);
		} else {
			scanLeDevice(false);
		}
	}
}
