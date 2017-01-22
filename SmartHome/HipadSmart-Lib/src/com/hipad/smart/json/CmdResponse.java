/**
 * CmdResponse.java 2014-11-21
 */
package com.hipad.smart.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author wangbaoming
 *
 */
public class CmdResponse/* extends Response*/{
	
	@Expose
	@SerializedName("data")
	private ResponseData mData;
	@Expose
	@SerializedName("msg")
	private String mMsg;
	@Expose
	@SerializedName("success")
	private boolean mIsSuccessful;
	
	public ResponseData getData(){
		return mData;
	}
	
	/**
	 * get the message from cloud.
	 * @return
	 * 		the message
	 */
	public String getMsg(){
		return mMsg;
	}
	
	/**
	 * judge if the request is responded successfully.
	 * @return
	 * 		true if successful, false otherwise
	 */
	public boolean isSuccessful(){
		return mIsSuccessful;
	}
	
	/**
	 * represent the cmd response data from cloud.
	 * @author wangbaoming
	 *
	 */
	public static class ResponseData extends Data {
		
		@Expose
		@SerializedName("body")
		private String mBody;
		@Expose
		@SerializedName("flag")
		private int mFlag;
		
		/**
		 * get the response data body, which is binary data encoded in base64.
		 * @return
		 * 		the base64 string representing the binary data, containing the state info. 
		 */
		public String getResponseBody(){
			return mBody;
		}
		/**
		 * get the error code.
		 * @see {@link ErrorCode}
		 * @return
		 * 		the error code
		 */
		public int getErrorCode(){
			byte flag = (byte) mFlag;
			return flag & 127; // 01111111 = 127
		}
		/**
		 * if the command is bypassed to the device directly or not.
		 * @return
		 * 		true if bypassed directly, false otherwise
		 */
		public boolean isBypass(){
			byte flag = (byte) mFlag;
			return (flag & 128) != 0; // 10000000 = 128
		}
		
	}
}
