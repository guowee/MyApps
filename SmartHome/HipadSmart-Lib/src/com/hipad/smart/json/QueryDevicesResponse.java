/**
 * QueryGatewayResponse.java 2014-11-20
 */
package com.hipad.smart.json;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.device.Device;

/**
 * @author wangbaoming
 *
 */
public class QueryDevicesResponse {
	
	@Expose
	@SerializedName("data")
	private ArrayList<CommonDevice> mData;
	@Expose
	@SerializedName("msg")
	private String mMsg;
	@Expose
	@SerializedName("success")
	private boolean mIsSuccessful;
	
	/**
	 * get the device list.
	 * @return
	 * 		the devices
	 */
	public ArrayList<CommonDevice> getData(){
		return mData;
	}
	
	public String getMsg(){
		return mMsg;
	}
	
	public boolean isSuccessful(){
		return mIsSuccessful;
	}
}
