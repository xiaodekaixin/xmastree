package com.dashboard.ble.adapter;

import java.util.ArrayList;
import java.util.List;

import com.dashboard.ble.adapter.DeviceListAdapter.ViewHolder;
import com.dashboard.ble.model.ClockInfo;
import com.dashboard.ble.model.DeviceInfo;


import com.dashboard.ble.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ClockListAdapter extends BaseAdapter {
    private List<ClockInfo> mClockInfos;
    private LayoutInflater mInflater;
    private Context mCtx;

    public ClockListAdapter(Context ctx) {
        mInflater = LayoutInflater.from(ctx);
        mCtx = ctx;

        mClockInfos = new ArrayList<ClockInfo>();
        String[] titleArray = mCtx.getResources().getStringArray(R.array.clockTitleArray);
        int[] valueArray = mCtx.getResources().getIntArray(R.array.clockValueArray);
        for (int i = 0; i < titleArray.length; i++) {
            ClockInfo info = new ClockInfo(titleArray[i], valueArray[i]);
            mClockInfos.add(info);
        }
    }

    @Override
    public int getCount() {
        return mClockInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mClockInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.clock_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.imgClockMark = (ImageView) convertView.findViewById(R.id.imgClockMark);
            viewHolder.tvClockTitle = (TextView) convertView.findViewById(R.id.tvClockTitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ClockInfo clockInfo = mClockInfos.get(position);
        viewHolder.tvClockTitle.setText(clockInfo.getTitle());
        if (clockInfo.isShowMark()) {
            viewHolder.imgClockMark.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imgClockMark.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView imgClockMark;
        TextView tvClockTitle;
    }

    public void refresh(int position) {
        ClockInfo curClockInfo = mClockInfos.get(position);
        if (curClockInfo.isShowMark()) {
            curClockInfo.setShowMark(false);
        } else {
            int size = mClockInfos.size();
            for (int i = 0; i < size; i++) {
                mClockInfos.get(i).setShowMark(false);
            }
            mClockInfos.get(position).setShowMark(true);
        }
        this.notifyDataSetChanged();
    }

}
