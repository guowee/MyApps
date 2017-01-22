package com.stcloud.driverecorder;

public class Settings {
	public static final int VIDEO_RESOLUTION = 0x10;
	public static final int VID_RES_1080P = 0x1;
	public static final int VID_RES_720P = 0x2; // default
	public static final int VID_RES_VGA = 0x3;
	
	public static final int VIDEO_LENGTH = 0x11;
	public static final int VID_LEN_1_MIN = 0x1;
	public static final int VID_LEN_2_MIN = 0x2; // default?
	public static final int VID_LEN_3_MIN = 0x3;
	public static final int VID_LEN_5_MIN = 0x4;
	
	public static final int CAMERA_ANTI_BANDING = 0x12;
	public static final int FREQ_50_HZ = 0x1;
	public static final int FREQ_60_HZ = 0x2;
	
	public static final int CAMERA_RECORD = 0x13;
	public static final int RECORD_FRONT_REAR = 0x1;
	public static final int RECORD_FRONT = 0x2; // default
	public static final int RECORD_REAR = 0x3;
	
	public static final int VIDEO_SOUND = 0x14;
	public static final int AUDIO_ON = 0x1;
	public static final int AUDIO_OFF = 0x0;
	
	public static final int CAMERA_PREVIEW = 0x15;
	public static final int PREVIEW_FRONT = 0x1; // default
	public static final int PREVIEW_REAR = 0x2;
	
	public static final int PICTURE_IN_PICTURE = 0x16;
	public static final int PIP_ON = 0x1;
	public static final int PIP_OFF = 0x0;
	
	public static final int GSENSOR_SENSITIVITY = 0x17;
	public static final int GSENSOR_HIGH = 0x3;
	public static final int GSENSOR_MEDIUM = 0x2; // default
	public static final int GSENSOR_LOW = 0x1;
	public static final int GSENSOR_OFF = 0x0;
	
	public static final int PARKING_MONITOR = 0x18;
	public static final int PARKING_HIGH = 0x3;
	public static final int PARKING_MEDIUM = 0x2;
	public static final int PARKING_LOW = 0x1;
	public static final int PARKING_OFF = 0x0;
	
	public static final int PHOTO_RESOLUTION = 0x30;
	public static final int PHOTO_RES_1M = 0x1;
	public static final int PHOTO_RES_2M = 0x2;
	public static final int PHOTO_RES_3M = 0x3; // default
	public static final int PHOTO_RES_4M = 0x4;
	public static final int PHOTO_RES_5M = 0x5;
	public static final int PHOTO_RES_6M = 0x6;
	public static final int PHOTO_RES_7M = 0x7;
	public static final int PHOTO_RES_8M = 0x8;
	public static final int PHOTO_RES_9M = 0x9;
	public static final int PHOTO_RES_10M = 0xa;
	
	public static final int PHOTO_BURST_SHOT = 0x31;
	public static final int BURST_1 = 0x1;
	public static final int BURST_2 = 0x2;
	public static final int BURST_3 = 0x3;
	public static final int BURST_4 = 0x4;
	public static final int BURST_5 = 0x5;
	
	public static final int PHOTO_BURST_INTERVAL = 0x32;
	public static final int INTERVAL_HALF_SECOND = 0x0;
	public static final int INTERVAL_ONE_SECOND = 0x1;
	public static final int INTERVAL_TWO_SECOND = 0x2;
	public static final int INTERVAL_THREE_SECOND = 0x3;
	
	public static final int PHOTO_TIMER_ON_OFF = 0x33;
	public static final int TIME_ON = 0x1;
	public static final int TIME_OFF = 0x2;
	
	public static final int PHOTO_TIMER_INTERVAL = 0x34;
	public static final int INTERVAL_1_MIN = 0x1;
	public static final int INTERVAL_2_MIN = 0x2;
	public static final int INTERVAL_3_MIN = 0x3;
	public static final int INTERVAL_4_MIN = 0x4;
	public static final int INTERVAL_5_MIN = 0x5;
	public static final int INTERVAL_6_MIN = 0x6;
	public static final int INTERVAL_7_MIN = 0x7;
	public static final int INTERVAL_8_MIN = 0x8;
}
