package com.hipad.tracker.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
/**
 * 
 * @author guowei
 *
 */
public class SmallTools {
	
	public static int hightToChangePixel(Context context,int old){
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int pixel = dm.heightPixels/80 * old;
		return pixel;
	}
	
	public static int widthToChangePixel(Context context,int old){
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int pixel = dm.widthPixels/80 * old;
		return pixel;
	}
	
	
	public static void showInfoLog(String tag,String msg){
		boolean isShow = true;
		if (isShow) {
			Log.i(tag, msg);
		}
	}
}
