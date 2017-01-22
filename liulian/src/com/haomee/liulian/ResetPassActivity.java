package com.haomee.liulian;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haomee.consts.PathConst;
import com.haomee.util.StringUtil;
import com.haomee.view.HintEditText;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ResetPassActivity extends BaseActivity {

	private EditText et_phone, et_code;
	private ImageView iv_phone, iv_code;
	private TextView tv_count_down;
	private Timer timer ;
	private int recLen = 60;
	private RelativeLayout rl_reset_pass, rl_check_code;

	private EditText et_pass, et_pass_over;
	private ImageView iv_submit;
	private String phone;
	private MyTask myTask;
	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpass);
		
		loadingDialog = new LoadingDialog(this);

		rl_check_code = (RelativeLayout) findViewById(R.id.rl_check_code);

		rl_reset_pass = (RelativeLayout) findViewById(R.id.rl_reset_pass);

		et_phone = (EditText) findViewById(R.id.et_phone);

		et_code = (EditText) findViewById(R.id.et_code);
		et_phone.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);
		et_code.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);
		iv_phone = (ImageView) findViewById(R.id.iv_phone);

		iv_code = (ImageView) findViewById(R.id.iv_code);

		tv_count_down = (TextView) findViewById(R.id.tv_count_down);

		et_pass = (EditText) findViewById(R.id.et_pass);

		et_pass_over = (EditText) findViewById(R.id.et_pass_over);

		et_pass.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);
		et_pass_over.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);
		iv_submit = (ImageView) findViewById(R.id.iv_submit);

		iv_phone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				phone = et_phone.getText().toString().trim();
				if (!"".equals(phone)) {
					
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
				} else {
					MyToast.makeText(ResetPassActivity.this, "请输入手机号", 1).show();
				}
			}
		});

		iv_code.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!"".equals(et_code.getText().toString())) {
					
					loadingDialog.show();

					CheckCode(phone, et_code.getText().toString());
				}
			}
		});

		iv_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String pass = et_pass.getText().toString().trim();
				String pass_over = et_pass_over.getText().toString().trim();
				

				if (!"".equals(pass) && !"".equals(pass_over)) {

					if (pass.equals(pass_over)) {
						
						if(pass.length()<6){
							MyToast.makeText(ResetPassActivity.this, "密码不能少于6位", 1).show();
						}else{
							loadingDialog.show();						
							Resetpass(phone, et_pass_over.getText().toString().trim());
						}
						

					} else {

						MyToast.makeText(ResetPassActivity.this, "两次密码输入不一致", 1).show();

					}

				} else {

					MyToast.makeText(ResetPassActivity.this, "密码不能为空", 1).show();
				}

			}
		});

		findViewById(R.id.tv_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

	}
	
	class MyTask extends TimerTask{
		@Override
		public void run(){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					recLen--;
					if (recLen < 0) {
						if (timer != null) {  
							timer.cancel();  
							timer = null;  
				        }  
				        if (myTask != null) {  
				        	myTask.cancel(); 
				        	myTask = null;
				        }   
						tv_count_down.setVisibility(View.GONE);
						iv_phone.setImageDrawable(ResetPassActivity.this.getResources().getDrawable(R.drawable.password_button_seleceted));
						iv_phone.setClickable(true);
					}else{
						tv_count_down.setText("重新发送验证码 " + recLen + "秒");
					}
				}
			});
		}
	};

	//获取验证码
	public void getV_code(String phone) {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("mobile", phone);
		re.put("ak", StringUtil.getMD5Str(StringUtil.getMD5Str(phone)));
		Log.e("地址：", PathConst.URL_RESET_PWD_CODE + "&mobile=" + phone + "&ak=" + StringUtil.getMD5Str(StringUtil.getMD5Str(phone)));
		asyncHttp.get(PathConst.URL_RESET_PWD_CODE, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					loadingDialog.dismiss();
					JSONObject json = new JSONObject(arg0);
					Log.e("返回：", json.toString());
					if (1 == json.optInt("flag")) {
						MyToast.makeText(ResetPassActivity.this, "验证码已发送", 1).show();
						tv_count_down.setVisibility(View.VISIBLE);
						iv_phone.setImageDrawable(ResetPassActivity.this.getResources().getDrawable(R.drawable.password_button_default));
						iv_phone.setClickable(false);
						timer.schedule(myTask, 1000, 1000);
					} else {
						MyToast.makeText(ResetPassActivity.this, json.optString("msg"), 1).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	//校验 验证码
	public void CheckCode(String phone, String code) {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("mobile", phone);
		re.put("code", code);
		re.put("ak", StringUtil.getMD5Str(phone + code));
		Log.e("地址：", PathConst.URL_RESET_CHECK_CODE + "&mobile=" + phone + "&code=" + code + "&ak=" + StringUtil.getMD5Str(phone + code));
		asyncHttp.get(PathConst.URL_RESET_CHECK_CODE, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					
					loadingDialog.dismiss();
					
					JSONObject json = new JSONObject(arg0);

					Log.e("地址：", json.toString());

					if (1 == json.optInt("flag")) {

						rl_check_code.setVisibility(View.GONE);
						rl_reset_pass.setVisibility(View.VISIBLE);

					} else {
						et_code.setText("");
						et_code.setHint(json.optString("msg"));
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	//重置密码
	public void Resetpass(String phone, String pass) {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("mobile", phone);
		re.put("password", StringUtil.getMD5Str(pass));
		Log.e("地址：", PathConst.URL_RESET_PASS + "&mobile=" + phone + "&password=" + StringUtil.getMD5Str(pass));
		asyncHttp.get(PathConst.URL_RESET_PASS, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					loadingDialog.dismiss();
					JSONObject json = new JSONObject(arg0);
					Log.e("地址：", json.toString());
					if (1 == json.optInt("flag")) {

						MyToast.makeText(ResetPassActivity.this, "重置成功", 1).show();

						Intent intent = new Intent();

						intent.setClass(ResetPassActivity.this, LoginActivity.class);

						ResetPassActivity.this.startActivity(intent);

						ResetPassActivity.this.finish();

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		this.finish();
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
