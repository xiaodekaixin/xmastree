package com.dashboard.ble.activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.dashboard.ble.R;

public class AboutActivity extends BaseActivity{
	final String TAG = "AboutActivity";
	private TextView tvTitle;
	private ImageView imgBackIcon;
	
	
	@Override
	protected void onCreate() {
		setContentView(R.layout.about_layout);
	}
	
	@Override
	protected void setupView() {
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		imgBackIcon = (ImageView)findViewById(R.id.imgBackIcon);
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
		tvTitle.setText(getString(R.string.information));
	} 
}
