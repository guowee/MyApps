package com.haomee.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.NetUtils;
import com.haomee.chat.activity.ChatGame1;
import com.haomee.chat.activity.ChatGame2;
import com.haomee.liulian.BaseFragmentActivity;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.LoginPageActivity;
import com.haomee.liulian.R;
import com.haomee.view.MyToast;

public class HiFragment extends BaseFragmentActivity {
	private FragmentActivity context;
	private Fragment fragment_message, fragment_idol;
	private int page_index;
	private TextView bt_message, bt_idol;
	public static DiscoverFragment instance;
	public RelativeLayout errorItem;
	public static TextView errorText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.fragment_hi2);
		bt_idol = (TextView) this.findViewById(R.id.bt_idols);
		bt_message = (TextView) this.findViewById(R.id.bt_message);
		errorItem = (RelativeLayout) this.findViewById(R.id.rl_error_item);
		errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
		bt_message.setOnClickListener(btClickListener);
		bt_idol.setOnClickListener(btClickListener);
		try {
			fragment_message = new MessageFragment();
			FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.fragment_container_hi, fragment_message);
			ft.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (page_index == 1) {
			showIdolPage();
		}
		// 注册一个监听连接状态的listener
		init_huanxin_receiver();
	}

	private void showMessagePage() {
		bt_message.setBackgroundResource(R.drawable.border_tab_left_selected);
		bt_idol.setBackgroundResource(R.drawable.border_tab_right);
		bt_message.setTextColor(Color.parseColor("#ffffff"));
		bt_idol.setTextColor(Color.parseColor("#999999"));

		if (page_index != 0) {
			page_index = 0;
			if (fragment_message == null) {
				fragment_message = new MessageFragment();
			}
			FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.fragment_container_hi, fragment_message);
			ft.commit();
		}
	}

	private void showIdolPage() {
		bt_idol.setBackgroundResource(R.drawable.border_tab_right_selected);
		bt_message.setBackgroundResource(R.drawable.border_tab_left);
		bt_idol.setTextColor(Color.parseColor("#ffffff"));
		bt_message.setTextColor(Color.parseColor("#989898"));

		if (page_index != 1) {
			page_index = 1;
			if (fragment_idol == null) {
				fragment_idol = new MyIdolFragment();
			}
			FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.fragment_container_hi, fragment_idol);
			ft.commit();
		}
	}

	private OnClickListener btClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_message: {

				showMessagePage();
				StatService.onEvent(context, "count_of_message", "消息页消息点击次数", 1);

				break;
			}
			case R.id.bt_idols: {

				if (LiuLianApplication.current_user == null) {
					Intent intent = new Intent();
					intent.setClass(context, LoginPageActivity.class);
					startActivity(intent);
					return;
				}
				showIdolPage();
				StatService.onEvent(context, "count_of_care", "消息页关注点击次数", 1);
				break;
			}
			}
		}
	};

	public void refresh() {
		try {
			MessageFragment hiFragment = (MessageFragment) fragment_message;
			hiFragment.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示帐号在别处登录dialog
	 */
	/**
	 * 连接监听listener
	 * 
	 */
	private class MyConnectionListener implements ConnectionListener {

		@Override
		public void onConnected() {
			errorItem.setVisibility(View.GONE);
		}

		@Override
		public void onConnecting(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDisConnected(String errorString) {
			if (errorString != null && errorString.contains("conflict")) {
				// 显示帐号在其他设备登陆dialog
				showConflictDialog();
				errorItem.setVisibility(View.VISIBLE);
				if (NetUtils.hasNetwork(context))
					errorText.setText("同一账号在其他设备上登陆");
				else
					errorText.setText("当前网络不可用，请检查网络设置");

			} else {
				errorItem.setVisibility(View.VISIBLE);
				if (NetUtils.hasNetwork(context))
					errorText.setText("连接不到聊天服务器");
				else
					errorText.setText("当前网络不可用，请检查网络设置");

			}

		}

		@Override
		public void onReConnected() {
			errorItem.setVisibility(View.GONE);
		}

		@Override
		public void onReConnecting() {
			// TODO Auto-generated method stub
		}

	}

	/**
	 * 显示帐号在别处登录dialog
	 */

	public boolean isConflict = false;
	private boolean isConflictDialogShow;
	private android.app.AlertDialog.Builder conflictBuilder;

	public void showConflictDialog() {
		isConflictDialogShow = true;

		if (!this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(context);
				conflictBuilder.setTitle("下线通知");
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						conflictBuilder = null;
						LiuLianApplication.getInstance().logout();
						startActivity(new Intent(context, LoginPageActivity.class));
						context.finish();
					}
				});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				Log.e("###", "---------color conflictBuilder error" + e.getMessage());
			}

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow)
			showConflictDialog();
	}

	private NewMessageBroadcastReceiver msgReceiver;

	public void init_huanxin_receiver() {
		// 注册一个接收消息的BroadcastReceiver
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(4);
		context.registerReceiver(msgReceiver, intentFilter);
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
	}

	/**
	 * 新消息广播接收者
	 */
	private static int REQUEST_CODE_FROM_GAME = 1;

	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看
			// 消息id
			refresh();
			String msgId = intent.getStringExtra("msgid");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			// EMMessage message =
			// EMChatManager.getInstance().getMessage(msgId);
			// 刷新bottom bar消息未读数
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			String chatGameLevel;
			try {
				// 表示对方同意
				if (message.getStringAttribute("chatGameState").equals("chatGameStateAgree")) {
					MyToast.makeText(context, "游戏即将开始", 1).show();
					chatGameLevel = message.getStringAttribute("chatGameLevel");
					intent.putExtra("game_id", message.getStringAttribute("chatGameId"));
					if (chatGameLevel.equals("0")) {
						intent.setClass(context, ChatGame1.class);
					} else if (chatGameLevel.equals("1")) {
						intent.setClass(context, ChatGame2.class);
					}
					startActivityForResult(intent, REQUEST_CODE_FROM_GAME);
				} else if (message.getStringAttribute("chatGameState").equals("chatGameStateStart")) {
					// 表示被邀请
					if (LiuLianApplication.PUBLIC_GAME_ID.equals("")) {
						// 1、被邀请者收到游戏邀请，并且当前没有正在进行的游戏,先修改全局游戏ID
						LiuLianApplication.PUBLIC_GAME_ID = message.getStringAttribute("chatGameId");
						LiuLianApplication.GAME_TIME_SEND_RECEIVE = 30;
						// 2、开始计时
						startRefresh();
					} else {
						LiuLianApplication.PUBLIC_GAME_ID = "";
					}
				}

			} catch (EaseMobException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// EMChatManager.getInstance().activityResumed();
			// 注销广播，否则在ChatActivity中会收到这个广播
			abortBroadcast();
		}
	}

	public void startRefresh() {
		if (LiuLianApplication.GAME_TIMER != null) {
			LiuLianApplication.GAME_TIMER.cancel();
		}
		LiuLianApplication.GAME_TIMER = new Timer();
		LiuLianApplication.GAME_TIMER.schedule(new TimerTask() {
			public void run() {
				if (LiuLianApplication.GAME_TIME_SEND_RECEIVE > 0) {
					LiuLianApplication.GAME_TIME_SEND_RECEIVE--;
				} else {
					stopRefresh();
				}

			}
		}, 0, 1000);
	}

	public void stopRefresh() {
		LiuLianApplication.GAME_TIME_SEND_RECEIVE = 30;
		LiuLianApplication.PUBLIC_GAME_ID = "";
		if (LiuLianApplication.GAME_TIMER != null) {
			LiuLianApplication.GAME_TIMER.cancel();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			context.unregisterReceiver(msgReceiver);
			msgReceiver = null;
		} catch (Exception e) {
		}

	}

}
