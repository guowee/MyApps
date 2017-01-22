package com.hipad.smart.local.device;

import android.os.Parcel;
import android.os.Parcelable;

public class Device implements Parcelable{
	
	private String mIp;
	private int mPort;
	private String mName;
	private int mDeviceType;
	private String mId;
	private String mVendor;
	private String mModel;
	
	public Device() {
	}
	
	public String getIp() {
		return mIp;
	}
	public int getPort() {
		return mPort;
	}
	public String getName() {
		return mName;
	}
	public int getDeviceType() {
		return mDeviceType;
	}
	public String getId() {
		return mId;
	}
	public String getVendor() {
		return mVendor;
	}
	public String getModel() {
		return mModel;
	}
	
	public void setIp(String ip) {
		this.mIp = ip;
	}
	public void setPort(int port) {
		this.mPort = port;
	}
	public void setName(String name) {
		this.mName = name;
	}
	public void setDeviceType(int deviceType) {
		this.mDeviceType = deviceType;
	}
	public void setId(String id) {
		this.mId = id;
	}	
	public void setVendor(String vendor) {
		this.mVendor = vendor;
	}	
	public void setModel(String model) {
		this.mModel = model;
	}	

	public static final String EXTRA_DEVICE = "device";
	public static final Parcelable.Creator<Device> CREATOR = new Creator<Device>() {
		
		@Override
		public Device[] newArray(int size) {
			return new Device[size];
		}
		
		@Override
		public Device createFromParcel(Parcel source) {
			return new Device(source);
		}
	};
	
	public Device(Parcel in) {
		mIp = in.readString();
		mPort = in.readInt();
		mName = in.readString();
		mDeviceType = in.readInt();
		mId = in.readString();
		mVendor = in.readString();
		mModel = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mIp);
		dest.writeInt(mPort);
		dest.writeString(mName);
		dest.writeInt(mDeviceType);
		dest.writeString(mId);
		dest.writeString(mVendor);
		dest.writeString(mModel);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("name: " + mName)
			.append(", type: " + mDeviceType)
			.append(", id: " + mId)
			.append(", port: " + mPort)
			.append(", ip: " + mIp);
		return sb.toString();
	}
	
	public final static class Id {
		public final static String NONE = "0000000000000000";
		public final static String ALL = "FFFFFFFFFFFFFFFF";
	}	
}
