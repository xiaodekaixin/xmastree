package com.dashboard.ble.activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.dashboard.ble.R;


public class FunctionActivity extends BaseActivity {
	private ImageView imgBackIcon;
	
	@Override
	protected void onCreate() {
		setContentView(R.layout.function_layout);
	}

	@Override
	protected void setupView() {
		findViewById(R.id.tvTitle).setVisibility(View.GONE);
		imgBackIcon = (ImageView) findViewById(R.id.imgBackIcon);
		imgBackIcon.setVisibility(View.VISIBLE);
	}

	@Override
	protected void setViewListner() {
		imgBackIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void initViewData() {
		// TODO Auto-generated method stub

	}

}
