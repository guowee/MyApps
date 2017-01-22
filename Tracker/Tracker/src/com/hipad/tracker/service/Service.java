package com.hipad.tracker.service;

import org.apache.http.client.ResponseHandler;

import android.content.Context;

import com.hipad.tracker.entity.TrackerMsg;
import com.hipad.tracker.entity.User;
import com.hipad.tracker.http.HttpUtil.ResponseResultHandler;
import com.hipad.tracker.json.BindInfoResponse;
import com.hipad.tracker.json.BindResponse;
import com.hipad.tracker.json.CodeResponse;
import com.hipad.tracker.json.CommonResponse;
import com.hipad.tracker.json.DevInfoResponse;
import com.hipad.tracker.json.LocResponse;
import com.hipad.tracker.json.LoginResponse;
import com.hipad.tracker.json.RegisterResponse;
import com.hipad.tracker.json.Response;
import com.hipad.tracker.json.StepResponse;

/**
 * 
 * @author guowei
 *
 */
public interface Service {
	/**
	 * login Tracker cloud.
	 * 
	 * @param name
	 *            the user phone used to login
	 * @param password
	 *            login password
	 * @param handler
	 *            the login result handler
	 */

	public void login(String name, String password,	ResponseResultHandler<LoginResponse> handler);
	
	public void getBindInfo(String account,ResponseResultHandler<BindInfoResponse> handler);
	/**
	 * logout app or cloud. the access token will be cleaned.
	 * @param context
	 * 		app context
	 * @param handler
	 * 		logout result handler
	 */
	public void logout(Context context, ResponseResultHandler<Response> handler);
	/**
	 * get SMS verification code
	 * @param mobile
	 * @param handler
	 */
	public void verify(String mobile,ResponseResultHandler<CodeResponse> handler);
	/**
	 * register the user on HipadSmart cloud.
	 * @param user
	 * 		the user info
	 * @param code identified code
	 * @param handler
	 * 		result handler
	 */
	public void register(User user,String code,ResponseResultHandler<RegisterResponse> handler);
	/**
	 * attach the tracker to user
	 * @param msg
	 * @param sim
	 * @param account
	 * @param handler
	 */
	public void bind(TrackerMsg msg,String imei,String sim,String account,ResponseResultHandler<BindResponse> handler);
	/**
	 * query the result of binding
	 * @param msg
	 * @param imei
	 * @param sim
	 * @param account
	 * @param handler
	 */
	public void bindResult(TrackerMsg msg,String imei,String sim,String account,ResponseResultHandler<BindResponse> handler);
	/**
	 * the request of location
	 * @param imei
	 * @param account
	 * @param handler
	 */
	public void loc(String imei,String account,ResponseResultHandler<LocResponse> handler);
	/**
	 * request the result of location
	 * @param imei
	 * @param account
	 * @param bizSn
	 * @param handler
	 */
	public void locResult(String imei,String account,String bizSn,ResponseResultHandler<LocResponse> handler);
	/**
	 * Restore factory equipment
	 * @param imei
	 * @param account
	 * @param handler
	 */
	public void factory(String imei,String account,ResponseResultHandler<CommonResponse> handler);
	
	public void facResult(String imei,String account,String bizSn,ResponseResultHandler<CommonResponse> handler);
	/**
	 * Curfew setting
	 * @param account
	 * @param imei
	 * @param curfewEnable
	 * @param wakeTime
	 * @param sleepTime
	 * @param handler
	 */
	public void curfew(String account,String imei,String curfewEnable,String wakeTime,String sleepTime,ResponseResultHandler<CommonResponse> handler);
	
	public void curfewResult(String account,String imei,String bizSn,ResponseResultHandler<CommonResponse> handler);
	/**
	 * SPN2 setting request
	 * @param account
	 * @param imei
	 * @param spn2
	 * @param handler
	 */
	public void spn2(String account,String imei,String spn2,ResponseResultHandler<CommonResponse> handler);
	
	public void spn2Result(String account,String imei,String bizSn,ResponseResultHandler<CommonResponse> handler);
	
	
	public void getSteps(String account,String imei,String stepType,ResponseResultHandler<StepResponse> handler);
	
	public void resetPwd(String account,String pwd,String code,ResponseResultHandler<CommonResponse> handler);
	
	public void getDevInfo(String account ,String imei,ResponseResultHandler<DevInfoResponse> handler);
}
