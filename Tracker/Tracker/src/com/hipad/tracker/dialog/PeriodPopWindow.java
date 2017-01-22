package com.hipad.tracker.dialog;

import java.util.Calendar;

import com.hipad.tracker.R;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;

public class PeriodPopWindow extends PopupWindow implements OnClickListener{
	private Context mContext;
	private Button mCancelBtn, mSubmitBtn;
	private View mContentView;
	private AbstractWheel mStartWheel, mEndWheel;
	private String[] items;
	private PeriodChoseListener mPeriodChoseListener;
	
	
	Calendar calendar = Calendar.getInstance();
	int hour= calendar.get(Calendar.HOUR_OF_DAY);
	
	private int mStart,mEnd;
	
	private boolean isStartChange = false;
	private boolean isEndChange = false;
	
	public PeriodPopWindow(Context context,String[] items) {
		super(context);
		this.items=items;
		mContext=context;
		mContentView = View.inflate(mContext, R.layout.period_dialog, null);
		setContentView(mContentView);
		getViews();
		initViews();
	}
	
	private void getViews(){
		mStartWheel=(AbstractWheel) mContentView.findViewById(R.id.startWheel);
		mEndWheel=(AbstractWheel) mContentView.findViewById(R.id.endWheel);
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

		mStartWheel.setViewAdapter(new ArrayWheelAdapter(mContext,items));
		mStartWheel.setCyclic(true);
		mStartWheel.setCurrentItem(hour);
		mStartWheel.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(AbstractWheel wheel, int oldValue,int newValue) {
				mStart=newValue;
				isStartChange=true;
			}});
		
		mEndWheel.setViewAdapter(new ArrayWheelAdapter(mContext, items));
		mEndWheel.setCyclic(true);
		mEndWheel.setCurrentItem(hour);
		mEndWheel.addChangingListener(new OnWheelChangedListener() {
			
			@Override
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				mEnd=newValue;
				isEndChange=true;
			}
		});
	}
	
	public void setPeriodChoseListener(PeriodChoseListener mPeriodChoseListener) {
		this.mPeriodChoseListener = mPeriodChoseListener;
	}
	
	public void showPopupWindow() {
		showAtLocation(getContentView(), Gravity.CENTER, 0, 0);
	}

	public interface PeriodChoseListener {
		public void onPeriodCancel(boolean flag);
		public void onPeriodChoose(int start,int end);
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.cancel:
			if(mPeriodChoseListener!=null){
				mPeriodChoseListener.onPeriodCancel(true);
			}
			dismiss();
			break;
		case R.id.start:
			if (mPeriodChoseListener != null) {
				if(!isStartChange){
					mStart=mStartWheel.getCurrentItem();
				}
				if(!isEndChange){
					mEnd=mEndWheel.getCurrentItem();
				}
				
				mPeriodChoseListener.onPeriodChoose(mStart, mEnd);
			}
			dismiss();
			break;
		default:
			break;
		}
	}
}
