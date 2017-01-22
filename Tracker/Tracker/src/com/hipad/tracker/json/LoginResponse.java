package com.hipad.tracker.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hipad.tracker.entity.User;

public class LoginResponse /*extends Response*/{
	@Expose
	@SerializedName("data")
	private User mData;
	@Expose
	@SerializedName("account")
    private String account;
	@Expose
	@SerializedName("msg")
	private String mMsg;
	@Expose
	@SerializedName("success")
	private boolean mIsSuccessful;
	public User getData() {
		return mData;
	}
	
	public String getAccount() {
		return account;
	}
	public String getMsg() {
		return mMsg;
	}
	public boolean isSuccessful() {
		return mIsSuccessful;
	}	
}
