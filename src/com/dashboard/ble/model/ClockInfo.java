package com.dashboard.ble.model;

public class ClockInfo {
	private String title;
	private int value;
	private boolean showMark = false;
	
	public ClockInfo() {
		
	}
	
	public ClockInfo(String title, int value) {
		super();
		this.title = title;
		this.value = value;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	public boolean isShowMark() {
		return showMark;
	}

	public void setShowMark(boolean showMark) {
		this.showMark = showMark;
	}

	@Override
	public String toString() {
		return "ClockInfo [title=" + title + ", value=" + value + ", showMark="
				+ showMark + "]";
	}
}
