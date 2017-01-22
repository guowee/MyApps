/**
 * QueryGatewayUserResponse.java 2014-11-20
 */
package com.hipad.smart.json;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hipad.smart.user.User;

/**
 * @author wangbaoming
 *
 */
public class QueryGatewayUserResponse{

	@Expose
	@SerializedName("data")
	private ArrayList<User> mData;
	@Expose
	@SerializedName("msg")
	private String mMsg;
	@Expose
	@SerializedName("success")
	private boolean mIsSuccessful;
	
	/**
	 * get the users who can access the devices that connect to the gateway.
	 * @return
	 * 		the user list
	 */
	public ArrayList<User> getData(){
		return mData;
	}
	
	public String getMsg(){
		return mMsg;
	}
	
	public boolean isSuccessful(){
		return mIsSuccessful;
	}
}
