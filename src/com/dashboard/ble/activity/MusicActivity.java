package com.dashboard.ble.activity;

import com.dashboard.ble.R;

import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicActivity extends BaseActivity implements View.OnClickListener {
	private ImageView imgBackIcon;
	private TextView chrismasTreeMusic;
	private TextView myMusic;
	
	private ImageView rotatePicture;
	private ImageView playPicture;
	private ImageView pausePicture;
	
	@Override
	protected void onCreate() {
		setContentView(R.layout.music_layout);
	}

	@Override
	protected void setupView() {
		chrismasTreeMusic = (TextView)findViewById(R.id.christmasTreeMusic);
		myMusic = (TextView)findViewById(R.id.myMusic);
		
		findViewById(R.id.tvTitle).setVisibility(View.GONE);
		imgBackIcon = (ImageView) findViewById(R.id.imgBackIcon);
		imgBackIcon.setVisibility(View.VISIBLE);
	}

	@Override
	protected void setViewListner() {
		imgBackIcon.setOnClickListener(this);
		chrismasTreeMusic.setOnClickListener(this);
		myMusic.setOnClickListener(this);
		
		playPicture.setOnClickListener(this);
		pausePicture.setOnClickListener(this);
	}

	@Override
	protected void initViewData() {
		

	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.imgBackIcon:
			finish();
			break;
		case R.id.christmasTreeMusic:
			break;
		case R.id.myMusic:
			break;
		case R.id.playPicture:
			startRotateAnim();
			break;
		case R.id.pausePicture:
			break;
		default:
			break;
		}
	}
	
	
	private void startRotateAnim() {
		ObjectAnimator anim = ObjectAnimator.ofFloat(rotatePicture, "rotation", 0f, 360f);  
        anim.setDuration(5000);  
        // 正式开始启动执行动画  
        anim.start(); 
	}
}
