/**
 * 
 */
package com.hipad.smarthome.utils;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.adapters.AbstractWheelTextAdapter;

import com.hipad.smarthome.R;

/**
 * 
 * @author wangbaoming
 *
 */
public class TargetTemperatureChooser extends PopupWindow {

	private final static String TAG = "TargetTemperatureChooser";;	

	private Context mContext;
	
	private View mContentView;
	private Button mBtnCancel, mBtnConfirm;
	
	private TempChosenListener mTempChoseListener;
	private int mTemp = 0;
	
	private AbstractWheel mTempWheel;
	
	public TargetTemperatureChooser(Context context) {
		super(context);
		mContext = context;

		mContentView = View.inflate(mContext, R.layout.view_target_temp_chooser, null);			
		setContentView(mContentView);			

		getView();
		setOnClickListener();
		
		init();
	}
	
	private void getView(){
		mTempWheel = (AbstractWheel) mContentView.findViewById(R.id.popupwindow_temp_chooser_wheelview_temp);
		 
		mBtnCancel = (Button) mContentView.findViewById(R.id.view_target_temp_chooser_btn_cancel);
		mBtnConfirm = (Button) mContentView.findViewById(R.id.view_target_temp_chooser_btn_confirm);
	}
	
	private void setOnClickListener(){
		mBtnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		mBtnConfirm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				if(null != mTempChoseListener) mTempChoseListener.onTempChosen(mTemp);
			}
		});
	}		
	
	private void init(){	
		setHeight(LayoutParams.WRAP_CONTENT);
		setWidth(LayoutParams.WRAP_CONTENT);

		setFocusable(true);
		 ColorDrawable cdw = new ColorDrawable(0x00000000);
		 this.setBackgroundDrawable(cdw);				
		
		// temperature wheel
		 mTempWheel.setVisibleItems(3);
		 mTempWheel.setViewAdapter(new TempWheelViewAdapter(mContext, R.layout.pop_wheel_item));
		 mTempWheel.addChangingListener(new OnWheelChangedListener() {
			
			@Override
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				Log.i(TAG, "temp old: " + oldValue + ", new: " + newValue);
				mTemp = TempWheelViewAdapter.MIN + newValue;

				Log.i(TAG, "temp: " + mTemp);
			}
		});
	}
	
	public void setTempChoseListener(TempChosenListener listener){
		mTempChoseListener = listener;
	}
	
	public void show(){
		mTempWheel.setCurrentItem(50);
		showAtLocation(getContentView(), Gravity.CENTER, 0, 0);
	}
	
	private class TempWheelViewAdapter extends AbstractWheelTextAdapter {

		private final static int MIN = 30;
		private final static int MAX = 95;
		
		public TempWheelViewAdapter(Context context,int resID) {
			super(context, resID, R.id.text_name);
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
	
	public interface TempChosenListener{
		public void onTempChosen(int temp);
	}
}
