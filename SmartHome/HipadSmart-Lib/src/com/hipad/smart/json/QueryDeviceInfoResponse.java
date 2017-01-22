/**
 * QueryDeviceInfoResponse.java 2014-11-20
 */
package com.hipad.smart.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author wangbaoming
 *
 */
public class QueryDeviceInfoResponse {

	@Expose
	@SerializedName("data")
	private DeviceInfo mData;
	@Expose
	@SerializedName("msg")
	private String mMsg;
	@Expose
	@SerializedName("success")
	private boolean mIsSuccessful;
	
	/**
	 * get the device info.
	 * @return
	 * 		the device info
	 */
	public DeviceInfo getData(){
		return mData;
	}
	
	public String getMsg(){
		return mMsg;
	}
	
	public boolean isSuccessful(){
		return mIsSuccessful;
	}
	
	public static class DeviceInfo extends Data {
		@Expose
		@SerializedName("status")
		private String mBody;
		@Expose
		@SerializedName("flag")
		private int mFlag;
		
		public String getResponseBody(){
			return mBody;
		}
		/**
		 * get the device error code, from the device or gateway.
		 * @see {@link ErrorCode}
		 * @return
		 * 		the error code
		 */
		public int getErrorCode(){
			byte flag = (byte) mFlag;
			return flag & 127; // 01111111 = 127
		}
		public boolean isBypass(){
			byte flag = (byte) mFlag;
			return (flag & 128) != 0; // 10000000 = 128
		}
	}
}
