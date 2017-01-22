package com.hipad.tracker;


import com.hipad.tracker.dialog.SecurityDialog;
import com.hipad.tracker.dialog.SecurityDialog.onSecurDialogListener;
import com.hipad.tracker.http.HttpUtil;
import com.hipad.tracker.json.CommonResponse;
import com.hipad.tracker.widget.WiperSwitch;
import com.hipad.tracker.widget.WiperSwitch.OnToggleStateChangeListener;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 
 * @author guowei
 *
 */
public class SecurityNumberActivity extends BaseActivity implements
		OnClickListener,OnToggleStateChangeListener {
	private ImageButton leftBtn;
	private TextView titleText;

	private RelativeLayout securityLayout;

	private WiperSwitch switchToggle;
	private TextView securityNumber, transferTxt, securityTxt;
	private Context mContext;
	
	private Dialog mDialog;

	private static int count = 0;
	private long firClick, secClick;
	private static String bizSn;
	
	
	
	private boolean isCancel=false;
	
	private String spn2Number;
	private static final int MSG_SPN=0x01;
	private SpnHandler sHandler;
	private SpnResultHandler srHandler;

	@SuppressLint("HandlerLeak")
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case MSG_SPN:
				service.spn2Result(MyApplication.account,MyApplication.imei, bizSn, srHandler);
				break;
			default:
				break;
			}
			
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.security_number_layout);
		mContext = this;
		getViews();
		initWidget();
		setClickListener();
	}
	
	private void getViews() {
		leftBtn = (ImageButton) findViewById(R.id.leftBtn);
		leftBtn.setVisibility(View.VISIBLE);

		titleText = (TextView) findViewById(R.id.titleTxt);
		titleText.setText(getString(R.string.security_number));

		securityLayout = (RelativeLayout) findViewById(R.id.security_layout);
		switchToggle=(WiperSwitch) findViewById(R.id.switch_toggle);
		securityNumber = (TextView) findViewById(R.id.sim_number);
		transferTxt = (TextView) findViewById(R.id.transfer);
		securityTxt = (TextView) findViewById(R.id.security);
		
		sHandler=new SpnHandler();
		srHandler=new SpnResultHandler();
		
	}
	
	private void initWidget(){
		String spn2=sph.getString("info_spn2");
		if(spn2!=null&&spn2.length()>0){
			securityNumber.setText(spn2);
			switchToggle.setToggleState(true);
			changeUiOn();
		}else{
			securityNumber.setText("");
			switchToggle.setToggleState(false);
			changeUiOff();
		}
	}
	
	private void initViews(){
		boolean isSpn=sph.getBoolean("isSpn");
		String spn2Str=sph.getString("spn2");
		String name=sph.getString("user");
		
		if(MyApplication.user.getName().equals(name)){
			securityNumber.setText(spn2Str!=null?spn2Str:sph.getString("info_spn2"));
			if(isSpn){
				switchToggle.setToggleState(true);
				changeUiOn();
			}else{
				switchToggle.setToggleState(false);
				changeUiOff();
			}
			
		}else{
			switchToggle.setToggleState(false);
			changeUiOff();
		}
		
	}
	
	
	private void setClickListener() {
		leftBtn.setOnClickListener(this);
		//securityLayout.setOnClickListener(this);
		switchToggle.setOnToggleStateChangeListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
		case R.id.security_layout:
			break;
		default:
			break;
		}

	}

	private void changeUiOff() {
		transferTxt.setTextColor(Color.rgb(196, 196, 196));
		securityTxt.setTextColor(Color.rgb(196, 196, 196));
		securityNumber.setTextColor(Color.rgb(196, 196, 196));
		securityLayout.setOnClickListener(null);
	}

	private void changeUiOn() {
		transferTxt.setTextColor(Color.rgb(0, 0, 0));
		securityTxt.setTextColor(Color.rgb(0, 0, 0));
		securityNumber.setTextColor(Color.rgb(139, 139, 122));
		securityLayout.setOnClickListener(this);
	}

	/*@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (MotionEvent.ACTION_DOWN == event.getAction()) {
			count++;
			if (count == 1) {
				firClick = System.currentTimeMillis();

			} else if (count == 2) {
				secClick = System.currentTimeMillis();
				if (secClick - firClick < 1000) {
					
					mDialog=new SecurityDialog(mContext, R.style.MyDialog, new onSecurDialogListener() {
						
						@Override
						public void onSetNumberClick(String number) {
							securityNumber.setText(number);
							service.spn2(MyApplication.account, MyApplication.imei, number, sHandler);
						}
					});
					mDialog.show();
				}
				count = 0;
				firClick = 0;
				secClick = 0;

			}
		}
		return true;
	}*/

	@Override
	public void onToggleStateChange(boolean flag) {
		if (flag) {
            mDialog=new SecurityDialog(mContext, R.style.MyDialog, new onSecurDialogListener() {
				
				@Override
				public void onSetNumberClick(String number) {
					spn2Number=number;
					if(spn2Number==null||spn2Number.length()==0){
						//showNotifyDialog(mContext, getString(R.string.number_not_null));
						showCustomToast(mContext, getString(R.string.number_not_null));
						switchToggle.setToggleState(false);
						//showToastShort(getString(R.string.number_not_null));
						return ;
					}
					
					if(spn2Number!=null){ 
				    securityNumber.setText(spn2Number);
					service.spn2(MyApplication.account, MyApplication.imei, spn2Number, sHandler);
					}
				}

				@Override
				public void onCancelClick(boolean flag) {
					isCancel=flag;
					if(isCancel){
						switchToggle.setToggleState(false);
						changeUiOff();
					}
				}
			});
			mDialog.show();
			changeUiOn();
		} else {
			changeUiOff();
			service.spn2(MyApplication.account,MyApplication.imei, null, sHandler);
		
		}
	}

	private class SpnHandler implements HttpUtil.ResponseResultHandler<CommonResponse>{

		@Override
		public void handle(boolean timeout, CommonResponse response) {

			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						
						if(getString(R.string.configSpn2_request_complete).equals(response.getMsg())){
							bizSn=response.getBizSN();
							service.spn2Result(MyApplication.account,MyApplication.imei,response.getBizSN(), srHandler);
						}
						
						if(getString(R.string.configSpn2_success).equals(response.getMsg())){
							showToastShort(getString(R.string.set_spn_success));
						}
						
					}else{
						//showToastShort(response.getMsg());
						execute(mContext, response.getMsg());
					}
				} else {
					showToastShort(getString(R.string.neterror_hint));
				}
			}else{
				showToastShort(getString(R.string.timeout_hint));
			}
		}
	}
	
	private class SpnResultHandler implements HttpUtil.ResponseResultHandler<CommonResponse>{

		@Override
		public void handle(boolean timeout, CommonResponse response) {
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						if(getString(R.string.spn2Result_request_complete).equals(response.getMsg())){
							mHandler.sendEmptyMessageDelayed(MSG_SPN, 3000);
						}
						if(getString(R.string.spn2Result_success).equals(response.getMsg())){
							sph.putString("spn2", spn2Number);
							sph.putString("user",MyApplication.user.getName());
							if("NULL".equals(response.getSpn2())){
								//close 
								//showNotifyDialog(mContext,getString(R.string.close_spn_success));
								showCustomToast(mContext, getString(R.string.close_spn_success));
								sph.putBoolean("isSpn",false);
							}else{
								//showNotifyDialog(mContext,getString(R.string.set_spn_success));
								showCustomToast(mContext, getString(R.string.set_spn_success));
								sph.putBoolean("isSpn", true);
							}
						}
					}else{
						//showToastShort(response.getMsg());
						execute(mContext, response.getMsg());
					}
				} else {
					showToastShort(getString(R.string.neterror_hint));
				}
			}else{
				showToastShort(getString(R.string.timeout_hint));
			}
		}
	}
	
	
	
	@Override
	protected void onDestroy() {
        mHandler.removeMessages(MSG_SPN);
		super.onDestroy();
	}
	
}
