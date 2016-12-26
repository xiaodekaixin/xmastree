package com.dashboard.ble.activity;

import com.dashboard.ble.R;
import com.dashboard.ble.adapter.ClockListAdapter;
import com.dashboard.ble.model.ClockInfo;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class ClockActivity extends BaseActivity{
	private ImageView imgBackIcon;
//	private View twoHourLayout;
//	private View fourHourLayout;
//	private View sixHourLayout;
//	private View eightHourLayout;
//	private View tenHourLayout;
//	private View twelveHourLayout;
	
	private ListView clockListView;
	private ClockListAdapter clockListAdapter;
	
	@Override
	protected void onCreate() {
		setContentView(R.layout.clock_layout);
	}

	@Override
	protected void setupView() {
		findViewById(R.id.tvTitle).setVisibility(View.GONE);
		imgBackIcon = (ImageView) findViewById(R.id.imgBackIcon);
		imgBackIcon.setVisibility(View.VISIBLE);
		
//		twoHourLayout = findViewById(R.id.twoHourLayout);
//		fourHourLayout = findViewById(R.id.fourHourLayout);
//		sixHourLayout = findViewById(R.id.sixHourLayout);
//		eightHourLayout = findViewById(R.id.eightHourLayout);
//		tenHourLayout = findViewById(R.id.tenHourLayout);
//		twelveHourLayout = findViewById(R.id.twelveHourLayout);
		
		clockListView = (ListView)findViewById(R.id.clockListView);
	}

	@Override
	protected void setViewListner() {
		imgBackIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
//		twoHourLayout.setOnClickListener(this);
//		fourHourLayout.setOnClickListener(this);
//		sixHourLayout.setOnClickListener(this);
//		eightHourLayout.setOnClickListener(this);
//		tenHourLayout.setOnClickListener(this);
//		twelveHourLayout.setOnClickListener(this);
		
		clockListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 刷新点击效果
				clockListAdapter.refresh(position);
				ClockInfo clockInfo = (ClockInfo) parent.getItemAtPosition(position);
				if(clockInfo != null) {
					if(clockInfo.isShowMark()) {
						// 打开闹钟
					} else {
						// 取消闹钟
					}
				}
			}
		});
		
		
	}

	@Override
	protected void initViewData() {
		clockListAdapter = new ClockListAdapter(this);
		clockListView.setAdapter(clockListAdapter);
	}
}
