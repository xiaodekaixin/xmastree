package com.dashboard.ble.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dashboard.ble.R;
import com.dashboard.ble.activity.LedGridActivity;
import com.dashboard.ble.model.LedInfo;
import com.dashboard.ble.view.GifView;
import com.dashboard.ble.view.GifView2;

public class LedGridAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<LedInfo> mLedInfos;
	
	public LedGridAdapter(Context context, List<LedInfo> ledInfos) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.mLedInfos = ledInfos;
	}

	@Override
	public int getCount() {
		return mLedInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return mLedInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
		if (convertView == null) {
        	convertView = mInflater.inflate(R.layout.led_item_layout, null);
        	viewHolder = new ViewHolder();
        	viewHolder.imgItemLogo = (GifView)convertView.findViewById(R.id.imgLogo);
        	viewHolder.tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
        	convertView.setTag(viewHolder);
        } else {
        	viewHolder = (ViewHolder) convertView.getTag();
        }
		convertView.setTag(R.id.led_item_position, position);
        final LedInfo ledInfo = mLedInfos.get(position);
        viewHolder.tvTitle.setText(ledInfo.getTitle());
		if (ledInfo.isPress()) {
			if (viewHolder.imgItemLogo.isPaused()) {
				viewHolder.imgItemLogo.play();
			}
		} else {
			if (viewHolder.imgItemLogo.isPlaying()) {
				viewHolder.imgItemLogo.pause();
			}
		}
        
        convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int pos = (Integer) v.getTag(R.id.led_item_position);
				Log.d("LedInfo", "click,pos=" + pos);
				((LedGridActivity) mContext).refreshLedGrid(pos);
			}
		});
        return convertView;
	}
	
	static class ViewHolder {
		GifView imgItemLogo;
		TextView tvTitle;
	}
}
