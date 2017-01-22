package com.hipad.smarthome.utils;

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

/**
 * 
 * @author guowei
 */
public class TimeSetPopWindow extends PopupWindow implements OnClickListener {
	private final static String TAG = "TimeSetPopWindow";
	private Context mContext;
	private Button mCancelBtn, mSubmitBtn;
	private View mContentView;
	private TimeChoseListener mTimeChoseListener;
	private AbstractWheel mHourWheel, mMinuteWheel;

	private int mHour = 0;
	private int mMinute = 0;

	public TimeSetPopWindow(Context context) {
		super(context);
		mContext = context;
		mContentView = View.inflate(mContext, R.layout.pop_wheel_layout, null);
		setContentView(mContentView);
		getView();
		init();
	}

	private void getView() {
		// mHourWheel = (AbstractWheel) mContentView
		// .findViewById(R.id.timeWheel_1);
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

		mMinuteWheel.setVisibleItems(3);
		final TimeWheelViewAdapter timeWheelAdapter = new TimeWheelViewAdapter(
				mContext, R.layout.pop_wheel_item, 0, 240);
		mMinuteWheel.setViewAdapter(timeWheelAdapter);
		mMinuteWheel.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(AbstractWheel wheel, int oldValue,
					int newValue) {
				Log.i(TAG, "time old: " + oldValue + ", new: " + newValue);
				mMinute = newValue + timeWheelAdapter.getMin();

				Log.i(TAG, "mins: " + mMinute);
			}
		});
	}

	private class TimeWheelViewAdapter extends AbstractWheelTextAdapter {

		private int mMin = 0;
		private int mMax = 99;

		protected TimeWheelViewAdapter(Context context, int itemResource,
				int start, int end) {
			super(context, itemResource, R.id.text_name);
			mMin = start;
			mMax = end;

		}

		public int getMax() {
			return mMax;
		}

		public int getMin() {
			return mMin;
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return mMax - mMin + 1;
		}

		@Override
		public CharSequence getItemText(int index) {
			int value = mMin + index;
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

			if (mTimeChoseListener != null) {
				mTimeChoseListener.onTimeChoose(mMinute);
			}
			break;
		default:
			break;
		}

	}

	public void showPopupWindow() {
		// mHourWheel.setCurrentItem(12);
		mMinuteWheel.setCurrentItem(60);
		showAtLocation(getContentView(), Gravity.CENTER, 0, 0);
	}

	public void setTimeChoseListener(TimeChoseListener mTimeChoseListener) {
		this.mTimeChoseListener = mTimeChoseListener;
	}

	public interface TimeChoseListener {
		// public void onTimeChoose(int hour, int minute);
		public void onTimeChoose(int minute);
	}
}
