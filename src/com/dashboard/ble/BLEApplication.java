package com.dashboard.ble;

import android.app.Application;

public class BLEApplication extends Application {
	private static BLEApplication instance;
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}
	
	
	public static BLEApplication getInstance()
	{
		return instance;
	}
}
