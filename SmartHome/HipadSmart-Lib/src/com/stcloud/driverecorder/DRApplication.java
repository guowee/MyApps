package com.stcloud.driverecorder;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
//import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

public class DRApplication extends Application {
	
	public int selectedPhotoSize; // persistent
	public int selectedVideoSize; // persistent
	public int selectedVideoLen; // persistent
	
	public boolean audioStatus; // persistent
	public boolean videoRecordStatus; // persistent
    public String videoFile; // non-persistent
    public String prev_videoFile;
    public String srtFile;
    public String photoFile; // non-persistent
    public boolean isBackgroundMode; // non-persistent
    public int videoWidth; // persistent
    public int videoHeight; // persistent
    public int photoWidth; // persistent
    public int photoHeight; // persistent
    public double latitude; // non-persistent
    public double longitude; // non-persistent
    public boolean PhotoCapture = false;
    public List<String> oneShotPhotos;
    
	private static DRApplication sApplication;
	
	final String SELECTED_PHOTO_SIZE = "selected_photo_size";
	final String SELECTED_VIDEO_SIZE = "selected_video_size";
	final String SELECTED_VIDEO_LEN = "selected_video_len";
	final String VIDEO_RECORD_STATUS = "video_record_status";
	final String VIDEO_WIDTH = "video_width";
	final String VIDEO_HEIGHT = "video_height";
	final String PHOTO_WIDTH = "photo_width";
	final String PHOTO_HEIGHT = "photo_height";
	final String AUDIO_STATUS = "audio_status";
	
	private static final String TAG = "DRApplication";
	ArrayList<Activity> list = new ArrayList<Activity>();
	
	@Override
	public void onCreate() {
		sApplication = this;
		Log.i("HARRY", "DRApplication onCreate");
		
		super.onCreate();

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		/* to be saved when application is closing */
		selectedPhotoSize = settings.getInt(SELECTED_PHOTO_SIZE, 2);
		selectedVideoSize = settings.getInt(SELECTED_VIDEO_SIZE, 0);
		selectedVideoLen = settings.getInt(SELECTED_VIDEO_LEN, 3*60*1000); // default: 3mins
		
		videoRecordStatus = settings.getBoolean(VIDEO_RECORD_STATUS, false);
		videoWidth = settings.getInt(VIDEO_WIDTH, 1920);
		videoHeight = settings.getInt(VIDEO_HEIGHT, 1080);
		photoWidth = settings.getInt(PHOTO_WIDTH, 2688); // default val to be corrected
		photoHeight = settings.getInt(PHOTO_HEIGHT, 1520); // default val to be corrected
		audioStatus = settings.getBoolean(AUDIO_STATUS, false);
		
		/* not to be saved */
		audioStatus = false;
		videoFile = "";
		prev_videoFile = "";
		photoFile = "";
		srtFile = "";
		isBackgroundMode = false;
		
		oneShotPhotos = new ArrayList<String>();
		
		// Listens to changes of preferences
		//settings.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
	}
	
	public void init() {
		UnCeHandler catchExcep = new UnCeHandler(this);
		Thread.setDefaultUncaughtExceptionHandler(catchExcep);
	}
	
	public void removeActivity(Activity a) {
		list.remove(a);
	}
	
	public void addActivity(Activity a) {
		list.add(a);
	}
	
	public void finishActivity() {
		for (Activity activity : list) {
			if (null != activity) {
				activity.finish();
			}
		}
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	public void save() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = settings.edit();
		editor.putInt(SELECTED_PHOTO_SIZE, selectedPhotoSize);
		editor.putInt(SELECTED_VIDEO_SIZE, selectedVideoSize);
		editor.putInt(SELECTED_VIDEO_LEN, selectedVideoLen);
		//editor.putBoolean(VIDEO_RECORD_STATUS, videoRecordStatus); do not need to save this value
		editor.putInt(VIDEO_WIDTH, videoWidth);
		editor.putInt(VIDEO_HEIGHT, videoHeight);
		editor.putInt(PHOTO_WIDTH, photoWidth);
		editor.putInt(PHOTO_HEIGHT, photoHeight);
		//editor.putBoolean(AUDIO_STATUS, audioStatus); do not need to save this value
		editor.commit();

	}

	public static DRApplication getInstance() {
		return sApplication;
	}

	/*
	private OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = 
			new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		}  
	};
	*/
}
