/**
 * CmdResponseWaitersQueue.java 2014-10-9
 */
package com.hipad.smart.local.device;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.hipad.smart.local.msg.Msg;

/**
 * @author wangbaoming
 *
 */
public class CmdResponseWaitersQueue {

	private Map<Long, ResponseWaiter> mMapResponseWaiters;
	
	public CmdResponseWaitersQueue() {
		mMapResponseWaiters = new Hashtable<Long, ResponseWaiter>();
	}

	public void accept(Msg response, boolean consume){
		ResponseWaiter waiter = null;
		if(consume){
			waiter = mMapResponseWaiters.remove(response.getNo());
		}else{
			waiter = mMapResponseWaiters.get(response.getNo());
		}
		if(null == waiter) return;

		waiter.handleMessage(response);
	}
	
	public void enqueueWait(ResponseWaiter waiter, int seconds){
		waiter.waitIt(seconds, mMapResponseWaiters);
	}
	
	public void clean(){
		Iterator<Entry<Long, ResponseWaiter>> it = mMapResponseWaiters.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Long, ResponseWaiter> entry = (Entry<Long, ResponseWaiter>) it.next();
			ResponseWaiter waiter = (ResponseWaiter) entry.getValue();
			waiter.goAway();
		}
		
		mMapResponseWaiters.clear();
	}
}
