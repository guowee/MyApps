/**
 * $(#)Settings.java 2015Äê4ÔÂ16ÈÕ
 */
package com.hipad.smarthome;

import java.io.File;

import com.hipad.smart.user.User;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author wangbaoming
 *
 */
public class Settings {
	private final static String PREFS_SETTINGS = "settings"; 
	
	private MyApplication mApp;
	private SharedPreferences mPrefs;
	private SharedPreferences.Editor mPrefsEditor;

	public Settings(MyApplication app) {
		mApp = app;
		mPrefs = mApp.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
		mPrefsEditor = mPrefs.edit();
	}
	
	public File getUserDir(User user){
		File userDir = new File(mApp.getCacheDir() + File.separator + MyApplication.user.getName());
		if(!userDir.exists()) userDir.mkdirs();
		return userDir;
	}
	
	public String getUserProfileCachePath(User user){
		String userProfileCache = mApp.getCacheDir() + File.separator + user.getUserId();
		return userProfileCache;
	}
}
