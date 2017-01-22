package com.example.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

	private final static String TAG = "SharedPreferencesHelper";

	private SharedPreferences prefs;

	private SharedPreferences.Editor editor;

	public SharedPreferencesHelper(Context context) {

		prefs = context.getSharedPreferences(TAG, context.MODE_PRIVATE);

		editor = prefs.edit();

	}

	public boolean putString(String key, String value) {

		editor.putString(key, value);

		return editor.commit();

	}

	public String getString(String key) {

		return prefs.getString(key, null);

	}

	public boolean putInt(String key, int value) {

		editor.putInt(key, value);

		return editor.commit();

	}

	public int getInt(String key) {

		return prefs.getInt(key, -1);

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
