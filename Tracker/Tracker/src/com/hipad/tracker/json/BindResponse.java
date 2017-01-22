package com.hipad.tracker.json;

import com.google.gson.annotations.Expose;

import com.google.gson.annotations.SerializedName;

public class BindResponse {

	@Expose
	@SerializedName("bizSN")
	private String bizSN;
	@Expose
	@SerializedName("battery")
	private String battery;
	@Expose
	@SerializedName("version")
	private String version;
	@Expose
	@SerializedName("spn1")
	private String spn1;
	@Expose
	@SerializedName("apn")
	private String apn;
	@Expose
	@SerializedName("ipPort")
    private String ipPort;
	@Expose
	@SerializedName("locType")
	private String locType;
	@Expose
	@SerializedName("loc")
	private String loc;
	@Expose
	@SerializedName("msg")
	private String mMsg;
	@Expose
	@SerializedName("success")
	private boolean mIsSuccessful;
	
	public String getBizSN() {
		return bizSN;
	}
	public String getBattery() {
		return battery;
	}
	public String getVersion() {
		return version;
	}
	public String getSpn1() {
		return spn1;
	}
	public String getApn() {
		return apn;
	}
	public String getIpPort() {
		return ipPort;
	}
	public String getLocType() {
		return locType;
	}
	public String getLoc() {
		return loc;
	}
	public String getMsg() {
		return mMsg;
	}
	public boolean isSuccessful() {
		return mIsSuccessful;
	}		

}
