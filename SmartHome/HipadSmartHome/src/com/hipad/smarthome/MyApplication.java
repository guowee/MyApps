package com.hipad.smarthome;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.hipad.smart.device.Device;
import com.hipad.smart.service.ServiceImpl;
import com.hipad.smart.user.User;
import com.hipad.smarthome.utils.DeviceListCache;
import com.hipad.smarthome.utils.SmallTools;

public class MyApplication extends Application implements Thread.UncaughtExceptionHandler{

	private static final String TAG = "MyApplication";

	private LinkedHashMap<String, Object> activityList = new LinkedHashMap<String, Object>();

	private Drawable defaultBG;
	
	public static User user;
	
	private Settings settings;

	@Override
	public void onCreate() {
		super.onCreate();
		
		settings = new Settings(this);

		Log.i(TAG, "alive:" + DeamonService.isAlive());
		startService(new Intent(this, DeamonService.class));
		
//		defaultBG = getResources().getDrawable(R.drawable.bg);
		
		ServiceImpl.restore(getApplicationContext());
		if(ServiceImpl.verifyLocally(getApplicationContext())) {
			restoreUserInfo();
		}
		
//        CrashHandler crashHandler = CrashHandler.getInstance();  
//        crashHandler.init(getApplicationContext());  
      // 设置Thread Exception Handler 
        Thread.setDefaultUncaughtExceptionHandler(this); 
        
		DeviceListCache.getInstance().init(this);
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable tw) {
		tw.printStackTrace();
		Log.e("MyApplication", "uncaughtException:" + 
					"\n" + tw.getMessage() + 
					"\n" + tw.getCause() + 
					"\n" + tw.getStackTrace().toString()  + 
					"\n" + tw.toString()  + 
					"\n" + tw.fillInStackTrace() + 
					"\n" + tw.getLocalizedMessage());
		
		new Thread() {  
            @Override  
            public void run() {  
                Looper.prepare();  
                Toast.makeText(getApplicationContext(), "很抱歉,程序出现异常,即将退出!", Toast.LENGTH_LONG).show();  
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
	
	public Settings getSettings(){
		return settings;
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
	
	public void restoreUserInfo(){
		ObjectInputStream ois = null;
		try {
			FileInputStream fos = new FileInputStream(getCacheDir() + File.separator +  "user_info");
			ois = new ObjectInputStream(fos);
			user = (User) ois.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally{
				try {
					if(null != ois)	ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public Drawable getDefaultBG() {
		return defaultBG;
	}

	public void setDefaultBG(Drawable defaultBG) {
		this.defaultBG = defaultBG;
	}

	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.put(activity.getLocalClassName(), activity);
		SmallTools.showInfoLog("addActivity", "activity = "+activity.getLocalClassName());
	}

	// 在容器中移除Activity
	public void removeActivity(Activity activity) {
		activityList.remove(activity.getLocalClassName());
		SmallTools.showInfoLog("removeActivity", "activity = "+activity.getLocalClassName());
	}

	// 添加Activity到容器中
	public Activity getActivity(String name) {
		return (Activity) activityList.get(name);
	}
	
	public ArrayList getActivityList(){
		ArrayList<Activity> lists = new ArrayList<Activity>();
		Iterator it = activityList.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			Activity activity = (Activity) entry.getValue();
			lists.add(activity);
		}
		return lists;
	}
	
	public void finishAll(){
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
		
		if(completely) System.exit(0);
	}
}
