package com.hipad.tracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.hipad.tracker.entity.User;
import com.hipad.tracker.utils.SmallTools;
/**
 * 
 * @author guowei
 *
 */
public class MyApplication extends Application implements Thread.UncaughtExceptionHandler {
	private LinkedHashMap<String, Object> activityList = new LinkedHashMap<String, Object>();
	private static Context context;
	
	public static User user;
	public static String account;
	public static String imei;
    public static String mAppId;
	public static String mDownloadPath;

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		
		 mAppId = getString(R.string.app_id);
	     mDownloadPath = "/" + mAppId+ "/download";
	}

	public static Context getContextObject() {
		return context;
	}

	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.put(activity.getLocalClassName(), activity);
		SmallTools.showInfoLog("addActivity",
				"activity = " + activity.getLocalClassName());
	}

	// 在容器中移除Activity
	public void removeActivity(Activity activity) {
		activityList.remove(activity.getLocalClassName());
		SmallTools.showInfoLog("removeActivity",
				"activity = " + activity.getLocalClassName());
	}

	// 添加Activity到容器中
	public Activity getActivity(String name) {
		return (Activity) activityList.get(name);
	}

	public ArrayList getActivityList() {
		ArrayList<Activity> lists = new ArrayList<Activity>();
		Iterator it = activityList.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			Activity activity = (Activity) entry.getValue();
			lists.add(activity);
		}
		return lists;
	}

	public void finishAll() {
		int i = 0;
		Iterator it = activityList.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			Activity activity = (Activity) entry.getValue();
			if (activity != null) {
				activity.finish();
				i++;
			}
		}
		SmallTools.showInfoLog("MyApplication", "exit activity num = " + i);
	}

	// 遍历所有Activity并finish
	public void exit(boolean completely) {
		finishAll();

		if (completely)
			System.exit(0);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable tw) {
		tw.printStackTrace();
		Log.e("MyApplication", "uncaughtException:" + "\n" + tw.getMessage()
				+ "\n" + tw.getCause() + "\n" + tw.getStackTrace().toString()
				+ "\n" + tw.toString() + "\n" + tw.fillInStackTrace() + "\n"
				+ tw.getLocalizedMessage());

		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(getApplicationContext(), "很抱歉,程序出现异常,即将退出!",
						Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		exit(true);

	}
	
	public void saveUserInfo(){
		ObjectOutputStream oos = null;
		try {
			FileOutputStream fos = new FileOutputStream(getCacheDir() + File.separator +  "user_info");
			oos = new ObjectOutputStream(fos);
			if(null != user) oos.writeObject(user);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
				try {
					if(null != oos)	oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public void saveUserToken(){
		ObjectOutputStream oos = null;
		try {
			FileOutputStream fos = new FileOutputStream(getCacheDir() + File.separator +  "user_token");
			oos = new ObjectOutputStream(fos);
			if(null != account) oos.writeObject(account);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
				try {
					if(null != oos)	oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
}
