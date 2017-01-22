package com.hipad.tracker;

import com.hipad.tracker.http.HttpUtil;
import com.hipad.tracker.json.CodeResponse;
import com.hipad.tracker.service.Service;
import com.hipad.tracker.service.ServiceImpl;
import com.hipad.tracker.utils.MD5Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RegisterActivity extends BaseActivity implements OnClickListener,TextWatcher,OnCheckedChangeListener{

	private Context mContext;
	private TimeCount timer;

	private ImageButton backBtn;
	private TextView titleTxt;
    private LinearLayout codeLayout;
	
	private EditText zoneNum;
	private EditText phoneNum;
	private EditText codeTxt;
	private Button codeBtn;
	private Button nextBtn;
	private CheckBox checkBox;
	private TextView userAgreement;
	
	private Service service;
	private VerifyHandler handler;
	
	private String codeResult;
	
	private boolean isRegister=false;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.code_layout);
		mContext=this;
		getViews();
		initDatas();
		setClick();
	}

	private void getViews() {
		backBtn = (ImageButton) findViewById(R.id.leftBtn);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
        codeLayout=(LinearLayout) findViewById(R.id.code_layout);
		zoneNum = (EditText) findViewById(R.id.zoneNumber);
		phoneNum = (EditText) findViewById(R.id.phoneNumber);
		codeTxt = (EditText) findViewById(R.id.codeNumber);
		codeBtn = (Button) findViewById(R.id.codeBtn);
		nextBtn = (Button) findViewById(R.id.c_next);
		nextBtn.setEnabled(false);
		checkBox = (CheckBox) findViewById(R.id.check_box);
		userAgreement = (TextView) findViewById(R.id.user_agreement);
	}

	private void initDatas() {

		timer = new TimeCount(60000, 1000);
		Intent intent = getIntent();
	    isRegister = intent.getBooleanExtra("isRegister", false);
		if (isRegister) {
			titleTxt.setText(getString(R.string.welcome));
		} else {
			titleTxt.setText(getString(R.string.reset_password));
		}
		
		String str = getString(R.string.user_agreement);
		SpannableString spanStr = new SpannableString(str);
		ClickableSpan clickSpan = new MyClickSpan(str);

		spanStr.setSpan(clickSpan, 12, str.length(),Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		userAgreement.setText(spanStr);
		userAgreement.setMovementMethod(LinkMovementMethod.getInstance());
		
		service=new ServiceImpl();
		handler=new VerifyHandler();
		
	}

	private void setClick() {
		codeLayout.setOnClickListener(this);
		codeBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		codeTxt.addTextChangedListener(this);
		checkBox.setOnCheckedChangeListener(this);
		
	}

	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			codeBtn.setText("Resend");
			codeBtn.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			codeBtn.setClickable(false);
			codeBtn.setText(millisUntilFinished / 1000 + "s");
		}
	}

	class MyClickSpan extends ClickableSpan {
		private String text;
		public MyClickSpan(String text) {
			this.text = text;
		}
		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setColor(Color.rgb(0, 204, 203)); // 设置链接的文本颜色
			ds.setUnderlineText(false); // 去掉下划线
		}
		@Override
		public void onClick(View widget) {
			startActivity(new Intent(mContext,AgreementActivity.class));
		}

	}

	@Override
	public void onClick(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		switch (v.getId()) {
		case R.id.code_layout:
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			break;
		case R.id.leftBtn:
			finish();
			break;
		case R.id.c_next:{
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			
			if(validate()){
				String mobile=getMobileNumber();
				String code=codeTxt.getText().toString().trim();
				if(verifyCode(code, codeResult)){
					Intent intent=new Intent(mContext,PasswordActivity.class);
					if(!isRegister){
						intent.putExtra("resetPwd",true);
					}
					intent.putExtra("mobile", mobile);
					intent.putExtra("code", code);
					startActivity(intent);
				}else{
					//showNotifyDialog(mContext, getString(R.string.error_code));
					showCustomToast(mContext, getString(R.string.error_code));
				}
			}else{
				//showNotifyDialog(mContext, getString(R.string.enter_correct_number));
				showCustomToast(mContext, getString(R.string.enter_correct_number));
			}
			break;}
		case R.id.codeBtn:{
			if(validate()){
				timer.start();// 开始计时
				String mobile=getMobileNumber();
				service.verify(mobile, handler);
			}else{
				//showNotifyDialog(mContext, getString(R.string.enter_correct_number));
				showCustomToast(mContext, getString(R.string.enter_correct_number));
			}
			
			break;}
		default:
			break;
		}

	}
	
	private boolean validate(){
		String zone=zoneNum.getText().toString().trim();
		if(zone==null||"".equals(zone)){
			zoneNum.startAnimation(shake);
			return false;
		}
		String number=phoneNum.getText().toString().trim();
		if(number==null||"".equals(number)){
			phoneNum.startAnimation(shake);
			return false;
		}
		return true;
	}
	
	
	private boolean verifyCode(String code,String codeResult){
		String strSrc = code.toLowerCase()+"g21jh#na6c5";
        String strEncode = MD5Util.encoding(strSrc).toLowerCase(); 
        if(codeResult!=null&&strEncode!=null&&codeResult.equals(strEncode)){
        	return true;
        }
		return false;
	}
	
	
	private String getMobileNumber(){
		StringBuffer sb=new StringBuffer();
		String zone=zoneNum.getText().toString().trim();
		
		sb.append(zone).append("@");
		String number=phoneNum.getText().toString().trim();
		return  sb.append(number).toString();
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		if(s.length()>=4&&checkBox.isChecked()){
			nextBtn.setEnabled(true);
			nextBtn.setBackgroundResource(R.drawable.title_bg);
		}else{
			nextBtn.setEnabled(false);
			nextBtn.setBackgroundResource(R.drawable.button_non_normal);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		String code=codeTxt.getText().toString();
		if(code.length()>=4&&checkBox.isChecked()){
			nextBtn.setEnabled(true);
			nextBtn.setBackgroundResource(R.drawable.title_bg);
		}else{
			nextBtn.setEnabled(false);
			nextBtn.setBackgroundResource(R.drawable.button_non_normal);
			
		}
	}

	private class VerifyHandler implements HttpUtil.ResponseResultHandler<CodeResponse>{

		@Override
		public void handle(boolean timeout, CodeResponse response) {
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						codeResult=response.getCode();
					} else {
						showToastShort(response.getMsg());
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
