package com.dashboard.ble.util;

import android.content.SharedPreferences;

import com.dashboard.ble.BLEApplication;

public class SharePreferenceWrapper {
	final String FILE_NAME = "dashboard.xml";
	
	private static SharePreferenceWrapper instance;
	private SharedPreferences sharePreferences;
	
	private SharePreferenceWrapper(){
		sharePreferences = BLEApplication.getInstance().getSharedPreferences(FILE_NAME, 0);
	}
	
	public static SharePreferenceWrapper getInstance()
	{
		if(instance == null){
			instance = new SharePreferenceWrapper();
		}
		return instance;
	}
	
	public void putStringValue(String key,String value){
		sharePreferences.edit().putString(key, value).commit();
	}
	
	public String getStringValue(String key){
		return sharePreferences.getString(key, "");
	}
	
	public void putIntegerValue(String key,int value){
		sharePreferences.edit().putInt(key, value).commit();
	}
	
	public int getIntegerValue(String key){
		return sharePreferences.getInt(key, 0);
	}
	
	public void putLongValue(String key,long value){
		sharePreferences.edit().putLong(key, value).commit();
	}
	
	public long getLongValue(String key){
		return sharePreferences.getLong(key, 0L);
	}
	
	public void putBooleanValue(String key,boolean value){
		sharePreferences.edit().putBoolean(key, value).commit();
	}
	
	public boolean getBooleanValue(String key){
		return sharePreferences.getBoolean(key, false);
	}
	
	public void putFloatValue(String key,float value){
		sharePreferences.edit().putFloat(key, value).commit();
	}
	
	public float getFloatValue(String key){
		return sharePreferences.getFloat(key, 0);
	}
}
