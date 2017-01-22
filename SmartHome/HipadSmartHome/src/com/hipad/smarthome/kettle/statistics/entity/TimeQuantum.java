package com.hipad.smarthome.kettle.statistics.entity;

import java.io.Serializable;

/**
 * 用水时间段
 * 
 * @author guowei
 *
 */
@SuppressWarnings("serial")
public class TimeQuantum implements Serializable {
	private String userId;
	private String deviceId;

	private String currDate;

	private double period_1=0;
	private double period_2=0;
	private double period_3=0;
	private double period_4=0;
	private double period_5=0;
	private double period_6=0;
	private double period_7=0;
	private double period_8=0;
	private double period_9=0;
	private double period_10=0;
	private double period_11=0;
	private double period_12=0;

	public TimeQuantum() {
	}

	public TimeQuantum(String userId, String deviceId, String currDate,
			double period_1, double period_2, double period_3, double period_4,
			double period_5, double period_6, double period_7, double period_8,
			double period_9, double period_10, double period_11,
			double period_12) {
		super();
		this.userId = userId;
		this.deviceId = deviceId;
		this.currDate = currDate;
		this.period_1 = period_1;
		this.period_2 = period_2;
		this.period_3 = period_3;
		this.period_4 = period_4;
		this.period_5 = period_5;
		this.period_6 = period_6;
		this.period_7 = period_7;
		this.period_8 = period_8;
		this.period_9 = period_9;
		this.period_10 = period_10;
		this.period_11 = period_11;
		this.period_12 = period_12;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getCurrDate() {
		return currDate;
	}

	public void setCurrDate(String currDate) {
		this.currDate = currDate;
	}

	public double getPeriod_1() {
		return period_1;
	}

	public void setPeriod_1(double period_1) {
		this.period_1 = period_1;
	}

	public double getPeriod_2() {
		return period_2;
	}

	public void setPeriod_2(double period_2) {
		this.period_2 = period_2;
	}

	public double getPeriod_3() {
		return period_3;
	}

	public void setPeriod_3(double period_3) {
		this.period_3 = period_3;
	}

	public double getPeriod_4() {
		return period_4;
	}

	public void setPeriod_4(double period_4) {
		this.period_4 = period_4;
	}

	public double getPeriod_5() {
		return period_5;
	}

	public void setPeriod_5(double period_5) {
		this.period_5 = period_5;
	}

	public double getPeriod_6() {
		return period_6;
	}

	public void setPeriod_6(double period_6) {
		this.period_6 = period_6;
	}

	public double getPeriod_7() {
		return period_7;
	}

	public void setPeriod_7(double period_7) {
		this.period_7 = period_7;
	}

	public double getPeriod_8() {
		return period_8;
	}

	public void setPeriod_8(double period_8) {
		this.period_8 = period_8;
	}

	public double getPeriod_9() {
		return period_9;
	}

	public void setPeriod_9(double period_9) {
		this.period_9 = period_9;
	}

	public double getPeriod_10() {
		return period_10;
	}

	public void setPeriod_10(double period_10) {
		this.period_10 = period_10;
	}

	public double getPeriod_11() {
		return period_11;
	}

	public void setPeriod_11(double period_11) {
		this.period_11 = period_11;
	}

	public double getPeriod_12() {
		return period_12;
	}

	public void setPeriod_12(double period_12) {
		this.period_12 = period_12;
	}

}
