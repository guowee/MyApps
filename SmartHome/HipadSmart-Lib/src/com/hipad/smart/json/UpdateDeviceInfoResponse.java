/**
 * UpdateDeviceInfoResponse.java 2014-11-21
 */
package com.hipad.smart.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author wangbaoming
 *
 */
public class UpdateDeviceInfoResponse/* extends Response*/{
	
	@Expose
	@SerializedName("data")
	private Data mData;
	@Expose
	@SerializedName("msg")
	private String mMsg;
	@Expose
	@SerializedName("success")
	private boolean mIsSuccessful;
	
	public Data getData(){
		return mData;
	}
	
	public String getMsg(){
		return mMsg;
	}
	
	public boolean isSuccessful(){
		return mIsSuccessful;
	}
}
