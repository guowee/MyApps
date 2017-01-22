package com.hipad.tracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
/**
 * 
 * @author guowei
 *
 */
public class ImeiNumberActivity extends BaseActivity implements OnClickListener {

	private ImageButton backBtn;
	private Context mContext;
	private EditText imeiNum;
	
	private Button nextBtn;

	
	private String simNumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		mContext=this;
		initData();
		getViews();
	}
	
	private void initData(){
        Intent intent =getIntent();
		simNumber=intent.getStringExtra("simNum");
	}

	private void getViews() {
        backBtn=(ImageButton) findViewById(R.id.leftBtn);
		imeiNum = (EditText) findViewById(R.id.bind_imei);
		nextBtn = (Button) findViewById(R.id.bind_next);
		backBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
		case R.id.bind_next:
			String imei = imeiNum.getText().toString().trim();
			MyApplication.imei=imei;
			if(imei==null||"".equals(imei)){
				imeiNum.startAnimation(shake);
				return;
			}
			
			Intent intent = new Intent(mContext,BindingActivity.class);
			intent.putExtra("IMEI", imei);
			intent.putExtra("simNum", simNumber);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

}
