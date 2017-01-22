package com.hipad.smarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

public class AppointMentModelActivity extends BaseActivity {

	private ImageView single, everyday;
	Intent data = new Intent();
	private Button comfirm;
	private RelativeLayout everyday_view, single_view;
	private ImageView leftBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appointment_model);
		single = (ImageView) findViewById(R.id.single);
		everyday = (ImageView) findViewById(R.id.everyday);
		comfirm = (Button) findViewById(R.id.appoint_more_icon);
		everyday_view = (RelativeLayout) findViewById(R.id.everyday_view);
		single_view = (RelativeLayout) findViewById(R.id.single_view);
		leftBtn = (ImageView) findViewById(R.id.title_left_icon);
		
		single.setClickable(false);
		everyday.setClickable(false);
		Intent intent = getIntent();		
		boolean iseveryday = intent.getBooleanExtra("iseveryday", false);
		if (iseveryday) {
			everyday.setBackgroundResource(R.drawable.rbtn_select);
			single.setBackgroundResource(R.drawable.rbtn_nor);
		} else {
			everyday.setBackgroundResource(R.drawable.rbtn_nor);
			single.setBackgroundResource(R.drawable.rbtn_select);
		}

		everyday_view.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				everyday.setBackgroundResource(R.drawable.rbtn_select);
				single.setBackgroundResource(R.drawable.rbtn_nor);
				data.putExtra("iseveryday", true);
				setResult();
			}
		});

		single_view.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				everyday.setBackgroundResource(R.drawable.rbtn_nor);
				single.setBackgroundResource(R.drawable.rbtn_select);
				data.putExtra("iseveryday", false);
				setResult();
			}
		});

		leftBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void setResult() {
		setResult(AppointMentSetActivity.MSG_MODE_ACTIVITY_RES, data);
		finish();
	}
}
