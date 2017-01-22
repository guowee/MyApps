package com.hipad.tracker.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommonResponse {
	@Expose
	@SerializedName("bizSN")
	private String bizSN;
	@Expose
	@SerializedName("battery")
	private String battery;
	@Expose
	@SerializedName("spn2")
	private String spn2;
	@Expose
	@SerializedName("enable")
	private String enable;
	@Expose
	@SerializedName("msg")
	private String mMsg;
	@Expose
	@SerializedName("success")
	private boolean mIsSuccessful;
	
	public String getEnable() {
		return enable;
	}
	public String getBizSN() {
		return bizSN;
	}
	public String getBattery() {
		return battery;
	}
	public String getSpn2() {
		return spn2;
	}
	public String getMsg() {
		return mMsg;
	}
	public boolean isSuccessful() {
		return mIsSuccessful;
	}		
}
