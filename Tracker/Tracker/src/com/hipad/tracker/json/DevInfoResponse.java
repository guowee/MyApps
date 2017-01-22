package com.hipad.tracker.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DevInfoResponse {
	@Expose
	@SerializedName("bindStatus")
	private String bindStatus;
	@Expose
	@SerializedName("devSIM")
	private String devSIM;
	@Expose
	@SerializedName("bindTime")
	private String  bindTime;
	@Expose
	@SerializedName("updateTime")
	private String updateTime;
	@Expose
	@SerializedName("apn")
	private String apn;
	@Expose
	@SerializedName("spn1")
	private String spn1;
	@Expose
	@SerializedName("spn2")
	private String spn2;
	@Expose
	@SerializedName("devVersion")
	private String devVersion;
	@Expose
	@SerializedName("curfewBeginTime")
	private String curfewBeginTime;
	@Expose
	@SerializedName("curfewEndTime")
	private String curfewEndTime;
	@Expose
	@SerializedName("curfewStatus")
	private String curfewStatus;
	@Expose
	@SerializedName("msg")
	private String mMsg;
	@Expose
	@SerializedName("success")
	private boolean mIsSuccessful;
	
	public String getBindStatus() {
		return bindStatus;
	}
	public String getDevSIM() {
		return devSIM;
	}
	public String getBindTime() {
		return bindTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public String getApn() {
		return apn;
	}
	public String getSpn1() {
		return spn1;
	}
	public String getSpn2() {
		return spn2;
	}
	public String getDevVersion() {
		return devVersion;
	}
	public String getMsg() {
		return mMsg;
	}
	public boolean isSuccessful() {
		return mIsSuccessful;
	}		
	public String getCurfewBeginTime() {
		return curfewBeginTime;
	}
	public String getCurfewEndTime() {
		return curfewEndTime;
	}
	public String getCurfewStatus() {
		return curfewStatus;
	}
}
