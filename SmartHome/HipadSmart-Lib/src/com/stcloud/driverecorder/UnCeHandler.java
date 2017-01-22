package com.stcloud.driverecorder;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

public class UnCeHandler implements UncaughtExceptionHandler{

	private Thread.UncaughtExceptionHandler mDefaultHandler;
	DRApplication application;
	
	public UnCeHandler(DRApplication application) {
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		this.application = application;
	}
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		if (!handleException(ex) && mDefaultHandler!=null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Intent intent = new Intent(application.getApplicationContext(),CameraActivity.class);
			PendingIntent restartIntent = PendingIntent.getActivity(application.getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
			AlarmManager mgr = (AlarmManager)application.getSystemService(Context.ALARM_SERVICE);
			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
			application.finishActivity();
		}
	}

	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				Looper.loop();
			}
			
		}.start();
		return true;
	}
}
