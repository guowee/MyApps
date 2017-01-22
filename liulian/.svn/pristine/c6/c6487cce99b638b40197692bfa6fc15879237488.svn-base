package com.haomee.liulian;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.haomee.consts.PathConst;
import com.haomee.entity.Users;
import com.haomee.util.StringUtil;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

//获取验证码
public class VerificationCodeActivity extends BaseActivity {

	private EditText et1, et2, et3, et4;
	private String phone, pass;
	private TextView tv_phone_toast, tv_submit;
	private StringBuffer sbf;
	private TextView result_yzm;
	private Timer timer ;
	private int recLen = 60;
	private LoadingDialog loadingDialog;
	private MyTask myTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findpass);

		loadingDialog  = new LoadingDialog(this);
		
		phone = this.getIntent().getStringExtra("phone");
		pass = this.getIntent().getStringExtra("pass");
		
		InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

		et1 = (EditText) findViewById(R.id.et1);
		et2 = (EditText) findViewById(R.id.et2);
		et3 = (EditText) findViewById(R.id.et3);
		et4 = (EditText) findViewById(R.id.et4);

		tv_phone_toast = (TextView) findViewById(R.id.tv_phone_toast);

		tv_submit = (TextView) findViewById(R.id.tv_submit);

		result_yzm = (TextView) findViewById(R.id.result_yzm);

		sbf = new StringBuffer();

		findViewById(R.id.tv_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		et1.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

				if (s.length() > 0) {

					setBg(et1, et2);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		et2.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

				if (s.length() > 0) {

					setBg(et2, et3);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		et3.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

				if (s.length() > 0) {

					setBg(et3, et4);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		et4.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

				if (s.length() > 0) {

					et4.setBackgroundResource(R.drawable.public_import_default);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		result_yzm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				loadingDialog.show();
				
				if(timer!= null){
					timer.cancel();
				}
				timer = new Timer();
				
				if(myTask != null){
					myTask.cancel();
				}
				recLen = 60;
				myTask = new MyTask();

				getV_code(phone);
			}
		});

		tv_phone_toast.setText("您的" + phone + "手机会收到验证码短信");

		tv_submit.setClickable(false);

		tv_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				sbf.append(et1.getText().toString()).append(et2.getText().toString()).append(et3.getText().toString()).append(et4.getText().toString());
				
				loadingDialog.show();

				Reg(phone, pass, sbf.toString());

			}
		});
		
		timer = new Timer();
		
		myTask = new MyTask();
		
		timer.schedule(myTask, 1000, 1000);
		
		result_yzm.setVisibility(View.VISIBLE);
		
		result_yzm.setClickable(false);


	}
	
	class MyTask extends TimerTask{
		
		@Override
		public void run() {
	            runOnUiThread(new Runnable() {      
	                @SuppressLint("ResourceAsColor")
					@Override  
	                public void run() {  
	                    recLen--;  
	                    
	                    if(recLen <= 0){  
	                        timer.cancel();  
	                        result_yzm.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	                        result_yzm.setText("重新发送验证码 ");  
	                        result_yzm.setTextColor(R.color.main_color);
	                        result_yzm.setVisibility(View.VISIBLE);
	                        result_yzm.setClickable(true);  		
	                    }else{
	                    	result_yzm.setText("重新发送验证码 "+recLen+"秒");  
	                    }
	                }  
	            });  
	        }  
		}


	public void setBg(EditText front_et, EditText next_et) {

		front_et.setBackgroundResource(R.drawable.public_import_default);

		next_et.setBackgroundResource(R.drawable.public_import_pressed);

		next_et.requestFocus();
	}

	//获取验证码

	public void getV_code(String phone) {

		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("mobile", phone);
		re.put("ak", StringUtil.getMD5Str(StringUtil.getMD5Str(phone)));
		Log.e("地址：", PathConst.URL_REG_SD_CODE + "&mobile=" + phone + "&ak=" + StringUtil.getMD5Str(StringUtil.getMD5Str(phone)));
		asyncHttp.get(PathConst.URL_REG_SD_CODE, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);

				try {
					
					loadingDialog.dismiss();
					
					JSONObject json = new JSONObject(arg0);

					Log.e("地址：", json.toString());

					if (1 == json.optInt("flag")) {

						result_yzm.setVisibility(View.VISIBLE);

						MyToast.makeText(VerificationCodeActivity.this, "验证码已发送", 1).show();

						result_yzm.setClickable(false);
						
						timer.schedule(myTask, 1000, 1000);

					} else {

						MyToast.makeText(VerificationCodeActivity.this, json.optString("msg"), 1).show();

						result_yzm.setVisibility(View.VISIBLE);

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	//注册
	public void Reg(String phone, String pass, String code) {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("mobile", phone);
		re.put("password", StringUtil.getMD5Str(pass));
		re.put("code", code);
		Log.e("地址：", PathConst.URL_REG + "&mobile=" + phone + "&password=" + StringUtil.getMD5Str(StringUtil.getMD5Str(pass)) + "&code=" + code);
		asyncHttp.get(PathConst.URL_REG, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					loadingDialog.dismiss();
					JSONObject json = new JSONObject(arg0);
					Log.e("地址：", json.toString());
					if (1 == json.optInt("flag")) {
						JSONObject json_user = json.getJSONObject("user");
						Users user = new Users();
						user.setUid(json_user.getString("id"));
						user.setName(json_user.getString("username"));
						user.setImage(json_user.getString("head_pic"));
						user.setSex(json_user.optInt("sex"));
						user.setPhone(json_user.getString("mobile"));
						user.setAccesskey(json_user.optString("accesskey"));
						user.setHx_username(json_user.optString("hx_username"));
						user.setHx_password(json_user.optString("hx_password"));
						user.setIs_new(json_user.optInt("is_new"));
						LiuLianApplication.current_user = user;
						LiuLianApplication.getInstance().saveLoginedUser();
						MyToast.makeText(VerificationCodeActivity.this, "注册成功", 1).show();
						login_hx();
						goActivity();
					}else{
						sbf = new StringBuffer();
						MyToast.makeText(VerificationCodeActivity.this, json.optString("msg"), 1).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void login_hx() {
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(LiuLianApplication.current_user.getHx_username(), LiuLianApplication.current_user.getHx_password(), new EMCallBack() {

			@Override
			public void onSuccess() {
				// 登陆成功，保存用户名密码
				/*
				 * try { // demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
				 * List<String> usernames =
				 * EMChatManager.getInstance().getContactUserNames();
				 * Map<String, User> userlist = new HashMap<String, User>(); for
				 * (String username : usernames) { User user = new User();
				 * user.setUsername(username); userlist.put(username, user); }
				 * // 添加user"申请与通知" User newFriends = new User();
				 * newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
				 * newFriends.setNick("申请与通知"); newFriends.setHeader("");
				 * userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
				 * 
				 * // 存入内存
				 * VideoApplication.getInstance().setContactList(userlist); //
				 * 存入db UserDao dao = new UserDao(MainActivity.this); List<User>
				 * users = new ArrayList<User>(userlist.values());
				 * dao.saveContactList(users); } catch (Exception e) {
				 * e.printStackTrace(); }
				 */
				/*
				 * boolean updatenick =
				 * EMChatManager.getInstance().updateCurrentUserNick
				 * (VideoApplication.currentUserNick); if (!updatenick) {
				 * EMLog.e("LoginActivity", "update current user nick fail"); }
				 */

				// 进入主页面
				// startActivity(new Intent(MainActivity.this,
				// MainActivity.class));
				// finish();
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, final String message) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), "登录失败: " + message, 0).show();
					}
				});
			}
		});
	}

	public void goActivity() {

		try {

			AppManager.getAppManager().finishActivity(Class.forName("com.haomee.liulian.RegActivity"));
			AppManager.getAppManager().finishActivity(Class.forName("com.haomee.liulian.LoginPageActivity"));

		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
		Intent intent = new Intent();
		intent.putExtra("from_activity", 0);
		intent.setClass(this, UserInfoActivity.class);
		this.startActivity(intent);
		this.finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(timer!=null){
			
			timer.cancel();
			
			timer = null;
		}
		
		if(myTask!=null){
			
			myTask.cancel();
			
			myTask = null;
		}
	}

}
