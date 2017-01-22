package com.hipad.smarthome;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.hipad.smart.service.Service;
import com.hipad.smart.service.ServiceImpl;
import com.hipad.smarthome.utils.SmallTools;

public abstract class BaseActivity extends Activity {

	public String tag;
	public MyApplication application;
	private Dialog dialog;
	public SharedPreferences sp;
	private int bgID;

	private DisplayMetrics dm;
	public int screenWidth, screenHight;

	private SoundPool pool;
	private int playID;
	protected Service service;

	private Activity context;

	private Dialog mDialog;

	private final static int MSG_ABNORMAL_STATE = 0x05;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_ABNORMAL_STATE:
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
		tag = this.getClass().getName();
		SmallTools.showInfoLog(tag, "---------onCreate ");

		context = this;
		application = (MyApplication) getApplication();
		application.addActivity(this);
		sp = getSharedPreferences("app_params", Context.MODE_PRIVATE);

		if (isInTimeQuantum()) {
			setBackground(R.drawable.bg1);
		} else {
			setBackground(R.drawable.bg2);
		}

		dm = getResources().getDisplayMetrics();
		// WindowManager wm =
		// (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
		screenWidth = dm.widthPixels;
		screenHight = dm.heightPixels;
		// if(!this.getClass().isInstance(CookTypeActivity.class)){
		// //从右到左
		// showInfoLog("切换动画");
		// overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		// }

		service = new ServiceImpl();

		// 指定声音池的最大音频流数目为10，声音品质为5
		pool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		// 载入音频流，返回在池中的id
		playID = pool.load(this, R.raw.ben, 0);
	}

	/**
	 * 播放音频，第二个参数为左声道音量;第三个参数为右声道音量;第四个参数为优先级；第五个参数为循环次数，0不循环，-1循环;第六个参数为速率 ，速率
	 * 最低0.5最高为2，1代表正常速度
	 */
	public void startPlay() {
		pool.play(playID, 1, 1, 0, 0, 1);
	}

	// 判断当前时间是否在指定的时间段内
	private boolean isInTimeQuantum() {
		try {
			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");// 设置日期格式
			// 定义区间值06:00:00--17:59:59
			Date startDate = df.parse("05:59:59");
			Date endDate = df.parse("18:00:00");

			Date curTime = df.parse(df.format(new Date()));
			if (curTime.after(startDate) && curTime.before(endDate)) {
				// 在指定时间段内
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private Dialog createLoadingDialog(Context context, String msg) {
		LayoutInflater inflater = LayoutInflater.from(context);
		LinearLayout v = (LinearLayout) inflater.inflate(
				R.layout.load_dialog_layout, null);
		TextView tipTextView = (TextView) v.findViewById(R.id.dialogTxt);
		/*
		 * // 加载动画 Animation animation = AnimationUtils.loadAnimation(context,
		 * R.anim.load_dialog_anim); LinearInterpolator li = new
		 * LinearInterpolator(); animation.setInterpolator(li); //
		 * 使用ImageView显示动画 // dialogImage.startAnimation(animation);
		 */if (msg != null) {
			// 设置加载信息e
			tipTextView.setText(msg);
		}

		if (null == dialog) {

			// 创建自定义样式dialog
			Dialog loadingDialog = new Dialog(context,
					R.style.CustomProgressDialog);
			loadingDialog.setCancelable(false);
			LinearLayout.LayoutParams params = new LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			v.setLayoutParams(params);
			v.setGravity(Gravity.CENTER);
			// loadingDialog.setContentView(v);
			loadingDialog.onBackPressed();
			loadingDialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK
							&& event.getRepeatCount() == 0) {
						if (dialogShowing()) {
							dialog.dismiss();
							BaseActivity.this.context.finish();
						}
					}
					return false;
				}
			});
			dialog = loadingDialog;
		}

		dialog.setContentView(v);
		return dialog;
	}

	public void dialogShow(Context context, String msg) {
		// if (dialog == null) {
		// dialog = createLoadingDialog(context, msg);
		// }
		dialog = createLoadingDialog(context, msg);
		dialog.show();
		showInfoLog("dialog.show");
	}

	public void dialogDismiss() {
		if (dialogShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		showInfoLog("dialog.dismiss");
	}

	public boolean dialogShowing() {
		return dialog != null && dialog.isShowing();
	}

	public void setBackground(int resID) {
		if (resID > 0) {
			Editor edit = sp.edit();
			edit.putInt("bg_id", resID);
			edit.commit();
		}
		bgID = sp.getInt("bg_id", R.drawable.bg1);
		ArrayList<Activity> lists = application.getActivityList();
		for (Activity activity : lists) {
			activity.getWindow().setBackgroundDrawableResource(bgID);
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
		if (mDialog == null) {
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
		}

		
		mDialog.show();

		Message msg = new Message();
		msg.what = MSG_ABNORMAL_STATE;
		handler.sendMessageDelayed(msg, 3000);
	}

	public void showInfoLog(String msg) {
		SmallTools.showInfoLog(tag, msg);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// finish();
			// 从左到右
			// overridePendingTransition(R.anim.in_from_left,
			// R.anim.out_to_right);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		super.onStart();
		SmallTools.showInfoLog(tag, "---------onStart ");
	}

	@Override
	protected void onResume() {
		super.onResume();
		SmallTools.showInfoLog(tag, "---------onResume ");
	}

	@Override
	protected void onStop() {
		super.onResume();
		SmallTools.showInfoLog(tag, "---------onStop ");
	}

	@Override
	protected void onPause() {
		super.onPause();
		SmallTools.showInfoLog(tag, "---------onPause ");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		SmallTools.showInfoLog(tag, "---------onRestart ");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (pool != null) {
			pool.stop(playID);
			pool.release();
			pool = null;
		}
		handler.removeMessages(MSG_ABNORMAL_STATE);
		application.removeActivity(this);
		SmallTools.showInfoLog(tag, "---------onDestroy ");
	}
}
