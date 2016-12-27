package com.dashboard.ble.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.dashboard.ble.R;

public class MenuActivity extends BaseActivity implements OnClickListener {
	private LinearLayout mDashBoard;
	private LinearLayout mDualColor;
	private LinearLayout mRgb;
	
	@Override
	protected void onCreate() {
		 setContentView(R.layout.menu_layout);
	}

	@Override
	protected void setupView() {
		mDashBoard = (LinearLayout)findViewById(R.id.menu_dashboard);
		mDualColor = (LinearLayout)findViewById(R.id.menu_dualcolor);
		mRgb = (LinearLayout)findViewById(R.id.menu_rgb);
	}

	@Override
	protected void setViewListner() {
		mDashBoard.setOnClickListener(this);
		mDualColor.setOnClickListener(this);
		mRgb.setOnClickListener(this);
	}

	@Override
	protected void initViewData() {

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		int viewId = v.getId();
		switch (viewId) {
		case R.id.menu_dashboard:
			intent.setClass(this, DashBoardActivity.class);
			break;
		case R.id.menu_dualcolor:
			intent.setClass(this, FunctionActivity.class);
			break;
		case R.id.menu_rgb:
			intent.setClass(this, FunctionActivity.class);
			break;
		default:
			break;
		}
		startActivity(intent);
	}

}
