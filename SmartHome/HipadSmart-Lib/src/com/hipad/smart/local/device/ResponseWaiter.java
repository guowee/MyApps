package com.hipad.smart.local.device;

import java.util.Map;

import android.os.Handler;
import android.util.Log;

import com.hipad.smart.local.msg.LocalCmd;
import com.hipad.smart.local.msg.IMsgHandler;
import com.hipad.smart.local.msg.Msg;

public abstract class ResponseWaiter implements IMsgHandler {
	private final static String TAG = "ResponseWaiter";

	private LocalCmd mCmd;

	private final Handler mHandler = new Handler();

	protected Map<Long, ResponseWaiter> mMapWaiters;
	private final Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			timeOut();			
			mMapWaiters.remove(mCmd.getNo());
		}
	};

	public ResponseWaiter(LocalCmd msg) {
		mCmd = msg;
	}
	
	@Override
	public void handleMessage(Msg response) {
		Log.d(TAG, "handler msg: " + mCmd.toString());
		
		mHandler.removeCallbacks(mRunnable);
		// TODO handle the come response 
		
	}
	
	public void goAway(){
		mHandler.removeCallbacks(mRunnable);
	}
	
	public void waitIt(int seconds, final Map<Long, ResponseWaiter> waiters){
		mMapWaiters = waiters;
		mHandler.postDelayed(mRunnable, 1000 * seconds);
		waiters.put(mCmd.getNo(), this);
	}
	
	public void timeOut(){
		Log.d(TAG, "timeout msg: " + mCmd.toString());
	}
}
