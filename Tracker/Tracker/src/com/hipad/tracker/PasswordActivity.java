package com.hipad.tracker;

import com.hipad.tracker.json.CommonResponse;
import com.hipad.tracker.json.RegisterResponse;
import com.hipad.tracker.entity.User;
import com.hipad.tracker.http.HttpUtil;
import com.hipad.tracker.service.Service;
import com.hipad.tracker.service.ServiceImpl;
import com.hipad.tracker.utils.Validation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class PasswordActivity extends BaseActivity implements OnClickListener,TextWatcher{
	private String tag;
	
    private Context mContext;
	private ImageButton backBtn;
	private EditText pwdTxt;
	private EditText repwdTxt;
	private Button nextBtn;
	private LinearLayout pwdLayout;
	private String password;
	
	private String mobile;
	private String code;
	private boolean resetPwd=false;
	
	private Service service;
	
	private RegisterHandler handler;
	private ResetPwdHandler rpHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_layout);
		mContext=this;
		tag = this.getClass().getName();
		initDatas();
		getViews();
		setClick();
	}
	
	private void initDatas(){
		service=new ServiceImpl();
		handler=new RegisterHandler();
		rpHandler=new ResetPwdHandler();
		Intent intent=getIntent();
		mobile=intent.getStringExtra("mobile");
		code=intent.getStringExtra("code");
		resetPwd=intent.getBooleanExtra("resetPwd", false);
		
	}
	private void getViews(){
		pwdLayout=(LinearLayout) findViewById(R.id.password_layout);
		backBtn=(ImageButton) findViewById(R.id.leftBtn);
		pwdTxt=(EditText) findViewById(R.id.pwd_txt);
		repwdTxt=(EditText) findViewById(R.id.repwd_txt);
		nextBtn=(Button) findViewById(R.id.next_btn);
		nextBtn.setEnabled(false);
	}
	
	private void setClick(){
		pwdLayout.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		repwdTxt.addTextChangedListener(this);
	}
	
	@Override
	public void onClick(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		switch (v.getId()) {
		case R.id.password_layout:
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			break;
		case R.id.leftBtn:
            finish();			
			break;
		case R.id.next_btn:
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			String password=pwdTxt.getText().toString().trim();
			String repwd=repwdTxt.getText().toString().trim();
			
			if(!(Validation.isENG_NUM_LTD(password)&&Validation.isENG_NUM_LTD(repwd))){
				//showNotifyDialog(mContext, getString(R.string.pwd_not_regex));
				showCustomToast(mContext, getString(R.string.pwd_not_regex));
				return;
			}
			
			if(validate(password, repwd)){
				User user=new User();
				user.setMobile(mobile);
				user.setPassword(password);
				if(resetPwd){
					service.resetPwd(mobile,password, code, rpHandler);
				}else{
				  service.register(user, code, handler);
				}
			}else{
				//showNotifyDialog(mContext, getString(R.string.pwd_not_match));
				showCustomToast(mContext, getString(R.string.pwd_not_match));
			}
			
			break;
		default:
			break;
		}
	}
	
	private boolean validate(String pwd,String repwd){
		
		if(pwd!=null&&pwd.length()!=0&&repwd!=null&&pwd.equals(repwd)){
			 return true;
		}
		
		return false;
	}
	
	@Override
	public void afterTextChanged(Editable s) {
      String  password=pwdTxt.getText().toString().trim();
       if(password.equals(s.toString())){
    	   nextBtn.setEnabled(true);
    	   nextBtn.setBackgroundResource(R.drawable.title_bg);
       }else{
    	   nextBtn.setEnabled(false);
		   nextBtn.setBackgroundResource(R.drawable.button_non_normal);
       }
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}
	
	private class RegisterHandler implements HttpUtil.ResponseResultHandler<RegisterResponse> {

		@Override
		public void handle(boolean timeout, RegisterResponse response) {
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						ServiceImpl.save(mContext, 7);

						User user = response.getData();
						MyApplication.user = user;
						MyApplication.account=response.getAccount();
						Log.i(tag, "user = " + user.getMobile());
						Log.i(tag, "account = " +MyApplication.account );
						MyApplication app = (MyApplication) getApplication();
						app.saveUserInfo();
						app.saveUserToken();

						Intent intent = new Intent(mContext,SimNumberActivity.class);
						startActivity(intent);

						finish();
					} else {
						showToastShort(getString(R.string.register_failure));
					}
				} else {
					showToastShort(getString(R.string.neterror_hint));
				}
			} else {
				showToastShort(getString(R.string.timeout_hint));
			}

		}
	}
	
	private class ResetPwdHandler implements HttpUtil.ResponseResultHandler<CommonResponse>{

		@Override
		public void handle(boolean timeout, CommonResponse response) {
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						Intent intent=new Intent(mContext, LoginActivity.class);
						intent.putExtra("isSuccess",true);
						intent.putExtra("mobile", mobile);
						startActivity(intent);
						finish();
					} else {
						showToastShort(getString(R.string.reset_failure));
					}
				} else {
					showToastShort(getString(R.string.neterror_hint));
				}
			} else {
				showToastShort(getString(R.string.timeout_hint));
			}

			
		}
		
	}
	
}
