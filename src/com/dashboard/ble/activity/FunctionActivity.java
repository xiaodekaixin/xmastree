package com.dashboard.ble.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.dashboard.ble.R;


public class FunctionActivity extends BaseActivity implements View.OnClickListener {
	private ImageView imgBackIcon;
	private ImageView imgClose;
	private ImageView imgTree;
	private ImageView imgMusic;
	private ImageView imgClock;
	
	@Override
	protected void onCreate() {
		setContentView(R.layout.function_layout);
	}

	@Override
	protected void setupView() {
		findViewById(R.id.tvTitle).setVisibility(View.GONE);
		imgBackIcon = (ImageView) findViewById(R.id.imgBackIcon);
		imgBackIcon.setVisibility(View.VISIBLE);
		
		imgClose = (ImageView)findViewById(R.id.imgClose);
		imgTree = (ImageView)findViewById(R.id.imgTree);
		imgMusic = (ImageView)findViewById(R.id.imgMusic);
		imgClock = (ImageView)findViewById(R.id.imgClock);
	}

	@Override
	protected void setViewListner() {
		imgBackIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		imgClose.setOnClickListener(this);
		imgTree.setOnClickListener(this);
		imgMusic.setOnClickListener(this);
		imgClock.setOnClickListener(this);
	}

	@Override
	protected void initViewData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.imgClose:
			finish();
			break;
		case R.id.imgTree:
			showLedGridActivity();
			break;
		case R.id.imgMusic:
			showMusicActivity();
			break;
		case R.id.imgClock:
			showClockActivity();
			break;
		default:
			break;
		}
	}
	
	private void showClockActivity() {
		Intent intent = new Intent();
		intent.setClass(this, ClockActivity.class);
		startActivity(intent);
	}
	
	private void showLedGridActivity() {
		Intent intent = new Intent();
		intent.setClass(this, LedGridActivity.class);
		startActivity(intent);
	}

	private void showMusicActivity() {
		Intent intent = new Intent();
		intent.setClass(this, MusicActivity.class);
		startActivity(intent);
	}
}
