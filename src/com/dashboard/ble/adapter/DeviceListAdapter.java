package com.dashboard.ble.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dashboard.ble.R;
import com.dashboard.ble.model.DeviceInfo;

public class DeviceListAdapter extends BaseAdapter {
        private List<DeviceInfo> mDevices;
        private LayoutInflater mInflater;
        private Context mCtx;

        public DeviceListAdapter(Context context, List<DeviceInfo> devices) {
            mInflater = LayoutInflater.from(context);
            mDevices = devices;
            mCtx = context;
        }

        @Override
        public int getCount() {
            return mDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return mDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	ViewHolder viewHolder = null;
            if (convertView == null) {
            	convertView = mInflater.inflate(R.layout.device_listitem_layout, null);
            	viewHolder = new ViewHolder();
            	viewHolder.imgXmasTree = (ImageView)convertView.findViewById(R.id.imgXmasTree);
            	viewHolder.tvDeviceName = (TextView)convertView.findViewById(R.id.tvDeviceName);
            	viewHolder.tvDeviceAddress = (TextView)convertView.findViewById(R.id.tvDeviceAddress);
            	viewHolder.tvDeviceState = (TextView)convertView.findViewById(R.id.tvDeviceState);
            	convertView.setTag(viewHolder);
            } else {
            	viewHolder = (ViewHolder) convertView.getTag();
            }

            final DeviceInfo deviceInfo = mDevices.get(position);
            viewHolder.tvDeviceName.setText(deviceInfo.getName());
            viewHolder.tvDeviceAddress.setText(deviceInfo.getAddress());
            if(deviceInfo.isConnectState()) {
            	viewHolder.tvDeviceState.setText(mCtx.getString(R.string.device_connected));
            }else{
            	viewHolder.tvDeviceState.setText(mCtx.getString(R.string.device_discontected));
            }
            return convertView;
        }
        
        static class ViewHolder {
    		ImageView imgXmasTree;
    		TextView tvDeviceName;
    		TextView tvDeviceAddress;
    		TextView tvDeviceState;
    	}
    }