/**
 * User.java 2014-11-17
 */
package com.hipad.smart.user;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hipad.smart.json.Data;

/**
 * represent the user, presenting the user information. 
 * @author wangbaoming
 *
 */
public class User extends Data implements Serializable {
	private static final long serialVersionUID = 3130098845743955536L;
	
	@Expose
	@SerializedName("id")
	private String mUserId;
	@Expose
	@SerializedName("loginPwd")
	private String mPassword;
	@Expose
	@SerializedName("nickName")
	private String mNickName;
	@Expose
	@SerializedName("userMobile")
	private String mMobile;
	@Expose
	@SerializedName("userEmail")
	private String mEmail;
	@Expose
	@SerializedName("userName")
	private String mName;
	@Expose
	@SerializedName("birthday")
	private String mBirthday;
	@Expose
	@SerializedName("gender")
	private String mGender;
	@Expose
	@SerializedName("address")
	private String mAddress;
	@Expose
	@SerializedName("job")
	private String mJob;
	@Expose
	@SerializedName("company")
	private String mCompany;
	@Expose
	@SerializedName("photo")
	private String mPhoto;
	@Expose
	@SerializedName("userId")
	private String mIdNo; // ID No.
	@Expose
	@SerializedName("lastLoginTime")
	private String mLastLoginTime;
	@Expose
	@SerializedName("defaultIgatewayId")
	private String mDefaultGateway;
	
//	public void setUserId(String userId) {
//		mUserId = userId;
//	}
	public void setMobile(String mobile) {
		mMobile = mobile;
	}
	public void setEmail(String email) {
		mEmail = email;
	}
	public void setPassword(String password) {
		mPassword = password;
	}
	public void setName(String name) {
		mName = name;
	}
	public void setBirthday(String birthday) {
		mBirthday = birthday;
	}
	public void setGender(String gender) {
		mGender = gender;
	}
	public void setAddress(String address) {
		mAddress = address;
	}
	public void setJob(String job) {
		mJob = job;
	}
	public void setCompany(String company) {
		mCompany = company;
	}
	public void setPhoto(String photo) {
		mPhoto = photo;
	}
	public void setIdNo(String idNo) {
		mIdNo = idNo;
	}
	public void setNickName(String nickName) {
		mNickName = nickName;
	}
	public void setDefaultGateway(String defaultGateway){
		mDefaultGateway = defaultGateway;
	}
	
	public String getUserId() {
		return mUserId;
	}
	public String getMobile() {
		return mMobile;
	}
	public String getEmail() {
		return mEmail;
	}
	public String getPassword() {
		return mPassword;
	}
	public String getName() {
		return mName;
	}
	public String getBirthday() {
		return mBirthday;
	}
	public String getGender() {
		return mGender;
	}
	public String getAddString() {
		return mAddress;
	}
	public String getJob() {
		return mJob;
	}
	public String getCompany() {
		return mCompany;
	}
	public String getPhoto() {
		return mPhoto;
	}
	public String getIdNo() {
		return mIdNo;
	}
	public String getNickName() {
		return mNickName;
	}	
	public String getLastLoginTime() {
		return mLastLoginTime;
	}
	public String getDefaultGateway(){
		return mDefaultGateway;
	}
}
