package com.stcloud.driverecorder;

import java.lang.reflect.Method;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

public class DialBroadcastReceiver extends BroadcastReceiver{
	 public static final String TAG = DialBroadcastReceiver.class.getSimpleName();
	 Handler mHandler;
	 TelephonyManager tm = null;
	 String number = null;
	 private Context mcontext =null; 
	 public void onReceive(Context context, Intent intent) {
		 tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		 Log.d(TAG,"onReceive");
		 mcontext =context;

			 if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				
				     number =intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

				    
				     if(number.equals("*#*#8787")){	 
		            	 new Thread(new Runnable(){
	            		    @Override
	            		    public void run() {
	            		        // TODO Auto-generated method stub           
	            		    	
	            		            try{
	            		            	     int i = 0;
		            		   	   			 Class<TelephonyManager> c= TelephonyManager.class;
		            		   	   			 Method getTelMethod = c.getDeclaredMethod("getITelephony");
		            		   	   			 getTelMethod.setAccessible(true);
		            		   	   			 com.android.internal.telephony.ITelephony telephonyService =
		            		   	   			            (ITelephony) getTelMethod.invoke(tm);
		            		   	   		     boolean bEndCall = telephonyService.endCall();
		            		   	   		     Log.d(TAG,bEndCall+",i="+i);
		            		   	   		     while(!bEndCall && i<5){
		            		   	   		       bEndCall=telephonyService.endCall();
		            		   	   		       Thread.sleep(100L);
		            		   	   		       i++;
		            		   	   		     }
		            		   	   		    
	            		   	              }
	            		            
	            		            catch(Exception e){
	            		                e.printStackTrace();
	            		            }	            		       
	            		    }            
		            	}).start();   	 
		            		            	 
		            	Intent newIntent = new Intent("com.marvell.hidenicon");
		   		    	//newIntent.setClassName("com.example.test", "com.example.test.MainActivity");
		   		    	newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		   		    	mcontext.startActivity(newIntent); 
				    }
                
			 }
         }
			
	   
}
