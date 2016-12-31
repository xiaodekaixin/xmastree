package com.dashboard.ble.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.dashboard.ble.R;
import com.dashboard.ble.adapter.LedGridAdapter;
import com.dashboard.ble.model.LedInfo;

public class LedGridActivity extends BaseActivity {
	private int[] dualColorLedDatas = {
			0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,
			0x0a,0x0b,0x0c,0x0c,0x0d,0x0e,0x0f,0x10,0x11,
			0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,0x1a,
			0x1b,0x1c,0x1c,0x1d,0x1e,0x1f,0x21,0x22,0x23,
			0x24,0x25,0x26
		};
	
	private int[] twelveLedDatas = {
			0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c
	};
	
	private View imgBackIcon;
	private GridView ledGridView;
	private ImageView imgShake;
	private String mSourceType;
	
	private int ledData = 0;
	
	private List<LedInfo> ledInfos;
	private LedGridAdapter ledGridAdapter;

	@Override
	protected void onCreate() {
		setContentView(R.layout.led_grid_layout);
	}

	@Override
	protected void setupView() {
		findViewById(R.id.tvTitle).setVisibility(View.GONE);

		ledGridView = (GridView)findViewById(R.id.ledGridView);
		imgShake = (ImageView)findViewById(R.id.imgAddDevice);
		imgShake.setVisibility(View.VISIBLE);
	}

	@Override
	protected void setViewListner() {
		imgShake.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startShakeActivity();
			}
		});
	}

	@Override
	protected void initViewData() {
		mSourceType =  getIntent().getStringExtra("sourceType");

		ledInfos = new ArrayList<LedInfo>();

		if(mSourceType == "dualcolor") {
			String[] dualColorLedTitleArray = getResources().getStringArray(R.array.dualColorLedArray);
			for (int i = 0; i < dualColorLedTitleArray.length; i++) {
				LedInfo ledInfo = new LedInfo(dualColorLedTitleArray[i], dualColorLedDatas[i]);
				ledInfos.add(ledInfo);
			}
		} else {
			String[] twelveLedArray = getResources().getStringArray(R.array.twelveLedArray);
			for (int i = 0; i < twelveLedArray.length; i++) {
				LedInfo ledInfo = new LedInfo(twelveLedArray[i], twelveLedDatas[i]);
				ledInfos.add(ledInfo);
			}
		}

		ledGridAdapter = new LedGridAdapter(this, ledInfos);
		ledGridView.setAdapter(ledGridAdapter);
	}
	
	public void refreshLedGrid(int position) {
		for (LedInfo tmpLedInfo : ledInfos) {
			tmpLedInfo.setPress(false);
		}
		LedInfo ledInfo = ledInfos.get(position);
		Log.d("LedInfo","refresh,position="+position+",ledInfo=" + ledInfo);
		ledInfo.setPress(true);
		ledGridAdapter.notifyDataSetChanged();
		ledData = ledInfo.getData();
	}
	
	private void startShakeActivity() {
		Intent intent = new Intent();
		intent.setClass(this, ShakeActivity.class);
		intent.putExtra("ledData", ledData);
		startActivity(intent);
	}
}
