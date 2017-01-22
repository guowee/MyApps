package com.hipad.tracker.utils;

import android.content.Context;
import android.content.SharedPreferences;
/**
 * 
 * @author guowei
 *
 */
public class SharedPreferencesHelper {

	private final static String TAG = "SharedPreferencesHelper";

	private SharedPreferences prefs;

	private SharedPreferences.Editor editor;

	public SharedPreferencesHelper(Context context) {

		prefs = context.getSharedPreferences(TAG, context.MODE_PRIVATE);
		editor = prefs.edit();

	}

	public boolean putBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		return editor.commit();
	}

	public boolean getBoolean(String key) {
		return prefs.getBoolean(key, false);
	}

	public boolean putString(String key, String value) {

		editor.putString(key, value);

		return editor.commit();

	}

	public String getString(String key) {

		return prefs.getString(key, null);

	}

	public boolean putLong(String key,long value){
		
		editor.putLong(key, value);
		return editor.commit();
	}
	
	public long getLong(String key){
		
		return prefs.getLong(key, -1);
	}
	
	public boolean putInt(String key, int value) {

		editor.putInt(key, value);

		return editor.commit();

	}

	public int getInt(String key) {

		return prefs.getInt(key, -1);

	}
	
	public boolean putFloat(String key,float value){
		editor.putFloat(key, value);
		return editor.commit();
	}
	
	public float getFloat(String key){
		return prefs.getFloat(key, 0f);
	}

	public boolean clear() {

		editor.clear();

		return editor.commit();
	}

	public void close() {

		if (prefs != null) {

			prefs = null;

		}
	}

}
