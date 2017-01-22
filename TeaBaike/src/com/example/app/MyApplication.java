package com.example.app;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.graphics.Bitmap;

public class MyApplication extends Application {

	/**
	 * ª∫¥ÊÕº∆¨ ∏Ò Ω<Õ¯÷∑£¨Bitmap>
	 */

	private Map<String, Bitmap> cacheImgMap = new HashMap<String, Bitmap>();

	@Override
	public void onCreate() {
		super.onCreate();
		cacheImgMap = new HashMap<String, Bitmap>();

	}

	public Map<String, Bitmap> getCacheImageMap() {
		return cacheImgMap;
	}

}