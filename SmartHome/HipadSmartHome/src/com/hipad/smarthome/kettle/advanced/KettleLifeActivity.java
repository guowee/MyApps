package com.hipad.smarthome.kettle.advanced;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hipad.smart.kettle.v14.KettleStatusInfo;
import com.hipad.smarthome.BaseActivity;
import com.hipad.smarthome.R;

/**
 * 水壶寿命
 * 
 * @author guowei
 *
 */
public class KettleLifeActivity extends Activity implements IFunction {

	public static final String MYACTION = "KettleLifeActivity_action";

	private TextView title;
	private TextView usedTime;
	private TextView leftTime;

	private KettleStatusInfo kettleStatusInfo;

	int usedNum = 0;

	String strTitle = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(true);
		setContentView(R.layout.kettle_life_layout);
		getData();
		initViews();

	}

	private void getData() {
		strTitle = getIntent().getStringExtra("title");
		byte[] info = getIntent().getByteArrayExtra("kettleStatusInfo");
		kettleStatusInfo = new KettleStatusInfo(info);

		usedNum = kettleStatusInfo.getBoiledTimes();
	}

	private void initViews() {
		title = (TextView) findViewById(R.id.title);
		title.setText(strTitle);
		usedTime = (TextView) findViewById(R.id.used_time);
		usedTime.setText(usedNum + " 次");
		leftTime = (TextView) findViewById(R.id.left_time);
		leftTime.setText((30000 - usedNum) + " 次");
	}

	@Override
	public String getName() {
		return "水壶寿命";
	}

	@Override
	public Intent execute(Context context) {
		Intent intent = new Intent();
		intent.setAction(MYACTION);
		return intent;
	}

	@Override
	public boolean isForResult() {
		return false;
	}

}
