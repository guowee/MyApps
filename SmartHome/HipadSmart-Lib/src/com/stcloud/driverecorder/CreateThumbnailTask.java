package com.stcloud.driverecorder;

import java.io.File;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

public class CreateThumbnailTask extends AsyncTask<String, Void, Bitmap> {

	private final WeakReference<ImageView> mImgViewRef;

    private String mImgName;
    private int mWidth;
    private int mHeight;
    private String mGridList;

    public CreateThumbnailTask(ImageView thumb) {
    	mImgViewRef = new WeakReference<ImageView>(thumb);
    }

    @Override
    protected Bitmap doInBackground(String... pathtoimage) {
        mImgName = pathtoimage[1];
        mWidth = Integer.parseInt(pathtoimage[2]);
        mHeight = Integer.parseInt(pathtoimage[3]);
        mGridList = pathtoimage[4];
        Bitmap bmp = createThumbnail(pathtoimage[0]);
        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap bmp) {
    	if (mImgViewRef != null && bmp != null) {
    		ImageView imageView = mImgViewRef.get();

    		if ((imageView != null) && (((File)imageView.getTag()).getName()).equals(mImgName)) {

    			imageView.setImageBitmap(bmp);
    		}
    	}
    }

    private Bitmap createThumbnail(String filepath) {
    	String mime = Utils.getMime(new File(filepath)).toLowerCase();
		if (mime.contains("image")) {
			Bitmap bmp = decodeSampledBitmapFromFile(filepath, mHeight, mWidth);
	    	return ThumbnailUtils.extractThumbnail(bmp, mHeight, mWidth);
		} else if (mime.contains("video")) {
			Bitmap bmp = ThumbnailUtils.createVideoThumbnail(filepath, MediaStore.Images.Thumbnails.MICRO_KIND);
			if (bmp != null)
				return bmp.createScaledBitmap(bmp, mWidth, mHeight, false);
			else
				return null;
		} else {
			return null;
		}
    }
    
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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
    
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
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
}
