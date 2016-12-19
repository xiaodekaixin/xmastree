package com.dashboard.ble.constants;


public class Constants {
	// 自定义服务uuid
//	public static final String UUID_SERVICE = "1b7e8251-2877-41c3-b46e-cf057c562023";
//	public static final String UUID_WRITE_DATA = "5e9bf2a8-f93f-4481-a67e-3b2f4a07891a";
//	public static final String UUID_READ_DATA  = "8ac32d3f-5cb9-4d44-bec2-ee689169f626";
//	
//	public static final String CMD_ALM = "ALM";
//	public static final String CMD_KEY = "KEY";
//	public static final String REPLY_CMD_ALM = "\tRETALMOK\r\n";
//	public static final String REPLY_CMD_KEY = "\tRETALMOK\r\n";
	
	/* 圣诞树特征值UUID */
	public static final String UUID_XMAS_TREE_SERVICE = "0000fcc0-0000-1000-8000-00805f9b34fb";
	public static final String UUID_XMAS_TREE = "0000fcc1-0000-1000-8000-00805f9b34fb";
	
	/* 打开圣诞树led */
	public static final String LED_ON = "0x01";
	/* 关闭圣诞树led */
	public static final String LED_OFF = "0x00";
	
	/* 扫描时长 10s */
	public static long SCAN_PERIOD = 10*1000;
	
	/* 发送命令后 睡眠100毫秒 */
	public static int GATT_TIMEOUT = 100; // milliseconds
	
}
