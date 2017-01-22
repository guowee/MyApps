package com.hipad.smart.local.msg;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Hashtable;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.hipad.varsock.UdpBroadcast;
import com.hipad.varsock.UdpBroadcastFactory;

public class UdpBroadcastManager {	
	private final static String TAG = "UdpBroadcastManager";
	
	private final static Hashtable<String, UdpBroadcastManager> sManagers = new Hashtable<String, UdpBroadcastManager>(4);
	
	private final UdpBroadcast mUdpBroadcast;

	private boolean mStarted = false;
	
	private MsgReader mMsgReader;
	private MsgSender mMsgSender;
	private ArrayList<IMsgHandler> mMsgHandlers;
	
	private final static int MSG_REPORT = 0x01;
	private Handler mUiHandler = new Handler(){
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REPORT:
				DatagramPacket dataPack = (DatagramPacket) msg.obj;				

				Msg report = SurpportedProtocols.get(DeviceProtocolV15.DEVICE_PROTOCOL_V15).parseDataPack(dataPack);

				Log.d(TAG, "report: " + report.toString());
				
				report(report);
				
				break;
			default:
				break;
			}			
		};
	};
	
	private UdpBroadcastManager(UdpBroadcast broadcast) {
		mMsgHandlers = new ArrayList<IMsgHandler>();
		mUdpBroadcast = broadcast;
		
		mMsgReader = new MsgReader();
		mMsgSender = new MsgSender();
	}
	
	public static UdpBroadcastManager manage(UdpBroadcast broadcast){
		UdpBroadcastManager manager = sManagers.get(broadcast.keyString());
		if(null == manager){
			manager = new UdpBroadcastManager(broadcast);
			sManagers.put(broadcast.keyString(), manager);
		}
		
		return manager;
	}

	public void send(Msg cmd) {
		mMsgSender.sendCmd(cmd);
	}

	private void report(Msg report) {
		if(null != mMsgHandlers){
			for(IMsgHandler handler : mMsgHandlers){
				handler.handleMessage(report);
			}
		}
	}
	
	public void addMsgHandler(IMsgHandler handler){
		mMsgHandlers.add(handler);
	}
	
	public void removeMsgHandler(IMsgHandler handler){
		mMsgHandlers.remove(handler);
	}
	
	public synchronized void start(){
		mMsgReader.start();
		mMsgSender.start();	
		mMsgSender.getHandler();
		
		mStarted = true;
	}
	
	public boolean isStarted(){
		return mStarted;
	}
	
	public synchronized void release(){
		mStarted = false;
		
		mMsgReader.stopRead();
		mMsgSender.stopSend();
		
		mUdpBroadcast.close();
		
		sManagers.remove(mUdpBroadcast.keyString());
		UdpBroadcastFactory.getInstance().remove(mUdpBroadcast);
//		mUdpBroadcast = null;	
	}
	
	// sender message loop
	private class MsgSender extends Thread {
		public final static int MSG_QUIT = 0x11;
		public final static int MSG_SEND = 0x12;
	      private Handler mHandler;

	      public MsgSender() {
				setName("udp sender");
		}
	      
	      public void run() {
	          Looper.prepare();

	          mHandler = new Handler() {
	              public void handleMessage(Message msg) {
					// process incoming messages here
					switch (msg.what) {
					case MSG_SEND:
						byte[] data = (byte[]) msg.obj;
						try {
							mUdpBroadcast.send(data);
						} catch (IOException e) {
							e.printStackTrace();
							mUdpBroadcast.close();
						}
						break;

					case MSG_QUIT:
						getLooper().quit();
						break;
					default:
						break;
					}
				}
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
	      
	      public void sendCmd(Msg cmd){
//	    	  byte[] data = MsgUtil.createDataPack(cmd);
	    	  byte[] data = SurpportedProtocols.get(DeviceProtocolV15.DEVICE_PROTOCOL_V15).createDataPack(cmd);
	    	  
	    	  Message msg = mHandler.obtainMessage();
	    	  msg.what = MSG_SEND;
	    	  msg.obj = data;
	    	  mHandler.sendMessage(msg);
	      }
	      
	      public void stopSend(){
	    	  mHandler.sendEmptyMessage(MSG_QUIT);
	      }
	  }
	
	// reader message loop
	private class MsgReader extends Thread {
		
		private boolean mStop = false;
		
		public MsgReader() {
			setName("udp reader");
		}
		
		@Override
		public void run() {
			super.run();
			while(!mStop){
				try {
					DatagramPacket dataPack = mUdpBroadcast.receive();
					
					Message msg = mUiHandler.obtainMessage();
					msg.what = MSG_REPORT;
					msg.obj = dataPack;
					mUiHandler.sendMessage(msg);
				} catch (IOException e) {
					e.printStackTrace();
					mUdpBroadcast.close();
				}
			}
		}		
		
		public void stopRead(){
			mStop = true;
		}
	}	
}
