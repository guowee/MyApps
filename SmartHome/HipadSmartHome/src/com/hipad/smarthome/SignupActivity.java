package com.hipad.smarthome;

import java.util.regex.Pattern;

import android.R.bool;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hipad.smart.http.HttpUtil;
import com.hipad.smart.json.LoginResponse;
import com.hipad.smart.json.RegisterResponse;
import com.hipad.smart.service.Service;
import com.hipad.smart.service.ServiceImpl;
import com.hipad.smart.user.User;
import com.hipad.smarthome.utils.SlideSwitch;
import com.hipad.smarthome.utils.SlideSwitch.OnChangedListener;

public class SignupActivity extends BaseActivity implements OnClickListener{

	private Context context;
	private EditText nameEdit, passEdit, emailEdit;
	private String name, email;

	private RelativeLayout viewLayout,switchBtn;
	private ImageButton signupBtn;

	private SlideSwitch slideSwitch;
	
	private SignupHandler signupHandler;
	
	private boolean isOpen = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_layout);

		context = this;

		viewLayout = (RelativeLayout) findViewById(R.id.viewLayout);
		viewLayout.setOnClickListener(this);
		
		nameEdit = (EditText) findViewById(R.id.nameEdit);
		passEdit = (EditText) findViewById(R.id.passEdit);
		
		emailEdit = (EditText) findViewById(R.id.emailEdit);

		switchBtn = (RelativeLayout)findViewById(R.id.switchBtn);
		slideSwitch = new SlideSwitch(context, R.drawable.switch_on_img_blue, R.drawable.switch_off_img, R.drawable.switch_slidebtn);
//		slideSwitch.setChecked(isOpen);
		slideSwitch.setOnChangedListener(new OnChangedListener() {
			@Override
			public void OnChanged(SlideSwitch slideSwitch, boolean checkState) {
				String text = passEdit.getText().toString();
				if(checkState){
					passEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					passEdit.setSelection(text.length());
				}else{
					passEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					passEdit.setSelection(text.length());
				}
				isOpen = checkState;
			}
		});
		slideSwitch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(isOpen){
					isOpen = false;
				}else{
					isOpen = true;
				}
				slideSwitch.setChecked(isOpen);
			}
		});
		
		switchBtn.addView(slideSwitch);
		
		signupHandler = new SignupHandler();
		
		signupBtn = (ImageButton) findViewById(R.id.signupBtn);
		signupBtn.setOnClickListener(this);
	}
	
	/**
	 * calc the str width, digit and alphabet is 1, Chinese character is 2. Only Chinese character, digit and alphabet is allowed, or -1. 
	 * @param str 
	 * 			the string
	 * @return
	 * 			the str width
	 */
	private int getWidth(String str){
		if(null == str) return 0;
		if(!str.matches("[\u4e00-\u9fa5\\w]+")) return -1;
		
		int len = 0;
		for (int i = 0; i < str.length(); i++) {
			if(Pattern.matches("[\u4e00-\u9fa5]", str.subSequence(i, i + 1))) { // Chinese character
				len += 2;
				continue;
			}
			if(Pattern.matches("[\\w]", str.subSequence(i, i + 1))) { // alphabet and digit
				len += 1;
				continue;
			}
		}
		return len;
	}
	
	@Override
	public void onClick(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		int keyId = view.getId();
		switch (keyId) {
		case R.id.viewLayout:
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			break;
		case R.id.signupBtn:
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			name = nameEdit.getText().toString();
			email = emailEdit.getText().toString();
			String passStr = passEdit.getText().toString();
			
			showInfoLog("nameStr.length() = "+name.length());
			if (name.isEmpty() || !name.matches("[\u4e00-\u9fa5\\w]+")) {
				showToastShort(getString(R.string.sign_name_illegal));
				return;
			}else{
				int width = getWidth(name);
				if(width < 4 || width > 16){
					showToastShort(getString(R.string.sign_name_length));
					return;
				}
			}
			

			if (email.isEmpty() || !email.matches("^\\w+@\\w+\\.(com|cn)")) {
				showToastShort(getString(R.string.sign_email_illegal));
				return;
			}

			if (!passStr.isEmpty()) {
				if (passStr.length() < 8 || passStr.length() > 16){
					showToastShort(getString(R.string.sign_pass_lenght));
					return;
				}
			}else{
				showToastShort(getString(R.string.sign_pass_null));
				return;
			}
			
			dialogShow(context, null);
			User user = new User();
			user.setName(name);
			user.setPassword(passStr);
			user.setEmail(email);
			service.register(user, signupHandler);
			
			break;
		}

	}
	
	private class SignupHandler implements HttpUtil.ResponseResultHandler<RegisterResponse>{
		@Override
		public void handle(boolean timeout,	RegisterResponse response) {
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
//						showToastShort("×¢²á³É¹¦");
						ServiceImpl.save(context, 7);
						
						showToastShort(response.getMsg());
						User user = response.getData();
						MyApplication.user = user;
						Log.i(tag, "user = " + user.getName());

						MyApplication app = (MyApplication) getApplication();
						app.saveUserInfo();
						
						//startActivity(new Intent(context,MainActivity.class));

						Intent intent = new Intent(SignupActivity.this,UserInfoActivity.class);
						startActivity(intent);
						finish();
					}else{
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
