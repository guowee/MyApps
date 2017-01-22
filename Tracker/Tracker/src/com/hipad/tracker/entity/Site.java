package com.hipad.tracker.entity;

public class Site {
	
	private double lon;
	private double lat;
	private double range;
	
	public Site(){}
	
	public Site(double lat,double lon){
		this.lat=lat;
		this.lon=lon;
	}
	
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getRange() {
		return range;
	}
	public void setRange(double range) {
		this.range = range;
	}
	

}
