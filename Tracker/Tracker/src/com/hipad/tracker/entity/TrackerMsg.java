package com.hipad.tracker.entity;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hipad.tracker.json.Data;

public class TrackerMsg extends Data implements Serializable {
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
	
	public TrackerMsg() {
	}

	public String getBizSN() {
		return bizSN;
	}

	public void setBizSN(String bizSN) {
		this.bizSN = bizSN;
	}

	public String getBattery() {
		return battery;
	}

	public void setBattery(String battery) {
		this.battery = battery;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSpn1() {
		return spn1;
	}

	public void setSpn1(String spn1) {
		this.spn1 = spn1;
	}

	public String getApn() {
		return apn;
	}

	public void setApn(String apn) {
		this.apn = apn;
	}

	public String getIpPort() {
		return ipPort;
	}

	public void setIpPort(String ipPort) {
		this.ipPort = ipPort;
	}

	public String getLocType() {
		return locType;
	}

	public void setLocType(String locType) {
		this.locType = locType;
	}

	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}
	
	
	
	
	

}
