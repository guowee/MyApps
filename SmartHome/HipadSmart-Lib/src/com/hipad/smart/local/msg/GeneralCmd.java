package com.hipad.smart.local.msg;

public final class GeneralCmd {
	public final static int FIND_DEVICE = 0x00;
	
	public final static int GET_DEVICE_CONFIG = 0x61;
	public final static int CONFIG_DEVICE_SSID = 0x62;	
	
	public static class Args {
		public final static String NAME = "NAME";

		public final static String TYPE = "TYPE";
		public final static String TEMP = "TEMP";
		public final static String SSID = "SSID";
		public final static String PASSWORD = "PASSWORD";
		public final static String IPTYPE = "IPTYPE";		
		public final static String IP = "IP";
		public final static String GW = "GW";
		public final static String MASK = "MASK";
		public final static String DNS1 = "DNS1";
		public final static String DNS2 = "DNS2";
		
		public final static String IPTYPE_STATIC = "static";
		public final static String IPTYPE_DHCP = "DHCP";
	}
}
