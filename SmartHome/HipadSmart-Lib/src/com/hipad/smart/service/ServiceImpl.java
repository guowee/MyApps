/**
 * ServiceImpl.java 2014-11-17
 */
package com.hipad.smart.service;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonSyntaxException;
import com.hipad.smart.config.Webservice;
import com.hipad.smart.config.Webservice.Param;
import com.hipad.smart.device.CommonDevice.Group;
import com.hipad.smart.http.HttpPostWraper;
import com.hipad.smart.http.HttpUtil;
import com.hipad.smart.http.HttpUtil.ResponseResultHandler;
import com.hipad.smart.json.CmdResponse;
import com.hipad.smart.json.LoginResponse;
import com.hipad.smart.json.QueryDeviceInfoResponse;
import com.hipad.smart.json.QueryDevicesResponse;
import com.hipad.smart.json.QueryGatewayResponse;
import com.hipad.smart.json.QueryGatewayUserResponse;
import com.hipad.smart.json.RegisterResponse;
import com.hipad.smart.json.Response;
import com.hipad.smart.json.UpdateDeviceInfoResponse;
import com.hipad.smart.json.util.GsonInstance;
import com.hipad.smart.user.User;
import com.hipad.smart.util.MsgBodyData;
import com.hipad.smart.util.StringUtil;

/**
 * <b>
 * NOTE: the {@link ResponseResultHandler} in all interfaces should be used as a field rather than a local variable, because the weak reference is used to avoid the object leak.
 * if the handler is used as a local variable, the handler may be release soon, that no result may be returned.
 * </b>
 * @author wangbaoming
 *
 */
public class ServiceImpl implements Service {
	private static final String SERVICE_PREF_FILE = "service_core";
	
	private static final String PREF_TOKEN = "service_token";
	private static final String PREF_TOKEN_EXPIRATION = "service_token_expiration";
	
	private static String sAccessToken = null;
	
	/**
	 * restore the access token stored previously.
	 * @see {@link ServiceImpl#save(Context, int)}
	 * @param context
	 * 		app context
	 */
	public static void restore(Context context){
		SharedPreferences pref = context.getSharedPreferences(SERVICE_PREF_FILE, Context.MODE_PRIVATE);
		sAccessToken = pref.getString(PREF_TOKEN, null);
	}
	
	/**
	 * save the access token
	 * @param context
	 * 		app context
	 * @param validDays
	 * 		the valid period in day
	 */
	public static void save(Context context, int validDays){
		SharedPreferences pref = context.getSharedPreferences(SERVICE_PREF_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(PREF_TOKEN, sAccessToken);

		if(validDays >= 0){ // if the valid days < 0, the token is indefinite. 
			// calc the expiration in millisecond
	    	Calendar calendar = Calendar.getInstance();
	        calendar.add(Calendar.DATE, validDays);
			editor.putLong(PREF_TOKEN_EXPIRATION, calendar.getTimeInMillis());
		}		
		
		editor.commit();
	}
	
	/**
	 * verify if having the access token that is not expired.
	 * @see {@link ServiceImpl#save(Context, int)}
	 * @param context
	 * @return
	 * 		true if valid, false otherwise
	 */
	public static boolean verifyLocally(Context context){
		SharedPreferences pref = context.getSharedPreferences(SERVICE_PREF_FILE, Context.MODE_PRIVATE);
		sAccessToken = pref.getString(PREF_TOKEN, null);
		if(null == sAccessToken) return false;
		
		long expiration = pref.getLong(PREF_TOKEN_EXPIRATION, -1);
		if(-1 == expiration) return true; // the token is indefinite

    	Calendar calendar = Calendar.getInstance();
		long now = calendar.getTimeInMillis();
		if(now >= expiration) return false; // expired
		
		return true;
	}
	
	/**
	 * verify if having the access token that is not expired and valid on cloud.
	 * @see {@link ServiceImpl#save(Context, int)}
	 * @param context
	 * @return
	 * 		true if valid, false otherwise
	 */
	public static boolean verifyOnCloud(Context context, ResponseResultHandler<Response> handler){

		return false;
	}

	/**
	 * just clean the access token locally. And just null is passed as the handler is ok.
	 */
	@Override
	public void logout(Context context, ResponseResultHandler<Response> handler) {
		sAccessToken = null;
		
		SharedPreferences pref = context.getSharedPreferences(SERVICE_PREF_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(PREF_TOKEN, null);		
		editor.commit();
		
		handler.handle(false, null);
	}
	
	@Override
	public void login(String id, String passwd, ResponseResultHandler<LoginResponse> handler) { 
		final WeakReference<ResponseResultHandler<LoginResponse>> wrhandler = new WeakReference<ResponseResultHandler<LoginResponse>>(handler);
		
		HttpPostWraper post = new HttpPostWraper(Webservice.LOGIN);
		post.addParam(Param.user, id);
		post.addParam(Param.password, passwd);
		post.commitParams();
		
		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {
			
			@Override
			public void handle(boolean timeout, JSONObject jobj) {
				if(!timeout && null != jobj){
					sAccessToken = (String) jobj.remove(Webservice.Param.token);
				}				
				
				LoginResponse loginResponse = null;
				if(null != jobj){
					try {
						loginResponse = GsonInstance.get().fromJson(jobj.toString(), LoginResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				ServiceImpl.handle(wrhandler, timeout, loginResponse);
			}
		}, JSONObject.class);
	}

	@Override
	public void register(User user, ResponseResultHandler<RegisterResponse> handler) {
		final WeakReference<ResponseResultHandler<RegisterResponse>> wrhandler = new WeakReference<ResponseResultHandler<RegisterResponse>>(handler);
		
		HttpPostWraper post = new HttpPostWraper(Webservice.REGISTER);
		post.addParam(Param.user, user.getName());
		post.addParam(Param.password, user.getPassword());
		
		String mobile = user.getMobile();
		if(!StringUtil.isNullOrEmpty(mobile)) post.addParam(Param.mobile, mobile);
		
		String email = user.getEmail();
		if(!StringUtil.isNullOrEmpty(email)) post.addParam(Param.email, email);
		
		String nickName = user.getName();
		if(!StringUtil.isNullOrEmpty(nickName)) post.addParam(Param.name, nickName); 
		
		post.commitParams(); 
		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {
			
			@Override
			public void handle(boolean timeout, JSONObject jobj) {
				if(!timeout && null != jobj){
					sAccessToken = (String) jobj.remove(Webservice.Param.token);
				}				

				RegisterResponse registerResponse = null;
				if(null != jobj){
					try {
						registerResponse = GsonInstance.get().fromJson(jobj.toString(), RegisterResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}					
				}
				ServiceImpl.handle(wrhandler, timeout, registerResponse);
			}
		}, JSONObject.class);		
	}
	
	@Override
	public void updateUserInfo(User user, ResponseResultHandler<Response> handler) {
		// call webservice to update the user info
		HttpPostWraper post = new HttpPostWraper(Webservice.UPDATE_USER_INFO);
		post.addParam(Param.name, user.getName());
		post.addParam(Param.nickname, user.getNickName());
		
		post.addParam(Param.email, user.getEmail());
		post.addParam(Param.mobile, user.getMobile());
		post.addParam(Param.birthday, user.getBirthday());
		post.addParam(Param.gender, user.getGender());
		post.addParam(Param.address, user.getAddString());
		post.addParam(Param.job, user.getJob());
		post.addParam(Param.company, user.getCompany());
		post.addParam(Param.photo, user.getPhoto());
	
		post.addParam(Param.token, sAccessToken);
		
		post.commitParams();
		HttpUtil.postAvoidLeak(post, handler, Response.class);
	}
	
	private static <T> void handle(WeakReference<ResponseResultHandler<T>> weakReference, final boolean timeout, final T obj){
		if(null != weakReference){
			final ResponseResultHandler<T> handler = weakReference.get();
			System.out.println("handler: " + handler);
			if(null != handler){
				handler.handle(timeout, obj);
			}
		}
	}

	@Override
	public void getGateways(ResponseResultHandler<QueryGatewayResponse> handler) {
		HttpPostWraper post = new HttpPostWraper(Webservice.GET_GATEWAY_LIST);

		post.addParam(Param.token, sAccessToken);
		
		post.commitParams();
		HttpUtil.postAvoidLeak(post, handler, QueryGatewayResponse.class);		
	}
	
	@Override
	public void addGateway(String gatewaySn, String code, ResponseResultHandler<Response> handler) {
		HttpPostWraper post = new HttpPostWraper(Webservice.ADD_GATEWAY);
		post.addParam(Param.gateway, gatewaySn);
		post.addParam(Param.code, code);

		post.addParam(Param.token, sAccessToken);
		
		post.commitParams();
		HttpUtil.postAvoidLeak(post, handler, Response.class);		
	}
	
	@Override
	public void delGateway(String gatewaySn, ResponseResultHandler<Response> handler) {
		HttpPostWraper post = new HttpPostWraper(Webservice.DEL_GATEWAY);
		post.addParam(Param.gateway, gatewaySn);

		post.addParam(Param.token, sAccessToken);
		
		post.commitParams();
		HttpUtil.postAvoidLeak(post, handler, Response.class);	
	}
	
	@Override
	public void setDefaultGateway(String gatewaySn,	ResponseResultHandler<Response> handler) {
		HttpPostWraper post = new HttpPostWraper(Webservice.SET_DEFAULT_GATEWAY);
		
		post.addParam(Param.gateway, gatewaySn);
		post.addParam(Param.token, sAccessToken);		
		post.commitParams();		

		HttpUtil.postAvoidLeak(post, handler, Response.class);	
	}
	
	@Override
	public void getGatewayUsers(String gatewaySn, ResponseResultHandler<QueryGatewayUserResponse> handler) {
		HttpPostWraper post = new HttpPostWraper(Webservice.GET_GATEWAY_USER_LIST);
		post.addParam(Param.gateway, gatewaySn);

		post.addParam(Param.token, sAccessToken);
		
		post.commitParams();
		HttpUtil.postAvoidLeak(post, handler, QueryGatewayUserResponse.class);		
	}

	@Override
	public void addUser(String userId, String gatewaySn, ResponseResultHandler<Response> handler) {
		HttpPostWraper post = new HttpPostWraper(Webservice.ADD_GATEWAY_USER);
		post.addParam(Param.userId, userId);
		post.addParam(Param.gateway, gatewaySn);

		post.addParam(Param.token, sAccessToken);
		
		post.commitParams();
		HttpUtil.postAvoidLeak(post, handler, Response.class);
		
	}

	@Override
	public void delUser(String userId, String gatewaySn, ResponseResultHandler<Response> handler) {
		HttpPostWraper post = new HttpPostWraper(Webservice.DEL_GATEWAY_USER);
		post.addParam(Param.userId, userId);
		post.addParam(Param.gateway, gatewaySn);

		post.addParam(Param.token, sAccessToken);
		
		post.commitParams();
		HttpUtil.postAvoidLeak(post, handler, Response.class);		
	}

	@Override
	public void getDevices(String gatewaySn, Group group, ResponseResultHandler<QueryDevicesResponse> handler) {
		HttpPostWraper post = new HttpPostWraper(Webservice.GET_DEVIVCE_LIST);
		
		if(!StringUtil.isNullOrEmpty(gatewaySn)){
			post.addParam(Param.gateway, gatewaySn);
		}
		
		if(null != group && !Group.None.equals(group)){
			post.addParam(Param.group, String.valueOf(group.getValue()) );
		}
	
		post.addParam(Param.token, sAccessToken);
		
		post.commitParams();
		HttpUtil.postAvoidLeak(post, handler, QueryDevicesResponse.class);		
	}

	@Override
	public void getDeviceInfo(String gatewaySn, String deviceId, ResponseResultHandler<QueryDeviceInfoResponse> handler) {
		HttpPostWraper post = new HttpPostWraper(Webservice.GET_DEVIVCE_INFO);
		post.addParam(Param.gateway, gatewaySn);
		post.addParam(Param.device, deviceId);	

		// bypass or not
		post.addParam(Param.flag, "0"); // not bypass
		
		// cmd no
		post.addParam(Param.cmdNo, "1"); // query
	
		post.addParam(Param.token, sAccessToken);
		
		post.commitParams();
		HttpUtil.postAvoidLeak(post, handler, QueryDeviceInfoResponse.class);
	}

	@Override
	public void addDirectDevice(String deviceId, String code, ResponseResultHandler<Response> handler) {
		HttpPostWraper post = new HttpPostWraper(Webservice.ADD_DIRECT_DEVICE);
		post.addParam(Param.device, deviceId);
		post.addParam(Param.code, code);

		post.addParam(Param.token, sAccessToken);
		
		post.commitParams();
		HttpUtil.postAvoidLeak(post, handler, Response.class);	
		
	}

	@Override
	public void delDevice(String deviceId, String gatewaySn, ResponseResultHandler<Response> handler) {
		HttpPostWraper post = new HttpPostWraper(Webservice.DEL_DEVICE);
		post.addParam(Param.gateway, gatewaySn);
		post.addParam(Param.device, deviceId);
	
		post.addParam(Param.token, sAccessToken);
		
		post.commitParams();
		HttpUtil.postAvoidLeak(post, handler, Response.class);
	}

	@Override
	public void updateDeviceInfo(String deviceId, String gatewaySn, String name, ResponseResultHandler<UpdateDeviceInfoResponse> handler) {
		HttpPostWraper post = new HttpPostWraper(Webservice.UPDATE_DEVICE_INFO);
		post.addParam(Param.gateway, gatewaySn);
		post.addParam(Param.device, deviceId);
		
		// TODO the info that needs to be updated
		post.addParam(Param.name, name);
	
		post.addParam(Param.token, sAccessToken);
		
		post.commitParams();
		HttpUtil.postAvoidLeak(post, handler, UpdateDeviceInfoResponse.class);		
	}

	@Override
	public void managerDevice(String deviceId, String gatewaySn, Cmd cmd, ResponseResultHandler<CmdResponse> handler) {
		HttpPostWraper post = new HttpPostWraper(Webservice.MGR_DEVICE);
		post.addParam(Param.gateway, gatewaySn);
		post.addParam(Param.device, deviceId);		

		// flag
		post.addParam(Param.flag, String.valueOf(cmd.getFlag()) );
		
		// cmd no
		post.addParam(Param.cmdNo, String.valueOf(cmd.getCmdNo()) );
		
		// cmd body to cloud or device
		MsgBodyData body = cmd.getBody();
		if(null != body) post.addParam(Param.cmd, body.encode());
	
		post.addParam(Param.token, sAccessToken);
		
		post.commitParams();
//		HttpUtil.postAvoidLeak(post, handler, CmdResponse.class);
		HttpUtil.post(post, handler, CmdResponse.class);
	}

}
