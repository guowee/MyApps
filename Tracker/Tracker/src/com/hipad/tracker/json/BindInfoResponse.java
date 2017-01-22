package com.hipad.tracker.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BindInfoResponse {
	@Expose
	@SerializedName("count")
	private int count;
	@Expose
	@SerializedName("imei")
	private String imei;
	@Expose
	@SerializedName("msg")
	private String mMsg;
	@Expose
	@SerializedName("success")
	private boolean mIsSuccessful;
	
	public int getCount() {
		return count;
	}
	public String getImei() {
		return imei;
	}
	public String getMsg() {
		return mMsg;
	}
	public boolean isSuccessful() {
		return mIsSuccessful;
	}		
	
	
	

}
