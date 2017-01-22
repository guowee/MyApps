package com.stcloud.driverecorder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspServer;
import net.majorkernelpanic.streaming.video.VideoQuality;

//import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.preference.PreferenceManager;
import java.text.DecimalFormat;
import java.io.FileOutputStream;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.media.SoundPool;

public class CameraActivity extends Activity implements View.OnClickListener,
		SharedPreferences.OnSharedPreferenceChangeListener {

	//private final Logger log = Logger.getLogger(CameraActivity.class);
	private static final String TAG = "CameraActivity";
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private STRecorder mRecorder;
	public static List<Size> sPhotoSizeList;
	public static List<Size> sVideoSizeList;
	private BroadcastReceiver mReceiver;
	private BroadcastReceiver mMountReceiver;
	private BroadcastReceiver mScreenReceiver;
	private BroadcastReceiver mBatteryReceiver;
	private MediaPlayer mShutterMP;
	private ImageSaver mSaver;
	private DRApplication mApp;
	private boolean photoUpload = false;
	private boolean canCapture = true;
	//
	//
	private boolean recvDisconnected = false;
	private boolean recvConnected = false;
	
	public static final int DIALOG_PHOTO_RESOLUTION = 10;
	public static final int DIALOG_VIDEO_RESOLUTION = 11;
	public static final int DIALOG_CLOSE_APP = 12;
	public static final int DIALOG_VIDEO_LENGTH_PER_FILE = 13;
	public static final int DIALOG_OPEN_CAMERA_FAIL = 14;
	public static final int DIALOG_SNAPSHOT_FREQ = 15;
	public static final int DIALOG_TF_SIZE_TOO_SMALL = 16;

	public static final int MSG_ACTIVITY_LATE_INIT = 100;
	private boolean ifFirstStart = true;

	// soundPool
	private SoundPool soundPool = null;
	private int musicID_01;
	private int musicID_02;
	private boolean isRTSP = false;

	private MediaPlayer mPlayer;

	private static final int PHOTO_CAPTURE_SHAKE = 0x001;
	private static final int PHOTO_CAPTURE_SHOCK = 0x002;
	private static final int PHOTO_CAPTURE_DURATION = 0x003;
	private static final int FAST_FLASH_STOP = 0x004;
	private int CaptureNums = 0;
	private CameraFlash mCameraFlash;
	private RecycleSpace mRecyle = null;
	
	static {
		ConfigureLog4J.configure();
	}

	private Handler mCaptureHandler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Log.d(TAG,"handleMessage: " + msg.what);
			switch (msg.what) {
			case PHOTO_CAPTURE_SHAKE:
				mApp.PhotoCapture = true;
				mCameraFlash.fastFlashStart();
				mCamera.takePicture(mShutterCallback, null, mOneShotPicture);
				mCaptureHandler.sendEmptyMessageDelayed(FAST_FLASH_STOP, 1000);
				break;
			case PHOTO_CAPTURE_SHOCK:
				mApp.PhotoCapture = true;
				if (CaptureNums < 3) {
					mCameraFlash.fastFlashStart();
					mCamera.takePicture(mShutterCallback, null, mOneShotPicture);
					CaptureNums++;
					mCaptureHandler.sendEmptyMessageDelayed(PHOTO_CAPTURE_SHOCK, 1200);
					mCaptureHandler.sendEmptyMessageDelayed(FAST_FLASH_STOP, 1000);
				} else {
					CaptureNums = 0;
				}
				break;
			case PHOTO_CAPTURE_DURATION:
				canCapture = true;
				break;
			case FAST_FLASH_STOP:
				mCameraFlash.fastFlashStop();
				if (mApp.videoRecordStatus) {
					mCameraFlash.slowFlashStart();
				}
				break;
			}
		}

	};

	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message inputMessage) {
			switch (inputMessage.what) {
			case MSG_ACTIVITY_LATE_INIT: {
				FrameLayout fl = (FrameLayout) findViewById(R.id.camera_preview);
				fl.removeView(mSurfaceView);
				mSurfaceView = new SurfaceView(CameraActivity.this, null);
				fl.addView(mSurfaceView, 0);

				mCamera = Utils.getCameraInstance();

				mRecorder = new STRecorder(mApp, CameraActivity.this,
						mSurfaceView, mCamera);

				populateSupportedPhotoSizes(mCamera);
				populateSupportedVideoSizes(mCamera);

				initUI();

				Log.d(TAG,"set surfaceview callback to mRecorder 2");
				mSurfaceView.getHolder().addCallback(mRecorder);
				mSurfaceView.getHolder().setType(
						SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

				// ////////////////////////////////////////////////
				if (mApp.videoRecordStatus == true)
					mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);

				break;
			}
			}
		}
	};

	private void startBgService() {
		/***** Start Knuth's background service *****/
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setComponent(new ComponentName("com.stcloud.drive",
				"com.stcloud.drive.bgService"));
		startService(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG,"onCreate");
		mApp = (DRApplication) getApplication();
		mApp.init();
		mApp.addActivity(this);
		mCameraFlash = new CameraFlash(mApp);
		setContentView(R.layout.main);
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);

		Button btn = (Button) findViewById(R.id.btn_photo);
		Button btn_exit = (Button) findViewById(R.id.btn_exit);
		Button btn_preivew = (Button) findViewById(R.id.btn_preview);
		btn.setOnClickListener(this);
		btn_exit.setOnClickListener(this);
		btn_preivew.setOnClickListener(this);

		btn = (Button) findViewById(R.id.btn_video);
		btn.setOnClickListener(this);
		btn = (Button) findViewById(R.id.btn_audio);
		btn.setOnClickListener(this);

		// /SoundPool
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
		musicID_01 = soundPool.load(getApplicationContext(), R.raw.bee, 1);
		// /

		/***** Check TF card format *****/
		checkTFCardFormat();
		startBgService();
		/***** Receiver for SCREEN ON/SCREEN OFF event *****/
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(DRIntent.EVT_ACC_STATUS);
		filter.addAction("com.android.rtsp.connected");
		filter.addAction("com.android.rtsp.disconnected");
		filter.addAction("com.android.updatedownload.finish");
		mScreenReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

					Log.d(TAG,"Received ACTION_SCREEN_OFF");
					if (mApp.videoRecordStatus == true) {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
					}
					releaseCamera();

				} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

					Log.d(TAG,"Received ACTION_SCREEN_ON");
					if (mApp.videoRecordStatus == false) {
						mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
					}

				} else if (intent.getAction().equals(DRIntent.EVT_ACC_STATUS)) {
					String acc = intent.getStringExtra("acc");
					if (acc.equals("1")) { // on
						Log.d(TAG,"Received ACC_ON");

						if (mApp.videoRecordStatus == false) {
							mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
						}

					} else { // off
						Log.d(TAG,"Received ACC_OFF");
						if (mApp.videoRecordStatus == true) {
							mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
						}
						releaseCamera();
					}
				} else if (intent.getAction().equals(
						"com.android.rtsp.connected")) {
					if (!recvConnected) {
						isRTSP = true;
						recvConnected = true;
						recvDisconnected = false;
						
						if (mApp.videoRecordStatus == true) {
							mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
						}
						releaseCamera();
					}


				} else if (intent.getAction().equals(
						"com.android.rtsp.disconnected")) {
					if (!recvDisconnected) {
						isRTSP = false;
						stopRtspServer();
						initCamera();
						startServerice();
						recvDisconnected = true;
						recvConnected = false;
					}

				} else if (intent.getAction().equals("com.android.updatedownload.finish")) {
					Log.d("UpdateService","com.android.updatedownload.finish");
					if (mApp.videoRecordStatus) {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
					}
					if (mCamera != null) {
						Log.d("UpdateService","release camera");
						mCamera.setPreviewCallback(null);
						mCamera.release(); // release the camera for other applications
						mCamera = null;
					}
					Intent mIntent = new Intent("com.android.recorder.stopped");
					sendBroadcast(mIntent);
					Log.d("UpdateService","send com.android.recorder.stopped");
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			}
		};

		registerReceiver(mScreenReceiver, filter);

		String prodName = Utils.getProductName();
		Log.d(TAG,prodName);
		if (prodName.equals("CW203")) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		startServerice();
		mSaver = new ImageSaver();

	}

	private void initCamera() {
		mCamera = Utils.getCameraInstance();
		if (mCamera==null) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					int i = 0;
					while((mCamera==null) && (i < 10)) {
						try {
							mCamera = Utils.getCameraInstance();
							i++;
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			mRecorder = new STRecorder(mApp, this, mSurfaceView, mCamera);
			
			populateSupportedPhotoSizes(mCamera);
			populateSupportedVideoSizes(mCamera);
	
			initUI();
	
			Log.d(TAG,"set surfaceview callback to mRecorder");
			mSurfaceView.getHolder().addCallback(mRecorder);
			mSurfaceView.getHolder().setType(
					SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
		} else {
			mRecorder = new STRecorder(mApp, this, mSurfaceView, mCamera);
	
			populateSupportedPhotoSizes(mCamera);
			populateSupportedVideoSizes(mCamera);
	
			initUI();
	
			Log.d(TAG,"set surfaceview callback to mRecorder");
			mSurfaceView.getHolder().addCallback(mRecorder);
			mSurfaceView.getHolder().setType(
					SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
		}
	}

	private void stopRtspServer() {
		this.stopService(new Intent(this, RtspServer.class));
	}

	private void startServerice() {

		Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
				.edit();
		editor.putString(RtspServer.KEY_PORT, String.valueOf(1234));
		editor.commit();

		SessionBuilder
				.getInstance()
				.setSurfaceView(
						(net.majorkernelpanic.streaming.gl.SurfaceView) mSurfaceView)
				.setPreviewOrientation(0)
				.setContext(getApplicationContext())
				// .setVideoQuality(new VideoQuality(1920, 1080, 30, 7500000))
				.setVideoQuality(new VideoQuality(1280, 720, 30, 1920000))
				.setAudioEncoder(SessionBuilder.AUDIO_NONE)
				.setVideoEncoder(SessionBuilder.VIDEO_H264);

		// Starts the RTSP server
		this.startService(new Intent(this, RtspServer.class));
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG,"onStart");
		mCamera = Utils.getCameraInstance();
		if (mCamera == null) {
			if (mApp.isBackgroundMode) {
				// camera is used by service
				// wait for a while until service releases camera
				Intent intent = new Intent(CameraActivity.this,
						RecorderService.class);
				stopService(intent);
			} else {
				// camera is used by other process
				//showDialog(DIALOG_OPEN_CAMERA_FAIL);
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						int i = 0;
						while((mCamera==null) && (i < 10)) {
							try {
								mCamera = Utils.getCameraInstance();
								i++;
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
			}
		} else {
			mRecorder = new STRecorder(mApp, this, mSurfaceView, mCamera);

			populateSupportedPhotoSizes(mCamera);
			populateSupportedVideoSizes(mCamera);

			initUI();

			Log.d(TAG,"set surfaceview callback to mRecorder");
			mSurfaceView.getHolder().addCallback(mRecorder);
			mSurfaceView.getHolder().setType(
					SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			if (!Utils.checkMediaState()) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						soundPool.play(musicID_01, 5, 5, 1, 0, 1f);
					}
				}, 100);
			} else if (!Utils.checkTFSize(Constants.DEF_TF_SIZE)) {
				playTfSmallSound();
			} else {		
				if (!mApp.videoRecordStatus && ifFirstStart) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
						Button btn = (Button) findViewById(R.id.btn_video);
						btn.setTextColor(Color.RED);
						ifFirstStart = false;
					}
				}, 1000);
			}
				
			}
			
			
			
			
		}
	}

	private void createFolder() {
		Long size = new File(Utils.TF_CARD).getTotalSpace() ;
		Log.d(TAG,"sdcard size: "+size);
		if (size < 1000000000L) {
			Log.d(TAG,"sdcard too small: "+size);
		} else {
			File photo = new File(Utils.TF_CARD+"/Hipad", "Photo");
			if (! photo.exists()){
				if (! photo.mkdirs()){
					Log.d(TAG, "failed to create Picture_lock");
				}
			}
			
			File video = new File(Utils.TF_CARD+"/Hipad", "Video");
			if (! video.exists()){
				if (! video.mkdirs()){
					Log.d(TAG, "failed to create Video_lock");
				}
			}
			
			File pic_lock = new File(Utils.TF_CARD+"/Hipad", "Photo_lock");
			if (! pic_lock.exists()){
				if (! pic_lock.mkdirs()){
					Log.d(TAG, "failed to create Picture_lock");
				}
			}
			
			File vid_lock = new File(Utils.TF_CARD+"/Hipad", "Video_lock");
			if (! vid_lock.exists()){
				if (! vid_lock.mkdirs()){
					Log.d(TAG, "failed to create Video_lock");
				}
			}	
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG,"onResume");
		mRecyle = new RecycleSpace();
		mRecyle.start();
		if (mSaver == null) {
			mSaver = new ImageSaver();
		}
		IntentFilter mountFilter = new IntentFilter();
		mountFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);// sd卡存在，但还没有挂载  
		mountFilter.addAction(Intent.ACTION_MEDIA_REMOVED);// sd卡被移除  
		mountFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);// sd卡已经从sd卡插槽拔出，但是挂载点还没解除  
		mountFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		mountFilter.addDataScheme("file");
		mMountReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				if (intent.getAction().equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
					if (!mApp.videoRecordStatus) {
						mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
					}

				} else {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							soundPool.play(musicID_01, 5, 5, 1, 0, 1f);
						}
					}, 100);
					if (mApp.videoRecordStatus == true) {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
					}
				}
			}

		};
		
		registerReceiver(mMountReceiver, mountFilter);
		/**
        IntentFilter Batteryfilter = new IntentFilter();
        Batteryfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		mBatteryReceiver= new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				 if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
					int status = intent.getIntExtra("status", 0);
	                int health = intent.getIntExtra("health", 0);
	                boolean present = intent.getBooleanExtra("present", false);
	                int level = intent.getIntExtra("level", 0);
	                int scale = intent.getIntExtra("scale", 0);
	                int icon_small = intent.getIntExtra("icon-small", 0);
	                int plugged = intent.getIntExtra("plugged", 0);
	                int voltage = intent.getIntExtra("voltage", 0);
	                int temperature = intent.getIntExtra("temperature", 0);
	                String technology = intent.getStringExtra("technology");

	                String statusString = "";
	                switch (status) {
	                case BatteryManager.BATTERY_STATUS_UNKNOWN:
	                    statusString = "unknown";
	                    break;
	                case BatteryManager.BATTERY_STATUS_CHARGING:
	                    statusString = "charging";
	                    break;
	                case BatteryManager.BATTERY_STATUS_DISCHARGING:
	                    statusString = "discharging";
	                    break;
	                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
	                    statusString = "not charging";
	                    break;
	                case BatteryManager.BATTERY_STATUS_FULL:
	                    statusString = "full";
	                    break;
	                }

	                String healthString = "";
	                switch (health) {
	                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
	                    healthString = "unknown";
	                    break;
	                case BatteryManager.BATTERY_HEALTH_GOOD:
	                    healthString = "good";
	                    break;
	                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
	                    healthString = "overheat";
	                    break;
	                case BatteryManager.BATTERY_HEALTH_DEAD:
	                    healthString = "dead";
	                    break;
	                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
	                    healthString = "voltage";
	                    break;
	                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
	                    healthString = "unspecified failure";
	                    break;
	                }

	                String acString = "";

	                switch (plugged) {
	                case BatteryManager.BATTERY_PLUGGED_AC:
	                    acString = "plugged ac";
	                    break;
	                case BatteryManager.BATTERY_PLUGGED_USB:
	                    acString = "plugged usb";
	                    break;
	                }
	                String s="";
	                s = "status:"+statusString+"\n"
	                +"health:"+healthString+"\n"
	                +"present:"+String.valueOf(present)+"\n"
	                +"level:"+String.valueOf(level)+"\n"
	                +"scale:"+String.valueOf(scale)+"\n"
	                +"icon_small:"+ String.valueOf(icon_small)+"\n"
	                +"plugged:"+acString+"\n"
	                +"voltage:"+String.valueOf(voltage)+"\n"
	                +"temperature:"+String.valueOf(temperature)+"\n"
	                +"technology:"+technology+"\n";
				 }

			}
		};
        registerReceiver(mBatteryReceiver, Batteryfilter);
		*/
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.stcloud.driverecorder.SERVICE_DESTROYED");
		filter.addAction(DRIntent.REQ_CAMERA_CAPTURE);
		filter.addAction(DRIntent.REQ_CAMERA_RECORD_START);
		filter.addAction(DRIntent.REQ_CAMERA_RECORD_STOP);
		filter.addAction(DRIntent.REQ_SENSOR_SHAKE);
		filter.addAction(DRIntent.EVT_CAMERA_RECORD_START);
		filter.addAction(DRIntent.EVT_CAMERA_RECORD_STOP);
		filter.addAction(DRIntent.EVT_RECORDER_ERROR);
		filter.addAction(DRIntent.CMD_APP_CLOSE);
		filter.addAction(DRIntent.SET_PHOTO_RESOLUTION);
		filter.addAction(DRIntent.SET_VIDEO_DURATION);
		filter.addAction(DRIntent.SET_VIDEO_RESOLUTION);
		filter.addAction(DRIntent.SET_VIDEO_SOUND);
		filter.addAction(DRIntent.REQ_SET_VIDEO_DURATION);
		filter.addAction(DRIntent.REQ_SOUND_RECORD_START);
		filter.addAction(DRIntent.REQ_SOUND_RECORD_STOP);
		filter.addAction(DRIntent.REQ_RECORD_SWITCH);
		filter.addAction(DRIntent.SET_FLASH);
		filter.addAction(DRIntent.REQ_ONE_SHOT_LOCK);
		filter.addAction("action.dog.notice.takephoto");
		filter.addAction("action.response.fileupload.complete");
		filter.addAction("com.stcloud.drive.REQ_VIDEO_RESOLUTION");
		filter.addAction("com.stcloud.drive.REQ_VIDEO_TIME");
		filter.addAction("com.stcloud.webserviceapi.REQ_SOUND_RECORD_SWITCH");
		filter.addAction("com.hipad.action.releasecamera");
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
			
				Log.d(TAG,"onReceive: " + intent.getAction());
				if (intent.getAction().equals(
						"com.stcloud.driverecorder.SERVICE_DESTROYED")) {

					Message msg = mHandler
							.obtainMessage(MSG_ACTIVITY_LATE_INIT);
					mHandler.sendMessage(msg);

				} else if (intent.getAction().equals(
						DRIntent.REQ_CAMERA_CAPTURE)) {
					if (!Utils.checkMediaState()) {
						Intent alertIntent = new Intent(CameraActivity.this,
								AlertDialogActivity.class);
						alertIntent.putExtra("type",
								AlertDialogActivity.MSG_TF_NOT_EXIST);
						// /SoundPool
						// soundPool.play(musicID_01,5, 5, 0, 0, 1);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								soundPool.play(musicID_01, 5, 5, 1, 0, 1f);
							}
						}, 20);
						// startActivity(alertIntent);mark for CW206 only,no
						// screen
					} else if (!Utils.checkTFSize(Constants.DEF_TF_SIZE)) {
						Log.d(TAG,"TF card size is too small");
						playTfSmallSound();
						Intent alertIntent = new Intent(CameraActivity.this,
								AlertDialogActivity.class);
						alertIntent.putExtra("type",
								AlertDialogActivity.MSG_INVALID_TF_SIZE);
						// startActivity(alertIntent);mark for CW206 only,no
						// screen
					} else {
						if (canCapture) {
							canCapture = false;
							mCameraFlash.fastFlashStart();
							mCamera.takePicture(mShutterCallback, null, mPicture);
							mCaptureHandler.sendEmptyMessageDelayed(FAST_FLASH_STOP, 1000);
							mCaptureHandler.sendEmptyMessageDelayed(PHOTO_CAPTURE_DURATION, 1500);
						}

					}
				} else if (intent.getAction().equals(
						DRIntent.REQ_CAMERA_RECORD_START)) {

					if (mApp.videoRecordStatus == true) {
						// started already, broadcast filename
						Intent rspIntent = new Intent(
								DRIntent.RESP_CAMERA_RECORD_START);
						rspIntent.putExtra("filename", mApp.videoFile);
						sendBroadcast(rspIntent);
					} else {
						mRecorder.handleVideoRecStartEvt(STRecorder.CMD_INTENT);
					}

				} else if (intent.getAction().equals(
						DRIntent.REQ_CAMERA_RECORD_STOP)) {

					if (mApp.videoRecordStatus == false) {
						// stopped already, broadcast filename
						Intent rspIntent = new Intent(
								DRIntent.RESP_CAMERA_RECORD_STOP);
						rspIntent.putExtra("filename", mApp.videoFile);
						sendBroadcast(rspIntent);
					} else {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_INTENT);
					}

				} else if (intent.getAction()
						.equals(DRIntent.REQ_RECORD_SWITCH)) {
					Intent swIntent = new Intent(DRIntent.RESP_RECORD_SWITCH);
					if (mApp.videoRecordStatus) {
						swIntent.putExtra("status", 1);
					} else {
						swIntent.putExtra("status", 0);
					}
					sendBroadcast(swIntent);
				} else if (intent.getAction().equals(DRIntent.REQ_SENSOR_SHAKE)) {

					Log.d(TAG,"receive sensor shake");
					if (!isRTSP) {
						Log.d(TAG,"RTSPing");
						if (!mApp.PhotoCapture) {
							mCaptureHandler.sendEmptyMessage(PHOTO_CAPTURE_SHAKE);
							Handler handler = mRecorder.getCameraHandler();
							Message msg = handler.obtainMessage(STRecorder.MSG_RESTART_VIDEO_SHAKE);
							msg.obj = intent.getStringExtra("acc");
							handler.sendMessage(msg);
						}

					} else {
						Log.d(TAG,"mark sensor when RTSPing");
					}

				} else if (intent.getAction().equals(
						DRIntent.EVT_RECORDER_ERROR)) {

					Button btn = (Button) findViewById(R.id.btn_video);
					btn.setTextColor(Color.BLACK);

				} else if (intent.getAction().equals(DRIntent.CMD_APP_CLOSE)) {

					finish();

				} else if (intent.getAction().equals(
						DRIntent.EVT_CAMERA_RECORD_START)) {
					Button btn = (Button) findViewById(R.id.btn_video);
					btn.setTextColor(Color.RED);

				} else if (intent.getAction().equals(
						DRIntent.EVT_CAMERA_RECORD_STOP)) {

					Button btn = (Button) findViewById(R.id.btn_video);
					btn.setTextColor(Color.BLACK);

				} else if (intent.getAction().equals(
						DRIntent.SET_PHOTO_RESOLUTION)) {

					String res = intent.getStringExtra("resolution");
					if (res.equals("0.3M")) { // 640x480
						for (int i = 0; i < sPhotoSizeList.size(); i++) {
							if (480 == sPhotoSizeList.get(i).height) {
								mApp.selectedPhotoSize = i;
							}
						}

					} else if (res.equals("3M")) { // 2048x1536
						for (int i = 0; i < sPhotoSizeList.size(); i++) {
							if (1080 == sPhotoSizeList.get(i).height) {
								mApp.selectedPhotoSize = i;
							}
						}
					} else if (res.equals("4M")) {
						for (int i = 0; i < sPhotoSizeList.size(); i++) {// 2688x1520
							if (1520 == sPhotoSizeList.get(i).height) {
								mApp.selectedPhotoSize = i;
							}
						}
					}

					mApp.photoWidth = sPhotoSizeList
							.get(mApp.selectedPhotoSize).width;
					mApp.photoHeight = sPhotoSizeList
							.get(mApp.selectedPhotoSize).height;

					setPictureSize(mApp.photoWidth, mApp.photoHeight);

					TextView tv = (TextView) findViewById(R.id.tv_photo_res);
					tv.setText(mApp.photoWidth + "x" + mApp.photoHeight);

					Intent evtIntent = new Intent(DRIntent.EVT_PHOTO_RESOLUTION);
					evtIntent.putExtra("resolution", res);
					sendBroadcast(evtIntent);

				} else if (intent.getAction().equals(
						DRIntent.SET_VIDEO_DURATION)) {

					int min = intent.getIntExtra("duration", 3);

					if (mApp.videoRecordStatus == true) {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
						mApp.selectedVideoLen = min * 60 * 1000;
						mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
					} else {
						mApp.selectedVideoLen = min * 60 * 1000;
					}

					Intent evtIntent = new Intent(DRIntent.EVT_VIDEO_DURATION);
					evtIntent.putExtra("duration", mApp.selectedVideoLen);
					sendBroadcast(evtIntent);

				} else if (intent.getAction().equals(
						DRIntent.REQ_SET_VIDEO_DURATION)) {
					Button btn = (Button) findViewById(R.id.btn_video);
					int min = intent.getIntExtra("duration", 3);
					if (mApp.videoRecordStatus == true) {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
						btn.setTextColor(Color.BLACK);
						mApp.selectedVideoLen = min * 60 * 1000;
						mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
						btn.setTextColor(Color.RED);
					} else {
						mApp.selectedVideoLen = min * 60 * 1000;
					}
					Intent evtIntent = new Intent(DRIntent.RESP_SET_VIDEO_DURATION);
					sendBroadcast(evtIntent);

				} else if (intent.getAction().equals(
						DRIntent.SET_VIDEO_RESOLUTION)) {
					String res = intent.getStringExtra("resolution");
					if (res.equals("720p")) {
						for (int i = 0; i < sVideoSizeList.size(); i++) {
							if (720 == sVideoSizeList.get(i).height) {
								mApp.selectedVideoSize = i;
								mApp.selectedVideoLen = 3 * 60 * 1000;
							}
						}

					} else if (res.equals("1080p")) {
						for (int i = 0; i < sVideoSizeList.size(); i++) {
							if (1080 == sVideoSizeList.get(i).height) {
								mApp.selectedVideoSize = i;
								mApp.selectedVideoLen = 2 * 60 * 1000;
							}
						}
					}

					mApp.videoWidth = sVideoSizeList
							.get(mApp.selectedVideoSize).width;
					mApp.videoHeight = sVideoSizeList
							.get(mApp.selectedVideoSize).height;

					TextView tv = (TextView) findViewById(R.id.tv_video_res);
					tv.setText(mApp.videoWidth + "x" + mApp.videoHeight);
					Button btn = (Button) findViewById(R.id.btn_video);
					if (mApp.videoRecordStatus == true) {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
						btn.setTextColor(Color.BLACK);
						mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
						btn.setTextColor(Color.RED);
					}

					Intent evtIntent = new Intent(DRIntent.EVT_VIDEO_RESOLUTION);
					evtIntent.putExtra("resolution", res);
					sendBroadcast(evtIntent);

				} else if (intent.getAction().equals(DRIntent.SET_VIDEO_SOUND)) {

					boolean status = intent.getBooleanExtra("status", false);
					if (status != mApp.audioStatus) {
						if (mApp.videoRecordStatus == true) {
							mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
							mApp.audioStatus = status;
							mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
						} else {
							mApp.audioStatus = status;
						}
					}

					Button btn = (Button) findViewById(R.id.btn_audio);
					if (mApp.audioStatus)
						btn.setTextColor(Color.RED);
					else
						btn.setTextColor(Color.BLACK);

					Intent evtIntent = new Intent(DRIntent.EVT_VIDEO_SOUND);
					evtIntent.putExtra("status", mApp.audioStatus);
					sendBroadcast(evtIntent);

				} else if (intent.getAction().equals(
						DRIntent.REQ_SOUND_RECORD_START)) {
					if (mApp.videoRecordStatus == true) {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
						mApp.audioStatus = true;
						mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
					} else {
						mApp.audioStatus = true;
					}
					Intent evtIntent = new Intent(
							DRIntent.RESP_SOUND_RECORD_START);
					sendBroadcast(evtIntent);
				} else if (intent.getAction().equals(
						DRIntent.REQ_SOUND_RECORD_STOP)) {
					if (mApp.videoRecordStatus == true) {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
						mApp.audioStatus = false;
						mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
					} else {
						mApp.audioStatus = false;
					}
					Intent evtIntent = new Intent(
							DRIntent.RESP_SOUND_RECORD_STOP);
					sendBroadcast(evtIntent);
				} else if (intent.getAction()
						.equals(DRIntent.REQ_ONE_SHOT_LOCK)) {
					mApp.oneShotPhotos.clear();

					if (!Utils.checkMediaState()) {
						Intent alertIntent = new Intent(CameraActivity.this,
								AlertDialogActivity.class);
						alertIntent.putExtra("type",
								AlertDialogActivity.MSG_TF_NOT_EXIST);
						// /SoundPool
						// soundPool.play(musicID_01,5, 5, 0, 0, 1);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								soundPool.play(musicID_01, 5, 5, 1, 0, 1f);
							}
						}, 20);
						// startActivity(alertIntent);mark for CW206 only,no
						// screen
					} else if (!Utils.checkTFSize(Constants.DEF_TF_SIZE)) {
						Log.d(TAG,"TF card size is too small");
						playTfSmallSound();
						Intent alertIntent = new Intent(CameraActivity.this,
								AlertDialogActivity.class);
						alertIntent.putExtra("type",
								AlertDialogActivity.MSG_INVALID_TF_SIZE);
						// startActivity(alertIntent);mark for CW206 only,no
						// screen
					} else {
						// mCamera.takePicture(mShutterCallback, null,
						// mOneShotPicture);
						if (!mApp.PhotoCapture) {
							mCaptureHandler.sendEmptyMessage(PHOTO_CAPTURE_SHOCK);
							Handler handler = mRecorder.getCameraHandler();
							Message msg = handler.obtainMessage(STRecorder.MSG_RESTART_VIDEO_ONE_SHOT_LOCK);
							handler.sendMessage(msg);
						}

					}
				} else if ("action.dog.notice.takephoto".equals(intent.getAction())) {
					mApp.oneShotPhotos.clear();
					photoUpload = true;
					if (!Utils.checkMediaState()) {
						Intent alertIntent = new Intent(CameraActivity.this,AlertDialogActivity.class);
						alertIntent.putExtra("type",AlertDialogActivity.MSG_TF_NOT_EXIST);
						// /SoundPool
						// soundPool.play(musicID_01,5, 5, 0, 0, 1);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								soundPool.play(musicID_01, 5, 5, 1, 0, 1f);
							}
						}, 20);
						// startActivity(alertIntent);mark for CW206 only,no
						// screen
					} else if (!Utils.checkTFSize(Constants.DEF_TF_SIZE)) {
						Log.d(TAG,"TF card size is too small");
						playTfSmallSound();
						Intent alertIntent = new Intent(CameraActivity.this,AlertDialogActivity.class);
						alertIntent.putExtra("type",AlertDialogActivity.MSG_INVALID_TF_SIZE);
						// startActivity(alertIntent);mark for CW206 only,no
						// screen
					} else {
						if (!mApp.PhotoCapture) {
							mCaptureHandler.sendEmptyMessage(PHOTO_CAPTURE_SHOCK);
							Handler handler = mRecorder.getCameraHandler();
							Message msg = handler.obtainMessage(STRecorder.MSG_RESTART_VIDEO_ONE_SHOT_LOCK);
							handler.sendMessage(msg);
						}

					}
				} else if ("action.response.fileupload.complete".equals(intent.getAction())) {
					Log.d(TAG,"file upload finished!");
				} else if (intent.getAction().equals(DRIntent.SET_FLASH)) {
					// added for engineering mode or API use
					String flashmode = intent.getStringExtra("flash");

					Parameters params = mCamera.getParameters();
					if (params != null) {
						List<String> flashModes = params.getSupportedFlashModes();
						Log.d(TAG,"flashmode cnt: " + flashModes.size());

						if (flashmode
								.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
							if (flashModes
									.contains(Parameters.FLASH_MODE_TORCH))
								params.setFlashMode(Parameters.FLASH_MODE_TORCH);
							// if not support, do not enable torch
							Log.d(TAG,"flash torch");
						} else {
							params.setFlashMode(Parameters.FLASH_MODE_OFF);
							Log.d(TAG,"flash off");
						}

						mCamera.setParameters(params);
					} else {
						Log.d(TAG,"Camera parameters is null, please check your camera");
					}
				} else if (intent.getAction().equals(
						"com.stcloud.drive.REQ_VIDEO_RESOLUTION")) {
					Intent resintent = new Intent(
							"com.stcloud.drive.RESP_VIDEO_RESOLUTION");
					resintent.putExtra("width", mApp.videoWidth);
					resintent.putExtra("height", mApp.videoHeight);
					sendBroadcast(resintent);
				} else if (intent.getAction().equals(
						"com.stcloud.drive.REQ_VIDEO_TIME")) {
					Intent tintent = new Intent(
							"com.stcloud.drive.RESP_VIDEO_TIME");
					tintent.putExtra("time", mApp.selectedVideoLen);
					sendBroadcast(tintent);
				} else if (intent.getAction().equals(
						"com.stcloud.webserviceapi.REQ_SOUND_RECORD_SWITCH")) {
					Intent sintent = new Intent(
							"com.stcloud.webserviceapi.RESP_SOUND_RECORD_SWITCH");
					if (mApp.audioStatus) {
						sintent.putExtra("status", 1);
					} else {
						sintent.putExtra("status", 0);
					}
					sendBroadcast(sintent);
				} else if (intent.getAction().equals("com.hipad.action.releasecamera")) {
					if (mApp.videoRecordStatus == true) {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
					} else {
						mRecorder.releaseMediaRecorder();
						releaseCamera();
					}
					finish();
				}

				/*
				 * else if
				 * (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
				 * log.info("ACTION_MEDIA_MOUNTED"); if (checkTFCardFormat())
				 * Utils.checkTFSize(2); }
				 */
			}
		};
		// filter.addDataScheme("file");
		registerReceiver(mReceiver, filter);

		//
	}

	private void sendUploadIntent(String filePath) {
		Intent intent = new Intent("action.receive.fileupload.begin");
		intent.putExtra("action.receive.filepath", filePath);
		intent.putExtra("action.receive.filepath.event", 2);
		sendBroadcast(intent);
	}

	@Override
	public void onPause() {
		super.onPause();

		Log.d(TAG,"onPause");
/*		//hb modify 2015-10-01 not stop recorder
		if(mRecyle!= null) {
			mRecyle.interrupt();
			mRecyle = null;
		}
		ifFirstStart = false;
		mApp.PhotoCapture = false;
		// stop recording and release camera resources
		// ...
		if (mApp.videoRecordStatus) {
			mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
		}
		if (mCamera != null) {
			Log.d(TAG,"release camera");
			mCamera.setPreviewCallback(null);
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
		this.stopService(new Intent(this, RtspServer.class));
		unregisterReceiver(mReceiver);
		unregisterReceiver(mMountReceiver);
		//
		//

		if (mSaver != null) {
			mSaver.finish();
			mSaver = null;
		}
*/
	}

	public void onStop() {
		super.onStop();

		Log.d(TAG,"onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(TAG,"onDestroy");

		unregisterReceiver(mScreenReceiver);

		/*
		 * if (mApp.videoRecordStatus == true)
		 * mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
		 * mRecorder.releaseMediaRecorder(); releaseCamera();
		 */
		if (mShutterMP != null)
			mShutterMP.release();

		mRecorder.getCameraHandler().removeMessages(
				STRecorder.MSG_RESTART_VIDEO_RECORD);
		mApp.save();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_photo_size:
			CreateDialog(DIALOG_PHOTO_RESOLUTION);
			//showDialog(DIALOG_PHOTO_RESOLUTION);
			return true;
		case R.id.menu_video_size:
			CreateDialog(DIALOG_VIDEO_RESOLUTION);
			//showDialog(DIALOG_VIDEO_RESOLUTION);
			return true;
		case R.id.menu_video_length_per_file:
			CreateDialog(DIALOG_VIDEO_LENGTH_PER_FILE);
			//showDialog(DIALOG_VIDEO_LENGTH_PER_FILE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		showDialog(DIALOG_CLOSE_APP);
	}

	private void playTfSmallSound() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				soundPool.play(musicID_01, 5, 5, 1, 0, 1f);
			}
		}, 20);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_exit:
			if (mApp.videoRecordStatus) {
				mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
			}
			if (mCamera != null) {
				Log.d(TAG,"release camera");
				mCamera.setPreviewCallback(null);
				mCamera.release(); // release the camera for other applications
				mCamera = null;
			}
			android.os.Process.killProcess(android.os.Process.myPid());
			break;
		case R.id.btn_preview:
			mRecorder.handlerPreviewDisPlay();
			break;
		case R.id.btn_photo: {
			mCameraFlash.fastFlashStart();
			if (!Utils.checkMediaState()) {
				Intent intent = new Intent(this, AlertDialogActivity.class);
				intent.putExtra("type", AlertDialogActivity.MSG_TF_NOT_EXIST);
				// /SoundPool
				// soundPool.play(musicID_01,5, 5, 0, 0, 1);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						soundPool.play(musicID_01, 5, 5, 1, 0, 1f);
					}
				}, 20);
				// startActivity(intent);mark for CW206 only,no screen
			} else if (!Utils.checkTFSize(Constants.DEF_TF_SIZE)) {
				Log.d(TAG,"TF card size is too small");
				playTfSmallSound();
				Intent intent = new Intent(this, AlertDialogActivity.class);
				intent.putExtra("type", AlertDialogActivity.MSG_INVALID_TF_SIZE);
				// startActivity(intent);mark for CW206 only,no screen
			} else {
				if (canCapture) {
					canCapture = false;
					mCameraFlash.fastFlashStart();
					mCamera.takePicture(mShutterCallback, null, mPicture);
					mCaptureHandler.sendEmptyMessageDelayed(FAST_FLASH_STOP, 1000);
					mCaptureHandler.sendEmptyMessageDelayed(PHOTO_CAPTURE_DURATION, 1500);
				}
			}
			break;
		}
		case R.id.btn_video: {
			if (mApp.videoRecordStatus) {
				mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
			} else {
				mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
			}
			break;
		}
		case R.id.btn_audio: {
			mApp.audioStatus = !mApp.audioStatus;
			Button btn = (Button) findViewById(R.id.btn_audio);
			if (mApp.audioStatus)
				btn.setTextColor(Color.RED);
			else
				btn.setTextColor(Color.BLACK);
			break;
		}
		}
	}

	public void initUI() {
		Button btnAudio = (Button) findViewById(R.id.btn_audio);
		Button btnVideo = (Button) findViewById(R.id.btn_video);
		TextView tvPhotoRes = (TextView) findViewById(R.id.tv_photo_res);
		TextView tvVideoRes = (TextView) findViewById(R.id.tv_video_res);

		if (mApp.audioStatus) {
			btnAudio.setTextColor(Color.RED);
		} else {
			btnAudio.setTextColor(Color.BLACK);
		}

		if (sPhotoSizeList != null
				&& sPhotoSizeList.size() > mApp.selectedPhotoSize) {
			mApp.photoWidth = sPhotoSizeList.get(mApp.selectedPhotoSize).width;
			mApp.photoHeight = sPhotoSizeList.get(mApp.selectedPhotoSize).height;
			setPictureSize(mApp.photoWidth, mApp.photoHeight);
			tvPhotoRes.setText(mApp.photoWidth + "x" + mApp.photoHeight);
		}

		if (sVideoSizeList != null
				&& sVideoSizeList.size() > mApp.selectedVideoSize) {
			mApp.videoWidth = sVideoSizeList.get(mApp.selectedVideoSize).width;
			mApp.videoHeight = sVideoSizeList.get(mApp.selectedVideoSize).height;
			tvVideoRes.setText(mApp.videoWidth + "x" + mApp.videoHeight);
		}

		if (mApp.videoRecordStatus) {
			btnVideo.setTextColor(Color.RED);
		} else {
			btnVideo.setTextColor(Color.BLACK);
		}
	}

	private void populateSupportedPhotoSizes(Camera camera) {
		if (camera == null)
			return;

		if (sPhotoSizeList == null) {
			sPhotoSizeList = new ArrayList<Size>();
		} else {
			sPhotoSizeList.clear();
		}

		Parameters params = camera.getParameters();
		if (params != null) {
			List<Size> picSizes = params.getSupportedPictureSizes();
			// List<Size> picSizes = params.getSupportedPreviewSizes();
			for (int i = 0; i < picSizes.size(); i++) {
				// if (picSizes.get(i).height >= 720)
				sPhotoSizeList.add(picSizes.get(i));
			}
		} else {
			Log.d(TAG,"Camera parameters is null, please check your camera");
		}
	}

	private void populateSupportedVideoSizes(Camera camera) {
		if (camera == null)
			return;

		if (sVideoSizeList == null) {
			sVideoSizeList = new ArrayList<Size>();
		} else {
			sVideoSizeList.clear();
		}

		Parameters params = camera.getParameters();
		if (params != null) {
			List<Size> videoSizes = params.getSupportedVideoSizes();
			if (videoSizes == null) {
				videoSizes = params.getSupportedPreviewSizes();
			}

			for (int i = 0; i < videoSizes.size(); i++) {
				if (videoSizes.get(i).height >= 720) // filter: only use
														// 720p/1080p
					sVideoSizeList.add(videoSizes.get(i));
			}
		} else {
			Log.d(TAG,"Camera parameters is null, please check your camera");
		}
	}

	private void setPictureSize(int width, int height) {
		Parameters params = mCamera.getParameters();
		if (params != null) {
			params.setPictureSize(width, height);

			// params.setPreviewSize(width, height);
			mCamera.setParameters(params);
		} else {
			Log.d(TAG,"Camera parameters is null, please check your camera");
		}
	}

	private void CreateDialog(int id) {
			switch (id) {
			case DIALOG_PHOTO_RESOLUTION: {
				String[] array = new String[sPhotoSizeList.size()];
				for (int i = 0; i < array.length; i++) {
					array[i] = sPhotoSizeList.get(i).width + "x"
							+ sPhotoSizeList.get(i).height;
				}
				new AlertDialog.Builder(CameraActivity.this)
						.setTitle(R.string.photo_resolution)
						.setSingleChoiceItems(array, mApp.selectedPhotoSize,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										mApp.selectedPhotoSize = whichButton;
	
										mApp.photoWidth = sPhotoSizeList
												.get(mApp.selectedPhotoSize).width;
										mApp.photoHeight = sPhotoSizeList
												.get(mApp.selectedPhotoSize).height;
	
										setPictureSize(mApp.photoWidth,
												mApp.photoHeight);
										TextView tv = (TextView) findViewById(R.id.tv_photo_res);
										tv.setText(mApp.photoWidth + "x"
												+ mApp.photoHeight);
	
										dialog.dismiss();
									}
								})
						.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
	
										dialog.dismiss();
									}
								}).create().show();
				break;
			}
	
			case DIALOG_VIDEO_RESOLUTION: {
				String[] array = new String[sVideoSizeList.size()];
				for (int i = 0; i < array.length; i++) {
					array[i] = sVideoSizeList.get(i).width + "x"
							+ sVideoSizeList.get(i).height;
				}

				new AlertDialog.Builder(CameraActivity.this)
						.setTitle(R.string.video_resolution)
						.setSingleChoiceItems(array, mApp.selectedVideoSize,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										mApp.selectedVideoSize = whichButton;
	
										mApp.videoWidth = sVideoSizeList
												.get(mApp.selectedVideoSize).width;
										mApp.videoHeight = sVideoSizeList
												.get(mApp.selectedVideoSize).height;
	
										TextView tv = (TextView) findViewById(R.id.tv_video_res);
										tv.setText(mApp.videoWidth + "x"
												+ mApp.videoHeight);
	
										dialog.dismiss();
									}
								})
						.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										dialog.dismiss();
									}
								}).create().show();
				break;
			}
			case DIALOG_VIDEO_LENGTH_PER_FILE: {
				String[] array = { "1 min", "2 mins", "3 mins", "5 mins", "10 mins" };
				int checkedItem = 0;
				if (mApp.selectedVideoLen == 60000) {
					checkedItem = 0;
				} else if (mApp.selectedVideoLen == 120000) {
					checkedItem = 1;
				} else if (mApp.selectedVideoLen == 180000) {
					checkedItem = 2;
				} else if (mApp.selectedVideoLen == 300000) {
					checkedItem = 3;
				} else if (mApp.selectedVideoLen == 600000) {
					checkedItem = 4;
				}
				new AlertDialog.Builder(CameraActivity.this)
						.setTitle(R.string.menu_video_length_per_file)
						.setSingleChoiceItems(array, checkedItem,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										switch (whichButton) {
										case 0: // 1 min
											mApp.selectedVideoLen = 60 * 1000;
											break;
										case 1: // 2 mins
											mApp.selectedVideoLen = 2 * 60 * 1000;
											break;
										case 2: // 3 mins
											mApp.selectedVideoLen = 3 * 60 * 1000;
											break;
										case 3: // 5 mins
											mApp.selectedVideoLen = 5 * 60 * 1000;
											break;
										case 4: // 10 mins
											mApp.selectedVideoLen = 10 * 60 * 1000;
											break;
										}
										dialog.dismiss();
									}
								})
						.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										dialog.dismiss();
									}
								}).create().show();
				break;
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_CLOSE_APP: {
			String[] array = { "Continue in background", "Exit" };
			return new AlertDialog.Builder(CameraActivity.this).setItems(array,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) { // continue in background
								// recording in service

								Log.d(TAG,"run in background");

								if (mApp.videoRecordStatus == true) {
									mRecorder
											.handleVideoRecStopEvt(STRecorder.CMD_BACKGROUND);
								}

								releaseCamera();

								Intent intent = new Intent(CameraActivity.this,
										RecorderService.class);
								startService(intent);

							} else if (which == 1) { // exit

								Log.d(TAG,"exit");

								// do cleanups
								if (mApp.videoRecordStatus == true)
									mRecorder
											.handleVideoRecStopEvt(STRecorder.CMD_UI);
								else
									mRecorder.releaseMediaRecorder();

								releaseCamera();
							}
							finish();
						}
					}).create();
		}
		case DIALOG_OPEN_CAMERA_FAIL: {
			Log.d(TAG,"open camera fail");
			return new AlertDialog.Builder(CameraActivity.this)
					.setIconAttribute(android.R.attr.alertDialogIcon)
					.setTitle(R.string.open_camera_fail)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									finish();
								}
							}).create();
		}
		case DIALOG_TF_SIZE_TOO_SMALL: {
			Log.d(TAG,"TF size too small");
			return new AlertDialog.Builder(CameraActivity.this)
					.setTitle(R.string.disk_space_too_small)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();
		}
		}
		return null;
	}

	private void releaseCamera() {
		if (mCamera != null) {
			Log.d(TAG,"release camera");
			mCamera.setPreviewCallback(null);
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
		Intent intent = new Intent("com.android.rec.stop");
		sendBroadcast(intent);
	}


	private boolean checkTFCardFormat() {
		if (Utils.isMediaUnmountable() || Utils.isMediaNoFs()) {
			Log.d(TAG,"TF card format error");
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setComponent(new ComponentName("com.android.settings",
					"com.android.settings.MediaFormat"));
			//startActivity(intent);
			playTfSmallSound();
			//finish();

			return false;
		}

		return true;
	}

	private PictureCallback mPicture = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			File pictureFile = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				Log.d(TAG,"Error creating media file, check storage permissions");
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {
				Log.d(TAG,"File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG,"Error accessing file: " + e.getMessage());
			}
			// mSaver.addImage(pictureFile, 3578, 2683,0);

			mCamera.startPreview();

			// create thumbnail
			try {
				File thumbnailDir = new File(FileExplorer.DIR_THUMBNAIL);
				if (!thumbnailDir.exists())
					thumbnailDir.mkdirs();

				FileOutputStream fos = new FileOutputStream(
						FileExplorer.DIR_THUMBNAIL + pictureFile.getName());
				Bitmap bmp = Utils.createThumbnail(pictureFile
						.getAbsolutePath());
				bmp.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			Intent rspIntent = new Intent(DRIntent.RESP_CAMERA_CAPTURE);
			rspIntent.putExtra("filename", pictureFile.getName());
			sendBroadcast(rspIntent);
		}
	};

	private PictureCallback mOneShotPicture = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			File pictureFile = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				Log.d(TAG,"Error creating media file, check storage permissions");
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {
				Log.d(TAG,"File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG,"Error accessing file: " + e.getMessage());
			}
			// mSaver.addImage(pictureFile, 3578, 2683,1);
			mCamera.startPreview();

			// create thumbnail
			try {
				File thumbnailDir = new File(FileExplorer.DIR_THUMBNAIL);
				if (!thumbnailDir.exists())
					thumbnailDir.mkdirs();

				FileOutputStream fos = new FileOutputStream(
						FileExplorer.DIR_THUMBNAIL + pictureFile.getName());
				Bitmap bmp = Utils.createThumbnail(pictureFile
						.getAbsolutePath());
				bmp.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			File lockDir = new File(FileExplorer.DIR_PHOTO_LOCK);
			if (!lockDir.exists())
				lockDir.mkdirs();
			mApp.photoFile = Utils.photoLock(pictureFile.getName());
			if (photoUpload) {
				sendUploadIntent(mApp.photoFile);
				photoUpload = false;
			}
			mApp.oneShotPhotos.add(mApp.photoFile);
		}
	};

	public void createThumbail(File pictureFile) {
		try {
			File thumbnailDir = new File(FileExplorer.DIR_THUMBNAIL);
			if (!thumbnailDir.exists())
				thumbnailDir.mkdirs();

			FileOutputStream fos = new FileOutputStream(
					FileExplorer.DIR_THUMBNAIL + pictureFile.getName());
			Bitmap bmp = Utils.createThumbnail(pictureFile.getAbsolutePath());
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Intent rspIntent = new Intent(DRIntent.RESP_CAMERA_CAPTURE);
		rspIntent.putExtra("filename", pictureFile.getName());
		sendBroadcast(rspIntent);
	}

	public void createOneShotThumbail(File pictureFile) {
		try {
			File thumbnailDir = new File(FileExplorer.DIR_THUMBNAIL);
			if (!thumbnailDir.exists())
				thumbnailDir.mkdirs();

			FileOutputStream fos = new FileOutputStream(
					FileExplorer.DIR_THUMBNAIL + pictureFile.getName());
			Bitmap bmp = Utils.createThumbnail(pictureFile.getAbsolutePath());
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		File lockDir = new File(FileExplorer.DIR_PHOTO_LOCK);
		if (!lockDir.exists())
			lockDir.mkdirs();
		mApp.photoFile = Utils.photoLock(pictureFile.getName());

		mApp.oneShotPhotos.add(mApp.photoFile);
	}

	public static class SaveRequest {
		File jepgFile;
		int width;
		int height;
		int flag;// 0 RESP_CAMERA_CAPTURE 1DIR_PHOTO_LOCK
	}

	private class ImageSaver extends Thread {
		private static final int QUEUE_LIMIT = 3;
		private ArrayList<SaveRequest> mQueue;
		private boolean mStop;

		public ImageSaver() {
			mQueue = new ArrayList<SaveRequest>();
			start();
		}

		public void addImage(File file, int width, int height, int flag) {
			SaveRequest r = new SaveRequest();
			r.jepgFile = file;
			r.width = width;
			r.height = height;
			r.flag = flag;

			synchronized (this) {
				while (mQueue.size() >= QUEUE_LIMIT) {
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				mQueue.add(r);
				notifyAll();
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				SaveRequest r;
				synchronized (this) {
					if (mQueue.isEmpty()) {
						notifyAll();

						if (mStop)
							break;

						try {
							wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
						}
						continue;
					}
					r = mQueue.get(0);
				}
				storageImage(r.jepgFile, r.width, r.height, r.flag);
				synchronized (this) {
					mQueue.remove(0);
					notifyAll();
				}
			}
		}

		public void waitDone() {
			synchronized (this) {
				while (!mQueue.isEmpty()) {
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		public void finish() {
			waitDone();
			synchronized (this) {
				mStop = true;
				notifyAll();
			}

			try {
				join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void storageImage(File file, int width, int height, int flag) {
			Bitmap bmp = null;
			String filename = file.getAbsolutePath().toString();
			bmp = BitmapFactory.decodeFile(filename);
			fillImageTo10M(filename, bmp, width, height, flag);
			if (flag == 0) {
				createThumbail(new File(filename));
			} else if (flag == 1) {
				createOneShotThumbail(new File(filename));
			}
			if (photoUpload) {
				sendUploadIntent(filename);
				photoUpload = false;
			}
		}
	}

	// 2048*1536 =====> 3578 2683
	public static void fillImageTo10M(String filename, Bitmap src, int desW,
			int desH, int flag) {
		Bitmap desImg = null;
		int srcW = src.getWidth(); // 原始图像宽
		int srcH = src.getHeight(); // 原始图像高
		int[] srcBuf = new int[srcW * srcH]; // 原始图片像素信息缓存

		src.getPixels(srcBuf, 0, srcW, 0, 0, srcW, srcH);

		// 计算插值表
		int[] tabY = new int[desH];
		int[] tabX = new int[desW];

		int sb = 0;
		int db = 0;
		int tems = 0;
		int temd = 0;
		int distance = srcH > desH ? srcH : desH;
		for (int i = 0; i <= distance; i++) { /* 垂直方向 */
			tabY[db] = sb;
			tems += srcH;
			temd += desH;
			if (tems > distance) {
				tems -= distance;
				sb++;
			}
			if (temd > distance) {
				temd -= distance;
				db++;
			}
		}

		sb = 0;
		db = 0;
		tems = 0;
		temd = 0;
		distance = srcW > desW ? srcW : desW;
		for (int i = 0; i <= distance; i++) { /* 水平方向 */
			tabX[db] = (short) sb;
			tems += srcW;
			temd += desW;
			if (tems > distance) {
				tems -= distance;
				sb++;
			}
			if (temd > distance) {
				temd -= distance;
				db++;
			}
		}

		// 生成放大缩小后图形像素
		int[] desBuf = new int[desW * desH];
		int dx = 0;
		int dy = 0;
		int sy = 0;

		int oldy = -1;
		for (int i = 0; i < desH; i++) {
			if (oldy == tabY[i]) {
				System.arraycopy(desBuf, dy - desW, desBuf, dy, desW);
			} else {
				dx = 0;
				for (int j = 0; j < desW; j++) {
					desBuf[dy + dx] = srcBuf[sy + tabX[j]];
					dx++;
				}
				sy += (tabY[i] - oldy) * srcW;
			}
			oldy = tabY[i];
			dy += desW;
		}

		// 生成图片
		desImg = Bitmap.createBitmap(desBuf, desW, desH,
				Bitmap.Config.ARGB_8888);

		File mFile = new File(filename);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 100;
		desImg.compress(Bitmap.CompressFormat.JPEG, options, baos);
		try {
			FileOutputStream fos = new FileOutputStream(mFile);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final ShutterCallback mShutterCallback = new ShutterCallback() {
		public void onShutter() {
			if (mShutterMP == null)
				mShutterMP = MediaPlayer
						.create(CameraActivity.this,
								Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
			if (mShutterMP != null)
				mShutterMP.start();
		}
	};

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
	}
}
