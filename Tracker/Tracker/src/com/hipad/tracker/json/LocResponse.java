package com.hipad.tracker.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class LocResponse {
	
	@Expose
	@SerializedName("bizSN")
	private String bizSN;
	@Expose
	@SerializedName("battery")
	private String battery;
	@Expose
	@SerializedName("locType")
	private String locType;
	@Expose
	@SerializedName("loc")
	private String loc;
	@Expose
	@SerializedName("A/V")
	private String av;
	@Expose
	@SerializedName("N/S")
	private String ns;
	@Expose
	@SerializedName("lat")
	private double lat;
	@Expose
	@SerializedName("E/W")
	private String ew;
	@Expose
	@SerializedName("lon")
	private double lon;
	@Expose
	@SerializedName("speed")
	private double speed;
	@Expose
	@SerializedName("step")
	private double step;
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
	public String getLocType() {
		return locType;
	}
	public String getLoc() {
		return loc;
	}
	public String getAv() {
		return av;
	}
	public String getNs() {
		return ns;
	}
	public double getLat() {
		return lat;
	}
	public String getEw() {
		return ew;
	}
	public double getLon() {
		return lon;
	}
	public double getSpeed() {
		return speed;
	}
	public double getStep() {
		return step;
	}
	public String getMsg() {
		return mMsg;
	}
	public boolean isSuccessful() {
		return mIsSuccessful;
	}		

}
