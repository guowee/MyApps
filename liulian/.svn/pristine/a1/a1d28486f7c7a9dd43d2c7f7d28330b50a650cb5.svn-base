package com.haomee.liulian;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.util.NetUtils;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.ShareContent;
import com.haomee.entity.Users;
import com.haomee.util.StringUtil;
import com.haomee.view.HintEditText;
import com.haomee.view.LoadingDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class LoginActivity extends BaseActivity {

	private EditText et_phone, et_pass;
	private ImageView iv_submit;
	private TextView tv_find_pas;
	private ImageView tv_back;
	private SharedPreferences preference;
	SharedPreferences.Editor editor = null;
	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		loadingDialog = new LoadingDialog(this);

		preference = this.getSharedPreferences(CommonConst.PREFERENCES_MEDAL, Activity.MODE_PRIVATE);
		editor = preference.edit();
		setContentView(R.layout.activity_login);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_phone.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);

		et_pass = (EditText) findViewById(R.id.et_pass);
		et_pass.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);
		iv_submit = (ImageView) findViewById(R.id.iv_submit);
		tv_find_pas = (TextView) findViewById(R.id.tv_find_pass);
		tv_find_pas.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_back = (ImageView) findViewById(R.id.tv_back);

		et_phone.setSelection(et_phone.getText().toString().length());

		tv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		tv_find_pas.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, ResetPassActivity.class);
				LoginActivity.this.startActivity(intent);
			}
		});
		iv_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (NetUtils.hasDataConnection(LoginActivity.this)) {
					if (!"".equals(et_phone.getText().toString().trim())) {
						if (!"".equals(et_pass.getText().toString().trim())) {
							loadingDialog.show();
							Login(et_phone.getText().toString().trim(), et_pass.getText().toString().trim());
						} else {

							Toast.makeText(LoginActivity.this, "请输入登录密码", 1).show();
						}
					} else {
						Toast.makeText(LoginActivity.this, "请输入手机号", 1).show();
					}
				} else {
					Toast.makeText(LoginActivity.this, "网络不给力", 1).show();
				}
			}
		});
	}

	public boolean isMobileNum(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher mtcher = p.matcher(mobiles);
		return mtcher.matches();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		this.finish();
	}

	//
	public void Login(String phone, String pass) {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("mobile", phone);
		re.put("password", StringUtil.getMD5Str(pass));
		Log.e("地址：", PathConst.URL_LOGIN + "&mobile=" + phone + "&password=" + StringUtil.getMD5Str(pass));
		asyncHttp.get(PathConst.URL_LOGIN, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					Users user = new Users();
					JSONObject json = new JSONObject(arg0);
					Log.e("返回数据：", json.toString());
					if (1 == json.optInt("flag")) {
						JSONObject json_user = json.getJSONObject("user");
						user.setUid(json_user.getString("id"));
						user.setName(json_user.getString("username"));
						user.setImage(json_user.getString("head_pic"));
						user.setSex(json_user.optInt("sex"));
						user.setPhone(json_user.getString("mobile"));
						user.setAccesskey(json_user.optString("accesskey"));
						user.setHx_username(json_user.optString("hx_username"));
						user.setHx_password(json_user.optString("hx_password"));
						user.setBack_pic(json_user.optString("back_pic"));
						
						
						user.setIs_new(json.optInt("is_new"));
						
						
						user.setUser_level_icon(json_user.optString("user_level_icon"));
						user.setUser_level(json_user.optString("user_level"));
						LiuLianApplication.current_user = user;
						LiuLianApplication.getInstance().saveLoginedUser();
						// 彩蛋埋点儿 好奇宝宝打開最新話題等于100次
						if (preference.getInt(CommonConst.LOGIN_SUCCEED, 0) == 0) {
							addMedal();
						} else {
							editor.putInt(CommonConst.LOGIN_SUCCEED, preference.getInt(CommonConst.LOGIN_SUCCEED, 0) + 1);
							editor.commit();
							Toast.makeText(LoginActivity.this, "登录成功", 1).show();
						}
						login_hx();
					} else {
						Toast.makeText(LoginActivity.this, json.optString("msg"), 1).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void addMedal() {
		String url = PathConst.URL_ADD_MEDAL + "&uid=" + LiuLianApplication.current_user.getUid() + "&id=28";
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					JSONObject egg_obj = new JSONObject(arg0);
					if (egg_obj.getBoolean("is_new")) {
						Intent intent_send = new Intent();
						intent_send.setClass(LoginActivity.this, ShareMedalActivity.class);
						ShareContent share = new ShareContent();
						share.setId(egg_obj.getString("id"));
						share.setTitle(egg_obj.getString("name"));
						share.setSummary(egg_obj.getString("desc"));
						share.setImg_url(egg_obj.getString("icon"));
						share.setRedirect_url(CommonConst.GOV_URL);
						intent_send.putExtra("share", share);
						LoginActivity.this.startActivity(intent_send);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void goActivity() {

		try {

			AppManager.getAppManager().finishActivity(Class.forName("com.haomee.liulian.LoginPageActivity"));

		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
		Intent intent = new Intent();
		if (LiuLianApplication.current_user.getIs_new() == 0) {
			intent.setClass(this, MainActivity.class);
		} else {
			intent.setClass(this, UserInfoActivity.class);
		}
		this.startActivity(intent);
		this.finish();
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
				loadingDialog.dismiss();
				// 进入主页面
				goActivity();
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, final String message) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), "登录失败: " + message, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

}
