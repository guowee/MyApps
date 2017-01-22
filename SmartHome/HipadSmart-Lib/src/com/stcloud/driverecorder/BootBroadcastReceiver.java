package com.stcloud.driverecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {

	static final String ACTION = "android.intent.action.BOOT_COMPLETED";   
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(ACTION)){
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Intent sayHelloIntent=new Intent(context,CameraActivity.class);    
						  
						   sayHelloIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
						  
						   context.startActivity(sayHelloIntent);   
					}
				}, 10*1000);
		}
	}

}
