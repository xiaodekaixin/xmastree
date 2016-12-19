package com.dashboard.ble.dao;

import java.util.List;

import com.dashboard.ble.BLEApplication;
import com.dashboard.ble.model.DeviceInfo;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

public class DeviceDao {
	private static DeviceDao instance;
	private DbUtils db;
	
	private DeviceDao()
	{
		db = DbUtils.create(BLEApplication.getInstance(), "dashboard.db");
	    db.configAllowTransaction(true);
	    db.configDebug(true);
	}
	
	public static synchronized DeviceDao getInstance()
	{
		if(instance == null)
		{
			instance = new DeviceDao();
		}
		return instance;
	}
	
	public void saveOrUpdate(DeviceInfo deviceInfo)
	{
		try {
			DeviceInfo device = db.findFirst(Selector.from(DeviceInfo.class).where("address", "=", deviceInfo.getAddress()));
			if(device != null)
			{
				deviceInfo.setId(device.getId());
			}
			db.saveOrUpdate(deviceInfo);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	public void delete(DeviceInfo deviceInfo)
	{
		try {
			db.delete(DeviceInfo.class, WhereBuilder.b("address", "=", deviceInfo.getAddress()));
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	public void updateConnectState(String address,boolean state)
	{
		try {
			DeviceInfo device = db.findFirst(Selector.from(DeviceInfo.class).where("address", "=", address));
			if(device != null)
			{
				device.setConnectState(state);
				db.saveOrUpdate(device);
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	public List<DeviceInfo> findAllConnectedDevice() 
	{
		List<DeviceInfo> deviceInfoList = null;
		try {
			deviceInfoList = db.findAll(Selector.from(DeviceInfo.class).where(WhereBuilder.b("connectState", "=", true)));
		} catch (DbException e) {
			e.printStackTrace();
		}
		return deviceInfoList;
	}
	
	public DeviceInfo findDeviceInfoByAddress(String address) {
		try {
			DeviceInfo device = db.findFirst(Selector.from(DeviceInfo.class).where("address", "=", address));
			return device;
		} catch (DbException e) {
			e.printStackTrace();
			return null;
		}
	}
}
