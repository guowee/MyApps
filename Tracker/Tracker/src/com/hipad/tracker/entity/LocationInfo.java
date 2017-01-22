package com.hipad.tracker.entity;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hipad.tracker.json.Data;

public class LocationInfo extends Data implements Serializable{
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
	
	public LocationInfo() {
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

	public String getLocType() {
		return locType;
	}

	public void setLocType(String locType) {
		this.locType = locType;
	}

	public String getAv() {
		return av;
	}

	public void setAv(String av) {
		this.av = av;
	}

	public String getNs() {
		return ns;
	}

	public void setNs(String ns) {
		this.ns = ns;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public String getEw() {
		return ew;
	}

	public void setEw(String ew) {
		this.ew = ew;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getStep() {
		return step;
	}

	public void setStep(double step) {
		this.step = step;
	}
}
