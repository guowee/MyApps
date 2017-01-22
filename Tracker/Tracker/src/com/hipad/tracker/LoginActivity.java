package com.hipad.tracker;

import com.hipad.tracker.json.BindInfoResponse;
import com.hipad.tracker.json.LoginResponse;
import com.hipad.tracker.json.Response;
import com.hipad.tracker.service.ServiceImpl;
import com.hipad.tracker.dialog.BindDialog;
import com.hipad.tracker.dialog.BindDialog.OnBindDialogListener;
import com.hipad.tracker.http.HttpUtil;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoginActivity extends BaseActivity implements OnClickListener{
	private Context mContext;
	private Button login;
	private Button register;
	private TextView forgetTxt;
	private EditText nameTxt;
	private EditText pwdTxt;
	private RelativeLayout viewLayout;

	private LoginHandler loginHandler;
	private BindInfoHandler infoHandler;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mContext=this;
		getViews();
		initDatas();
		setClick();
		if(null != application.user) {
			nameTxt.setText(application.user.getName());
			pwdTxt.requestFocus();
		}
	}
	
	private void initDatas(){
		loginHandler = new LoginHandler();
		infoHandler=new BindInfoHandler();
		Intent intent=getIntent();
		if(intent!=null){
			boolean isSuccess=intent.getBooleanExtra("isSuccess", false);
			if(isSuccess){
				//showNotifyDialog(mContext, getString(R.string.reset_pwd_success));
				showCustomToast(mContext, getString(R.string.reset_pwd_success));
			}
			String str=intent.getStringExtra("mobile");
			if(str!=null){
			String mobile=str.substring(str.indexOf("@")+1);
			nameTxt.setText(mobile);
			}
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		application.removeActivity(this);
		application.finishAll();
		if(ServiceImpl.verifyLocally(mContext)){
			service.logout(mContext,new HttpUtil.ResponseResultHandler<Response>() {
				@Override
				public void handle(boolean timeout, Response response) {
				}
			});
		}
	}
	
	private void getViews()
	{
		viewLayout=(RelativeLayout) findViewById(R.id.viewLayout);
		login=(Button) findViewById(R.id.loginBtn);
		register=(Button) findViewById(R.id.registBtn);
		forgetTxt=(TextView) findViewById(R.id.forget_txt);
		nameTxt=(EditText) findViewById(R.id.usernameTxt);
		pwdTxt=(EditText) findViewById(R.id.passwordTxt);
		pwdTxt.setTypeface(Typeface.DEFAULT);  
		pwdTxt.setTransformationMethod(new PasswordTransformationMethod());  
	}
	
	private void setClick(){
		login.setOnClickListener(this);
		register.setOnClickListener(this);
		forgetTxt.setOnClickListener(this);
		viewLayout.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		switch (v.getId()) {
		case R.id.viewLayout:
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			break;
		case R.id.loginBtn:
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			String phoneNum=nameTxt.getText().toString().trim();
			String password=pwdTxt.getText().toString().trim();
			
			if(phoneNum==null||"".equals(phoneNum)){
				nameTxt.startAnimation(shake);
				return;
			}
			if(password==null||"".equals(password)){
				pwdTxt.startAnimation(shake);
				return;
			}
			service.login(phoneNum, password, loginHandler);
			
			break;
		case R.id.registBtn:
			Intent intent1=new Intent(mContext,RegisterActivity.class);
			intent1.putExtra("isRegister", true);
			startActivity(intent1);
			break;
		case R.id.forget_txt:
			Intent intent2=new Intent(mContext,RegisterActivity.class);
			startActivity(intent2);
			break;
		default:
			break;
		}
	}
	
	private class LoginHandler implements HttpUtil.ResponseResultHandler<LoginResponse>{
		@Override
		public void handle(boolean timeout, LoginResponse response) {
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						ServiceImpl.save(mContext, 7);
						MyApplication.user = response.getData();
						MyApplication.account=response.getAccount();
						MyApplication app = (MyApplication) getApplication();
						app.saveUserInfo();
						service.getBindInfo(response.getAccount(), infoHandler);
					}else{
						showToastShort(getString(R.string.login_failure));
					}
				} else {
					showToastShort(getString(R.string.neterror_hint));
				}
			}else{
				showToastShort(getString(R.string.timeout_hint));
			}
		}
	}
	
	private class BindInfoHandler implements HttpUtil.ResponseResultHandler<BindInfoResponse>{

		@Override
		public void handle(boolean timeout, BindInfoResponse response) {
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						Intent intent=new Intent(mContext,MainActivity.class);
						intent.putExtra("classname", getRunningActivityName());
						MyApplication.imei=response.getImei();
						startActivity(intent);
						finish();
					}else{
						//The current account is not binding device
						if(getString(R.string.no_bind_info).equals(response.getMsg())){
							BindDialog dialog=new BindDialog(mContext, R.style.MyDialog, new OnBindDialogListener(){
								@Override
								public void OnClick(View v) {
									startActivity(new Intent(mContext,SimNumberActivity.class));
								}});
							dialog.show();
							
						}else{
							//showToastShort(response.getMsg());
							execute(mContext, response.getMsg());
						}
					}
				} else {
					showToastShort(getString(R.string.neterror_hint));
				}
			}else{
				showToastShort(getString(R.string.timeout_hint));
			}
		}
		
	}

	
}