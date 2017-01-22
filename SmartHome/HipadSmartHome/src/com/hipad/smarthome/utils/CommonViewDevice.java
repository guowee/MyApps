package com.hipad.smarthome.utils;

import com.hipad.smart.kettle.v14.KettleStatusInfo;

public class CommonViewDevice {
	
	public static int NETWORK_STATE_OFFLINE = 0;
	public static int NETWORK_STATE_ONLINE_LOCAL = 1;
	public static int NETWORK_STATE_ONLINE_REMOTE = 2;

	private String mName;
	private String mDeviceId;
	private String mModel;
	private int mNetworkState;
	private KettleStatusInfo info;
	
	private com.hipad.smart.device.CommonDevice commonDevice;
	private com.hipad.smart.local.device.Device localDevice;

	public CommonViewDevice(com.hipad.smart.local.device.Device dev) {
		this.localDevice = dev;
		mDeviceId = dev.getId();
		mName = dev.getName();
		mName = mName==null||mName.equals("")?"Ë®ºø-¾ÖÓòÍø":mName;
		mModel = dev.getModel();
		mNetworkState = NETWORK_STATE_ONLINE_LOCAL;
	}
	
	public CommonViewDevice(com.hipad.smart.device.CommonDevice dev) {
		this.commonDevice = dev;
		mDeviceId = dev.getDeviceId();
		mName = dev.getName();
		mModel = dev.getModel();
		mNetworkState = dev.isOnLine()?NETWORK_STATE_ONLINE_REMOTE:NETWORK_STATE_OFFLINE;
		if(dev.getStatusWord()!=null&&!dev.getStatusWord().equals(""))
			info = new KettleStatusInfo(dev.getStatusWord());
		//isCloudLinked = true;
	}
	
	public int getNetworkState() {
		return mNetworkState;
	}

	public String getName() {
		return mName;
	}
	
	public void setName(String name){
		this.mName = name;
	}

	public String getDeviceId() {
		return mDeviceId;
	}

	public String getModel() {
		return mModel;
	}

	public void setInfo(KettleStatusInfo info) {
		this.info = info;
	}
	
	public KettleStatusInfo getInfo() {
		return info;
	}
	
	public com.hipad.smart.device.CommonDevice getCommonDevice() {
		return commonDevice;
	}
	
	public com.hipad.smart.local.device.Device getLocalDevice() {
		return localDevice;
	}
	
	public void setLocalDevice(com.hipad.smart.local.device.Device dev){
		if(dev!=null){
			this.localDevice = dev;
			mNetworkState = NETWORK_STATE_ONLINE_LOCAL;
		}
	}
}