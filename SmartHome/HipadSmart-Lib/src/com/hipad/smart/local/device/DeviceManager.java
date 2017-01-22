package com.hipad.smart.local.device;

import android.util.Log;

import com.hipad.smart.local.msg.CtrlSocketManager;
import com.hipad.smart.local.msg.GeneralCmd;
import com.hipad.smart.local.msg.IMsgHandler;
import com.hipad.smart.local.msg.Msg;
import com.hipad.smart.local.msg.UdpBroadcastManager;
import com.hipad.varsock.CtrlSocket;
import com.hipad.varsock.CtrlSocketFactory;
import com.hipad.varsock.UdpBroadcast;
import com.hipad.varsock.UdpBroadcastFactory;


public class DeviceManager {	
	private static final String TAG = "DeviceManager";

	private CtrlSocketManager mSockMgr;
	private UdpBroadcastManager mBroadcastManager;
	
	private DeviceFoundReporter mDeviceFoundReporter;
	private ResponceHandler mResponceHandler;
	private DeviceReportHandler mDeviceReportHandler;
	
	private final IMsgHandler mBroadcastMsgHandler = new IMsgHandler() {
		
		@Override
		public void handleMessage(Msg msg) {			
			Log.e(TAG, "msg: " + msg);
			if(!msg.isSuccessful() || Msg.Type.CMD == msg.getType()) return;			
			
			switch (msg.getType()) {
			case Msg.Type.CMD:
				
				break;
			case Msg.Type.REPONCE:
				switch(msg.getCmd()){
				case GeneralCmd.FIND_DEVICE:
					Device device = new Device();
					device.setId(msg.getDevice());
					device.setIp(msg.getTargetIp());
					device.setPort(9527); // the port value should been passed from the device, or it is a deal.
					
					byte[] args = msg.getArgs();
					// parse the params to get the type, then map the name
					final int indexType = 16;
					byte type = args[indexType];
					device.setDeviceType(type);
					// vendor
					final int indexVendor = 18;
					String vendor = new String(args, indexVendor, 6);
					device.setVendor(vendor);
					// model
					final int indexModel = 24;
					String model = new String(args, indexModel, 6);
					device.setModel(model);
					
					reportDeviceFound(device);
					break;
//					return;
				}	
				
				handleResponce(msg);
				break;
			case Msg.Type.REPORT:
				handleDeviceReport(msg);
				break;

			default:
				break;
			}
		}
	};
	private final IMsgHandler mMsgHandler = new IMsgHandler() {
		
		@Override
		public void handleMessage(Msg msg) {
			if(null == msg) return;
			
			switch (msg.getType()) {
			case Msg.Type.CMD:
				
				break;
			case Msg.Type.REPONCE:
				handleResponce(msg);
				break;
			case Msg.Type.REPORT:
				handleDeviceReport(msg);
				break;

			default:
				break;
			}
		}
	};
	
	public DeviceManager() {		
	}		
	
	public void startBroadcastEngine(){
		UdpBroadcast broadcast = UdpBroadcastFactory.getInstance().getSocket(9527);
		mBroadcastManager = UdpBroadcastManager.manage(broadcast);		

		if(!mBroadcastManager.isStarted()){
			mBroadcastManager.start();
//			mBroadcastManager.addMsgHandler(mBroadcastMsgHandler);
		}
		mBroadcastManager.addMsgHandler(mBroadcastMsgHandler);
	}
	
	public void stopBroadcastEngine(){
		if(null != mBroadcastManager){
			mBroadcastManager.removeMsgHandler(mBroadcastMsgHandler);
			mBroadcastManager.release();
			mBroadcastManager = null;
		}
	}
	
	public void connect(Device device){
		CtrlSocket sock = CtrlSocketFactory.getInstance().get(device.getIp(), device.getPort());
		 mSockMgr = CtrlSocketManager.manager(sock);
		 if(!mSockMgr.isStarted()) {
			 mSockMgr.start();
//			 mSockMgr.addMsgHandler(mMsgHandler);
		 }
		 mSockMgr.addMsgHandler(mMsgHandler);		 
	}
	
	public void disconnect(){			 
		if (null != mSockMgr) {
			mSockMgr.removeMsgHandler(mMsgHandler);
			mSockMgr.release();
			mSockMgr = null;
		}
	}
	
	public void broadcast(Msg msg){
		mBroadcastManager.send(msg);
	}
	
	public void send(Msg msg){
		mSockMgr.send(msg);
	}
	
	public void scanConnectedDevice(){
		if(null == mBroadcastManager || !mBroadcastManager.isStarted()) return ;
		
		Msg cmd = new Msg();
		cmd.setCmd(GeneralCmd.FIND_DEVICE);
		cmd.setDevice(Device.Id.NONE);
		cmd.setSuccessful(true);
		cmd.setArgs(new byte[0]);
		
		Log.d(TAG, "cmd: " + cmd.toString());
		
		mBroadcastManager.send(cmd);
	}
	
	private void reportDeviceFound(Device device){
		if(null != mDeviceFoundReporter){
			mDeviceFoundReporter.reportDeviceFound(device);
		}
	}
	
	private synchronized void handleResponce(Msg responce){
		if(null != mResponceHandler){
			mResponceHandler.handleResponce(responce);
		}
	}
	
	private synchronized void handleDeviceReport(Msg report){
		if(null != mDeviceReportHandler){
			mDeviceReportHandler.handleDeviceReport(report);
		}
	}
	
	public void setDeviceFoundReporter(DeviceFoundReporter reporter){
		mDeviceFoundReporter = reporter;
	}
	
	public void setCmdResponceHandler(ResponceHandler handler){
		mResponceHandler = handler;
	}
	
	public void setReportHandler(DeviceReportHandler handler){
		mDeviceReportHandler = handler;
	}
	
	public void release(){		
		stopBroadcastEngine();
		
		disconnect();
	}
	
	public static interface DeviceFoundReporter {
		public void reportDeviceFound(Device device);
	}
	
	public static interface ResponceHandler{
		public void handleResponce(Msg response);
	}
	
	public static interface DeviceReportHandler{
		public void handleDeviceReport(Msg report);
	}
}
