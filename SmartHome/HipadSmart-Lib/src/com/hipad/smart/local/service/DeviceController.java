/**
 * $(#)LocalService.java 2015-1-22
 */
package com.hipad.smart.local.service;

import com.hipad.smart.local.device.CmdResponseWaitersQueue;
import com.hipad.smart.local.device.Device;
import com.hipad.smart.local.device.DeviceManager;
import com.hipad.smart.local.device.DeviceManager.DeviceReportHandler;
import com.hipad.smart.local.device.DeviceManager.ResponceHandler;
import com.hipad.smart.local.device.ResponseWaiter;
import com.hipad.smart.local.msg.LocalCmd;
import com.hipad.smart.local.msg.Msg;
import com.hipad.smart.service.Cmd;
import com.hipad.smart.util.MsgBodyData;

/**
 * @author wangbaoming
 *
 */
public class DeviceController {
	private DeviceManager mDeviceManager;

	private ResponceHandler mCmdResponceHandler;
	
	private CmdResponseWaitersQueue mCmdResponseWaitersQueue;

	private Device mDevice;
	
	public DeviceController() {
		mDeviceManager = new DeviceManager();
		
		mCmdResponceHandler = new ResponceHandler() {

			@Override
			public void handleResponce(Msg response) {
				mCmdResponseWaitersQueue.accept(response, true);
				System.out.println("msg: " + response.toString());
			}
		};
	
		mCmdResponseWaitersQueue = new CmdResponseWaitersQueue();
	}	
	
	public void stopSelf() {
		mDevice = null;
		mCmdResponseWaitersQueue.clean();
		mDeviceManager.setCmdResponceHandler(null);
		mDeviceManager.release();
	}
	
	public boolean isInited(){
		return null != mDevice;
	}
	
	public void init(Device device){
		mDevice = device;
		mDeviceManager.connect(device);
		mDeviceManager.setCmdResponceHandler(mCmdResponceHandler);
	}
	
	public Device getDevice(){
		return mDevice;
	}
	
	public void setReportHandler(DeviceReportHandler handler){
		mDeviceManager.setReportHandler(handler);
	}
	
	public void sendCmd(String deviceId, Cmd cmd, ResponseWaiter waiter){
		LocalCmd localCmd = new LocalCmd(cmd.getCmdNo(), deviceId, true, "");
		localCmd.setArgs(cmd.getBody().getData());
		
		sendCmd(localCmd, waiter);
	}
	
	public void sendCmd(LocalCmd cmd, ResponseWaiter waiter){
		mCmdResponseWaitersQueue.enqueueWait(waiter, 5);
		mDeviceManager.send(cmd);
	}
	
	public final static class CmdAdapter {
		public static LocalCmd convert(String deviceId, Cmd cmd){
			LocalCmd localCmd = new LocalCmd(cmd.getCmdNo(), deviceId, true, "");
			MsgBodyData cmdBody = cmd.getBody();
			if(null != cmdBody) localCmd.setArgs(cmdBody.getData());
			
			return localCmd;
		}
	}
}
