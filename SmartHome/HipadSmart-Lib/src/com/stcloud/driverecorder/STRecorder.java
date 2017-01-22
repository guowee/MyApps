package com.stcloud.driverecorder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

//import org.apache.log4j.Logger;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class STRecorder implements SurfaceHolder.Callback {

	//private final Logger log = Logger.getLogger(STRecorder.class);
	private static final String TAG = "STRecorder";
	private Camera mCamera;
	private MediaRecorder mMediaRecorder;

	private Context mContext;
	private DRApplication mApp;
	private SurfaceView mSurfaceView;
	private long mRecordStart;
	private long mRecordEnd;
	private GpsGsensorTracker mGpsGsensorTracker = null;
	
	//public static final int MSG_TAKE_PICTURE = 100;
	public static final int MSG_START_VIDEO_RECORD = 101;
	public static final int MSG_RESTART_VIDEO_RECORD = 102;
	public static final int MSG_RESTART_VIDEO_SHAKE = 103;
	public static final int MSG_CLOSE_APP = 104;
	public static final int MSG_STOP_VIDEO_RECORD = 105;
	public static final int MSG_RESTART_VIDEO_ONE_SHOT_LOCK = 106;

	// video recording start/stop cmd source
	public static final int CMD_UI = 1;
	public static final int CMD_INTENT = 2;
	public static final int CMD_BACKGROUND = 3;
	
	//soundPool
	private SoundPool soundPool=null;
	private int musicID_01;
	private int musicID_02;
	private CameraFlash mCameraFlash;
	private int mPreviewDisplay=0;//Prev button
	public STRecorder(DRApplication app, Context context, 
			SurfaceView surfaceView, Camera camera) {
		
		mApp = app;
		mContext = context;
		mSurfaceView = surfaceView;
		mCamera = camera;
		///SoundPool
		soundPool= new SoundPool(1,AudioManager.STREAM_MUSIC,5);
		musicID_01 =soundPool.load(mContext,R.raw.bee,1);
		mCameraFlash = new CameraFlash(app);
		if (mGpsGsensorTracker == null) {
			mGpsGsensorTracker = new GpsGsensorTracker(mContext);
			mGpsGsensorTracker.init();
		}
	}

	public boolean initVideoRecorder() {
		Log.d(TAG,"init video recorder ... unlock camera");
		//mMediaRecorder = new MediaRecorder();
        try{
                if (mMediaRecorder == null) {
                    	mMediaRecorder = new MediaRecorder();
			//mMediaRecorder.setOnErrorListener(this);
                } else {
                    	mMediaRecorder.reset();
                }
		
		mCamera.unlock();

		mMediaRecorder.setCamera(mCamera);

		CamcorderProfile profile = null;

		if (mApp.videoHeight/1080 == 1) {
			profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
		} else if (mApp.videoHeight/720 == 1) {
			profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
		}
		
		// Set sources
		if (mApp.audioStatus) {
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		}
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

		if (mApp.audioStatus) {
			mMediaRecorder.setAudioChannels(profile.audioChannels);
			mMediaRecorder.setAudioEncoder(profile.audioCodec);
			mMediaRecorder.setAudioEncodingBitRate(profile.audioBitRate);
			mMediaRecorder.setAudioSamplingRate(profile.audioSampleRate);
		}

		if (mApp.videoHeight/1080 == 1) {
			mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
		} else if (mApp.videoHeight/720 == 1) {
			mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		}

		mMediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
		mMediaRecorder.setVideoFrameRate(Constants.VIDEO_FRAME_RATE);
		mMediaRecorder.setVideoSize(mApp.videoWidth, mApp.videoHeight);

		// For later use
		//mMediaRecorder.setLocation(latitude, longitude);

		// Set output file
		File outFile = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_VIDEO);
		if (outFile != null) {
			String filename = outFile.toString();
			Log.d(TAG,"filename:" + filename + " mApp.videoFile:" + mApp.videoFile);
			if (mApp.videoFile != "") {
				mApp.prev_videoFile = mApp.videoFile;
				mApp.videoFile = filename;
			} else {
				mApp.videoFile = filename;
				mApp.prev_videoFile = filename;
			}
			mMediaRecorder.setOutputFile(mApp.videoFile);
		} else {
			Intent intent = new Intent(DRIntent.EVT_RECORDER_ERROR);
			// TODO: put error code into the extra
			mContext.sendBroadcast(intent);

			// cleanup
			Log.d(TAG,"error!!! video record status=false");
			mApp.videoRecordStatus = false;
			releaseMediaRecorder();

			return false;
		}

		// Set the preview output
		if(mPreviewDisplay==1)
			mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
		else
			mMediaRecorder.setPreviewDisplay(null);

		return true;
	} catch (IllegalStateException e) {
		e.printStackTrace();
		Log.e(TAG, "initVideoRecorder", e);
	} catch (RuntimeException e) {
		e.printStackTrace();
		Log.e(TAG, "initVideoRecorder", e);
	} catch (Exception e) {
		e.printStackTrace();
		Log.e(TAG, "initVideoRecorder", e);
	}
		return false;

	}

	private void startSrtRecord () {
		mGpsGsensorTracker.start(mApp.videoFile,mApp.selectedVideoLen);
	}

	private void stopSrtRecord() {
		mGpsGsensorTracker.stop();
	}
	
	public void startVideoRecording() {
		Log.d(TAG,"\r\nstartVideoRecording");
		if (false == initVideoRecorder()) {
			mHandler.removeMessages(MSG_RESTART_VIDEO_RECORD);

			///SoundPool
			//soundPool.play(musicID_01,5, 5, 0, 0, 1);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
				soundPool.play(musicID_01, 5, 5, 1, 0, 1f);
				}
				}, 20);
			
			Log.d(TAG,"TF card does not exist");
			Intent intent = new Intent(mContext, AlertDialogActivity.class);
			intent.putExtra("type", AlertDialogActivity.MSG_TF_NOT_EXIST);
			//mContext.startActivity(intent);mark for CW206 only,no screen
			return;
		}

		try {
			mMediaRecorder.prepare();
			mRecordStart = System.currentTimeMillis();
			if (mRecordEnd != 0)
				Log.d(TAG,"Recording gap: " + (mRecordStart - mRecordEnd));
			mMediaRecorder.start();
			new Handler().post(new Runnable() {
					@Override
					public void run() {
						startSrtRecord ();
					}
				});
			mCameraFlash.slowFlashStart();
			Intent intent = new Intent(DRIntent.EVT_CAMERA_RECORD_START);
			intent.putExtra("filename", mApp.videoFile);
			intent.putExtra("duration", mApp.selectedVideoLen/60000);
			mContext.sendBroadcast(intent);
		} catch (IllegalStateException e) {
			Log.d(TAG,"IllegalStateException preparing MediaRecorder: " + e.getMessage());
			mApp.videoRecordStatus = false;
			releaseMediaRecorder();
			Intent intent = new Intent(DRIntent.EVT_RECORDER_ERROR);
			mContext.sendBroadcast(intent);
			return;
		} catch (IOException e) {
			Log.d(TAG,"IOException preparing MediaRecorder: " + e.getMessage());
			mApp.videoRecordStatus = false;
			releaseMediaRecorder();
			Intent intent = new Intent(DRIntent.EVT_RECORDER_ERROR);
			mContext.sendBroadcast(intent);
			return;
		}
	}

	public void restartVideoRecording(int cause) {

		Log.d(TAG,"\r\nrestartVideoRecording");
		if (mMediaRecorder != null) {
           		mMediaRecorder.setOnErrorListener(null);
            		mMediaRecorder.setPreviewDisplay(null);
			try {
				Log.d(TAG,"restartVideoRecording: mMediaRecorder.stop");
				mMediaRecorder.stop();
			} catch (IllegalStateException e) {
				Log.d(TAG,"IllegalStateException stop MediaRecorder: " + e.getMessage());
			} catch (RuntimeException e) {
				Log.d(TAG,"RuntimeException stop MediaRecorder: " + e.getMessage());
			} catch (Exception e) {
				Log.d(TAG,"Exception stop MediaRecorder: " + e.getMessage());
			}
		}
		Log.d(TAG,"restartVideoRecording: stopSrtRecord");
		stopSrtRecord();
		Log.d(TAG,"restartVideoRecording: slowFlashStop");
		mCameraFlash.slowFlashStop();
		Log.d(TAG,"restartVideoRecording: releaseMediaRecorder");
		releaseMediaRecorder();

		Intent stopIntent = new Intent(DRIntent.EVT_CAMERA_RECORD_STOP);
		stopIntent.putExtra("filename", mApp.videoFile);
		stopIntent.putExtra("duration", mApp.selectedVideoLen/60000);
		mContext.sendBroadcast(stopIntent);
		// create thumbnail
		Log.d(TAG,"restartVideoRecording: createVideoThumbnail");
		createVideoThumbnail();
		
		if (cause == Constants.RESTART_SHAKE) {
			new CopyThread(Constants.RESTART_SHAKE).start();
		} else if (cause == Constants.RESTART_ONE_SHOT_LOCK) {
			new CopyThread(Constants.RESTART_ONE_SHOT_LOCK).start();
		}

		Log.d(TAG,"restartVideoRecording: initVideoRecorder");
		if (false == initVideoRecorder()) {
			mHandler.removeMessages(MSG_RESTART_VIDEO_RECORD);

			///SoundPool
			//soundPool.play(musicID_01,5, 5, 0, 0, 1);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
				soundPool.play(musicID_01, 5, 5, 1, 0, 1f);
				}
			}, 20);
			
			
			Log.d(TAG,"TF card does not exist");
			Intent intent = new Intent(mContext, AlertDialogActivity.class);
			intent.putExtra("type", AlertDialogActivity.MSG_TF_NOT_EXIST);
			//mContext.startActivity(intent);mark for CW206 only,no screen
			return;
		}

		Log.d(TAG,"restartVideoRecording: mMediaRecorder.prepare");
		try {
			mMediaRecorder.prepare();
			mRecordStart = System.currentTimeMillis();
			if (mRecordEnd != 0)
				Log.d(TAG,"Recording gap: " + (mRecordStart - mRecordEnd));
			mMediaRecorder.start();
			mApp.videoRecordStatus = true; //hb add 2015-09-30
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					startSrtRecord ();
				}
			});

			mCameraFlash.slowFlashStart();
			Intent startIntent = new Intent(DRIntent.EVT_CAMERA_RECORD_START);
			startIntent.putExtra("filename", mApp.videoFile);
			startIntent.putExtra("duration", mApp.selectedVideoLen/60000);
			mContext.sendBroadcast(startIntent);
		} catch (IllegalStateException e) {
			Log.d(TAG,"IllegalStateException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return;
		} catch (IOException e) {
			Log.d(TAG,"IOException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return;
		}
	}

	class CopyThread extends Thread {
		private int mFlag;
		int duration = 0;
		int prev_duration = 0;
		public CopyThread(int flag) {
			this.mFlag = flag;
		}
		
		@Override
		public void run() {
			switch(mFlag) {
				case Constants.RESTART_SHAKE:
					Intent shakeIntent = new Intent(DRIntent.RESP_SENSOR_SHAKE);
					mApp.srtFile = mApp.videoFile.substring(0,mApp.videoFile.lastIndexOf('.')) + ".srt";
					mApp.videoFile = Utils.videoLock(new File(mApp.videoFile).getName());
					if (!new File(mApp.prev_videoFile).exists()) {
						mApp.prev_videoFile = mApp.videoFile;
					}
					if (new File(mApp.srtFile).exists()) {
						mApp.srtFile = Utils.videoLock(new File(mApp.srtFile).getName());
					}

					shakeIntent.putExtra("filename", mApp.videoFile);
					shakeIntent.putExtra("prev_filename", mApp.prev_videoFile);
					duration = getVideoLength(mApp.videoFile);
					prev_duration = getVideoLength(mApp.prev_videoFile);
					shakeIntent.putExtra("duration", duration);
					shakeIntent.putExtra("prev_duration", prev_duration);
					mContext.sendBroadcast(shakeIntent);
					mApp.PhotoCapture = false;
					Log.d(TAG,"filename:" + mApp.videoFile + " duration:" + duration);
					Log.d(TAG,"prev_filename:" + mApp.prev_videoFile + " prev_duration:" + prev_duration);
					Log.d(TAG,"srt_filename:" + mApp.srtFile + "\r\n" );
					break;
				case Constants.RESTART_ONE_SHOT_LOCK:
					Intent oneShotIntent = new Intent(DRIntent.RESP_ONE_SHOT_LOCK);
					mApp.srtFile = mApp.videoFile.substring(0,mApp.videoFile.lastIndexOf('.')) + ".srt";
					mApp.videoFile = Utils.videoLock(new File(mApp.videoFile).getName());
					if (!new File(mApp.prev_videoFile).exists()) {
						mApp.prev_videoFile = mApp.videoFile;
					}
					if (new File(mApp.srtFile).exists()) {
						mApp.srtFile = Utils.videoLock(new File(mApp.srtFile).getName());
					}
					oneShotIntent.putExtra("filename", mApp.videoFile);
					oneShotIntent.putExtra("prev_filename", mApp.prev_videoFile);
					duration = getVideoLength(mApp.videoFile);
					prev_duration = getVideoLength(mApp.prev_videoFile);
					oneShotIntent.putExtra("duration", duration);
					oneShotIntent.putExtra("prev_duration", prev_duration);
					
					if (mApp.oneShotPhotos != null && mApp.oneShotPhotos.size() > 0)
						oneShotIntent.putExtra("photo_path", mApp.oneShotPhotos.get(0));
						mContext.sendBroadcast(oneShotIntent);
						mApp.PhotoCapture = false;
					Log.d(TAG,"filename:" + mApp.videoFile + " duration:" + duration);
					Log.d(TAG,"prev_filename:" + mApp.prev_videoFile + " prev_duration:" + prev_duration);
					Log.d(TAG,"srt_filename:" + mApp.srtFile + "\r\n" );
					break;
			}

		}
	}

	public void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			try {
				Log.d(TAG,"mMediaRecorder.reset");
				mMediaRecorder.reset();   // clear recorder configuration
				Log.d(TAG,"mMediaRecorder.release");
				mMediaRecorder.release(); // release the recorder object
				mRecordEnd = System.currentTimeMillis();
				mMediaRecorder = null;
				Log.d(TAG,"lock camera");
				mCamera.lock();           // lock camera for later use
				Log.d(TAG,"mCameraFlash.slowFlashStop");
				mCameraFlash.slowFlashStop();
			} catch (IllegalStateException e) {
				Log.d(TAG,"IllegalStateException: " + e.getMessage());
			} catch (RuntimeException e) {
				Log.d(TAG,"RuntimeException: " + e.getMessage());
			} catch (Exception e) {
				Log.d(TAG,"Exception: " + e.getMessage());
			}
		}
	}

	public void handleVideoRecStartEvt(int reqSrc) {
		Log.d(TAG,"handleVideoRecStartEvt: " + reqSrc);
		if (!Utils.checkMediaState()) {
			
			Log.d(TAG,"TF card not exist");
			///SoundPool
			//soundPool.play(musicID_01,5, 5, 0, 0, 1);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
				soundPool.play(musicID_01, 5, 5, 1, 0, 1f);
				}
				}, 20);
			
			Intent intent = new Intent(mContext, AlertDialogActivity.class);
			intent.putExtra("type", AlertDialogActivity.MSG_TF_NOT_EXIST);
			//mContext.startActivity(intent);//mark for CW206 only,no screen
		}
		else if (!Utils.checkTFSize(Constants.DEF_TF_SIZE)) {
			Log.d(TAG,"TF card size is too small");
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					soundPool.play(musicID_01, 5, 5, 1, 0, 1f);
				}
			}, 20);
			Intent intent = new Intent(mContext, AlertDialogActivity.class);
			intent.putExtra("type", AlertDialogActivity.MSG_INVALID_TF_SIZE);
			//mContext.startActivity(intent);mark for CW206 only,no screen
		} else {
			// disk size is large enough for operation
			// start recording
			mApp.videoRecordStatus = true;
			
			Message msg = mHandler.obtainMessage(MSG_START_VIDEO_RECORD);
			mHandler.sendMessage(msg);

			if (reqSrc == CMD_INTENT) {
				Log.d(TAG,"reqSrc == CMD_INTENT ");
				Intent intent = new Intent(DRIntent.RESP_CAMERA_RECORD_START);
				intent.putExtra("filename", mApp.videoFile);
				mContext.sendBroadcast(intent);
			}
		}
	}

	public void handlerPreviewDisPlay() {
		if(mPreviewDisplay==1)
			mPreviewDisplay=0;
		else
			mPreviewDisplay=1;
		Log.d(TAG,"handlerPreviewDisPlay " + mPreviewDisplay);
		//restartVideoRecording(-1);

		
		if (mSurfaceView.getVisibility()==View.VISIBLE) {
			mSurfaceView.setVisibility(View.INVISIBLE);
		} else {
			mSurfaceView.setVisibility(View.VISIBLE);
		}
	
	}
	public void handleVideoRecStopEvt(int reqSrc) {
		Log.d(TAG,"handleVideoRecStopEvt");
		// stop recording
		if (reqSrc != CMD_BACKGROUND) {
			Log.d(TAG,"handle video rec stop event, video rec status=false");
			mApp.videoRecordStatus = false;
		}

		if (mMediaRecorder != null) {
			mMediaRecorder.setOnErrorListener(null);
			mMediaRecorder.setPreviewDisplay(null);
			try {
				mMediaRecorder.stop();
			} catch (IllegalStateException e) {
				Log.d(TAG,"IllegalStateException stop MediaRecorder: " + e.getMessage());
			} catch (RuntimeException e) {
				Log.d(TAG,"RuntimeException stop MediaRecorder: " + e.getMessage());
			} catch (Exception e) {
				Log.d(TAG,"Exception stop MediaRecorder: " + e.getMessage());
			}
		}
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				stopSrtRecord ();
			}
		});

		mCameraFlash.slowFlashStop();
		releaseMediaRecorder();
		mHandler.removeMessages(MSG_RESTART_VIDEO_RECORD);
		
		// create thumbnail
		createVideoThumbnail();
		
		Intent stopIntent = new Intent(DRIntent.EVT_CAMERA_RECORD_STOP);
		stopIntent.putExtra("filename", mApp.videoFile);
		stopIntent.putExtra("duration", mApp.selectedVideoLen/60000);
		mContext.sendBroadcast(stopIntent);
	    
		if (reqSrc == CMD_INTENT) {
			Intent intent = new Intent(DRIntent.RESP_CAMERA_RECORD_STOP);
			intent.putExtra("filename", mApp.videoFile);
			mContext.sendBroadcast(intent);
		}
	}


	public Handler getCameraHandler() {
		return mHandler;
	}

	private void createVideoThumbnail() {
		try {
			File thumbnailDir = new File(FileExplorer.DIR_THUMBNAIL);
			if (!thumbnailDir.exists())
				thumbnailDir.mkdirs();

			String thumbFN = FileExplorer.DIR_THUMBNAIL + new File(mApp.videoFile).getName();
			thumbFN = thumbFN.substring(0, thumbFN.indexOf(".mp4")) + ".jpg";
			FileOutputStream fos = new FileOutputStream(thumbFN);
			Bitmap bmp = Utils.createThumbnail(mApp.videoFile);
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message inputMessage) {
			Log.d(TAG,"inputMessage.what: " + inputMessage.what);
			switch (inputMessage.what) {
			/*
    		case MSG_TAKE_PICTURE:
    		{
				mCamera.takePicture(null, null, mPicture);

				Message msg = mHandler.obtainMessage(MSG_TAKE_PICTURE);
				mHandler.sendMessageDelayed(msg, 60*1000);
    			break;
    		}
			 */
			case MSG_START_VIDEO_RECORD:
			{
				startVideoRecording();

				Message msg = mHandler.obtainMessage(MSG_RESTART_VIDEO_RECORD);
				msg.arg1 = Constants.RESTART_NORMAL;
				mHandler.sendMessageDelayed(msg, mApp.selectedVideoLen);
				break;
			}
			case MSG_RESTART_VIDEO_RECORD:
			{
				restartVideoRecording(inputMessage.arg1);

				Message msg = mHandler.obtainMessage(MSG_RESTART_VIDEO_RECORD);
				msg.arg1 = Constants.RESTART_NORMAL;
				mHandler.sendMessageDelayed(msg, mApp.selectedVideoLen);
				break;
			}
			case MSG_CLOSE_APP:
			{
				Intent intent = new Intent(DRIntent.CMD_APP_CLOSE);
				mContext.sendBroadcast(intent);
				//finish();
				break;
			}
			// this message is exclusive only to be called from MSG_RESTART_VIDEO_SHAKE
			case MSG_STOP_VIDEO_RECORD:
			{
				if (mApp.videoRecordStatus == true)
					handleVideoRecStopEvt(STRecorder.CMD_UI);

				String acc = (String)inputMessage.obj;
				
				Log.d(TAG,"MSG_STOP_VIDEO_RECORD acc: " + acc);
				
				Intent shakeIntent = new Intent(DRIntent.RESP_SENSOR_SHAKE);
				shakeIntent.putExtra("filename", mApp.videoFile);
				int duration = getVideoLength(mApp.videoFile);
				shakeIntent.putExtra("duration", duration);
				mContext.sendBroadcast(shakeIntent);
				mApp.PhotoCapture = false;
				break;
			}
			case MSG_RESTART_VIDEO_ONE_SHOT_LOCK:
			{
				if (mApp.videoRecordStatus == true) {
					mHandler.removeMessages(MSG_RESTART_VIDEO_RECORD);
					Message msg = mHandler.obtainMessage(MSG_RESTART_VIDEO_RECORD);
					msg.arg1 = Constants.RESTART_ONE_SHOT_LOCK;
					mHandler.sendMessageDelayed(msg, 10000);
				} else {
					handleVideoRecStartEvt(CMD_UI);
					Message msg = mHandler.obtainMessage(MSG_STOP_VIDEO_RECORD);
					mHandler.sendMessageDelayed(msg, 10000);
				}
				
				break;
			}
			case MSG_RESTART_VIDEO_SHAKE:
			{
				//String acc = (String)inputMessage.obj;
				//Log.d(TAG,"MSG_RESTART_VIDEO_SHAKE acc: " + acc);
				String acc = "1";
				if (acc.equals("1")) {
					if (mApp.videoRecordStatus == true) {
						mHandler.removeMessages(MSG_STOP_VIDEO_RECORD);
						mHandler.removeMessages(MSG_RESTART_VIDEO_RECORD);
						Message msg = mHandler.obtainMessage(MSG_RESTART_VIDEO_RECORD);
						msg.arg1 = Constants.RESTART_SHAKE;
						mHandler.sendMessageDelayed(msg, 10000);
					} else {
						//case: ACC is on and you are not recording (e.g.: stopped by remote app)
						handleVideoRecStartEvt(CMD_UI);
						
						Message msg = mHandler.obtainMessage(MSG_STOP_VIDEO_RECORD);
						msg.obj = "1";
						mHandler.sendMessageDelayed(msg, 10000);
					}
				} else if (acc.equals("0")) {
					//case: ACC is off and you are not recording (e.g.: parking)
					if (mApp.videoRecordStatus == false)
						handleVideoRecStartEvt(STRecorder.CMD_UI);
					
					mHandler.removeMessages(MSG_STOP_VIDEO_RECORD);
					Message msg = mHandler.obtainMessage(MSG_STOP_VIDEO_RECORD);
					msg.obj = acc;
					mHandler.sendMessageDelayed(msg, 10000);
				}
				break;
			}
			}
		}
	};

	private int getVideoLength(String filepath) {
		int duration = 0;
		android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();  
		mmr.setDataSource(filepath);  
	    String length = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);  
	    duration = Integer.parseInt(length)/1000;
	    return duration;
	}
	public void surfaceCreated(SurfaceHolder holder) {
		if (mContext instanceof CameraActivity)
			Log.d(TAG,"Activity --- surfaceCreated");
		else
			Log.d(TAG,"Service --- surfaceCreated");
			
		// The Surface has been created, now tell the camera where to draw the preview.
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (Exception e) {
			Log.d(TAG,"Error setting camera preview: " + e.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mContext instanceof CameraActivity)
			Log.d(TAG,"Activity --- surfaceDestroyed");
		else
			Log.d(TAG,"Service --- surfaceDestroyed");
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (mContext instanceof CameraActivity)
			Log.d(TAG,"Activity --- surfaceChanged");
		else
			Log.d(TAG,"Service --- surfaceChanged");

		if (holder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e){
			// ignore: tried to stop a non-existent preview
		}

		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();

		} catch (Exception e) {
			Log.d(TAG,"Error starting camera preview: " + e.getMessage());
		}
	}
    
}
