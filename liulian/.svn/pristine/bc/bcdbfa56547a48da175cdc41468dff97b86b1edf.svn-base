package com.haomee.liulian;

import java.io.File;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.haomee.chat.Utils.PreferenceUtils;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.imageloader.ImageLoaderCharles;
import com.haomee.view.CircleImageView;
import com.haomee.view.MyToast;

public class SettingActivity extends BaseActivity {

	private ImageView bt_back;

	private LinearLayout layout_personal;

	private RelativeLayout layout_exit, black_list, clear_storage, update_app, user_protocol, about_us, rl_his, rl_fav, rl_praise, dating_safety;

	private CircleImageView user_icon;

	private TextView user_name;


	private TextView txt_clear_info, txt_storage_size;

	private boolean isCleared;

	private Button bt_clear_commit, bt_clear_cancel;

	private ViewGroup layout_waiting;

	private ImageView img_waiting;

	private AlertDialog alertDialog;

	private EMChatOptions chatOptions;

	private ImageView open_notification, close_notification, open_sound, close_sound, open_vibrate, close_vibrate, open_sound_operation, close_sound_operation;
	private RelativeLayout switch_notification, switch_sound, switch_vibrate, switch_sound_operation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		chatOptions = EMChatManager.getInstance().getChatOptions();
		bt_back = (ImageView) findViewById(R.id.bt_back);
		layout_personal = (LinearLayout) findViewById(R.id.layout_personal);
		layout_exit = (RelativeLayout) findViewById(R.id.layout_exit);
		black_list = (RelativeLayout) findViewById(R.id.black_list);
		clear_storage = (RelativeLayout) findViewById(R.id.clear_storage);
		update_app = (RelativeLayout) findViewById(R.id.update_app);
		user_protocol = (RelativeLayout) findViewById(R.id.user_protocol);
		about_us = (RelativeLayout) findViewById(R.id.about_us);
		rl_his = (RelativeLayout) findViewById(R.id.rl_his);
		rl_fav = (RelativeLayout) findViewById(R.id.rl_fav);
		rl_praise = (RelativeLayout) findViewById(R.id.rl_praise);
		dating_safety = (RelativeLayout) findViewById(R.id.dating_safety);

		user_icon = (CircleImageView) findViewById(R.id.user_img);
		user_name = (TextView) findViewById(R.id.user_name);
		txt_storage_size = (TextView) findViewById(R.id.txt_storage_size);

		bt_back.setOnClickListener(itemOnClick);
		layout_personal.setOnClickListener(itemOnClick);
		layout_exit.setOnClickListener(itemOnClick);
		black_list.setOnClickListener(itemOnClick);
		clear_storage.setOnClickListener(itemOnClick);
		update_app.setOnClickListener(itemOnClick);
		user_protocol.setOnClickListener(itemOnClick);
		about_us.setOnClickListener(itemOnClick);
		rl_his.setOnClickListener(itemOnClick);
		rl_fav.setOnClickListener(itemOnClick);
		rl_praise.setOnClickListener(itemOnClick);
		dating_safety.setOnClickListener(itemOnClick);

		// 获取缓存
		getCacheSize();

		switch_notification = (RelativeLayout) findViewById(R.id.rl_switch_notification);
		switch_sound = (RelativeLayout) findViewById(R.id.rl_switch_sound);
		switch_sound_operation = (RelativeLayout) findViewById(R.id.rl_switch_sound_operation);
		switch_vibrate = (RelativeLayout) findViewById(R.id.rl_switch_vibrate);
		open_notification = (ImageView) findViewById(R.id.iv_switch_open_notification);
		close_notification = (ImageView) findViewById(R.id.iv_switch_close_notification);
		open_sound = (ImageView) findViewById(R.id.iv_switch_open_sound);
		close_sound = (ImageView) findViewById(R.id.iv_switch_close_sound);
		open_sound_operation = (ImageView) findViewById(R.id.iv_switch_open_sound_operation);
		close_sound_operation = (ImageView) findViewById(R.id.iv_switch_close_sound_operation);
		open_vibrate = (ImageView) findViewById(R.id.iv_switch_open_vibrate);
		close_vibrate = (ImageView) findViewById(R.id.iv_switch_close_vibrate);

		switch_notification.setOnClickListener(itemOnClick);
		switch_sound.setOnClickListener(itemOnClick);
		switch_sound_operation.setOnClickListener(itemOnClick);
		switch_vibrate.setOnClickListener(itemOnClick);

		if (chatOptions.getNotificationEnable()) {
			open_notification.setVisibility(View.VISIBLE);
			close_notification.setVisibility(View.INVISIBLE);
		} else {
			open_notification.setVisibility(View.INVISIBLE);
			close_notification.setVisibility(View.VISIBLE);
		}
		if (chatOptions.getNoticedBySound()) {
			open_sound.setVisibility(View.VISIBLE);
			close_sound.setVisibility(View.INVISIBLE);
		} else {
			open_sound.setVisibility(View.INVISIBLE);
			close_sound.setVisibility(View.VISIBLE);
		}
		if (chatOptions.getNoticedByVibrate()) {
			open_vibrate.setVisibility(View.VISIBLE);
			close_vibrate.setVisibility(View.INVISIBLE);
		} else {
			open_vibrate.setVisibility(View.INVISIBLE);
			close_vibrate.setVisibility(View.VISIBLE);
		}
		
		if (PreferenceUtils.getInstance(SettingActivity.this).getSettingSound_operation()) {
			open_sound_operation.setVisibility(View.VISIBLE);
			close_sound_operation.setVisibility(View.INVISIBLE);
		} else {
			open_sound_operation.setVisibility(View.INVISIBLE);
			close_sound_operation.setVisibility(View.VISIBLE);
		}
		

	}

	public void initData() {


        ImageLoaderCharles.getInstance(SettingActivity.this).addTask(LiuLianApplication.current_user.getImage(),user_icon);
		user_icon.setBackgroundResource(CommonConst.user_sex[LiuLianApplication.current_user.getSex()]);
		user_name.setText(LiuLianApplication.current_user.getName());

	}

	// 获取缓存大小
	private void getCacheSize() {
		// 扫描文件比较费时，起线程
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				String path_img = FileDownloadUtil.getDefaultLocalDir(PathConst.IMAGE_CACHDIR);
				if (path_img == null) {
					return;
				}
				File dir_img = new File(path_img);
				long size_img = FileDownloadUtil.getDirSize(dir_img);
				double size_mb = size_img * 1.0 / (1024 * 1024);
				txt_storage_size.setText(String.format("%.2f", size_mb) + "MB");

				if (size_img < 1024) {
					isCleared = true;
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (LiuLianApplication.current_user == null) {
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, LoginPageActivity.class);
			startActivity(intent);
			MyToast.makeText(SettingActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
			return;
		}
		initData();
	}

	OnClickListener itemOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {

			case R.id.rl_switch_notification:
				if (open_notification.getVisibility() == View.VISIBLE) {
					open_notification.setVisibility(View.INVISIBLE);
					close_notification.setVisibility(View.VISIBLE);
					switch_sound.setVisibility(View.GONE);
					switch_vibrate.setVisibility(View.GONE);
					chatOptions.setNotificationEnable(false);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					PreferenceUtils.getInstance(SettingActivity.this).setSettingMsgNotification(false);
					// HXSDKHelper.getInstance().getModel().setSettingMsgNotification(false);
				} else {
					open_notification.setVisibility(View.VISIBLE);
					close_notification.setVisibility(View.INVISIBLE);
					switch_sound.setVisibility(View.VISIBLE);
					switch_vibrate.setVisibility(View.VISIBLE);
					chatOptions.setNotificationEnable(true);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					PreferenceUtils.getInstance(SettingActivity.this).setSettingMsgNotification(true);
					// HXSDKHelper.getInstance().getModel().setSettingMsgNotification(true);
				}
				break;
			case R.id.rl_switch_sound:
				if (open_sound.getVisibility() == View.VISIBLE) {
					open_sound.setVisibility(View.INVISIBLE);
					close_sound.setVisibility(View.VISIBLE);
					chatOptions.setNoticeBySound(false);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					PreferenceUtils.getInstance(SettingActivity.this).setSettingMsgSound(false);
					// HXSDKHelper.getInstance().getModel().setSettingMsgSound(false);
				} else {
					open_sound.setVisibility(View.VISIBLE);
					close_sound.setVisibility(View.INVISIBLE);
					chatOptions.setNoticeBySound(true);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					PreferenceUtils.getInstance(SettingActivity.this).setSettingMsgSound(true);
				}
				break;
			case R.id.rl_switch_sound_operation:
				if (open_sound_operation.getVisibility() == View.VISIBLE) {
					open_sound_operation.setVisibility(View.INVISIBLE);
					close_sound_operation.setVisibility(View.VISIBLE);				
					PreferenceUtils.getInstance(SettingActivity.this).setSettingSound_operation(false);
				} else {
					open_sound_operation.setVisibility(View.VISIBLE);
					close_sound_operation.setVisibility(View.INVISIBLE);					
					PreferenceUtils.getInstance(SettingActivity.this).setSettingSound_operation(true);
				}
				break;
			case R.id.rl_switch_vibrate:
				if (open_vibrate.getVisibility() == View.VISIBLE) {
					open_vibrate.setVisibility(View.INVISIBLE);
					close_vibrate.setVisibility(View.VISIBLE);
					chatOptions.setNoticedByVibrate(false);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					PreferenceUtils.getInstance(SettingActivity.this).setSettingMsgVibrate(false);
				} else {
					open_vibrate.setVisibility(View.VISIBLE);
					close_vibrate.setVisibility(View.INVISIBLE);
					chatOptions.setNoticedByVibrate(true);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					PreferenceUtils.getInstance(SettingActivity.this).setSettingMsgVibrate(true);
				}
				break;

			case R.id.bt_back:
				SettingActivity.this.finish();
				break;
			case R.id.layout_personal:
				intent.putExtra("from_activity", 1);
				intent.setClass(SettingActivity.this, UserInfoActivity.class);
				startActivity(intent);
				break;
			case R.id.layout_exit:
				LiuLianApplication.getInstance().logout();
				try {
					AppManager.getAppManager().finishActivity(Class.forName("com.haomee.liulian.MainActivity"));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				intent.setClass(SettingActivity.this, LoginPageActivity.class);
				SettingActivity.this.startActivity(intent);
				SettingActivity.this.finish();
				break;
			case R.id.black_list:
				intent.setClass(SettingActivity.this, BlackListActivity.class);
				startActivity(intent);
				break;
			case R.id.clear_storage:
				if (isCleared) {
					MyToast.makeText(SettingActivity.this, "已经清理完毕", Toast.LENGTH_SHORT).show();
				} else {
					alertDialog = new AlertDialog.Builder(SettingActivity.this).create();
					alertDialog.show();
					Window window = alertDialog.getWindow();
					window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
					window.setContentView(R.layout.dialog_clear);
					bt_clear_commit = (Button) window.findViewById(R.id.bt_clear_commit);
					bt_clear_cancel = (Button) window.findViewById(R.id.bt_clear_cancel);
					img_waiting = (ImageView) window.findViewById(R.id.img_waiting);
					layout_waiting = (ViewGroup) window.findViewById(R.id.layout_waiting);
					txt_clear_info = (TextView) window.findViewById(R.id.txt_clear_info);
					bt_clear_commit.setOnClickListener(itemOnClick);
					bt_clear_cancel.setOnClickListener(itemOnClick);
				}
				break;
			case R.id.bt_clear_commit: {
				StatService.onEvent(SettingActivity.this, "setting_clear_cache", "设置页清除缓存点击次数", 1);
				startRotate_waiting();
				// 删除文件比较费时，起后台
				new ClearTask().execute();
				break;
			}
			case R.id.bt_clear_cancel: {
				alertDialog.dismiss();
				break;
			}
			case R.id.update_app:
				intent.setClass(SettingActivity.this, UpdateAppActivity.class);
				startActivity(intent);
				break;
			case R.id.user_protocol:
				intent.setClass(SettingActivity.this, WebPageActivity.class);
				intent.putExtra("title", "用户协议");
				intent.putExtra("url", PathConst.URL_USER_PORTAL);
				SettingActivity.this.startActivity(intent);
				break;
			case R.id.about_us:
				intent.setClass(SettingActivity.this, FeedbackActivity.class);
				startActivity(intent);
				break;
			case R.id.rl_his:
				intent.setClass(SettingActivity.this, HistoryActivity.class);
				startActivity(intent);
				break;
			case R.id.rl_fav:
				intent.setClass(SettingActivity.this, FavActivity.class);
				startActivity(intent);
				break;
			case R.id.rl_praise:
				intent.setClass(SettingActivity.this, PraiseActivity.class);
				startActivity(intent);
				break;
			case R.id.dating_safety:
				intent.setClass(SettingActivity.this, WebPageActivity.class);
				intent.putExtra("title", "交友安全提示");
				intent.putExtra("url", "http://api.durian.haomee.cn/html/protocol/safetyTips.html");
				startActivity(intent);
				break;
			}
		}
	};

	Handler handler_clear = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String message = msg.getData().getString("message");
			if ("finish".equals(message)) {
				alertDialog.dismiss();
			} else {
				txt_clear_info.setText(message);
			}
		}
	};

	class ClearTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			Message msg_img = new Message();
			Bundle data_img = new Bundle();
			msg_img.setData(data_img);
			data_img.putString("message", "清除缓存中...");
			handler_clear.sendMessage(msg_img);

			String path_img = FileDownloadUtil.getDefaultLocalDir(PathConst.IMAGE_CACHDIR);
			if (path_img != null) {
				FileDownloadUtil.deleteDir(new File(path_img));
			}

			/*
			 * Message msg_video = new Message(); Bundle data_video = new
			 * Bundle(); msg_video.setData(data_video);
			 * data_video.putString("message", "清除视频缓冲文件...");
			 * handler_clear.sendMessage(msg_video);
			 */
			FileDownloadUtil.clearVideoCacheFiles();

			// 清理temp下载缓存
			String path_temp = FileDownloadUtil.getDefaultLocalDir(PathConst.DIR_TEMP);
			if (path_temp != null) {
				FileDownloadUtil.deleteDir(new File(path_temp));
			}

			// 清理日志缓存
			String path_log = FileDownloadUtil.getDefaultLocalDir(PathConst.DIR_LOG);
			if (path_log != null) {
				FileDownloadUtil.deleteDir(new File(path_log));
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			isCleared = true;
			txt_storage_size.setText("0.00MB");
			txt_clear_info.setText("清除缓存完毕。");
			stopRotate_waiting();

			Message msg_finish = new Message();
			Bundle data_finish = new Bundle();
			msg_finish.setData(data_finish);
			data_finish.putString("message", "finish");
			handler_clear.sendMessageDelayed(msg_finish, 1000);

		}

	}

	// 等待动画
	private RotateAnimation rotateAnimation;
	private boolean isRotating = false;

	private void startRotate_waiting() {
		if (rotateAnimation == null) {
			rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			rotateAnimation.setDuration(1000);
			rotateAnimation.setRepeatCount(-1);
			rotateAnimation.setInterpolator(new LinearInterpolator());
			rotateAnimation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					isRotating = true;
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					isRotating = false;
				}
			});
		}

		layout_waiting.setVisibility(View.VISIBLE);
		if (!isRotating) {
			img_waiting.startAnimation(rotateAnimation);
		}

	}

	private void stopRotate_waiting() {
		if (rotateAnimation != null) {
			rotateAnimation.cancel();
		}
		layout_waiting.setVisibility(View.INVISIBLE);
	}

}
