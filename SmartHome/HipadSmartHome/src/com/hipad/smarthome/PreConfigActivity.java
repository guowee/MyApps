/**
 * $(#)PreConfigActivity.java 2015Äê3ÔÂ24ÈÕ
 */
package com.hipad.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @author wangbaoming
 *
 */
public class PreConfigActivity extends BaseActivity {

	private ImageButton mImgbtnBack;	
	private TextView mTvTitle;

	private Button mBtnOk;	

	private PreConfigActivity mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preconfig);
		mContext = this;
		
		getView();
		setOnClickListener();
		
		init();
	}
	
	private void getView(){
		mImgbtnBack = (ImageButton) findViewById(R.id.leftBtn);
		mTvTitle = (TextView) findViewById(R.id.titleTxt);
		
		mBtnOk = (Button) findViewById(R.id.preconfig_btn_ok);
	}	

	private void setOnClickListener(){
		mImgbtnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mBtnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, ScanDeviceActivity.class));
			}
		});
	}
	
	private void init(){
		mTvTitle.setText(R.string.title_add_device);
	}
}
