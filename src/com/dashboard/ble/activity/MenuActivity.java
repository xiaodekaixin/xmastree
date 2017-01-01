package com.dashboard.ble.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.dashboard.ble.R;

public class MenuActivity extends BaseActivity implements OnClickListener {
	private LinearLayout mDashBoard;
	private LinearLayout mTwelveRgb;
	private LinearLayout mFortyRgb;
	
	@Override
	protected void onCreate() {
		 setContentView(R.layout.menu_layout);
	}

	@Override
	protected void setupView() {
		mDashBoard = (LinearLayout)findViewById(R.id.menuDashboard);
		mTwelveRgb = (LinearLayout)findViewById(R.id.menuTwelveRgb);
		mFortyRgb = (LinearLayout)findViewById(R.id.menuFortyRgb);
	}

	@Override
	protected void setViewListner() {
		mDashBoard.setOnClickListener(this);
		mTwelveRgb.setOnClickListener(this);
		mFortyRgb.setOnClickListener(this);
	}

	@Override
	protected void initViewData() {

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		int viewId = v.getId();
		switch (viewId) {
		case R.id.menuDashboard:
			intent.setClass(this, DashBoardActivity.class);
			break;
		case R.id.menuTwelveRgb:
			intent.setClass(this, FunctionActivity.class);
			intent.putExtra("sourceType","twelveRgb");
			break;
		case R.id.menuFortyRgb:
			intent.setClass(this, FunctionActivity.class);
			intent.putExtra("sourceType","fortyRgb");
			break;
		default:
			break;
		}
		startActivity(intent);
	}

}
