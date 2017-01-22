/**
 * CtrlSocketManager.java 2014-9-18
 */
package com.hipad.smart.local.msg;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.hipad.varsock.CtrlSocket;
import com.hipad.varsock.CtrlSocketFactory;

/**
 * @author wangbaoming
 *
 */
public class CtrlSocketManager {
	
	private final static Hashtable<String, CtrlSocketManager> sManagers = new Hashtable<String, CtrlSocketManager>(8);
	
	private final CtrlSocket mSock;
	
	private boolean mStarted = false;
	
	private ReaderThread mReaderThread;
	private SenderThread mSenderThread;
	private Vector<IMsgHandler> mMsgHandlers;
	
	private final static int MSG_REPORT = 0x01;

	protected static final String TAG = "CtrlSocketManager";
	private final Handler mUiHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REPORT:
				byte[] data = (byte[]) msg.obj;
				
				if(data[0] == 'K' && data[1] == 'O'){ // heart beat
					mSenderThread.sendHeartBit("OK");
					break;
				}
				
				String ip = mSock.getIp();
				Msg report = SurpportedProtocols.get(DeviceProtocolV15.DEVICE_PROTOCOL_V15).parseDataPack(data, ip);
				report(report);
				break;

			default:
				break;
			}
		};
	};
	
	private CtrlSocketManager(CtrlSocket sock){
		mMsgHandlers = new Vector<IMsgHandler>();
		mSock = sock;
		mReaderThread = new ReaderThread();
		mSenderThread = new SenderThread();
	}
	
	public static synchronized CtrlSocketManager manager(CtrlSocket sock){
		CtrlSocketManager instance = sManagers.get(sock.keyString());
		synchronized (CtrlSocketManager.class) {
			if(null == instance){
				instance = new CtrlSocketManager(sock);
				sManagers.put(sock.keyString(), instance);
			}
		}
		
//		CtrlSocketManager instance = new CtrlSocketManager(sock);
		
		return instance;
	}
	
	private void report(Msg msg){
		// handle the msg
		synchronized (mMsgHandlers) {
			if (null != mMsgHandlers) {
				for (IMsgHandler handler : mMsgHandlers) {
					handler.handleMessage(msg);
				}
			}
		}
	}
	
	public void addMsgHandler(IMsgHandler handler){
		mMsgHandlers.add(handler);
	}
	
	public void removeMsgHandler(IMsgHandler handler){
		mMsgHandlers.removeElement(handler);
	}
	
	public void send(Msg cmd){
		mSenderThread.sendMsg(cmd);
	}
	
	public synchronized void start(){	
		mReaderThread.start();
		mSenderThread.start();
		mSenderThread.getHandler();	
		
		mStarted = true;
	}
	
	public boolean isStarted(){
		return mStarted;
	}
	
	public synchronized void release(){
		mReaderThread.stopRead();
		mSenderThread.stopSend();
		
		synchronized (CtrlSocketManager.class) {
			mSock.close();
			
			sManagers.remove(mSock.keyString());
			CtrlSocketFactory.getInstance().remove(mSock);
//			mSock = null;
		}
	}
	
	// reader loop
	private class ReaderThread extends Thread {
		private boolean mStop = false;
		
		public ReaderThread() {
			setName("tcp reader");
		}
		
		@Override
		public void run() {
			super.run();
			while(!mStop){
				byte[] data = mSock.receive();
				
				if(null != data){
					Message msg = mUiHandler.obtainMessage();
					msg.what = MSG_REPORT;
					msg.obj = data;
					mUiHandler.sendMessage(msg);
				}
			}
		}
		
		public void stopRead(){
			mStop = true;
		}
	}
	
	// sender loop
	private class SenderThread extends Thread {
		private final static int MSG_SEND = 0X11;
		private final static int MSG_QUIT = 0X12;
		
		private Handler mHandler;
		
		public SenderThread() {
			setName("tcp sender");
		}
		
		@Override
		public void run() {
			Looper.prepare();
			mHandler = new Handler(){
				public void handleMessage(Message msg) {
					// TODO process the incoming messages here
					switch (msg.what) {
					case MSG_SEND:
						byte[] data = (byte[]) msg.obj;
						mSock.send(data);
						break;
					case MSG_QUIT:
						getLooper().quit();
						break;

					default:
						break;
					}
				};
			};
			
			Looper.loop();
		}
		
		public Handler getHandler(){
			if(null == mHandler){
				try {
					sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			return mHandler;
		}
		
		public void sendHeartBit(String data){
			Message msg = mHandler.obtainMessage(MSG_SEND);
			msg.obj = data.getBytes();
			mHandler.sendMessage(msg);
		}
		
		public void sendMsg(Msg cmd){
			if(null == cmd) return;
			
//			byte[] data = MsgUtil.createDataPack(cmd);
			byte[] data = SurpportedProtocols.get(DeviceProtocolV15.DEVICE_PROTOCOL_V15).createDataPack(cmd);			
			
			Message msg = mHandler.obtainMessage(MSG_SEND);
			msg.obj = data;
			mHandler.sendMessage(msg);
		}
		
		public void stopSend(){
			Message msg = mHandler.obtainMessage(MSG_QUIT);
			mHandler.sendMessage(msg);
		}
		
	}
	
}
