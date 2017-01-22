package com.hipad.tracker.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * 
 * @author guowei
 *
 */
public class Response {
	@Expose
	@SerializedName("data")
	private Data mData;

	@Expose
	@SerializedName("msg")
	private String mMsg;
	@Expose
	@SerializedName("success")
	private boolean mIsSuccessful;
	public Data getData() {
		return mData;
	}
	public String getMsg() {
		return mMsg;
	}
	public boolean isSuccessful() {
		return mIsSuccessful;
	}	
	
	
	
	

}
