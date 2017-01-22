package com.hipad.tracker;

import java.util.ArrayList;







import com.hipad.tracker.service.Service;
import com.hipad.tracker.service.ServiceImpl;
import com.hipad.tracker.utils.SharedPreferencesHelper;
import com.hipad.tracker.widget.CustomToast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
/**
 * 
 * @author guowei
 *
 */
public class BaseActivity extends Activity {
	public MyApplication application;
	public SharedPreferencesHelper sph;
	private Activity context;
	private DisplayMetrics dm;
	public int screenWidth, screenHight;
	private  Dialog mDialog;
	public Animation shake;
	protected Service service;
	//创建一个tManager类的实例 
    private TelephonyManager tManager; 
	private final static int MSG_BOUND_STATE = 0x01;
	@SuppressLint("HandlerLeak")
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_BOUND_STATE:
				mDialog.dismiss();
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		application = (MyApplication) getApplication();
		application.addActivity(this);
		context=this;
		sph = new SharedPreferencesHelper(context);
	    //获取系统的tManager对象 
		tManager=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		
		service = new ServiceImpl();
		
		dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHight = dm.heightPixels;
		
		shake=AnimationUtils.loadAnimation(context, R.anim.shake);// 加载动画资源文件
	}

	public void setBackground() {
		ArrayList<Activity> lists = application.getActivityList();
		for (Activity activity : lists) {
			activity.getWindow().setBackgroundDrawableResource(R.drawable.address_bg);
		}
	}
	
	public void showToastLong(String msg) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast_layout, null);
		TextView title = (TextView) layout.findViewById(R.id.toastTxt);
		title.setText(msg);
		Toast toast = new Toast(context);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(layout);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();
	}

	public void showToastShort(String msg) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast_layout, null);
		TextView title = (TextView) layout.findViewById(R.id.toastTxt);
		title.setText(msg);
		Toast toast = new Toast(context);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(layout);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}

	public void showNotifyDialog(Context context, String str) {
		LayoutInflater inflater = LayoutInflater.from(context);
		LinearLayout v = (LinearLayout) inflater.inflate(
				R.layout.custom_toast_layout, null);
		TextView tipTextView = (TextView) v.findViewById(R.id.toastTxt);
		if (str != null) {
			// 设置加载信息e
			tipTextView.setText(str);
		}
		// 创建自定义样式dialog
		Dialog _dialog = new Dialog(context, R.style.CustomProgressDialog);
		_dialog.setCancelable(true);
		LinearLayout.LayoutParams params = new LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		v.setLayoutParams(params);
		v.setGravity(Gravity.CENTER);
		_dialog.onBackPressed();
		_dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getRepeatCount() == 0) {
					if (mDialog != null && mDialog.isShowing()) {
						mDialog.dismiss();
					}
				}
				return false;
			}
		});
		mDialog = _dialog;
		mDialog.setContentView(v);

		mDialog.show();
		Message msg = new Message();
		msg.what = MSG_BOUND_STATE;
		handler.sendMessageDelayed(msg, 3000);
	}
	
	
	public void showCustomToast(Context context,String text){
		CustomToast toast=new CustomToast(context);
		toast.setMessage(text);
		toast.showTime(3000);
		toast.show();
	}
	
	
	public void execute(Context context,String string){
		if(string.equals(getString(R.string.status_error))){
		 showToastShort(getString(R.string.account_login));
		 startActivity(new Intent(context,LoginActivity.class));	
		}/*else{
			showToastShort(string);
		}*/
	}
	
	public String getPhoneNumber(){
		String number=null;
		if(number==null){
			number=tManager.getLine1Number();
		}
		return number;
	}
	public String getRunningActivityName() {
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity
				.getClassName();
		return runningActivity;
	}
}
