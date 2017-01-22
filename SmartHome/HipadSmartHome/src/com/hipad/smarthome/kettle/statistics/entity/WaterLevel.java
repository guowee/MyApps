package com.hipad.smarthome.kettle.statistics.entity;

import java.io.Serializable;
/**
 * Ë®ÖÊ
 * @author guowei
 *
 */
@SuppressWarnings("serial")
public class WaterLevel implements Serializable {

	private String userId;
	private String deviceId;

	private double level=0;

	public WaterLevel() {
	}

	public WaterLevel(String userId,String deviceId, double level) {
		super();
		this.userId=userId;
		this.deviceId = deviceId;
		this.level = level;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public double getLevel() {
		return level;
	}

	public void setLevel(double level) {
		this.level = level;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
