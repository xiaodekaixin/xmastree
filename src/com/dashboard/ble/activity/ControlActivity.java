package com.dashboard.ble.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dashboard.ble.R;
import com.dashboard.ble.constants.Constants;
import com.dashboard.ble.model.DeviceInfo;
import com.dashboard.ble.service.BluetoothLeService;

public class ControlActivity extends BaseActivity implements OnClickListener{
	final String TAG = "ControlActivity";
	private TextView tvTitle;
	private ImageView imgBackIcon;
	private ImageView imgSendData;
	
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothLeService mBluetoothLeService;
	private DeviceInfo deviceInfo;
    private static final int REQUEST_ENABLE_BT = 1;
    private boolean isLightOn = true;
    
	// 是否报警
	final int CMD_ALRM = 0x01;
	final int DEVICE_CONNECTED = 0x02;
	final int DEVICE_DISCONNECTED = 0x03;
	
	private IntentFilter mFilter;
	
	@Override
	protected void onCreate() {
		setContentView(R.layout.control_layout);
	}
	
	@Override
	protected void setupView() {
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		imgBackIcon = (ImageView)findViewById(R.id.imgBackIcon);
		imgSendData = (ImageView)findViewById(R.id.imgSendData);
		imgBackIcon.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void setViewListner() {
		imgBackIcon.setOnClickListener(this);
		imgSendData.setOnClickListener(this);
	}

	@Override
	protected void initViewData() {
		initBluetoothDevice();
		
	    Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        
        deviceInfo = (DeviceInfo) getIntent().getSerializableExtra("deviceInfo");
        if(deviceInfo != null) {
        	tvTitle.setText(deviceInfo.getName());
        }
        
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
	
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish(); 
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    
	 @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBluetoothLeService != null){
            unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }
        unregisterReceiver(mGattReceiver);
        deviceInfo = null;
    }
	 
	 public void writeFirewallData(String data)
	 {
		 if(deviceInfo != null && deviceInfo.isConnectState()) {
			 writeDataOnSubThread(data);
		 }else{
			 Toast.makeText(this, getString(R.string.no_selected_device), Toast.LENGTH_SHORT).show();
		 }
	 }

	private void writeDataOnSubThread(final String data) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if(mBluetoothLeService != null) 
				 {
					boolean ok = mBluetoothLeService.writeCharacteristic(deviceInfo.getAddress(), Constants.UUID_XMAS_TREE,data);
					if(ok){
						mBluetoothLeService.waitIdle(Constants.GATT_TIMEOUT);
					}
				 }
			}
		});
	}
	 
	 private IntentFilter makeGattUpdateIntentFilter() {
	        IntentFilter intentFilter = new IntentFilter();
	        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
	        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
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
		         } else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
//		        	 String address = intent.getStringExtra(BluetoothLeService.EXTRA_ADDRESS);
//		        	 byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
//		        	 if(data != null && data.length > 0) {
//		     			  String cmd = new String(data);
//		     			  if(cmd != null && cmd.equals(Constants.CMD_ALM)) 
//		     			  {
//				        	 mHandler.obtainMessage(CMD_ALRM, address).sendToTarget();
//		     			  }
//		     		 }
		         }
			}
		};
		
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgBackIcon:
			finish();
			break;
		case R.id.imgSendData:
			writeCmdToBleDevice();
			break;
		default:
			break;
		}
	}

	private void writeCmdToBleDevice() {
		if(isLightOn) {
			isLightOn = false;
			writeFirewallData(Constants.LED_ON);
		} else {
			isLightOn = true;
			writeFirewallData(Constants.LED_OFF);
		}
//		writeFirewallData(Constants.CMD_ALM);
	}
	
	 private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == CMD_ALRM){
//				writeFirewallData(Constants.REPLY_CMD_ALM);
			}else if(msg.what == DEVICE_CONNECTED){
				String address = (String) msg.obj;
				if(!TextUtils.isEmpty(address)){
					if(deviceInfo != null && address.equalsIgnoreCase(deviceInfo.getAddress())){
						 deviceInfo.setConnectState(true);
					}
				}
			}else if(msg.what == DEVICE_DISCONNECTED){
				String address = (String) msg.obj;
				if(!TextUtils.isEmpty(address)){
					if(deviceInfo != null && address.equalsIgnoreCase(deviceInfo.getAddress())){
						 deviceInfo.setConnectState(false);
					}
				}
			}
		}
	 };
}
