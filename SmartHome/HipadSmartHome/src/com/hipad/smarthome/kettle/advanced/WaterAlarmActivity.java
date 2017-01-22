package com.hipad.smarthome.kettle.advanced;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.adapters.AbstractWheelTextAdapter;

import com.hipad.smarthome.MyApplication;
import com.hipad.smarthome.R;

/**
 * ÒûË®ÄÖÁå
 * @author guowei
 */
public class WaterAlarmActivity extends Activity implements IFunction,
		OnClickListener {

	public static final String MYACTION = "WaterAlarmActivity_action";
	private Context mContext;
	private TextView title;
	private AbstractWheel mWheel;
	private Button cancel, confirm;

	float mPeriod = 0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(true);
		mContext = this;
		setContentView(R.layout.water_alarm_layout);
		
		mPeriod = getDrinkAlarmInterval();
		
		initViews();
	}
	
	private float getDrinkAlarmInterval(){
		SharedPreferences preferences=getSharedPreferences("waterAlarm", Context.MODE_PRIVATE);
		boolean hasDrinkAlarm = preferences.getBoolean("flag", false);
		String usrId = preferences.getString("user", "unknown");
		
		if(hasDrinkAlarm && MyApplication.user.getUserId().equals(usrId)){
			return preferences.getFloat("time", 0);
		}
		
		return 0;
	}

	private void initViews() {

		String titleStr = getIntent().getStringExtra("title");

		title = (TextView) findViewById(R.id.title);
		title.setText(titleStr);
		cancel = (Button) findViewById(R.id.chooser_btn_cancel);
		cancel.setOnClickListener(this);

		confirm = (Button) findViewById(R.id.chooser_btn_confirm);
		confirm.setOnClickListener(this);
		
		
		mWheel = (AbstractWheel) findViewById(R.id.chooser_wheelview_time);

		mWheel.setVisibleItems(3);
		
		final PeriodWheelViewAdapter adapter = new PeriodWheelViewAdapter(mContext,
				R.layout.pop_wheel_item);
		mWheel.setViewAdapter(adapter);
		mWheel.setCurrentItem(adapter.value2index(mPeriod));
		mWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(AbstractWheel wheel, int oldValue,
					int newValue) {
				mPeriod = adapter.index2value(newValue);
			}
		});

	}

	private class PeriodWheelViewAdapter extends AbstractWheelTextAdapter {		
		private final static int MIN = 0;
		private final static int MAX = 8;

		protected PeriodWheelViewAdapter(Context context, int resID) {
			super(context, resID, R.id.text_name);
		}

		@Override
		public int getItemsCount() {
			return (MAX - MIN) + 1;
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		protected CharSequence getItemText(int index) {
			if(0 == index) return "off";
			
			float value = 0.5f * index;
			return String.format("%.1f", value)+"h";
		}
		
		public float index2value(int index){
			return MIN + 0.5f * index;
		}

		public int value2index(float value){
			
			return (int) (2*(value-MIN));
		}
	}

	@Override
	public String getName() {
		return "ÒûË®ÄÖÁå";
	}

	@Override
	public Intent execute(Context context) {
		Intent intent = new Intent();
		intent.setAction(MYACTION);
		return intent;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.chooser_btn_cancel:
			finish();
			break;
		case R.id.chooser_btn_confirm:

			Intent data = new Intent();
			data.putExtra("period", mPeriod);
			setResult(RESULT_OK, data);
			finish();
			break;
		default:
			break;
		}

	}

	@Override
	public boolean isForResult() {
		return true;
	}
}
