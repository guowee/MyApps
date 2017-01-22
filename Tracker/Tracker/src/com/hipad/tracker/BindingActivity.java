package com.hipad.tracker;

import java.util.Timer;

import java.util.TimerTask;
import com.hipad.tracker.entity.TrackerMsg;
import com.hipad.tracker.http.HttpUtil;
import com.hipad.tracker.json.BindResponse;
import com.hipad.tracker.service.ServiceImpl;
import com.hipad.tracker.utils.CanvasUtils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author guowei
 */
public class BindingActivity extends BaseActivity implements OnClickListener {

	private ImageButton backBtn;
	private ImageView progressView;
	private Context mContext;
	private TextView percent;

	private String imei;
	private String account;
	private String spn1;
	private String devSIM;
	private String apn;

	private String bizSn;

	private TrackerMsg tMsg = new TrackerMsg();
	private BindHandler handler;
	private BindResultHandler rHandler;
	
	private Timer mTimer;
	private MyTimerTask mTimerTask;
	
	TrackerMsg trackerInfo = new TrackerMsg();

	private static boolean flag = false;
	private static final int MSG_PROGRESS = 0X01;
	private static final int MSG_TASK = 0X02;
	
	private static final int MSG_UI=0X03;

	private final Handler mHandler = new Handler(MyApplication
			.getContextObject().getMainLooper()) {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case MSG_PROGRESS:

				percent.setText(60 - msg.arg1 + "");
				CanvasUtils.getProgressBitmap(progressView, getResources(),	R.drawable.progress_full, msg.arg1, 6);
				break;
			case MSG_TASK:
				service.bindResult(tMsg, imei, devSIM, account, rHandler);
				mHandler.sendEmptyMessageDelayed(MSG_TASK, 5000);
				break;
			case MSG_UI:
				//showNotifyDialog(mContext, getString(R.string.bind_failure));
				showCustomToast(mContext, getString(R.string.bind_failure));
				break;
			
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_binding);
		mContext = this;
		initDatas();
		getViews();
		bindDevice();
	}

	@Override
	protected void onStart() {
		
		super.onStart();
	}

	private void initDatas() {
		mTimer=new Timer(true);
		service = new ServiceImpl();
		handler = new BindHandler();
		rHandler = new BindResultHandler();
		imei=MyApplication.imei;
	}

	private void getViews() {
		percent = (TextView) findViewById(R.id.percent);
		backBtn = (ImageButton) findViewById(R.id.leftBtn);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(this);
		progressView = (ImageView) findViewById(R.id.image_progress);

	}

	private void bindDevice() {
		initParams();
		service.bind(tMsg, imei, devSIM, account, handler);
		//new Timer().schedule(timer, 0, 900);
        startLockWindowTimer();
	}

	private void initParams() {
		account = MyApplication.account;
		Intent intent=getIntent();
		devSIM = intent.getStringExtra("simNum");
		tMsg.setSpn1(MyApplication.user.getMobile());
	}

	public void startLockWindowTimer(){
		if (mTimer != null) {
			if (mTimerTask != null) {
				mTimerTask.cancel(); // 将原任务从队列中移除
			}
			mTimerTask = new MyTimerTask(); // 新建一个任务
			mTimer.schedule(mTimerTask,0,900);
		}
	}
	
	class MyTimerTask extends TimerTask{
		int progress = 0;
		@Override
		public void run() {
			Message msg = new Message();
			msg.what = MSG_PROGRESS;
			msg.arg1 = progress++;
			if (progress > 60) {
				mHandler.removeMessages(MSG_TASK);
				mHandler.sendEmptyMessage(MSG_UI);
				flag = true;
				this.cancel();
			}
			mHandler.sendMessage(msg);
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
		default:
			break;
		}
	}

	private class BindHandler implements HttpUtil.ResponseResultHandler<BindResponse> {
		@Override
		public void handle(boolean timeout, BindResponse response) {
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {
						String message = response.getMsg();
						if (getString(R.string.success_bind).equals(message)) {
							mHandler.removeMessages(MSG_PROGRESS);
							
							trackerInfo.setBizSN(response.getBizSN());
							trackerInfo.setBattery(response.getBattery());
							trackerInfo.setVersion(response.getVersion());
							trackerInfo.setSpn1(response.getSpn1());
							trackerInfo.setApn(response.getApn());
							trackerInfo.setIpPort(response.getIpPort());
							trackerInfo.setLocType(response.getLocType());
							trackerInfo.setLoc(response.getLoc());

							Intent intent = new Intent(mContext,MainActivity.class);
							intent.putExtra("tracker", trackerInfo);
							intent.putExtra("imei",imei);
							intent.putExtra("classname", getRunningActivityName());
							startActivity(intent);
							finish();
						}
						if (getString(R.string.already_bind).equals(message)) {
							//skip to MainActivity to request location 
							Intent intent=new Intent(mContext,MainActivity.class);
							intent.putExtra("imei", imei);
							intent.putExtra("tracker", trackerInfo);
							intent.putExtra("classname", getRunningActivityName());
							startActivity(intent);
							finish();
						}

						if (getString(R.string.complete_bind).equals(message)) {
							tMsg.setBizSN(response.getBizSN());
							service.bindResult(tMsg, imei, devSIM, account,	rHandler);
							mHandler.sendEmptyMessageDelayed(MSG_TASK, 5000);
						}
					} else {
						execute(mContext, response.getMsg());
						if(getString(R.string.other_account_bound).equals(response.getMsg())){
							//showNotifyDialog(mContext, getString(R.string.other_account_bound));
							showCustomToast(mContext, getString(R.string.other_account_bound));
						}
					}
				} else {
					showToastShort(getString(R.string.neterror_hint));
				}
			} else {
				showToastShort(getString(R.string.timeout_hint));
			}
		}
	}

	private class BindResultHandler implements HttpUtil.ResponseResultHandler<BindResponse> {

		@Override
		public void handle(boolean timeout, BindResponse response) {
			if (!timeout) {
				if (response != null) {
					if (response.isSuccessful()) {

						mHandler.removeMessages(MSG_TASK);
						mHandler.removeMessages(MSG_PROGRESS);
						mTimerTask.cancel();
						
						trackerInfo.setBizSN(response.getBizSN());
						trackerInfo.setBattery(response.getBattery());
						trackerInfo.setVersion(response.getVersion());
						trackerInfo.setSpn1(response.getSpn1());
						trackerInfo.setApn(response.getApn());
						trackerInfo.setIpPort(response.getIpPort());
						trackerInfo.setLocType(response.getLocType());
						trackerInfo.setLoc(response.getLoc());

						Intent intent = new Intent(mContext, MainActivity.class);
						intent.putExtra("tracker", trackerInfo);
						intent.putExtra("imei", imei);
						intent.putExtra("classname", getRunningActivityName());
						sph.putBoolean("isBinding", true);
						startActivity(intent);
						finish();
					} else {
						execute(mContext, response.getMsg());
						if(getString(R.string.other_account_bound).equals(response.getMsg())){
							//showNotifyDialog(mContext, getString(R.string.other_account_bound));
							showCustomToast(mContext, getString(R.string.other_account_bound));
						}
					}
				} else {
					showToastShort(getString(R.string.neterror_hint));
				}
			} else {
				showToastShort(getString(R.string.timeout_hint));
			}
		}
	}


	@Override
	protected void onDestroy() {
		mHandler.removeCallbacksAndMessages(null);
		mTimerTask.cancel();
		
		super.onDestroy();
	}
}
