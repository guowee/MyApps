package com.stcloud.driverecorder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.rtsp.RtspServer;
import net.majorkernelpanic.streaming.video.VideoQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
//import org.apache.log4j.Logger;

import com.stcloud.driverecorder.CameraActivity.SaveRequest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class RecorderService extends Service {

	//private final Logger log = Logger.getLogger(RecorderService.class);
	private static final String TAG = "RecorderService";
	private Camera mCamera;
	private SurfaceView mPreview;
	private STRecorder mRecorder;
	private WindowManager windowManager;
	private BroadcastReceiver mReceiver;
	private MediaPlayer mShutterMP;
	
	///
	//

	private DRApplication mApp;
	
	private NotificationManager mNotifMgr;
	
	private int NOTIFICATION_ID = R.drawable.ic_launcher;
    
	private static String STR_RECORDING = "Status: recording";
	private static String STR_NOT_RECORDING = "Status: not recording";
	private static boolean uploadPhoto = false;
	private ImageSaver mSaver;
	
	private static final int PHOTO_CAPTURE_SHAKE = 0x001;
	private static final int PHOTO_CAPTURE_SHOCK = 0x002;
	private int CaptureNums = 0;
	
	private Handler mCaptureHandler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case PHOTO_CAPTURE_SHAKE:
				mApp.PhotoCapture = true;
				mCamera.takePicture(mShutterCallback, null,mOneShotPicture);
				break;
			case PHOTO_CAPTURE_SHOCK:
				mApp.PhotoCapture = true;
				mCamera.takePicture(mShutterCallback, null,mOneShotPicture);
				CaptureNums ++;
				if (CaptureNums < 3) {
					mCaptureHandler.sendEmptyMessageDelayed(PHOTO_CAPTURE_SHAKE, 500);
				} else {
					CaptureNums = 0;
				}
				break;
			}
		}
		
	};
	
	@Override
	public void onCreate() {
		Log.d(TAG,"onCreate");
		mApp = (DRApplication)getApplication();
		
		// Create new SurfaceView, set its size to 1x1, move it to the top left corner 
		// and set this service as a callback
        windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        mPreview = new net.majorkernelpanic.streaming.gl.SurfaceView(this,null);
        LayoutParams layoutParams = new WindowManager.LayoutParams(
            120, 90,
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        );
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(mPreview, layoutParams);

        mCamera = Utils.getCameraInstance();

		mRecorder = new STRecorder(mApp, this, mPreview, mCamera);
        
        mPreview.getHolder().addCallback(mRecorder);
        // deprecated setting, but required on Android versions prior to 3.0
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
        IntentFilter filter = new IntentFilter();
        filter.addAction(DRIntent.REQ_CAMERA_CAPTURE);
        filter.addAction(DRIntent.REQ_CAMERA_RECORD_START);
        filter.addAction(DRIntent.REQ_CAMERA_RECORD_STOP);
        filter.addAction(DRIntent.REQ_SENSOR_SHAKE);
        filter.addAction(DRIntent.CMD_APP_CLOSE);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(DRIntent.EVT_ACC_STATUS);
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
        filter.addAction(DRIntent.ACTION_REVERSE_OFF);
        filter.addAction(DRIntent.ACTION_REVERSE_ON);
		filter.addAction("action.dog.notice.takephoto");
		filter.addAction("action.response.fileupload.complete");
		filter.addAction("com.stcloud.drive.REQ_VIDEO_RESOLUTION");
		filter.addAction("com.stcloud.drive.REQ_VIDEO_TIME");
		filter.addAction("com.stcloud.webserviceapi.REQ_SOUND_RECORD_SWITCH");
		
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(DRIntent.REQ_CAMERA_CAPTURE)) {
					if (!Utils.checkMediaState()) {
						Intent alertIntent = new Intent(RecorderService.this, AlertDialogActivity.class);
						alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						alertIntent.putExtra("type", AlertDialogActivity.MSG_TF_NOT_EXIST);
						startActivity(alertIntent);
					}
					else if (!Utils.checkTFSize(Constants.DEF_TF_SIZE)) {
						Log.d(TAG,"TF card size is too small");
						Intent alertIntent = new Intent(RecorderService.this, AlertDialogActivity.class);
						alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						alertIntent.putExtra("type", AlertDialogActivity.MSG_INVALID_TF_SIZE);
						startActivity(alertIntent);
					} else {
						mCamera.takePicture(mShutterCallback, null, mPicture);
					}
				} else if (intent.getAction().equals(DRIntent.REQ_CAMERA_RECORD_START)) {
					// handle camera record start event
					if (mApp.videoRecordStatus == true) {
						Intent rspIntent = new Intent(DRIntent.RESP_CAMERA_RECORD_START);
						rspIntent.putExtra("filename", mApp.videoFile);
						sendBroadcast(rspIntent);
					} else {
						mRecorder.handleVideoRecStartEvt(STRecorder.CMD_INTENT);
					}
				} else if (intent.getAction().equals(DRIntent.REQ_CAMERA_RECORD_STOP)) {
					// handle camera record stop event
					if (mApp.videoRecordStatus == false) {
						// stopped already, broadcast filename
						Intent rspIntent = new Intent(DRIntent.RESP_CAMERA_RECORD_STOP);
						rspIntent.putExtra("filename", mApp.videoFile);
						sendBroadcast(rspIntent);
					} else {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_INTENT);
						createNotification(STR_NOT_RECORDING);

					}
				} else if (intent.getAction().equals(DRIntent.REQ_SENSOR_SHAKE)) {
					Log.d(TAG,"receive sensor shake");
					if (!mApp.PhotoCapture) {
						mCaptureHandler.sendEmptyMessage(PHOTO_CAPTURE_SHAKE);
						Handler handler = mRecorder.getCameraHandler();
						Message msg = handler.obtainMessage(STRecorder.MSG_RESTART_VIDEO_SHAKE);
						msg.obj = intent.getStringExtra("acc");
						handler.sendMessage(msg);
						createNotification(STR_RECORDING);
					}

				}
				/*
				else if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
					// check whether TF card exist and check TF card condition
					if (Utils.checkTFSize(Constants.DEF_TF_SIZE)) {
						
					}
				}
				*/ 
				else if (intent.getAction().equals(DRIntent.CMD_APP_CLOSE)) {
					stopSelf();
				} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
					Log.d(TAG,"received ACTION_SCREEN_ON");

					if (mApp.videoRecordStatus == false) {
						mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
						createNotification(STR_RECORDING);
					}

					
				} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
					Log.d(TAG,"received ACTION_SCREEN_OFF");
					if (mApp.videoRecordStatus == true) {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
						createNotification(STR_NOT_RECORDING);
					} 
					//releaseCamera();
				} 
				
				else if (intent.getAction().equals(DRIntent.EVT_ACC_STATUS)) {
					String acc = intent.getStringExtra("acc");
					if (acc.equals("1")) { // on
						Log.d(TAG,"received ACC_ON");

						if (mApp.videoRecordStatus == false) {
							mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
							createNotification(STR_RECORDING);
						}
						
					} else { // off
						Log.d(TAG,"received ACC_OFF");
						if (mApp.videoRecordStatus == true) {
							mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
							createNotification(STR_NOT_RECORDING);
						} 
						
					}
				}
				
				else if (intent.getAction().equals(DRIntent.SET_PHOTO_RESOLUTION)) {
					String res = intent.getStringExtra("resolution");
					if (res.equals("0.3M")) { // 640x480
						for (int i=0; i<CameraActivity.sPhotoSizeList.size(); i++) {
							if (480 == CameraActivity.sPhotoSizeList.get(i).height) {
								mApp.selectedPhotoSize = i;
							}
						}
						
					} else if (res.equals("3M")) { // 2048x1536
						for (int i=0; i<CameraActivity.sPhotoSizeList.size(); i++) {
							if (1536 == CameraActivity.sPhotoSizeList.get(i).height || 1520 == CameraActivity.sPhotoSizeList.get(i).height) {
								mApp.selectedPhotoSize = i;
							}
						}
					}
					
					mApp.photoWidth = CameraActivity.sPhotoSizeList.get(mApp.selectedPhotoSize).width;
					mApp.photoHeight = CameraActivity.sPhotoSizeList.get(mApp.selectedPhotoSize).height;

					setPictureSize(mApp.photoWidth, mApp.photoHeight);

					Intent evtIntent = new Intent(DRIntent.EVT_PHOTO_RESOLUTION);
					evtIntent.putExtra("resolution", res);
					sendBroadcast(evtIntent);

				} else if (intent.getAction().equals(DRIntent.SET_VIDEO_DURATION)) {
					
					int min = intent.getIntExtra("duration", 3);

					if (mApp.videoRecordStatus == true) {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
						mApp.selectedVideoLen = min*60*1000;
						mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
					} else {
						mApp.selectedVideoLen = min*60*1000;
					}

					Intent evtIntent = new Intent(DRIntent.EVT_VIDEO_DURATION);
					evtIntent.putExtra("duration", mApp.selectedVideoLen);
					sendBroadcast(evtIntent);
					
				} else if (intent.getAction().equals(
						DRIntent.REQ_SET_VIDEO_DURATION)) {
					int min = intent.getIntExtra("duration", 3);

					if (mApp.videoRecordStatus == true) {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
						mApp.selectedVideoLen = min * 60 * 1000;
						mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
					} else {
						mApp.selectedVideoLen = min * 60 * 1000;
					}

					Intent evtIntent = new Intent(DRIntent.RESP_SET_VIDEO_DURATION);
					sendBroadcast(evtIntent);
					
				} else if (intent.getAction().equals(DRIntent.SET_VIDEO_RESOLUTION)) {

					String res = intent.getStringExtra("resolution");
					if (res.equals("720p")) {
						for (int i=0; i<CameraActivity.sVideoSizeList.size(); i++) {
							if (720 == CameraActivity.sVideoSizeList.get(i).height) {
								mApp.selectedVideoSize = i;
							}
						}
						
					} else if (res.equals("1080p")) {
						for (int i=0; i<CameraActivity.sVideoSizeList.size(); i++) {
							if (1080 == CameraActivity.sVideoSizeList.get(i).height) {
								mApp.selectedVideoSize = i;
							}
						}
					}
					
					mApp.videoWidth = CameraActivity.sVideoSizeList.get(mApp.selectedVideoSize).width;
					mApp.videoHeight = CameraActivity.sVideoSizeList.get(mApp.selectedVideoSize).height;

					if (mApp.videoRecordStatus == true) {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
						mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
					}
					
					Intent evtIntent = new Intent(DRIntent.EVT_VIDEO_RESOLUTION);
					evtIntent.putExtra("resolution", res);
					sendBroadcast(evtIntent);

					
				} else if (intent.getAction().equals(DRIntent.REQ_RECORD_SWITCH)) {
					Intent swIntent = new Intent(DRIntent.RESP_RECORD_SWITCH);
					if (mApp.videoRecordStatus) {
						swIntent.putExtra("status", 1);
					} else {
						swIntent.putExtra("status", 0);
					}
					sendBroadcast(swIntent);
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
					
					Intent evtIntent = new Intent(DRIntent.EVT_VIDEO_SOUND);
					evtIntent.putExtra("status", mApp.audioStatus);
					sendBroadcast(evtIntent);
					
				} else if (intent.getAction().equals(DRIntent.REQ_SOUND_RECORD_START)) {
					if (mApp.videoRecordStatus == true) {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
						mApp.audioStatus = true;
						mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
					} else {
						mApp.audioStatus = true;
					}
					Intent evtIntent = new Intent(DRIntent.RESP_SOUND_RECORD_START);
					sendBroadcast(evtIntent);
				} else if (intent.getAction().equals(DRIntent.REQ_SOUND_RECORD_STOP)) {
					if (mApp.videoRecordStatus == true) {
						mRecorder.handleVideoRecStopEvt(STRecorder.CMD_UI);
						mApp.audioStatus = false;
						mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
					} else {
						mApp.audioStatus = false;
					}
					Intent evtIntent = new Intent(DRIntent.RESP_SOUND_RECORD_STOP);
					sendBroadcast(evtIntent);
				}
				// added for engineering mode or API use
				else if (intent.getAction().equals(DRIntent.SET_FLASH)) { 
					String flashmode = intent.getStringExtra("flash");

					Parameters params = mCamera.getParameters();
					if (params != null) {
						List<String> flashModes = params.getSupportedFlashModes();
						Log.d(TAG,"flashmode cnt: "+ flashModes.size());
	
						if(flashmode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
							if(flashModes.contains(Parameters.FLASH_MODE_TORCH))
								params.setFlashMode(Parameters.FLASH_MODE_TORCH);
							//if not support, do not enable torch
							Log.d(TAG,"flash torch");
						}
						else {
							params.setFlashMode(Parameters.FLASH_MODE_OFF);
							Log.d(TAG,"flash off");
						}
	
						mCamera.setParameters(params);
					} else {
						Log.d(TAG,"Camera parameters is null, please check your camera");
					}
				} else if (intent.getAction().equals(DRIntent.REQ_ONE_SHOT_LOCK)) {
					mApp.oneShotPhotos.clear();
					
					if (!Utils.checkMediaState()) {
						Intent alertIntent = new Intent(RecorderService.this, AlertDialogActivity.class);
						alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						alertIntent.putExtra("type", AlertDialogActivity.MSG_TF_NOT_EXIST);
						startActivity(alertIntent);
					}
					else if (!Utils.checkTFSize(Constants.DEF_TF_SIZE)) {
						Log.d(TAG,"TF card size is too small");
						Intent alertIntent = new Intent(RecorderService.this, AlertDialogActivity.class);
						alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						alertIntent.putExtra("type", AlertDialogActivity.MSG_INVALID_TF_SIZE);
						startActivity(alertIntent);
					} else {
						if (!mApp.PhotoCapture) {
							mCaptureHandler.sendEmptyMessage(PHOTO_CAPTURE_SHAKE);
							Handler handler = mRecorder.getCameraHandler();
							Message msg = handler.obtainMessage(STRecorder.MSG_RESTART_VIDEO_ONE_SHOT_LOCK);
							handler.sendMessage(msg);
						}
					}
				} else if ("action.dog.notice.takephoto".equals(intent.getAction())) {
					mApp.oneShotPhotos.clear();
					uploadPhoto = true;
					if (!Utils.checkMediaState()) {
						Intent alertIntent = new Intent(RecorderService.this, AlertDialogActivity.class);
						alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						alertIntent.putExtra("type", AlertDialogActivity.MSG_TF_NOT_EXIST);
						startActivity(alertIntent);
					}
					else if (!Utils.checkTFSize(Constants.DEF_TF_SIZE)) {
						Log.d(TAG,"TF card size is too small");
						Intent alertIntent = new Intent(RecorderService.this, AlertDialogActivity.class);
						alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						alertIntent.putExtra("type", AlertDialogActivity.MSG_INVALID_TF_SIZE);
						startActivity(alertIntent);
					} else {
						if (!mApp.PhotoCapture) {
							mCaptureHandler.sendEmptyMessage(PHOTO_CAPTURE_SHAKE);
							Handler handler = mRecorder.getCameraHandler();
							Message msg = handler.obtainMessage(STRecorder.MSG_RESTART_VIDEO_ONE_SHOT_LOCK);
							handler.sendMessage(msg);
						}

					}
				} else if ("action.response.fileupload.complete".equals(intent.getAction())) {
					Log.d(TAG,"file upload finished!");
				} else if (intent.getAction().equals(DRIntent.ACTION_REVERSE_OFF)) {
					Log.d(TAG,DRIntent.ACTION_REVERSE_OFF);
				} else if (intent.getAction().equals(DRIntent.ACTION_REVERSE_ON)) {
					Log.d(TAG,DRIntent.ACTION_REVERSE_ON);
					Intent reverseIntent = new Intent(RecorderService.this, CameraActivity.class);
					reverseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(reverseIntent);
				} else if (intent.getAction().equals("com.stcloud.drive.REQ_VIDEO_RESOLUTION")) {
					android.util.Log.d("cheishu","com.stcloud.drive.REQ_VIDEO_RESOLUTION");
					Intent resintent = new Intent("com.stcloud.drive.RESP_VIDEO_RESOLUTION");
					resintent.putExtra("width", mApp.VIDEO_WIDTH);
					resintent.putExtra("height", mApp.VIDEO_HEIGHT);
					sendBroadcast(resintent);
				} else if (intent.getAction().equals("com.stcloud.drive.REQ_VIDEO_TIME")) {
					Intent tintent = new Intent("com.stcloud.drive.RESP_VIDEO_TIME");
					tintent.putExtra("time", mApp.selectedVideoLen);
					sendBroadcast(tintent);
				} else if (intent.getAction().equals("com.stcloud.webserviceapi.REQ_SOUND_RECORD_SWITCH")) {
					android.util.Log.d("cheishu","com.stcloud.webserviceapi.REQ_SOUND_RECORD_SWITCH");
					Intent sintent = new Intent(" com.stcloud.webserviceapi.RESP_SOUND_RECORD_SWITCH");
					if (mApp.audioStatus) {
						sintent.putExtra("status", 1);
					} else {
						sintent.putExtra("status", 0);
					}
					sendBroadcast(sintent);
				}
			}
		};
		
		//filter.addDataScheme("file");
		registerReceiver(mReceiver, filter);		
        
		// TF card condition check
		checkTFCard();
		startServerice();
		
		//
		//
		mSaver = new ImageSaver();
		
	}

	private void startServerice() {
		SessionBuilder.getInstance()
		.setSurfaceView((net.majorkernelpanic.streaming.gl.SurfaceView) mPreview)
		.setPreviewOrientation(0)
		.setContext(getApplicationContext())
		.setVideoQuality(new VideoQuality(1920, 1080, 30, 1920000))
		.setAudioEncoder(SessionBuilder.AUDIO_NONE)
		.setVideoEncoder(SessionBuilder.VIDEO_H264);
		
		// Starts the RTSP server
		this.startService(new Intent(this,RtspServer.class));
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG,"onStartCommand");
		if (mApp.videoRecordStatus == true) {
			Log.d(TAG,"onStartCommand, video record status=true");
			mApp.isBackgroundMode = true;
			mRecorder.handleVideoRecStartEvt(STRecorder.CMD_UI);
			createNotification(STR_RECORDING);

		} else {
			Log.d(TAG,"onStartCommand, video record status=false");
			mApp.isBackgroundMode = true;
			createNotification(STR_NOT_RECORDING);
		}
		
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG,"onDestroy");
		mApp.save();
		unregisterReceiver(mReceiver);

		if (mApp.videoRecordStatus == true) {
			mRecorder.handleVideoRecStopEvt(STRecorder.CMD_BACKGROUND);
			createNotification(STR_NOT_RECORDING);
		}
		
		releaseCamera();
		
		if (mShutterMP != null)
			mShutterMP.release();
		
		windowManager.removeView(mPreview);
		mApp.isBackgroundMode = false;
		
		stopForeground(true);
	    mNotifMgr.cancel(NOTIFICATION_ID);
		
		// broadcast service destroyed
		Intent intent = new Intent("com.stcloud.driverecorder.SERVICE_DESTROYED");
		sendBroadcast(intent);
		this.stopService(new Intent(this,RtspServer.class));
	}	
	
	private void setPictureSize(int width, int height) {
		Parameters params = mCamera.getParameters();
		if (params != null) {
			params.setPictureSize(width, height);
	
			//params.setPreviewSize(width, height);
			mCamera.setParameters(params);
		} else {
			Log.d(TAG,"Camera parameters is null, please check your camera");
		}
	}
	
	private void releaseCamera() {
		if (mCamera != null) {
			Log.d(TAG,"release camera");
			mCamera.setPreviewCallback(null);
			mCamera.release();        // release the camera for other applications
			mCamera = null;
		}
	}
    
	private void checkTFCard() {
		if (Utils.isMediaUnmountable() || Utils.isMediaNoFs()) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.MediaFormat"));
			startActivity(intent);
			
			stopSelf();
		}
	}
	
	void createNotification(String text) {
		int icon = 0;
		
	    if (mNotifMgr == null) 
	        mNotifMgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

	    // Instantiate the Notification
	    if (text.equals(STR_RECORDING))
	    	icon = R.drawable.red_bullet;
	    else if (text.equals(STR_NOT_RECORDING))
	    	icon = R.drawable.grey_bullet;
	    	
	    CharSequence tickerText = "Drive Recorder";

	    Notification notif = new Notification(icon, tickerText, System.currentTimeMillis());

	    // Define Notification's expanded message and intent
	    Context context = getApplicationContext();
	    String contentText = text;

	    Intent notifIntent = new Intent(this, CameraActivity.class);
	    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notifIntent, 0);

	    notif.setLatestEventInfo(context, "DriveRecorder", contentText, contentIntent);

	    mNotifMgr.notify(NOTIFICATION_ID, notif);
	    startForeground(NOTIFICATION_ID, notif);
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

			mCamera.startPreview();

			// create thumbnail
			
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
	};
	
	private void sendUploadIntent(String filePath) {
		Intent intent = new Intent("action.receive.fileupload.begin");
		intent.putExtra("action.receive.filepath", filePath);
		intent.putExtra("action.receive.filepath.event", 2);
		sendBroadcast(intent);
	}
	
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
			//mSaver.addImage(pictureFile, 3578, 2683,1);
			mCamera.startPreview();
			// create thumbnail
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
			if (uploadPhoto) {
				sendUploadIntent(mApp.photoFile);
				uploadPhoto = false;
			}
			mApp.oneShotPhotos.add(mApp.photoFile);
		}
	};

	public void createThumbail (File pictureFile) {
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
	
	public void createOneShotThumbail(File pictureFile) {
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

		mApp.oneShotPhotos.add(mApp.photoFile);
	}
	
	public static class SaveRequest {
		File jepgFile;
		int width;
		int height;
		int flag;//0 RESP_CAMERA_CAPTURE  1DIR_PHOTO_LOCK
	}
	
	private class ImageSaver extends Thread {
		private static final int QUEUE_LIMIT = 3;
		private ArrayList<SaveRequest> mQueue;
		private boolean mStop;
		
		public ImageSaver () {
			mQueue = new ArrayList<SaveRequest>();
			start();
		}
		
		public void addImage(File file,int width,int height,int flag) {
			SaveRequest r = new SaveRequest();
			r.jepgFile = file;
			r.width = width;
			r.height = height;
			r.flag = flag;
			
			synchronized (this) {
				while(mQueue.size() >= QUEUE_LIMIT) {
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
						
						if (mStop) break;
						
						try {
							wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
						}
						continue;
					}
					r = mQueue.get(0);
				}
				storageImage(r.jepgFile,r.width,r.height,r.flag);
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
		
		public void storageImage(File file,int width,int height,int flag) {
			Bitmap bmp = null;
			String filename = file.getAbsolutePath().toString();
			bmp = BitmapFactory.decodeFile(filename);
			fillImageTo10M(filename,bmp, width, height,flag);
			if (flag == 0) {
				createThumbail(new File(filename));
			} else if (flag == 1) {
				createOneShotThumbail(new File(filename));
			}
			if (uploadPhoto) {
				sendUploadIntent(filename);
				uploadPhoto = false;
			}
		}
	}
	//2048*1536   =====> 3578  2683  
	public static void fillImageTo10M(String filename,Bitmap src, int desW, int desH,int flag) {
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
            	mShutterMP = MediaPlayer.create(RecorderService.this, 
            			Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            if (mShutterMP != null)
            	mShutterMP.start();
	    }
	};	
}
