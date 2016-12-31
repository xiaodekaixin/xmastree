package com.dashboard.ble.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.dashboard.ble.R;

public abstract class BaseActivity extends Activity {
	protected ImageView imgBackIcon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		onCreate();

		initBackIcon();
		
		setupView();
		
		setViewListner();
		
		initViewData();
	}
	
	protected abstract void onCreate();
	
	protected abstract void setupView();
	
	protected abstract void setViewListner();
	
	protected abstract void initViewData();

	protected void initBackIcon() {
		imgBackIcon = (ImageView) findViewById(R.id.imgBackIcon);
		imgBackIcon.setVisibility(View.VISIBLE);
		imgBackIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
}
