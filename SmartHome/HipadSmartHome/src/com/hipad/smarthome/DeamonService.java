/**
 * $(#)DeamonService.java 2015Äê5ÔÂ11ÈÕ
 */
package com.hipad.smarthome;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * @author wangbaoming
 *
 */
public class DeamonService extends Service {
	
	private static boolean sAlive = false; 
	
	private static boolean sRegistered = false;

	private static final String TAG = DeamonService.class.getSimpleName();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public static boolean isAlive(){
		return sAlive;
	}
	
	public static boolean isRegistered(){
		return sRegistered;
	}
	
	public static void setRegistered(boolean registered){
		sRegistered = registered;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		sAlive = true;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
//		return Service.START_STICKY;
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

}
