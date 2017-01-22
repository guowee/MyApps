/**
 * CommonDevice.java 

 */
package com.hipad.smart.device;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * one common device holding the device common info.
 * @author wangbaoming
 *
 */
public class CommonDevice extends Device implements Parcelable{
	public final static String EXTRA_DEVICE = "extra_device";
	
	@Expose
	@SerializedName("bigClassID")
	private int mGroup;
	@Expose
	@SerializedName("isOnline")
	private int mIsOnLine;
	@Expose
	@SerializedName("isPowerOn")
	private int mIsPowerOn;
	@Expose
	@SerializedName("statusWord")
	private String mStatusWord;
	
	/**
	 * 
	 * @author wangbaoming
	 *
	 */
	private static class Bool {
		public static boolean isTrue(int value){
			return 0 != value; 
		}
	}	

	public CommonDevice() {
	}
	
	public CommonDevice(String gateway, String device) {
		super(gateway, device);
	}
	
	public Group getGroup() {
		Group group = Group.UNKNOWN;
		if(Group.None.getValue() < mGroup && mGroup < Group.UNKNOWN.getValue()){
			group = Group.values()[mGroup]; 
		}
		return group;
	}
	
	public boolean isPowerOn(){
		return Bool.isTrue(mIsPowerOn);
	}
	
	public boolean isOnLine(){
		return Bool.isTrue(mIsOnLine);
	}
	
	public String getStatusWord(){
		return mStatusWord;
	}

	 public static final Parcelable.Creator<CommonDevice> CREATOR = new Parcelable.Creator<CommonDevice>() {

		@Override
		public CommonDevice createFromParcel(Parcel source) {
			return new CommonDevice(source);
		}

		@Override
		public CommonDevice[] newArray(int size) {
			return new CommonDevice[size];
		}
	};
	
//	private CommonDevice() {
//	}
	
	private CommonDevice(Parcel source){
		// super
		mName = source.readString();
		mDeviceId = source.readString();
		mCategory = source.readInt();
		mGateway = source.readString();
		mVender = source.readString();
		mModel = source.readString();
		// self
		mGroup = source.readInt();
		mIsOnLine = source.readInt();
		mIsPowerOn = source.readInt();
		mStatusWord = source.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// super
		dest.writeString(mName);
		dest.writeString(mDeviceId);
		dest.writeInt(mCategory);
		dest.writeString(mGateway);
		dest.writeString(mVender);
		dest.writeString(mModel);
		// self
		dest.writeInt(mGroup);
		dest.writeInt(mIsOnLine);
		dest.writeInt(mIsPowerOn);
		dest.writeString(mStatusWord);
	}
	
	/**
	 * device group.
	 * @author wangbaoming
	 *
	 */
	public static enum Group {
		None(0), Environment(1), Cook(2), Healthy(3), Security(4), Entertainment(5), UNKNOWN(6);
		private Group(int group) {
			mGroup = group;
		}
		public int getValue(){
			return mGroup;
		}
		private int mGroup;
	}
}
