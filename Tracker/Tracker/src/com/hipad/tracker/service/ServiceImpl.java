package com.hipad.tracker.service;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import org.json.JSONObject;

import android.content.Context;

import com.google.gson.JsonSyntaxException;
import com.hipad.tracker.config.WebService;
import com.hipad.tracker.config.WebService.Param;
import com.hipad.tracker.entity.TrackerMsg;
import com.hipad.tracker.entity.User;
import com.hipad.tracker.http.HttpPostWraper;
import com.hipad.tracker.http.HttpUtil;
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
import com.hipad.tracker.json.util.GsonInstance;
import com.hipad.tracker.utils.SharedPreferencesHelper;
import com.hipad.tracker.utils.StringUtil;

/**
 * 
 * @author guowei
 *
 */
public class ServiceImpl implements Service{
	
    private static final String PREF_TOKEN = "service_token";
    private static final String PREF_TOKEN_EXPIRATION = "service_token_expiration";
    private static SharedPreferencesHelper  sph;
    private static String sAccessToken = null;
	/**
	 * restore the access token stored previously.
	 * @see {@link ServiceImpl#save(Context, int)}
	 * @param context
	 * 		app context
	 */
	public static void restore(Context context){
		sph=new SharedPreferencesHelper(context);
		sAccessToken = sph.getString(PREF_TOKEN);
	}
	/**
	 * save the access token
	 * @param context
	 * 		app context
	 * @param validDays
	 * 		the valid period in day
	 */
	public static void save(Context context, int validDays){
		sph=new SharedPreferencesHelper(context);
		sph.putString(PREF_TOKEN, sAccessToken);

		if(validDays >= 0){ // if the valid days < 0, the token is indefinite. 
			// calc the expiration in millisecond
	    	Calendar calendar = Calendar.getInstance();
	        calendar.add(Calendar.DATE, validDays);
			sph.putLong(PREF_TOKEN_EXPIRATION, calendar.getTimeInMillis());
		}		
	}
	
	public static boolean verifyLocally(Context context){
		sph=new SharedPreferencesHelper(context);
		sAccessToken = sph.getString(PREF_TOKEN);
		if(null == sAccessToken) return false;
		
		long expiration = sph.getLong(PREF_TOKEN_EXPIRATION);
		if(-1 == expiration) return true; // the token is indefinite

    	Calendar calendar = Calendar.getInstance();
		long now = calendar.getTimeInMillis();
		if(now >= expiration) return false; // expired
		
		return true;
	}
	
	
	@Override
	public void login(String name, String password,	ResponseResultHandler<LoginResponse> handler) {
        final WeakReference<ResponseResultHandler<LoginResponse>> wrhandler = new WeakReference<ResponseResultHandler<LoginResponse>>(handler);
		
		HttpPostWraper post = new HttpPostWraper(WebService.LOGIN);
		post.addParam(Param.user, name);
		post.addParam(Param.password, password);
		post.commitParams();
		
		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {
			
			@Override
			public void handle(boolean timeout, JSONObject jobj) {
							
				LoginResponse loginResponse = null;
				if(null != jobj){
					try {
						loginResponse = GsonInstance.get().fromJson(jobj.toString(), LoginResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				if(!timeout && null != jobj){
					sAccessToken = (String) jobj.remove(WebService.Param.token);
				}
				ServiceImpl.handle(wrhandler, timeout, loginResponse);
			}
		}, JSONObject.class); 
	}
	
	@Override
	public void logout(Context context, ResponseResultHandler<Response> handler) {
        sAccessToken = null;
		sph=new SharedPreferencesHelper(context);
		sph.putString(PREF_TOKEN, null);		
 		handler.handle(false, null);
	}
	
	@Override
	public void getBindInfo(String account,	ResponseResultHandler<BindInfoResponse> handler) {
		final WeakReference<ResponseResultHandler<BindInfoResponse>> wrhandler = new WeakReference<ResponseResultHandler<BindInfoResponse>>(handler);
		HttpPostWraper post = new HttpPostWraper(WebService.BINDINFO);

		post.addParam(Param.token, account);
		post.commitParams();
		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {

			@Override
			public void handle(boolean timeout, JSONObject jobj) {
				BindInfoResponse infoResponse = null;
				if (null != jobj) {
					try {
						infoResponse = GsonInstance.get().fromJson(jobj.toString(), BindInfoResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				ServiceImpl.handle(wrhandler, timeout, infoResponse);
			}
		}, JSONObject.class);
	}
	@Override
	public void register(User user, String code,ResponseResultHandler<RegisterResponse> handler) {
       final WeakReference<ResponseResultHandler<RegisterResponse>> wrhandler = new WeakReference<ResponseResultHandler<RegisterResponse>>(handler);
		
		HttpPostWraper post = new HttpPostWraper(WebService.REGISTER);
		String mobile = user.getMobile();
		if(!StringUtil.isNullOrEmpty(mobile)) 
			post.addParam(Param.mobile, mobile);
		
		post.addParam(Param.password,user.getPassword());
		post.addParam(Param.code, code);
		post.commitParams();
		
        HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {
			
			@Override
			public void handle(boolean timeout, JSONObject jobj) {
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
	public void verify(String mobile,ResponseResultHandler<CodeResponse> handler) {
		 final WeakReference<ResponseResultHandler<CodeResponse>> wrhandler = new WeakReference<ResponseResultHandler<CodeResponse>>(handler);
			
			HttpPostWraper post = new HttpPostWraper(WebService.VERIFY);
			
			if(!StringUtil.isNullOrEmpty(mobile)) 
				post.addParam(Param.mobile, mobile);
			post.commitParams();
			
			HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {
				
				@Override
				public void handle(boolean timeout, JSONObject jobj) {

					CodeResponse codeResponse = null;
					if(null != jobj){
						try {
							codeResponse = GsonInstance.get().fromJson(jobj.toString(), CodeResponse.class);
						} catch (JsonSyntaxException e) {
							e.printStackTrace();
						}					
					}
					ServiceImpl.handle(wrhandler, timeout, codeResponse);
				}
			}, JSONObject.class);	
		}

	@Override
	public void bind(TrackerMsg msg, String imei, String sim, String account,ResponseResultHandler<BindResponse> handler) {
		final WeakReference<ResponseResultHandler<BindResponse>> wrhandler = new WeakReference<ResponseResultHandler<BindResponse>>(handler);

		HttpPostWraper post = new HttpPostWraper(WebService.BIND);

		post.addParam(Param.token, account);
		if (!StringUtil.isNullOrEmpty(imei))
			post.addParam(Param.imei, imei);
		if (!StringUtil.isNullOrEmpty(sim))
			post.addParam(Param.sim, sim);
		post.addParam(Param.spn1, msg.getSpn1());
		post.commitParams();

		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {
			@Override
			public void handle(boolean timeout, JSONObject jobj) {
				BindResponse bindResponse = null;
				if (null != jobj) {
					try {
						bindResponse = GsonInstance.get().fromJson(jobj.toString(), BindResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				ServiceImpl.handle(wrhandler, timeout, bindResponse);
			}
		}, JSONObject.class);
	}
	@Override
	public void bindResult(TrackerMsg msg, String imei, String sim,String account, ResponseResultHandler<BindResponse> handler) {
		final WeakReference<ResponseResultHandler<BindResponse>> wrhandler = new WeakReference<ResponseResultHandler<BindResponse>>(handler);

		HttpPostWraper post = new HttpPostWraper(WebService.BINDRESULT);

		post.addParam(Param.token, account);
		if (!StringUtil.isNullOrEmpty(imei))
			post.addParam(Param.imei, imei);
		if (!StringUtil.isNullOrEmpty(sim))
			post.addParam(Param.sim, sim);
		post.addParam(Param.spn1, msg.getSpn1());
		post.addParam(Param.bizsn, msg.getBizSN());
		post.commitParams();
		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {
			@Override
			public void handle(boolean timeout, JSONObject jobj) {
				BindResponse bindResponse = null;
				if (null != jobj) {
					try {
						bindResponse = GsonInstance.get().fromJson(jobj.toString(), BindResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				ServiceImpl.handle(wrhandler, timeout, bindResponse);
			}
		}, JSONObject.class);
	}
	@Override
	public void loc(String imei, String account,ResponseResultHandler<LocResponse> handler) {
		final WeakReference<ResponseResultHandler<LocResponse>> wrhandler = new WeakReference<ResponseResultHandler<LocResponse>>(handler);
		HttpPostWraper post = new HttpPostWraper(WebService.LOC);
		post.addParam(Param.token,account);
		
		if(!StringUtil.isNullOrEmpty(imei))
		post.addParam(Param.imei, imei);
		post.commitParams();
		
		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {

			@Override
			public void handle(boolean timeout, JSONObject jobj) {

				LocResponse locResponse = null;
				if (null != jobj) {
					try {
						locResponse = GsonInstance.get().fromJson(jobj.toString(), LocResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				ServiceImpl.handle(wrhandler, timeout, locResponse);
			}
		}, JSONObject.class);
		
	}
	
	@Override
	public void locResult(String imei, String account, String bizSn,ResponseResultHandler<LocResponse> handler) {
		final WeakReference<ResponseResultHandler<LocResponse>> wrhandler = new WeakReference<ResponseResultHandler<LocResponse>>(handler);
		HttpPostWraper post = new HttpPostWraper(WebService.LOCRESULT);
		
		post.addParam(Param.token, account);
		if(!StringUtil.isNullOrEmpty(imei))
			post.addParam(Param.imei,imei);
		
		if(!StringUtil.isNullOrEmpty(bizSn))
			post.addParam(Param.bizsn, bizSn);
		post.commitParams();
		
		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {

			@Override
			public void handle(boolean timeout, JSONObject jobj) {
			   LocResponse locResponse = null;
				if (null != jobj) {
					try {
						locResponse = GsonInstance.get().fromJson(jobj.toString(), LocResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				ServiceImpl.handle(wrhandler, timeout, locResponse);
			}
		}, JSONObject.class);
	}
	
	
	
	@Override
	public void factory(String imei, String account,ResponseResultHandler<CommonResponse> handler) {
		final WeakReference<ResponseResultHandler<CommonResponse>> wrhandler = new WeakReference<ResponseResultHandler<CommonResponse>>(handler);
		HttpPostWraper post = new HttpPostWraper(WebService.FACTORY);

		if (!StringUtil.isNullOrEmpty(imei))
			post.addParam(Param.imei, imei);
		post.addParam(Param.token, account);
		post.commitParams();
		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {

			@Override
			public void handle(boolean timeout, JSONObject jobj) {
				CommonResponse factoryResponse = null;
				if (null != jobj) {
					try {
						factoryResponse = GsonInstance.get().fromJson(jobj.toString(), CommonResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				ServiceImpl.handle(wrhandler, timeout, factoryResponse);
			}
		}, JSONObject.class);
		
	}
	@Override
	public void facResult(String imei, String account, String bizSn,ResponseResultHandler<CommonResponse> handler) {
		final WeakReference<ResponseResultHandler<CommonResponse>> wrhandler = new WeakReference<ResponseResultHandler<CommonResponse>>(handler);
		HttpPostWraper post = new HttpPostWraper(WebService.FACRESULT);
		post.addParam(Param.token, account);
		if (!StringUtil.isNullOrEmpty(imei))
			post.addParam(Param.imei, imei);
		if(!StringUtil.isNullOrEmpty(bizSn))
			post.addParam(Param.bizsn, bizSn);
		
		post.commitParams();
		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {
			@Override
			public void handle(boolean timeout, JSONObject jobj) {
				CommonResponse factoryResponse = null;
				if (null != jobj) {
					try {
						factoryResponse = GsonInstance.get().fromJson(jobj.toString(), CommonResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				ServiceImpl.handle(wrhandler, timeout, factoryResponse);
			}
		}, JSONObject.class);
	}
	
	@Override
	public void curfew(String account, String imei, String curfewEnable,String wakeTime, String sleepTime,	ResponseResultHandler<CommonResponse> handler) {
		final WeakReference<ResponseResultHandler<CommonResponse>> wrhandler = new WeakReference<ResponseResultHandler<CommonResponse>>(handler);
		HttpPostWraper post = new HttpPostWraper(WebService.CURFEW);
		post.addParam(Param.token, account);
		if (!StringUtil.isNullOrEmpty(imei))
			post.addParam(Param.imei, imei);
		post.addParam(Param.curfew, curfewEnable);
		if(!StringUtil.isNullOrEmpty(wakeTime))
			post.addParam(Param.waketime, wakeTime);
		if(!StringUtil.isNullOrEmpty(sleepTime))
			post.addParam(Param.sleeptime, sleepTime);
		post.commitParams();
		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {
			@Override
			public void handle(boolean timeout, JSONObject jobj) {
				CommonResponse curfewResponse = null;
				if (null != jobj) {
					try {
						curfewResponse = GsonInstance.get().fromJson(jobj.toString(), CommonResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				ServiceImpl.handle(wrhandler, timeout, curfewResponse);
			}
		}, JSONObject.class);
	}
	@Override
	public void curfewResult(String account, String imei, String bizSn,	ResponseResultHandler<CommonResponse> handler) {
		final WeakReference<ResponseResultHandler<CommonResponse>> wrhandler = new WeakReference<ResponseResultHandler<CommonResponse>>(handler);
		HttpPostWraper post = new HttpPostWraper(WebService.CURFEWRESULT);
		post.addParam(Param.token, account);
		if (!StringUtil.isNullOrEmpty(imei))
			post.addParam(Param.imei, imei);
		if(!StringUtil.isNullOrEmpty(bizSn))
			post.addParam(Param.bizsn, bizSn);
		post.commitParams();
		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {
			@Override
			public void handle(boolean timeout, JSONObject jobj) {
				CommonResponse curfewResponse = null;
				if (null != jobj) {
					try {
						curfewResponse = GsonInstance.get().fromJson(jobj.toString(), CommonResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				ServiceImpl.handle(wrhandler, timeout, curfewResponse);
			}
		}, JSONObject.class);
		
	}
	@Override
	public void spn2(String account, String imei, String spn2,ResponseResultHandler<CommonResponse> handler) {
		final WeakReference<ResponseResultHandler<CommonResponse>> wrhandler = new WeakReference<ResponseResultHandler<CommonResponse>>(handler);
		HttpPostWraper post = new HttpPostWraper(WebService.SPN);
		post.addParam(Param.token, account);
		if (!StringUtil.isNullOrEmpty(imei))
			post.addParam(Param.imei, imei);
		if(!StringUtil.isNullOrEmpty(spn2))
			post.addParam(Param.spn2, spn2);
		post.commitParams();
		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {
			@Override
			public void handle(boolean timeout, JSONObject jobj) {
				CommonResponse spn2Response = null;
				if (null != jobj) {
					try {
						spn2Response = GsonInstance.get().fromJson(jobj.toString(), CommonResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				ServiceImpl.handle(wrhandler, timeout, spn2Response);
			}
		}, JSONObject.class);
		
	}
	
	@Override
	public void spn2Result(String account, String imei, String bizSn,ResponseResultHandler<CommonResponse> handler) {
		final WeakReference<ResponseResultHandler<CommonResponse>> wrhandler = new WeakReference<ResponseResultHandler<CommonResponse>>(handler);
		HttpPostWraper post = new HttpPostWraper(WebService.SPNRESULT);
		post.addParam(Param.token, account);
		if (!StringUtil.isNullOrEmpty(imei))
			post.addParam(Param.imei, imei);
		if(!StringUtil.isNullOrEmpty(bizSn))
			post.addParam(Param.bizsn, bizSn);
		post.commitParams();
		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {
			@Override
			public void handle(boolean timeout, JSONObject jobj) {
				CommonResponse spn2Response = null;
				if (null != jobj) {
					try {
						spn2Response = GsonInstance.get().fromJson(jobj.toString(), CommonResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				ServiceImpl.handle(wrhandler, timeout, spn2Response);
			}
		}, JSONObject.class);
	}
	@Override
	public void getSteps(String account, String imei,String stepType,ResponseResultHandler<StepResponse> handler) {
		final WeakReference<ResponseResultHandler<StepResponse>> wrhandler = new WeakReference<ResponseResultHandler<StepResponse>>(handler);
		HttpPostWraper post = new HttpPostWraper(WebService.STEPS);
		post.addParam(Param.token, account);
		if (!StringUtil.isNullOrEmpty(imei))
			post.addParam(Param.imei, imei);
		if(!StringUtil.isNullOrEmpty(stepType))
			post.addParam(Param.steptype, stepType);
		post.commitParams();
		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {
			@Override
			public void handle(boolean timeout, JSONObject jobj) {
				StepResponse stepResponse = null;
				if (null != jobj) {
					try {
						stepResponse = GsonInstance.get().fromJson(jobj.toString(), StepResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				ServiceImpl.handle(wrhandler, timeout, stepResponse);
			}
		}, JSONObject.class);
	}
	@Override
	public void resetPwd(String mobile, String pwd, String code,ResponseResultHandler<CommonResponse> handler) {
		final WeakReference<ResponseResultHandler<CommonResponse>> wrhandler = new WeakReference<ResponseResultHandler<CommonResponse>>(handler);
		HttpPostWraper post = new HttpPostWraper(WebService.RESETPWD);
		post.addParam(Param.mobile, mobile);
		if (!StringUtil.isNullOrEmpty(pwd))
			post.addParam(Param.password, pwd);
		if(!StringUtil.isNullOrEmpty(code))
			post.addParam(Param.code, code);
		post.commitParams();
		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {
			@Override
			public void handle(boolean timeout, JSONObject jobj) {
				CommonResponse repwdResponse = null;
				if (null != jobj) {
					try {
						repwdResponse = GsonInstance.get().fromJson(jobj.toString(), CommonResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				ServiceImpl.handle(wrhandler, timeout, repwdResponse);
			}
		}, JSONObject.class);
		
	}
	
	
	@Override
	public void getDevInfo(String account, String imei,	ResponseResultHandler<DevInfoResponse> handler) {

		final WeakReference<ResponseResultHandler<DevInfoResponse>> wrhandler = new WeakReference<ResponseResultHandler<DevInfoResponse>>(handler);
		HttpPostWraper post = new HttpPostWraper(WebService.DEVINFO);
		post.addParam(Param.token, account);
		if (!StringUtil.isNullOrEmpty(imei))
			post.addParam(Param.imei, imei);
		post.commitParams();
		HttpUtil.post(post, new ResponseResultHandler<JSONObject>() {
			@Override
			public void handle(boolean timeout, JSONObject jobj) {
				DevInfoResponse devInfoResponse = null;
				if (null != jobj) {
					try {
						devInfoResponse = GsonInstance.get().fromJson(jobj.toString(), DevInfoResponse.class);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				ServiceImpl.handle(wrhandler, timeout, devInfoResponse);
			}
		}, JSONObject.class);
		
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
	
}
