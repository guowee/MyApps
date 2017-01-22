/**
 * Gateway.java 2014-11-20
 */
package com.hipad.smart.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * represents one gateway.
 * @author wangbaoming
 *
 */
public class Gateway {
	@Expose
	@SerializedName("activateDate")
	private String mActivedDate;
	@Expose
	@SerializedName("aliasName")
	private String mName;
	@Expose
	@SerializedName("faultCode")
	private String mFaultCode;
	@Expose
	@SerializedName("igatewayId")
	private String mGatewayId;
	@Expose
	@SerializedName("ipAddress")
	private String mIpAddr;
	@Expose
	@SerializedName("isHouseHold")
	private int mIsHolder;
	@Expose
	@SerializedName("lastConnectTime")
	private String mLastConnTime;
	@Expose
	@SerializedName("softwareVersion")
	private String mSoftwareVer;
	
	public String getActivedDate() {
		return mActivedDate;
	}
	public String getName() {
		return mName;
	}
	public String getFaultCode() {
		return mFaultCode;
	}
	public String getGatewayId() {
		return mGatewayId;
	}
	public String getIpAddr() {
		return mIpAddr;
	}
	public boolean isHolder() {
		return 1 == mIsHolder;
	}
	public String getLastConnTime() {
		return mLastConnTime;
	}
	public String getSoftwareVer() {
		return mSoftwareVer;
	}
}
