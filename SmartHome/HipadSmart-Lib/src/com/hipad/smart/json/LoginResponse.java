/**
 * LoginResponse.java 2014-11-20
 */
package com.hipad.smart.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hipad.smart.user.User;

/**
 * represent the login response. the user info will be returned with {@link User}.
 * @author wangbaoming
 *
 */
public class LoginResponse/* extends Response*/{
	
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
	 * 
	 * @return
	 * 		the user info if logged in successfully, null otherwise.
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
