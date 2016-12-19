package com.dashboard.ble.activity;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		onCreate();
		
		setupView();
		
		setViewListner();
		
		initViewData();
	}
	
	protected abstract void onCreate();
	
	protected abstract void setupView();
	
	protected abstract void setViewListner();
	
	protected abstract void initViewData();
	
	
}
