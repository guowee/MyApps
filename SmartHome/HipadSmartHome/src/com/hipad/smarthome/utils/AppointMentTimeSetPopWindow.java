package com.hipad.smarthome.utils;

import java.util.Calendar;

import com.hipad.smarthome.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.app.ActionBar.LayoutParams;
import android.view.View.OnClickListener;
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.adapters.AbstractWheelTextAdapter;

public class AppointMentTimeSetPopWindow extends PopupWindow implements OnClickListener {
	private final static String TAG = "TimeSetPopWindow";
	private Context mContext;
	private Button mCancelBtn, mSubmitBtn;
	private View mContentView;
	private TimeChooseListener mTimeChooseListener;
	private AbstractWheel mHourWheel, mMinuteWheel;

	private int mHour = 0;
	private int mMinute = 0;

	public AppointMentTimeSetPopWindow(Context context) {
		super(context);
		mContext = context;
		mContentView = View.inflate(mContext, R.layout.pop_appointment_time_set, null);
		setContentView(mContentView);
		getView();
		init();
	}

	private void getView() {
		 mHourWheel = (AbstractWheel) mContentView
		 .findViewById(R.id.timeWheel_1);
		mMinuteWheel = (AbstractWheel) mContentView
				.findViewById(R.id.timeWheel_2);
		mCancelBtn = (Button) mContentView.findViewById(R.id.cancel);
		mCancelBtn.setOnClickListener(this);
		mSubmitBtn = (Button) mContentView.findViewById(R.id.start);
		mSubmitBtn.setOnClickListener(this);
	}

	private void init() {

		setHeight(LayoutParams.WRAP_CONTENT);
		setWidth(LayoutParams.WRAP_CONTENT);

		setFocusable(true);
		ColorDrawable cdw = new ColorDrawable(0x00000000);
		this.setBackgroundDrawable(cdw);
		mHourWheel.setVisibleItems(3);
		mHourWheel.setViewAdapter(new TimeWheelViewAdapter(mContext,
				R.layout.pop_wheel_item, 0, 23));
		
		mMinuteWheel.setVisibleItems(3);
		mMinuteWheel.setViewAdapter(new TimeWheelViewAdapter(mContext,
				R.layout.pop_wheel_item, 0, 59));
		
		mHourWheel.addChangingListener(new OnWheelChangedListener() {
			
			@Override
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				mHour = newValue;
			}
		});
		
		mMinuteWheel.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(AbstractWheel wheel, int oldValue,
					int newValue) {
				mMinute = newValue;
			}
		});
	}

	private class TimeWheelViewAdapter extends AbstractWheelTextAdapter {

		private int MIN = 0;
		private int MAX = 0;

		protected TimeWheelViewAdapter(Context context, int itemResource,
				int start, int end) {
			super(context, itemResource, R.id.text_name);
			MIN = start;
			MAX = end;

		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return MAX - MIN + 1;
		}

		@Override
		public CharSequence getItemText(int index) {
			int value = MIN + index;
			return String.valueOf(value);
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.cancel:

			dismiss();

			break;
		case R.id.start:
			dismiss();

			if (mTimeChooseListener != null) {
				mTimeChooseListener.onTimeChoose(mHour,mMinute);
			}
			break;
		default:
			break;
		}

	}

	public void showPopupWindow() {
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		mHourWheel.setCurrentItem(hour);
		mMinuteWheel.setCurrentItem(minute);
		showAtLocation(getContentView(), Gravity.CENTER, 0, 0);
	}

	public void setTimeChooseListener(TimeChooseListener mTimeChooseListener) {
		this.mTimeChooseListener = mTimeChooseListener;
	}

	public interface TimeChooseListener {
		public void onTimeChoose(int hour,int minute);
	}
}
