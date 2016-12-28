package com.dashboard.ble.model;

public class LedInfo {
	private String title;
	private int data;
	private boolean press;
	
	public LedInfo() {
		this("");
	}
	
	public LedInfo(String title) {
		this(title, 0);
	}
	
	public LedInfo(String title, int data) {
		this(title, data, false);
	}

	public LedInfo(String title, int data, boolean press) {
		this.title = title;
		this.data = data;
		this.press = press;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public boolean isPress() {
		return press;
	}


	public void setPress(boolean press) {
		this.press = press;
	}


	public int getData() {
		return data;
	}


	public void setData(int data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "LedInfo [title=" + title + ", data=" + data + ", press="
				+ press + "]";
	}
}
