package com.dashboard.ble.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dashboard.ble.R;
import com.dashboard.ble.activity.LedGridActivity;
import com.dashboard.ble.adapter.ClockListAdapter.ViewHolder;
import com.dashboard.ble.model.LedInfo;
import com.lidroid.xutils.db.sqlite.CursorUtils.FindCacheSequence;

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
	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
		if (convertView == null) {
        	convertView = mInflater.inflate(R.layout.led_item_layout, null);
        	viewHolder = new ViewHolder();
        	viewHolder.imgItemLogo = (ImageView)convertView.findViewById(R.id.imgLogo);
        	viewHolder.tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
        	convertView.setTag(viewHolder);
        	convertView.setTag(10, position);
        	convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int pos = (Integer) v.getTag(10);
					((LedGridActivity)mContext).refreshLedGrid(pos);
				}
			});
        } else {
        	viewHolder = (ViewHolder) convertView.getTag();
        }
        final LedInfo ledInfo = mLedInfos.get(position);
        viewHolder.tvTitle.setText(ledInfo.getTitle());
        if(ledInfo.isPress()) {
        	viewHolder.imgItemLogo.setImageResource(R.drawable.ic_logo);;
        } else {
        	viewHolder.imgItemLogo.setImageResource(R.drawable.ic_launcher);;
        }
        return convertView;
	}
	
	static class ViewHolder {
		ImageView imgItemLogo;
		TextView tvTitle;
	}
}
