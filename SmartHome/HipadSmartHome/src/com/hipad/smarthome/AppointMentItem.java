package com.hipad.smarthome;

import android.os.Parcel;
import android.os.Parcelable;

public class AppointMentItem implements Parcelable {

	public int appoint_start = 0;
	public int warm_time;
	public int warm_tmp;
	public int appoint_type;
	public int is_everyday;
	public int menu1;
	public int menu2;
	public int delay_time;
//	byte[] menu = new byte[2];
	public String appoint_name;
	public String appoint_time;
	public String appoint_date;
	public String scene_name;
	public String gatewayID;
	public String deviceID;
	public String userId;
	
	public void setUserId(String user_id) {
		this.userId = user_id;
	}
	
	public void setAppointName(String name) {
		this.appoint_name = name;
	}

	public void setAppointStart(int start) {
		this.appoint_start = start;
	}

	public void setGateWayId(String getwayId) {
		this.gatewayID = getwayId;
	}

	public void setDeviceId(String devId) {
		this.deviceID = devId;
	}

	public void setAppointTime(String appointTime) {
		this.appoint_time = appointTime;
	}

	public void setAppointDate(String appointDate) {
		this.appoint_date = appointDate;
	}
	
	public void setWarmTime (int time) {
		this.warm_time = time;
	}
	
	public void setWarmTmp (int tmp) {
		this.warm_tmp = tmp;
	}

	public void setSceneName(String name) {
		this.scene_name = name;
	}
	
	public void setMenu1(int menu) {
		this.menu1 = menu;
	}
	
	public void setMenu2(int menu) {
		this.menu2 = menu;
	}
	
	public void setAppointType(int type) {
		this.appoint_type = type;
	}
	
	public void setIsEveryDay(int value) {
		this.is_everyday = value;
	}
	
	public void setDelayTime(int time) {
		this.delay_time = time;
	}
	//get
	public String getSceneName() {
		return this.scene_name;
	}
	public String getGateWayId() {
		return this.gatewayID;
	}

	public String getDeviceId() {
		return this.deviceID;
	}

	public String getAppointTime() {
		return this.appoint_time;
	}

	public String getAppointDate() {
		return this.appoint_date;
	}

	public String getAppointName() {
		return this.appoint_name;
	}
	
	public int getWarmTime() {
		return this.warm_time;
	}
	
	public int getWarmTmp() {
		return this.warm_tmp;
	}
	
	public int getDelayTime() {
		return this.delay_time;
	}
	
	public int getIsEveryDay() {
		return this.is_everyday;
	}
	
	public int getAppointStart() {
		return this.appoint_start;
	}
	
	public int getAppointType() {
		return this.appoint_type;
	}
	
	public int getMenu1() {
		return this.menu1;
	}
	
	public int getMenu2() {
		return this.menu2;
	}
	
	public String getUserId() {
		return this.userId;
	}
	
    public static final Parcelable.Creator<AppointMentItem> CREATOR = new Creator<AppointMentItem>() {    
        @Override  
        public AppointMentItem createFromParcel(Parcel source) {    
        	AppointMentItem mItem = new AppointMentItem();    
            mItem.appoint_name = source.readString();
            mItem.appoint_time = source.readString();
            mItem.appoint_date = source.readString();
            mItem.scene_name = source.readString();
            mItem.userId = source.readString();
            mItem.gatewayID = source.readString();
            mItem.deviceID = source.readString();
            mItem.appoint_start = source.readInt();
            mItem.warm_time = source.readInt();
            mItem.warm_tmp = source.readInt();
            mItem.appoint_type = source.readInt();
            mItem.is_everyday = source.readInt();
            mItem.menu1 = source.readInt();
            mItem.menu2 = source.readInt();
            mItem.delay_time = source.readInt();
            return mItem;    
        }    
        @Override  
        public AppointMentItem[] newArray(int size) {    
            return new AppointMentItem[size];    
        }    
    };    
    
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(appoint_name);
		dest.writeString(appoint_time);
		dest.writeString(appoint_date);
		dest.writeString(scene_name);
		dest.writeString(userId);
		dest.writeString(gatewayID);
		dest.writeString(deviceID);
		dest.writeInt(appoint_start);
		dest.writeInt(warm_time);
		dest.writeInt(warm_tmp);
		dest.writeInt(appoint_type);
		dest.writeInt(is_everyday);
		dest.writeInt(menu1);
		dest.writeInt(menu2);
		dest.writeInt(delay_time);
	}

}
