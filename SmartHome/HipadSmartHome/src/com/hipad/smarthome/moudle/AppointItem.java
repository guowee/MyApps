package com.hipad.smarthome.moudle;

public class AppointItem {
	String eventTitle;
	String gatewayID;
	String deviceID;
	String dateStr;
	int requestCode;
	public int getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}

	int keepWarm;
	int keepTime;

	public void setEventTitle(String title) {
		this.eventTitle = title;
	}
	
	public String getEventTitle() {
		return this.eventTitle;
	}
	
	public String getGatewayID() {
		return gatewayID;
	}

	public void setGatewayID(String gatewayID) {
		this.gatewayID = gatewayID;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getDataStr() {
		return dateStr;
	}

	public void setDataStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public int getKeepWarm() {
		return keepWarm;
	}

	public void setKeepWarm(int keepWarm) {
		this.keepWarm = keepWarm;
	}

	public int getKeepTime() {
		return keepTime;
	}

	public void setKeepTime(int keepTime) {
		this.keepTime = keepTime;
	}

	@Override
	public String toString() {
		return "AppointItem [gatewayID=" + gatewayID + ", deviceID=" + deviceID
				+ ", dataStr=" + dateStr + ", keepWarm=" + keepWarm + ", keepTime=" + keepTime + ", requestCode=" + requestCode + "]";
	}
}
