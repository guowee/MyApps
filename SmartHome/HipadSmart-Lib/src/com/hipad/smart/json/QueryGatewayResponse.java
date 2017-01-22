/**
 * QueryGatewayResponse.java 2014-11-20
 */
package com.hipad.smart.json;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author wangbaoming
 *
 */
public class QueryGatewayResponse {
	
	@Expose
	@SerializedName("data")
	private ArrayList<Gateway> mData;
	@Expose
	@SerializedName("msg")
	private String mMsg;
	@Expose
	@SerializedName("success")
	private boolean mIsSuccessful;
	
	/**
	 * get the gateway list.
	 * @return
	 * 		the gateways
	 */
	public ArrayList<Gateway> getData(){
		return mData;
	}
	
	public String getMsg(){
		return mMsg;
	}
	
	public boolean isSuccessful(){
		return mIsSuccessful;
	}
}
