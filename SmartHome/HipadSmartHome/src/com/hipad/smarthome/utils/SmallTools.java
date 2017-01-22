package com.hipad.smarthome.utils;

import java.util.ArrayList;
import java.util.List;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.device.CommonDevice.Group;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

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
	
	public static Group getGroup(int curType){
		List<Group> typeList = new ArrayList<CommonDevice.Group>();
		typeList.add(Group.Environment);
		typeList.add(Group.Cook);
		typeList.add(Group.Healthy);
		typeList.add(Group.Security);
		typeList.add(Group.Entertainment);
		return typeList.get(curType);
	}
	
	public static void showInfoLog(String tag,String msg){
		// 设置是否打印Log信息标志
		boolean isShow = true;
		if (isShow) {
			Log.i(tag, msg);
		}
	}
}
