package com.hipad.tracker;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class AgreementActivity extends BaseActivity implements OnClickListener{

	private WebView webView;
	
	private ImageButton backBtn;
	private Button okBtn;
	private TextView titleTxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agreement);
		getViews();
        initViews();
        setClick();
	}
	
	private void getViews(){
		 backBtn=(ImageButton) findViewById(R.id.leftBtn);
		 titleTxt=(TextView) findViewById(R.id.titleTxt);
		 webView=(WebView) findViewById(R.id.webView);
		 okBtn=(Button) findViewById(R.id.ok_btn);
		
	}
	
	private void initViews(){
		titleTxt.setText(getString(R.string.agreement));
		webView.loadUrl("file:///android_asset/agreement.html");  		
	}

	private void setClick(){
		backBtn.setOnClickListener(this);
		okBtn.setOnClickListener(this);
	}
	

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.leftBtn:
		case R.id.ok_btn:
			finish();
			break;
		default:
			break;
		}
		
	}
	
	
	
	
	
	

}
