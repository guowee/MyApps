package com.haomee.liulian;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.haomee.consts.PathConst;
import com.haomee.util.StringUtil;
import com.haomee.view.HintEditText;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RegActivity extends BaseActivity {

	private EditText et_phone, et_pass;
	//private ImageView iv_submit;
	private TextView tv_find_pas;
	private ImageView tv_back;
	private TextView tv_user_portal;
	private TextView tv_user_privacy;
	private View bt_next;
	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg);
		
		loadingDialog  = new LoadingDialog(this);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_phone.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);
		et_pass = (EditText) findViewById(R.id.et_pass);
		et_pass.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);
		//iv_submit = (ImageView) findViewById(R.id.iv_submit);
		bt_next = this.findViewById(R.id.bt_next);
		tv_find_pas = (TextView) findViewById(R.id.tv_find_pass);
		tv_user_portal = (TextView) findViewById(R.id.tv_user_portal);
		tv_user_privacy = (TextView) findViewById(R.id.tv_user_privacy);

		tv_find_pas.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_user_portal.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_user_privacy.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		tv_back = (ImageView) findViewById(R.id.tv_back);
		tv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		tv_user_portal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(RegActivity.this, WebPageActivity.class);
				intent.putExtra("title", "用户协议");
				intent.putExtra("url", PathConst.URL_USER_PORTAL);
				RegActivity.this.startActivity(intent);
			}
		});

		tv_user_privacy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(RegActivity.this, WebPageActivity.class);
				intent.putExtra("title", "隐私政策");
				intent.putExtra("url", PathConst.URL_USER_PRIVACY);
				RegActivity.this.startActivity(intent);
			}
		});

		bt_next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phone = et_phone.getText().toString().trim();
				String password = et_pass.getText().toString().trim();
				if("".equals(phone)){
					MyToast.makeText(RegActivity.this, "请输入手机号", 1).show();
					return;
				}
				if("".equals(password)){
					MyToast.makeText(RegActivity.this, "请输入登录密码", 1).show();
					return;
				}
				if(password.length()<6){
					MyToast.makeText(RegActivity.this, "密码不能少于6位", 1).show();
					return;
				}
				
				if (isMobileNum(et_phone.getText().toString())) {
					
					loadingDialog.show();
					getV_code(et_phone.getText().toString().trim());

				} else {
					MyToast.makeText(RegActivity.this, "手机格式不正确", 1).show();
				}

			}
		});
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
						Intent intent = new Intent();
						intent.putExtra("phone", et_phone.getText().toString().trim());
						intent.putExtra("pass", et_pass.getText().toString().trim());
						intent.setClass(RegActivity.this, VerificationCodeActivity.class);
						RegActivity.this.startActivity(intent);
						MyToast.makeText(RegActivity.this, "验证码已发送", 1).show();

					} else {

						MyToast.makeText(RegActivity.this, json.optString("msg"), 1).show();

					}

				} catch (JSONException e) {
					e.printStackTrace();
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

}
