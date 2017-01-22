/**
 * $(#)BitmapUtil.java 2015-4-15
 */
package com.hipad.smarthome.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 
 * @author wangbaoming
 *
 */
public class BitmapUtil {

	public static Bitmap clipARoundPortrait(Bitmap portrait){
		final int width = portrait.getWidth();
		final int height = portrait.getHeight();
		
		int d = width;		
		Rect src = null;
		if(width < height){
			final int clip = (height - width) / 2;
			src = new Rect(0, clip, width, height - clip);
		}else{
			final int clip = (width - height) / 2;
			d = height;
			src = new Rect(clip, 0, width - clip, height);
		}
		Rect dst = new Rect(0, 0, d, d);
		
		Bitmap result = Bitmap.createBitmap(d, d, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		
		Paint paint = new Paint();       
		canvas.drawARGB(0, 0, 0, 0); 

		paint.setAntiAlias(true); 
//		paint.setColor(0xff424242);
		System.out.println(String.format("color: %X", paint.getColor()));
		canvas.drawRoundRect(new RectF(dst), d / 2, d /2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		
		canvas.drawBitmap(portrait, src, dst, paint);
		
		return result;
	}
	
	public static Bitmap loadBitmapFromUrl(String urlStr){
		Bitmap bm = null;		
		
		try {
			URL url = new URL(urlStr);
			HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
			httpUrlConn.setConnectTimeout(1000 * 5);
			httpUrlConn.setReadTimeout(1000 * 5);
			httpUrlConn.setDoInput(true);
			httpUrlConn.connect();
			if(httpUrlConn.getResponseCode() != HttpURLConnection.HTTP_OK) return null;
			
			InputStream ins = httpUrlConn.getInputStream();
			bm = BitmapFactory.decodeStream(ins);			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return bm;
	}
	
}
