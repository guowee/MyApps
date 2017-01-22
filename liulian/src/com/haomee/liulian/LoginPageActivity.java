package com.haomee.liulian;

import org.ice4j.StunMessageEvent;
import org.ice4j.stack.RequestListener;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.util.NetUtils;
import com.haomee.chat.domain.User;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.ShareContent;
import com.haomee.entity.Users;
import com.haomee.liulian.LoginPageActivity.AuthListener;
import com.haomee.util.StringUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class LoginPageActivity extends BaseActivity {

	private Button bt_reg, bt_login, bt_sina, bt_zone;
	private ImageView tv_default;
	public static Tencent mTencent;
	private WeiboAuth mWeiboAuth;
	private SsoHandler mSsoHandler;
	private Oauth2AccessToken mAccessToken;
	public static final int resultCode_login_success = 3000;
	private SharedPreferences preference;
	SharedPreferences.Editor editor = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		preference = this.getSharedPreferences(CommonConst.PREFERENCES_MEDAL, Activity.MODE_PRIVATE);
		editor = preference.edit();
		setContentView(R.layout.activity_login_page);

		bt_reg = (Button) findViewById(R.id.bt_reg);
		bt_login = (Button) findViewById(R.id.bt_login);
		bt_sina = (Button) findViewById(R.id.bt_sina);
		bt_zone = (Button) findViewById(R.id.bt_zone);

		tv_default = (ImageView) findViewById(R.id.tv_default);

		bt_reg.setOnClickListener(myOnClickListener);
		bt_login.setOnClickListener(myOnClickListener);
		bt_sina.setOnClickListener(myOnClickListener);
		bt_zone.setOnClickListener(myOnClickListener);

		tv_default.setOnClickListener(myOnClickListener);

	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			Intent intent = new Intent();

			switch (v.getId()) {

			case R.id.bt_reg:

				if (NetUtils.hasDataConnection(LoginPageActivity.this)) {

					intent.setClass(LoginPageActivity.this, RegActivity.class);

					LoginPageActivity.this.startActivity(intent);

				} else {

					Toast.makeText(LoginPageActivity.this, "请检查网络设置", 1).show();

				}

				break;

			case R.id.bt_login:

				if (NetUtils.hasDataConnection(LoginPageActivity.this)) {

					intent.setClass(LoginPageActivity.this, LoginActivity.class);

					LoginPageActivity.this.startActivity(intent);

				} else {

					Toast.makeText(LoginPageActivity.this, "请检查网络设置", 1).show();

				}

				break;

			case R.id.bt_sina:

				mWeiboAuth = new WeiboAuth(LoginPageActivity.this, CommonConst.WB_APP_KEY, CommonConst.WB_REDIRECT, CommonConst.WB_SCOPE);
				mSsoHandler = new SsoHandler(LoginPageActivity.this, mWeiboAuth);
				mSsoHandler.authorize(new AuthListener());
				break;

			case R.id.bt_zone:

				if (NetUtils.hasDataConnection(LoginPageActivity.this)) {

					// QQ登陆
					if (mTencent == null) {
						mTencent = Tencent.createInstance(CommonConst.QQ_APP_ID, LoginPageActivity.this);
					} else {
						mTencent.logout(LoginPageActivity.this);
					}
					mTencent.login(LoginPageActivity.this, "all", loginListener);

				} else {

					Toast.makeText(LoginPageActivity.this, "请检查网络设置", 1).show();

				}
				break;

			case R.id.tv_default:

				if (NetUtils.hasDataConnection(LoginPageActivity.this)) {

					if (LiuLianApplication.current_user == null) {

						intent.setClass(LoginPageActivity.this, MainActivity.class);

					} else {

						intent.setClass(LoginPageActivity.this, UserInfoActivity.class);

					}
					LoginPageActivity.this.startActivity(intent);
					LoginPageActivity.this.finish();
				} else {

					Toast.makeText(LoginPageActivity.this, "请检查网络设置", 1).show();

				}
				break;
			default:
				break;
			}
		}
	};

	private void initOpenidAndToken(JSONObject jsonObject) {
		try {
			String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
			String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
			String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
			if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
				mTencent.setAccessToken(token, expires);
				mTencent.setOpenId(openId);
			}
		} catch (Exception e) {
		}
	}

	IUiListener loginListener = new BaseUiListener() {
		@Override
		protected void doComplete(JSONObject values) {
			// Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" +
			// SystemClock.elapsedRealtime());
			initOpenidAndToken(values);
			UserInfo mInfo = new UserInfo(LoginPageActivity.this, mTencent.getQQToken());
			mInfo.getUserInfo(new IUiListener() {
				@Override
				public void onError(UiError arg0) {
					// progressDialog.dismiss();
				}

				@Override
				public void onComplete(Object response) {
					try {
						JSONObject json = (JSONObject) response;
						// Log.i("test", json.toString());
						Users user = new Users();
						user.setName(json.getString("nickname"));
						// user.setImage(json.getString("figureurl_qq_2"));
						user.setQq_id(mTencent.getOpenId());
						user.setSex(json.getString("gender").equals("男") ? 1 : 0);
						user.setSign("");

						afterLogin(user);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onCancel() {
					// progressDialog.dismiss();
				}
			});

		}
	};

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {
		}

		@Override
		public void onError(UiError e) {
		}

		@Override
		public void onCancel() {

			// progressDialog.dismiss();

		}
	}

	// qq 登录成功之后
	public void afterLogin(final Users user) {

		if (user != null) {
			int pf_type = 1;
			if (user.getSina_id() != null) {
				pf_type = 2;
			}
			String pf_id = user.getQq_id();
			if (pf_id == null) {
				pf_id = user.getSina_id();
			}
			String url = PathConst.URL_PF_LOGIN + "&pf_type=" + pf_type + "&pf_id=" + pf_id + "&ak=" + StringUtil.getMD5Str(pf_type + pf_id);

			Log.e("请求地址", url + "");

			AsyncHttpClient asyncHttp = new AsyncHttpClient();
			asyncHttp.get(url, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					try {
						Users user = new Users();
						JSONObject json = new JSONObject(arg0);
						Log.e("返回数据：", json.toString());
						if (1 == json.optInt("flag")) {
							JSONObject json_user = json.getJSONObject("user");
							user.setUid(json_user.optString("id"));
							user.setName(json_user.optString("username"));
							user.setImage(json_user.optString("head_pic"));
							user.setSex(json_user.optInt("sex"));
							user.setPhone(json_user.optString("mobile"));
							user.setAccesskey(json_user.optString("accesskey"));
							user.setHx_username(json_user.optString("hx_username"));
							user.setHx_password(json_user.optString("hx_password"));
							user.setBirthday(json_user.optString("birthday"));
							user.setStar(json_user.optString("star"));
							user.setIs_new(json.optInt("is_new"));
							user.setBack_pic(json_user.optString("back_pic"));
							user.setUser_level_icon(json_user.optString("user_level_icon"));
							user.setUser_level(json_user.optString("user_level"));
							LiuLianApplication.current_user = user;
							LiuLianApplication.getInstance().saveLoginedUser();
							login_hx();
						} else {
							String msg = json.optString("msg");
							Toast.makeText(LoginPageActivity.this, "登录失败：" + msg, 1).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}

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
						intent_send.setClass(LoginPageActivity.this, ShareMedalActivity.class);
						ShareContent share = new ShareContent();
						share.setId(egg_obj.getString("id"));
						share.setTitle(egg_obj.getString("name"));
						share.setSummary(egg_obj.getString("desc"));
						share.setImg_url(egg_obj.getString("icon"));
						share.setRedirect_url(CommonConst.GOV_URL);
						intent_send.putExtra("share", share);
						LoginPageActivity.this.startActivity(intent_send);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void goActivity() {
		Intent intent = new Intent();
		if (LiuLianApplication.current_user.getIs_new() == 1) {
			intent.setClass(this, UserInfoActivity.class);
		} else {
			intent.setClass(this, MainActivity.class);
		}
		this.startActivity(intent);
		this.finish();
	}

	class AuthListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {
				UsersAPI mUsersAPI = new UsersAPI(mAccessToken); // 获取用户信息接口
				if (mAccessToken != null) {
					long uid = Long.parseLong(mAccessToken.getUid());
					//mUsersAPI.show(uid, mListener); // 调用RequestListener
				}

			} else {
				String code = values.getString("code");
				String message = "failed";
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nObtained the code: " + code;
				}
				Toast.makeText(LoginPageActivity.this, message, 1).show();
			}
		}

		@Override
		public void onCancel() {

		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(LoginPageActivity.this, "登陆失败 : " + e.getMessage(), 1).show();
		}
	}

	/**
	 * 微博最终登陆成功，获取User信息
	 * 
	 */
	private RequestListener mListener = new RequestListener() {
	/*	@Override
		public void onComplete(String response) {
			if (response != null && !"".equals(response)) {
				// 调用 User#parse 将JSON串解析成User对象
				final User user_sina = User.parse(response);
				Users user = new Users();
				user.setSina_id(user_sina.id);
				user.setName(user_sina.name);
				user.setImage(user_sina.avatar_large);
				user.setSex(user_sina.gender.equals("m") ? 1 : 0);
				user.setSign(user_sina.description);
				afterLogin(user);
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {

			Toast.makeText(LoginPageActivity.this, "登陆失败：" + e.getMessage(), 1).show();
		}
*/
		@Override
		public void processRequest(StunMessageEvent arg0) throws IllegalArgumentException {
			// TODO Auto-generated method stub
			
		}

	};

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
				goActivity();
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
}
