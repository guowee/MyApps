package com.haomee.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

public class MyToast {

	private static Context mContext;
	private static Toast toast;
	
	@SuppressLint("ShowToast")
	public static Toast makeText(Context context, String msg, int length){
		if (mContext==null || toast == null) {
			mContext = context;
			if(context!=null){
				toast = Toast.makeText(context, msg, length);
			}
//			toast = Toast.makeText(context, msg, length);
		} else {
			toast.setText(msg);
		}
		return toast;
	}
}
