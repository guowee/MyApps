package com.hipad.tracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class SimNumberActivity extends BaseActivity implements OnClickListener {
	
	private Context mContext;
	private ImageButton leftBtn;
	private TextView titleText;
	private EditText simTxt;
	private Button nextBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sim);
		mContext=this;
		getViews();
		setClick();
	}
	
	
	private void getViews(){
		leftBtn = (ImageButton) findViewById(R.id.leftBtn);
		titleText = (TextView) findViewById(R.id.titleTxt);
		titleText.setText(getString(R.string.sim_number));
		simTxt=(EditText) findViewById(R.id.sim_txt);
		nextBtn=(Button) findViewById(R.id.next_btn);
		
	}
private void setClick(){
	leftBtn.setOnClickListener(this);
	nextBtn.setOnClickListener(this);
}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
		case R.id.next_btn:
			String simNumber=simTxt.getText().toString().trim();
            if(validate(simNumber)){
            	Intent intent=new Intent(mContext,GuideActivity.class);
    			intent.putExtra("simNum", simNumber);
    			startActivity(intent);
            }else{
            	//showNotifyDialog(mContext, getString(R.string.sim_null));
            	showCustomToast(mContext, getString(R.string.sim_null));
            }
			break;
		default:
			break;
		}
		
		
	}
	
	
	private boolean validate(String number){
		if(number==null||number.length()==0){
			return false;
		}
		return true;
	}
	
	
	

}
