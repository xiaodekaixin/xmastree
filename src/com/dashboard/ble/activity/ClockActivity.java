package com.dashboard.ble.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dashboard.ble.R;
import com.dashboard.ble.adapter.ClockListAdapter;
import com.dashboard.ble.constants.PreferenceUtil;
import com.dashboard.ble.model.ClockInfo;
import com.dashboard.ble.receiver.ClockReceiver;
import com.dashboard.ble.util.SharePreferenceWrapper;

public class ClockActivity extends BaseActivity{
	public static String TAG = "MYCLOCK";
	private ImageView imgBackIcon;
	
	private ListView clockListView;
	private ClockListAdapter clockListAdapter;
	private TextView tvRemainTime;
	private PendingIntent mPendingIntent;
	
	@Override
	protected void onCreate() {
		setContentView(R.layout.clock_layout);
	}

	@Override
	protected void setupView() {
		findViewById(R.id.tvTitle).setVisibility(View.GONE);
		imgBackIcon = (ImageView) findViewById(R.id.imgBackIcon);
		imgBackIcon.setVisibility(View.VISIBLE);
		
		clockListView = (ListView)findViewById(R.id.clockListView);
		tvRemainTime = (TextView)findViewById(R.id.tvRemainTime);
	}

	@Override
	protected void setViewListner() {
		imgBackIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		clockListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 刷新点击效果
				clockListAdapter.refresh(position);
				ClockInfo clockInfo = (ClockInfo) parent.getItemAtPosition(position);
				if(clockInfo != null) {
					SharePreferenceWrapper.getInstance().putIntegerValue(PreferenceUtil.CLOCK_ALARM_HOUR, clockInfo.getValue());
					if(clockInfo.isShowMark()) {
						SharePreferenceWrapper.getInstance().putIntegerValue(PreferenceUtil.CLOCK_REMAIN_TIME, 60 * clockInfo.getValue());
						SharePreferenceWrapper.getInstance().putBooleanValue(PreferenceUtil.CLOCK_SWITH_STATUS, true);
						startAlarm();
						tvRemainTime.setVisibility(View.VISIBLE);
					} else {
						SharePreferenceWrapper.getInstance().putBooleanValue(PreferenceUtil.CLOCK_SWITH_STATUS, false);
						stopAlarm();
						tvRemainTime.setVisibility(View.GONE);
					}
				}
			}
		});
	}

	@Override
	protected void initViewData() {
		clockListAdapter = new ClockListAdapter(this);
		clockListView.setAdapter(clockListAdapter);

		IntentFilter filter = new IntentFilter(ClockReceiver.TIME_CLOCK_ACTION);
		registerReceiver(mReceiver, filter);

		if (SharePreferenceWrapper.getInstance().getBooleanValue(
				PreferenceUtil.CLOCK_SWITH_STATUS)) {
			int alarmHours = SharePreferenceWrapper.getInstance()
					.getIntegerValue(PreferenceUtil.CLOCK_ALARM_HOUR);
			int position = alarmHours / 2 - 1;
			if(position >= 0) {
				clockListAdapter.refresh(alarmHours / 2 - 1);
			}
		} else {
			tvRemainTime.setVisibility(View.GONE);
		}
		
		Intent intent = new Intent("android.intent.action.ALARM_RECEIVER");
		mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
	
	private void startAlarm() {
		long firstime=SystemClock.elapsedRealtime();
		AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime, 1000, mPendingIntent);
		Log.i(TAG, "start alarm");
	}
	
	private void stopAlarm() {
		AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);
		alarm.cancel(mPendingIntent);
		Log.i(TAG, "stop alarm");
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// boolean status =
			// SharePreferenceWrapper.getInstance().getBooleanValue(PreferenceUtil.CLOCK_SWITH_STATUS);
			// if(status) {
			int remainMinutes = SharePreferenceWrapper.getInstance()
					.getIntegerValue(PreferenceUtil.CLOCK_REMAIN_TIME);
			int hour = remainMinutes / 60;
			int minute = remainMinutes % 60;
			String sFormat = context.getString(R.string.clock_remain_time);
			String remainTime = String.format(sFormat, hour, minute);
			tvRemainTime.setText(remainTime);
			// }
		}
	};
}
