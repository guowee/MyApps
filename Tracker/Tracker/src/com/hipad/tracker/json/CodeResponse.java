package com.hipad.tracker.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CodeResponse {
	@Expose
	@SerializedName("msg")
	private String mMsg;
	@Expose
	@SerializedName("code")
	private String mCode;
	@Expose
	@SerializedName("success")
	private boolean mIsSuccessful;
	public String getMsg() {
		return mMsg;
	}
	public String getCode(){
		return mCode;
	}
	public boolean isSuccessful() {
		return mIsSuccessful;
	}	
}
