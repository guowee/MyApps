package com.stcloud.driverecorder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GpsGsensorTracker implements LocationListener, SensorEventListener {
	private final static boolean DEBUG = true;
	private final static String TAG = "GpsGsensorTracker";
	private final static String KEY_FILENAME = "fileName";
	private final static String KEY_TIMEOUT = "timeout";
	
	private Context mContext;
	private String mGps;
	private String mGsensor;
	private FileThread mFileThread = null;
	private LocationManager mLocationManager = null;
	private SensorManager mSensorManager = null;
	private Sensor mSensor;
	private boolean isRecording = false;
	
	public GpsGsensorTracker(Context context) {
		mContext = context;
	}

	public void init() {
		if (DEBUG) Log.d(TAG, "init()");
		
		if (mLocationManager == null) {
			if (DEBUG) Log.d(TAG, "start locationManager");
			mLocationManager = (LocationManager) mContext
					.getSystemService(Context.LOCATION_SERVICE);
			if (!mLocationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				if (DEBUG)Toast.makeText(mContext, "...", Toast.LENGTH_SHORT).show();
				if (DEBUG)Log.d(TAG, "GPS_PROVIDER");
				mLocationManager = null;
			} else {
				mLocationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 200, 0, this);
				if (DEBUG) Log.d(TAG, "end locationManager");
			}
		}
		
		
		mSensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mSensor, mSensorManager.SENSOR_DELAY_NORMAL);

	}
	public void start(String filename,int rec_time) {
		isRecording = true;
		if (mFileThread != null) {
			mFileThread.close();
		}
		Log.e(TAG, "rec_time:"+rec_time);
		
		Long startTime = new Date().getTime();
		mFileThread = new FileThread(filename, rec_time, startTime);
		mFileThread.start();
		
	}

	public void stop() {
		if (mFileThread != null) {
			mFileThread.close();
		}
	}
	
	class FileThread extends Thread {
		private String mFileName = null;
		private boolean mIsExit = false;		
		private String mStartTimeString = "00:00:00,000";		
		private final int SLEEP_TIME = 200;
		private final int mLoopCount;
		private final int mtimeout;
		private final long mStartTime;
		private int count = 0;

		public FileThread(String filename, int timeout, Long startTime) {
			mFileName = filename;
			mLoopCount = timeout / SLEEP_TIME;
			mtimeout = timeout;
			mStartTime = startTime;		
			count = 0;
		}
		
		public String getTimeString() {
			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss,SSS", Locale.US);
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date dt=new Date();
			Long diff = dt.getTime() - mStartTime;
			String endTimeString = df.format(diff);
			String result = mStartTimeString + " --> " +  endTimeString;
			mStartTimeString = endTimeString;
			
			if (DEBUG) Log.d(TAG, "diff time=>"+diff);		
			
			return result;
		}
		
		@Override
		public void run() {
			
			if (DEBUG) Log.d(TAG, "run() :");
			mFileName = mFileName.replace(".mp4", "");	
			String fileName = String.format("%s.srt", mFileName);	
			FileOutputStream outputStream = null;
			try {				
				outputStream = new FileOutputStream(fileName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				if (DEBUG) Log.d(TAG, "FileNotFoundException");
				return;
			}
			if (DEBUG) Log.d(TAG, "fileName"+fileName);	
			while (isRecording) {	
				StringBuilder output = new StringBuilder();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
				String currentDateandTime = sdf.format(new Date());
				
				if (GpsGsensorTracker.this.mGps == null) {
					output.append(String.format(Locale.US, "%d\r\n%s\r\n%s\r\n", (count + 1), getTimeString(),currentDateandTime));
				} else {
					output.append(String.format(Locale.US, "%d\r\n%s\r\n%s\r\n%s\r\n", (count + 1), getTimeString(),currentDateandTime, GpsGsensorTracker.this.mGps));					
				}
				if (GpsGsensorTracker.this.mGsensor != null) {
					output.append(GpsGsensorTracker.this.mGsensor + "\r\n");
				}
				output.append("\r\n"+"LEI CHE BAO");
				
				if (DEBUG) Log.d(TAG, output.toString());
				try {
					outputStream.write(output.toString().getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				count ++;
				try {					
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			if (DEBUG) Log.d(TAG, "srt record finish,thread stop!");
			try {
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}

		public void close() {
			isRecording = false;
			if (mFileThread != null) {
				mFileThread.interrupt();
				mFileThread = null;
			}
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if (DEBUG) Log.d(TAG, "onLocationChanged");
		if (location != null) {
			mGps = getLocation(location);
			//Toast.makeText(mContext, g_GPS, Toast.LENGTH_SHORT).show();
			if (DEBUG) Log.d(TAG, "onLocationChanged: " + mGps);
		}

	}

	public void onDestory() {
		if (DEBUG) Log.d(TAG, "onDestory()");
		if (mFileThread != null) {
			mFileThread.close();
			mFileThread = null;
		}
		if (mLocationManager != null) {
			mLocationManager.removeUpdates(this);
			mLocationManager = null;
		}
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(this);
			mSensorManager = null;			
		}
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	private String getLocation(Location location) { // 
		double longitude = 0; //
		double latitude = 0; // 
		double Altitude = 0;
		float Speed = 0;
		float Bearing = 0;

		if (location != null) {
			longitude = location.getLongitude(); //
			latitude = location.getLatitude(); //
			Altitude = location.getAltitude();
			Speed = location.getSpeed();
			Bearing = location.getBearing();
		} else {
			if (DEBUG)Toast.makeText(mContext, "no GPS", Toast.LENGTH_LONG).show();
		}
		String content = "longitude:" + longitude + ", latitude:" + latitude
				+ ", Altitude:" + Altitude + ", Speed:" + Speed + ", Bearing:"
				+ Bearing;
		if (DEBUG) Log.d(TAG, content);
		return content;
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event != null) {
		    float x = event.values[0];
		    float y = event.values[1];
		    float z = event.values[2];
			mGsensor = String.format("%f,%f,%f", x, y, z);
		}		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

		
	}	

}
