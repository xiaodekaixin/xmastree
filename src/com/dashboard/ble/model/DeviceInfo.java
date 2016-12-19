package com.dashboard.ble.model;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name="bledevice")
public class DeviceInfo extends EntityBase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(column="address")
	private String address;
	@Column(column="name")
	private String name;
	@Column(column="connectState")
	private boolean connectState;
	@Column(column="rssi")
	private int rssi;
	
	public DeviceInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public DeviceInfo(String address, String name, boolean connectState,
			int rssi) {
		super();
		this.address = address;
		this.name = name;
		this.connectState = connectState;
		this.rssi = rssi;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isConnectState() {
		return connectState;
	}

	public void setConnectState(boolean connectState) {
		this.connectState = connectState;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}
}
