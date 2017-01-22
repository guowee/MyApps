/**
 * $(#)AddChooserActivity.java 2015Äê3ÔÂ24ÈÕ
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
public class AddChooserActivity extends BaseActivity {

	private ImageButton mImgbtnBack;
	private TextView mTvTitle;

	private Button mBtnConfig, mBtnSearch;

	private AddChooserActivity mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addchooser);
		mContext = this;

		getView();
		setOnClickListener();

		init();
	}

	private void getView() {
		mImgbtnBack = (ImageButton) findViewById(R.id.leftBtn);
		mTvTitle = (TextView) findViewById(R.id.titleTxt);

		mBtnConfig = (Button) findViewById(R.id.addchooser_btn_config);
		mBtnSearch = (Button) findViewById(R.id.addchooser_btn_search);
	}

	private void setOnClickListener() {
		mImgbtnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mBtnConfig.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, PreConfigActivity.class));
			}
		});
		mBtnSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(mContext, ScanDevOnWifiActivity.class));

			}
		});
	}

	private void init() {
		mTvTitle.setText(R.string.title_add_device);
	}
}
