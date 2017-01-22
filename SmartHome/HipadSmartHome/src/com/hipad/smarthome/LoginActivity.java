package com.hipad.smarthome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hipad.smart.http.HttpUtil;
import com.hipad.smart.json.LoginResponse;
import com.hipad.smart.json.Response;
import com.hipad.smart.service.Service;
import com.hipad.smart.service.ServiceImpl;
import com.hipad.smarthome.receiver.BootReceiver;
import com.hipad.smarthome.receiver.DrinkAlarmReceiver;
import com.hipad.smarthome.utils.DeviceListCache;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private LoginActivity context;
	private EditText nameEdit, passEdit;

	private ImageButton loginBtn, SignupBtn;

	private TextView forgetTxt, signupTxt;

	private RelativeLayout viewLayout;

	private LoginHandler loginHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		context = this;
		
//		dialogShow(this, null);
		
		viewLayout = (RelativeLayout) findViewById(R.id.viewLayout);
		viewLayout.setOnClickListener(this);

		nameEdit = (EditText) findViewById(R.id.nameEdit);
		passEdit = (EditText) findViewById(R.id.passEdit);

		loginBtn = (ImageButton) findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(this);

		forgetTxt = (TextView) findViewById(R.id.forgotTxt);
//		forgetTxt.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		forgetTxt.setOnClickListener(this);

		signupTxt = (TextView) findViewById(R.id.signTxt);
//		signupTxt.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		signupTxt.setOnClickListener(this);
		
		loginHandler = new LoginHandler();
		
		if(null != application.user) {
			nameEdit.setText(application.user.getName());
			passEdit.requestFocus();
		}
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// when login page occurs, finish the others all. 
		application.removeActivity(this);
		application.finishAll();
		
		if(ServiceImpl.verifyLocally(getApplicationContext())) { // re-login or logout
			DrinkAlarmReceiver.stopDrinkAlarm(context);
			BootReceiver.stopAllAppoint(context, MyApplication.user.getUserId());
			
			service.logout(context,	new HttpUtil.ResponseResultHandler<Response>() {

				@Override
				public void handle(boolean timeout, Response response) {
				}
			});
			
			DeamonService.setRegistered(false);
		}
	}

	@Override
	public void onClick(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		int keyId = view.getId();
		switch (keyId) {
		case R.id.viewLayout:
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			break;
		case R.id.loginBtn:
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			String nameStr = nameEdit.getText().toString();
			String passStr = passEdit.getText().toString();

			if (nameStr.isEmpty()) {
				showToastShort(getString(R.string.login_name_null));
				return;
			}else{
//				if(!nameStr.matches("^\\w+@\\w+\\.(com|cn)")){
//					showToastShort(getString(R.string.login_name_email_error));
//					return;
//				}
			}

			if (passStr.isEmpty()) {
				showToastShort(getString(R.string.login_pass_null));
				return;
			}else {
//				startActivity(new Intent(context, MainActivity.class));
//				finish();
			}

			dialogShow(context, null);
			service.login(nameStr, passStr, loginHandler);
			
			break;
		case R.id.signTxt:
			startActivity(new Intent(context, SignupActivity.class));
			break;
		case R.id.forgotTxt:

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
//						showToastShort("µÇÂ½³É¹¦");
						ServiceImpl.save(context, 7);
						
						showToastShort(response.getMsg());
						MyApplication.user = response.getData();
						showInfoLog("user = " + MyApplication.user.getName());
						
						MyApplication app = (MyApplication) getApplication();
						app.saveUserInfo();						

						DeviceListCache.getInstance().syncDeviceList();
						
						startActivity(new Intent(context,MainActivity.class));
						finish();
					}else{
//						showToastShort(getString(R.string.login_pass_error));
						showToastShort(response.getMsg());
					}
				} else {
//					showToastShort(response.getMsg());
					showToastShort(getString(R.string.neterror_hint));
				}
			}else{
				showToastShort(getString(R.string.timeout_hint));
			}
			dialogDismiss();
		}
	}
}
