/**
 * RegisterResponse.java 2014-11-20
 */
package com.hipad.smart.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hipad.smart.user.User;

/**
 * represent the register response. the user info will be returned with {@link User}.
 * @author wangbaoming
 *
 */
public class RegisterResponse {
	
	@Expose
	@SerializedName("data")
	private User mData;
	@Expose
	@SerializedName("msg")
	private String mMsg;
	@Expose
	@SerializedName("success")
	private boolean mIsSuccessful;
	
	/**
	 * get the user info if registered successfully.
	 * @return
	 * 		the user info
	 */
	public User getData(){
		return mData;
	}
	
	public String getMsg(){
		return mMsg;
	}
	
	public boolean isSuccessful(){
		return mIsSuccessful;
	}
}
