package com.stcloud.driverecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.text.DecimalFormat;

//import org.apache.log4j.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

public class Utils {

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	public static final String TF_CARD = "/storage/external";
	//public static final String TF_CARD = "/sdcard";

	//private static final String TAG = "DriveRecorder";

	//private static final Logger log = Logger.getLogger(Utils.class);
	private static final String TAG = "Utils";
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance

			Log.d(TAG,"get camera instance");

			Parameters params = c.getParameters();
			
			if (params != null) {
				// set recording hint will cause crash when setting torch mode 
				//params.setRecordingHint(true);
				params.setPreviewFrameRate(30);
	
				// set preview size to maximum resolution
				int width = 0, height = 0;
				List<Size> sizes = params.getSupportedPreviewSizes();
				for (int i=0; i<sizes.size(); i++) {
					if (sizes.get(i).height > height) {
						height = sizes.get(i).height;
						width = sizes.get(i).width;
					}
				}
				params.setPreviewSize(width, height);
				Log.d(TAG,"set preview size to: " + width + "x" + height);
	
				c.setParameters(params);
			} else {
				Log.d(TAG,"Camera parameters is null, please check your camera");
			}

		} catch (Exception e){
			// Camera is not available (in use or does not exist)
			Log.d(TAG,e.getMessage());
		}

		return c; // returns null if camera is unavailable
	}    

	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(int type){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		/*
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "DriveRecorder");
		 */
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.
		File mediaStorageDir = null;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaStorageDir = new File(FileExplorer.DIR_PHOTO_UNLOCK);
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaStorageDir = new File(FileExplorer.DIR_VIDEO_UNLOCK);
		}

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d(TAG,"failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"IMG_"+ timeStamp + ".jpg");
		} else if(type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"VID_"+ timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}    

	/** Create a file Uri for saving an image or video */
	/*
	public static Uri getOutputMediaFileUri(int type){
		return Uri.fromFile(getOutputMediaFile(type));
	}
	*/

	// check TF card size in GB
	public static boolean checkTFSize(int size) {
		boolean retVal = false;

		StatFs stat = null;
		try {
			stat = new StatFs(TF_CARD);
			long blockSize = stat.getBlockSize();  
			long totalBlocks = stat.getBlockCount();  
			float totalsize = totalBlocks * blockSize/1024f/1024f/1024f;
			
			if (totalsize < size)
				retVal = false;
			else 
				retVal = true;
		} catch (Exception ex) {
			retVal = false;
		}

		return retVal;
	}

	public static String getMime(File file) {
		if (file == null) return null;

		MimeTypeMap map = MimeTypeMap.getSingleton();
		String ext = null;
		try {
			URI uri = file.toURI();
			ext = MimeTypeMap.getFileExtensionFromUrl(URLEncoder.encode(uri.toString(), "UTF-8"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ext != null)
			return map.getMimeTypeFromExtension(ext.toLowerCase());
		else
			return null;
	}

	public static Drawable getIcon(Context context, File file) {
		Drawable drawable = null;

		drawable = context.getResources().getDrawable(R.drawable.ic_launcher);

		return drawable;
	}


	public static boolean checkMediaState() {
		String state = Environment.getStorageState(new File(TF_CARD));

		if(state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}

		if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			Log.d(TAG,"checkMediaState - SD card is read-only. [state=MEDIA_MOUNTED_READ_ONLY]");
		} else if(Environment.MEDIA_CHECKING.equals(state)) {
			Log.d(TAG,"checkMediaState - SD card is being disk-checked. [state=MEDIA_CHECKING]");
		} else if(Environment.MEDIA_BAD_REMOVAL.equals(state)) {
			Log.d(TAG,"checkMediaState - SD card was removed before it was unmounted. [state=MEDIA_BAD_REMOVAL]");
		} else if(Environment.MEDIA_NOFS.equals(state)) {
			Log.d(TAG,"checkMediaState - SD card is blank or is using an unsupported filesystem. [state=MEDIA_NOFS]");
		} else if(Environment.MEDIA_REMOVED.equals(state)) {
			Log.d(TAG,"checkMediaState - SD card is missing. [state=MEDIA_REMOVED]");
		} else if(Environment.MEDIA_SHARED.equals(state)) {
			Log.d(TAG,"checkMediaState - SD card is shared. If you are connected to a PC via USB please disconnect and try again. [state=MEDIA_SHARED]");
		} else if(Environment.MEDIA_UNMOUNTABLE.equals(state)) {
			Log.d(TAG,"checkMediaState - SD card is present but cannot be mounted. [state=MEDIA_UNMOUNTABLE]"); 
		} else if(Environment.MEDIA_UNMOUNTED.equals(state)) {
			Log.d(TAG,"checkMediaState - SD card is not mounted. [state=MEDIA_UNMOUNTED]");
		} else {
			Log.d(TAG,"checkMediaState - Unknown media state. [state="+state+"]");
		}

		return false;
	}

	public static boolean isMediaUnmountable() {
		String state = Environment.getStorageState(new File(TF_CARD));
		return (state.equals(Environment.MEDIA_UNMOUNTABLE)); 
	}

	public static boolean isMediaNoFs() {
		String state = Environment.getStorageState(new File(TF_CARD));
		return (state.equals(Environment.MEDIA_NOFS)); 
	}

	public static Bitmap createThumbnail(String filepath) {
		String mime = Utils.getMime(new File(filepath)).toLowerCase();
		if (mime.contains("image")) {
			Bitmap bmp = decodeSampledBitmapFromFile(filepath, 140, 140);
			//return ThumbnailUtils.extractThumbnail(bmp, 140, 140);
			return bmp;
		} else if (mime.contains("video")) {
			Bitmap bmp = ThumbnailUtils.createVideoThumbnail(filepath, MediaStore.Images.Thumbnails.MINI_KIND);
			return bmp;
		} else {
			Log.d(TAG,"create thumbnail: unknown file format");
			return null;
		}
	}

	private static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	} 

	public static String videoLock(String filename)
	{
		File src = null;
		File dst = null;

		src = new File(FileExplorer.DIR_VIDEO_UNLOCK + filename);
		dst = new File(FileExplorer.DIR_VIDEO_LOCK + filename);

		try {
			copy(src, dst);
			src.delete();		
		} catch (IOException e) {
			Log.d(TAG,"Video lock error: " + e.getMessage());
			e.printStackTrace();
		}

		return dst.getAbsolutePath();
	}

	public static String photoLock(String filename)
	{
		File src = null;
		File dst = null;

		src = new File(FileExplorer.DIR_PHOTO_UNLOCK + filename);
		dst = new File(FileExplorer.DIR_PHOTO_LOCK + filename);

		try {
			copy(src, dst);
			src.delete();		
		} catch (IOException e) {
			Log.d(TAG,"Photo lock error: " + e.getMessage());
			e.printStackTrace();
		}

		return dst.getAbsolutePath();
	}

	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static String getProductName() {
		String model = getSystemProperty("ro.product.name");
		return (model == null) ? "" : model;
	}

	private static String getSystemProperty(String propName) {
		Class<?> clsSystemProperties = tryClassForName("android.os.SystemProperties");
		Method mtdGet = tryGetMethod(clsSystemProperties, "get", String.class);
		return tryInvoke(mtdGet, null, propName);
	}

	private static Class<?> tryClassForName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	private static Method tryGetMethod(Class<?> cls, String name, Class<?>... parameterTypes) {
		try {
			return cls.getDeclaredMethod(name, parameterTypes);
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T tryInvoke(Method m, Object object, Object... args) {
		try {
			return (T) m.invoke(object, args);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			return null;
		}
	}	

}
