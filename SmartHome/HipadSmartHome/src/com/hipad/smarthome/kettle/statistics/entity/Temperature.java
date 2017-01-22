package com.hipad.smarthome.kettle.statistics.entity;

import java.io.Serializable;
/**
 * ÎÂ¶È
 * @author guowei
 *
 */
@SuppressWarnings("serial")
public class Temperature implements Serializable {
    private String userId;
    private String deviceId;
	private String currDate;
	private double tempc_1=0;
	private double tempc_2=0;
	private double tempc_3=0;
	private double tempc_4=0;
	private double tempc_5=0;
	private double tempc_6=0;
	private double tempc_7=0;
	private double tempc_8=0;

	public Temperature() {
	}

	public Temperature(String userId,String deviceId,String currDate, double tempc_1, double tempc_2,
			double tempc_3, double tempc_4, double tempc_5, double tempc_6,
			double tempc_7, double tempc_8) {
		super();
		this.userId=userId;
		this.deviceId=deviceId;
		this.currDate = currDate;
		this.tempc_1 = tempc_1;
		this.tempc_2 = tempc_2;
		this.tempc_3 = tempc_3;
		this.tempc_4 = tempc_4;
		this.tempc_5 = tempc_5;
		this.tempc_6 = tempc_6;
		this.tempc_7 = tempc_7;
		this.tempc_8 = tempc_8;
	}

	public String getCurrDate() {
		return currDate;
	}

	public void setCurrDate(String currDate) {
		this.currDate = currDate;
	}

	public double getTempc_1() {
		return tempc_1;
	}

	public void setTempc_1(double tempc_1) {
		this.tempc_1 = tempc_1;
	}

	public double getTempc_2() {
		return tempc_2;
	}

	public void setTempc_2(double tempc_2) {
		this.tempc_2 = tempc_2;
	}

	public double getTempc_3() {
		return tempc_3;
	}

	public void setTempc_3(double tempc_3) {
		this.tempc_3 = tempc_3;
	}

	public double getTempc_4() {
		return tempc_4;
	}

	public void setTempc_4(double tempc_4) {
		this.tempc_4 = tempc_4;
	}

	public double getTempc_5() {
		return tempc_5;
	}

	public void setTempc_5(double tempc_5) {
		this.tempc_5 = tempc_5;
	}

	public double getTempc_6() {
		return tempc_6;
	}

	public void setTempc_6(double tempc_6) {
		this.tempc_6 = tempc_6;
	}

	public double getTempc_7() {
		return tempc_7;
	}

	public void setTempc_7(double tempc_7) {
		this.tempc_7 = tempc_7;
	}

	public double getTempc_8() {
		return tempc_8;
	}

	public void setTempc_8(double tempc_8) {
		this.tempc_8 = tempc_8;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
