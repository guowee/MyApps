package com.stcloud.driverecorder;

import java.io.FileOutputStream;
import java.io.File;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.util.Log;

public class CameraFlash {
	private static final String CAMERA_LIGHT = "/sys/lights/enable_camera";
	private static final String CAMERA_LIGHT_FREQ = "/sys/lights/freq_camera";
	private static final String LIGHT_ON = "1";
	private static final String LIGHT_OFF = "0";
	private static final int MSG_CAMERA_LIGHT_ON = 0x004;
	private static final int MSG_CAMERA_LIGHT_OFF = 0x005;
	private static DRApplication mApp;
	
	public CameraFlash(DRApplication app) {
		mApp = app;
	}
		
	private static Handler mFastFlashHandler= new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
				case MSG_CAMERA_LIGHT_ON:
					setFreq(250);
					lightOn();
					mFastFlashHandler.sendEmptyMessageDelayed(MSG_CAMERA_LIGHT_OFF,1000);
					break;
				case MSG_CAMERA_LIGHT_OFF:
					setFreq(0);
					lightOff();
					break;
				default:
					break;

			}
		}

	};

	private static Handler mSlowFlashHandler= new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
				case MSG_CAMERA_LIGHT_ON:
					lightOn();
					mSlowFlashHandler.sendEmptyMessageDelayed(MSG_CAMERA_LIGHT_OFF,700);
					break;
				case MSG_CAMERA_LIGHT_OFF:
					lightOff();
					mSlowFlashHandler.sendEmptyMessageDelayed(MSG_CAMERA_LIGHT_ON,3000);
					break;
				default:
					break;
			}
		}

	};

	public static void slowFlashStart() {
		setFreq(0);
		mSlowFlashHandler.removeMessages(MSG_CAMERA_LIGHT_ON);
		mSlowFlashHandler.removeMessages(MSG_CAMERA_LIGHT_OFF);
		mSlowFlashHandler.sendEmptyMessage(MSG_CAMERA_LIGHT_ON);
	}

	public static void slowFlashStop() {
		mSlowFlashHandler.removeMessages(MSG_CAMERA_LIGHT_ON);
		mSlowFlashHandler.removeMessages(MSG_CAMERA_LIGHT_OFF);
		setFreq(0);
		lightOff();
	}
	
	public static void fastFlashStart() {
		mSlowFlashHandler.removeMessages(MSG_CAMERA_LIGHT_ON);
		mSlowFlashHandler.removeMessages(MSG_CAMERA_LIGHT_OFF);
		mFastFlashHandler.sendEmptyMessage(MSG_CAMERA_LIGHT_ON);
	}

	public static void fastFlashStop() {
		mSlowFlashHandler.removeMessages(MSG_CAMERA_LIGHT_ON);
		mSlowFlashHandler.removeMessages(MSG_CAMERA_LIGHT_OFF);
		setFreq(0);
		lightOff();
	}

	public static void setFreq(int freq) {
		try{ 
			FileOutputStream fout = new FileOutputStream(new File(CAMERA_LIGHT_FREQ));
			byte [] bytes = String.valueOf(freq).getBytes(); 
			fout.write(bytes); 
			fout.close(); 
		 } catch(Exception e){ 
		  	e.printStackTrace(); 
		 } 

	}
	
	public static void lightOn() {
		try{ 
			FileOutputStream fout = new FileOutputStream(new File(CAMERA_LIGHT));
			byte [] bytes = LIGHT_ON.getBytes(); 
			fout.write(bytes); 
			fout.close(); 
		 } catch(Exception e){ 
		  	e.printStackTrace(); 
		 } 
	}

	public static void lightOff() {
		try{ 
			FileOutputStream fout = new FileOutputStream(new File(CAMERA_LIGHT));
			byte [] bytes = LIGHT_OFF.getBytes(); 
			fout.write(bytes); 
			fout.close(); 
		 } catch(Exception e){ 
		  	e.printStackTrace(); 
		 } 

	}

}
