package com.dashboard.ble.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.dashboard.ble.R;

public class SplashActivity extends BaseActivity {

	
	@Override
	protected void onCreate() {
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
	                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
	        this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
	          
	        setContentView(R.layout.splash_layout);
	        new Handler().postDelayed(new Runnable() {  
	            public void run() {  
	              Intent it = new Intent();  
	              it.setClass(SplashActivity.this, MenuActivity.class);  
	              startActivity(it);  
	              finish();  
	            }  
	        }, 1000 * 3);
	}

	@Override
	protected void setupView() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setViewListner() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initViewData() {
		// TODO Auto-generated method stub

	}

	protected void initBackIcon() {}

}
