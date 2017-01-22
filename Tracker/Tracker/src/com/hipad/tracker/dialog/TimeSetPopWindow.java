package com.hipad.tracker.dialog;

import java.util.*;

import com.hipad.tracker.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.ActionBar.LayoutParams;
import android.widget.PopupWindow;
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;
/**
 * 
 * @author guowei
 *
 */
public class TimeSetPopWindow extends PopupWindow implements OnClickListener {
	private Context mContext;
	private Button mCancelBtn, mSubmitBtn;
	private View mContentView;
	private TimeChoseListener mTimeChoseListener;
	private AbstractWheel mYearWheel, mMonthWheel, mDayWheel;

	private static int START_YEAR = 1900, END_YEAR = 2100;

	private int mYear, mMonth, mDay;

	private boolean isYearChange = false;
	private boolean isMonthChange = false;
	private boolean isDayChange = false;

	// 添加大小月月份并将其转换为list,方便之后的判断
	String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
	String[] months_little = { "4", "6", "9", "11" };

	final List<String> list_big = Arrays.asList(months_big);
	final List<String> list_little = Arrays.asList(months_little);

	String[] items={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	
	
	Calendar calendar = Calendar.getInstance();
	int year = calendar.get(Calendar.YEAR);
	int month = calendar.get(Calendar.MONTH);
	int day = calendar.get(Calendar.DATE);

	public TimeSetPopWindow(Context context) {
		super(context);
		mContext = context;
		mContentView = View.inflate(mContext, R.layout.date_dialog, null);
		setContentView(mContentView);
		getView();
		initViews();
	}

	private void getView() {
		mMonthWheel = (AbstractWheel) mContentView
				.findViewById(R.id.monthWheel);
		mYearWheel = (AbstractWheel) mContentView.findViewById(R.id.yearWheel);
		mDayWheel = (AbstractWheel) mContentView.findViewById(R.id.dayWheel);
		mCancelBtn = (Button) mContentView.findViewById(R.id.cancel);
		mCancelBtn.setOnClickListener(this);
		mSubmitBtn = (Button) mContentView.findViewById(R.id.start);
		mSubmitBtn.setOnClickListener(this);
	}

	private void initViews() {

		setHeight(LayoutParams.WRAP_CONTENT);
		setWidth(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		ColorDrawable cdw = new ColorDrawable(0x000000);
		this.setBackgroundDrawable(cdw);

		Log.i("asdd", year + "-" + month + "-" + day);

		mYearWheel.setViewAdapter(new NumericWheelAdapter(mContext, START_YEAR,
				END_YEAR));// 设置"年"的显示数据
		mYearWheel.setCyclic(true);// 可循环滚动
		mYearWheel.setCurrentItem(year - START_YEAR);// 初始化时显示的数据
		mYearWheel.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(AbstractWheel wheel, int oldValue,
					int newValue) {
				mYear = newValue + START_YEAR;
				setDaysByLeapYear(mYear,mMonthWheel.getCurrentItem()+1);
				isYearChange = true;

			}
		});

		mMonthWheel.setViewAdapter(new ArrayWheelAdapter<String>(mContext, items));
		mMonthWheel.setCyclic(true);
		mMonthWheel.setCurrentItem(month);
		mMonthWheel.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(AbstractWheel wheel, int oldValue,
					int newValue) {
				mMonth = newValue + 1;
				setDaysByLeapYear(mYearWheel.getCurrentItem()+1, mMonth);
				isMonthChange = true;

			}
		});

		mDayWheel.setCyclic(true);
		setDaysByLeapYear(year, month+1);
		mDayWheel.setCurrentItem(day -1);
		mDayWheel.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(AbstractWheel wheel, int oldValue,
					int newValue) {

				mDay = newValue + 1;
				isDayChange = true;
			}
		});

	}

	private void setDaysByLeapYear(int year, int month) {

		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month))) {
			mDayWheel.setViewAdapter(new NumericWheelAdapter(mContext, 1, 31));
		} else if (list_little.contains(String.valueOf(month))) {
			mDayWheel.setViewAdapter(new NumericWheelAdapter(mContext, 1, 30));
		} else {
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				mDayWheel.setViewAdapter(new NumericWheelAdapter(mContext, 1,
						29));
			else
				mDayWheel.setViewAdapter(new NumericWheelAdapter(mContext, 1,
						28));
		}
	}

	public void showPopupWindow() {
		showAtLocation(getContentView(), Gravity.CENTER, 0, 0);
	}

	public void setTimeChoseListener(TimeChoseListener mTimeChoseListener) {
		this.mTimeChoseListener = mTimeChoseListener;
	}

	public interface TimeChoseListener {

		public void onTimeChoose(int year, int month, int day);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.cancel:
			dismiss();
			break;
		case R.id.start:
			if (mTimeChoseListener != null) {

				if (!isYearChange) {
					mYear = mYearWheel.getCurrentItem()+START_YEAR;
				}
				if (!isMonthChange) {
					mMonth = mMonthWheel.getCurrentItem()+1;
				}
				if (!isDayChange) {
					mDay = mDayWheel.getCurrentItem()+1;
				}

				mTimeChoseListener.onTimeChoose(mYear, mMonth, mDay);
			}
			dismiss();
			break;
		default:
			break;
		}

	}

}
