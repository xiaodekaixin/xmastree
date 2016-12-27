package com.dashboard.ble.receiver;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.dashboard.ble.activity.ClockActivity;
import com.dashboard.ble.constants.PreferenceUtil;
import com.dashboard.ble.util.SharePreferenceWrapper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ClockReceiver extends BroadcastReceiver {
	public static String TIME_CLOCK_ACTION = "com.dashboard.ble.clock.action";
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(action != null && action.equals("android.intent.action.ALARM_RECEIVER")) {
			// 查看定时时间是否已经到达
			Log.i(ClockActivity.TAG,"time tick" + sdf.format(new Date()));
			boolean status = SharePreferenceWrapper.getInstance().getBooleanValue(PreferenceUtil.CLOCK_SWITH_STATUS);
			if(status) {
				int remainMinutes = SharePreferenceWrapper.getInstance().getIntegerValue(PreferenceUtil.CLOCK_REMAIN_TIME);
				remainMinutes -= 1;
				if(remainMinutes >= 0) {
					SharePreferenceWrapper.getInstance().putIntegerValue(PreferenceUtil.CLOCK_REMAIN_TIME,remainMinutes);
				}
				
				if(remainMinutes == 0) {
					// 定时时间到
					Log.i(ClockActivity.TAG,"发送数据");
					
					PendingIntent stopIntent = PendingIntent.getBroadcast(context, 0, new Intent("android.intent.action.ALARM_RECEIVER"), 0);
					AlarmManager alarm=(AlarmManager)context.getSystemService(context.ALARM_SERVICE);
					alarm.cancel(stopIntent);
					Log.i(ClockActivity.TAG,"关闭闹钟");
					SharePreferenceWrapper.getInstance().putBooleanValue(PreferenceUtil.CLOCK_SWITH_STATUS,false);
				}
				
				Intent refreshIntent = new Intent(TIME_CLOCK_ACTION);
				context.sendBroadcast(refreshIntent);
			}
		} 
	}
}
