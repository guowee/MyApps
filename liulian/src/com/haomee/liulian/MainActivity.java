package com.haomee.liulian;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.mobstat.StatService;
import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.HanziToPinyin;
import com.haomee.baidu.push.Utils;
import com.haomee.chat.activity.ChatGame1;
import com.haomee.chat.activity.ChatGame2;
import com.haomee.chat.domain.Constant;
import com.haomee.chat.domain.User;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.fragment.DiscoverFragment;
import com.haomee.fragment.HiFragment;
import com.haomee.fragment.PersonalFragment;
import com.haomee.fragment.RadarFragment2;
import com.haomee.fragment.TestFragment;
import com.haomee.util.NetworkUtil;
import com.haomee.util.UpdateUtil;
import com.haomee.util.ViewUtil;
import com.haomee.view.MyToast;
import com.haomee.view.PagerSlidingTabStrip;
import com.haomee.view.PagerSlidingTabStrip.IconTabProvider;

public class MainActivity extends BaseFragmentActivity {

	private ViewPager pager;
	private Context context;
	private NewMessageBroadcastReceiver msgReceiver;
	private static int REQUEST_CODE_FROM_GAME = 1;
	private PagerSlidingTabStrip tabs;
	private MyPagerAdapter adapter;
	private static String[] TITLES = { "探索", "测试", "发现", "我" };

	private static ImageView iv_search, to_chat;
	public static TextView current_title;

	private TextView unread_message;

	private static ImageView bt_setting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(this, "api_key"));
		context = this;
		current_title = (TextView) this.findViewById(R.id.current_title);
		unread_message = (TextView) this.findViewById(R.id.unread_msg_number);
		to_chat = (ImageView) this.findViewById(R.id.to_chat);
		bt_setting = (ImageView) this.findViewById(R.id.bt_setting);
		bt_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(context, SettingActivity.class);
				context.startActivity(intent);
			}
		});
		to_chat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(context, HiFragment.class);
				startActivity(intent);
			}
		});
		pager = (ViewPager) this.findViewById(R.id.pager_container);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs_animation);
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		pager.setCurrentItem(2);
		int pageMargin = ViewUtil.dip2px(context, 4);
		pager.setPageMargin(pageMargin);
		tabs.setViewPager(pager);
		if (NetworkUtil.dataConnected(this)) {
			if (LiuLianApplication.current_user != null) {
				init_huanxin_receiver();
			}
		}
		iv_search = (ImageView) findViewById(R.id.search_type);
		iv_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(context, TopicTypeActivity2.class);
				startActivity(intent);
				StatService.onEvent(context, "main_discover_category", "发现页兴趣分类点击次数", 1);
			}
		});

		ViewTreeObserver vto = pager.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			int height = 0;

			@Override
			public void onGlobalLayout() {
				if (height != 0) {
					return;
				}
				height = pager.getMeasuredHeight();
				LiuLianApplication.height_fragment_main = height;
				Log.i("test", "height_fragment_main: " + LiuLianApplication.height_fragment_main);
			}
		});
		// 延时，没有加载完会报错
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// 检查更新,只有wifi才检查
				if (NetworkUtil.dataConnected(context)) {
					new UpdateUtil(context, handler_update).chechUpdate();
				}
			}
		}, 1000);
	}

	public void init_huanxin_receiver() {
		// 注册一个接收消息的BroadcastReceiver
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		context.registerReceiver(msgReceiver, intentFilter);
		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		context.registerReceiver(ackMessageReceiver, ackMessageIntentFilter);
		// 注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
		// 注册一个离线消息的BroadcastReceiver
		IntentFilter offlineMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getOfflineMessageBroadcastAction());
		context.registerReceiver(offlineMessageReceiver, offlineMessageIntentFilter);
		EMChat.getInstance().setAppInited();
	}

	@Override
	protected void onDestroy() {
		// 退出应用

		try {
			context.unregisterReceiver(msgReceiver);
			context.unregisterReceiver(ackMessageReceiver);
			context.unregisterReceiver(offlineMessageReceiver);
			msgReceiver = null;
			ackMessageReceiver = null;
			offlineMessageReceiver = null;
		} catch (Exception e) {
		}

		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}
		TestFragment item = (TestFragment) adapter.getItem(1);
		item.destory_recever();
		super.onDestroy();
		LiuLianApplication application = (LiuLianApplication) this.getApplication();
		application.exit();
	}

	/**
	 * 再按一次退出
	 */
	long waitTime = 2000;
	long touchTime = 0;

	@Override
	public void onBackPressed() {

		long currentTime = System.currentTimeMillis();
		if ((currentTime - touchTime) >= waitTime) {
			Toast.makeText(MainActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
			touchTime = currentTime;
		} else {
			LiuLianApplication.PUBLIC_GAME_ID = "";
			finish();
		}
	}

	/**
	 * 获取版本更新
	 */
	private Handler handler_update = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle_update = msg.getData();
			if (bundle_update != null) {
				SharedPreferences preferences = getSharedPreferences(CommonConst.PREFERENCES_SETTING, Activity.MODE_PRIVATE);
				int last_app_version = preferences.getInt("last_app_version", 0); // 上个版本勾选不提示
				int new_app_version = bundle_update.getInt("version_num");
				boolean is_notice = bundle_update.getBoolean("is_force", true); // 后台控制是否弹出

				Log.i("test", "new_app_version:" + new_app_version);
				Log.i("test", "last_app_version:" + last_app_version);

				// 当选择这个版本不更新之后判断
				if (is_notice && new_app_version > last_app_version && new_app_version > LiuLianApplication.appVersion) {
					Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
					intent.putExtras(bundle_update);
					MainActivity.this.startActivity(intent);
				}
			}
		}
	};
	/**
	 * 消息回执BroadcastReceiver
	 */
	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isAcked = true;
				}
			}
			abortBroadcast();
		}
	};
	/**
	 * 离线消息BroadcastReceiver sdk 登录后，服务器会推送离线消息到client，这个receiver，是通知UI
	 * 有哪些人发来了离线消息 UI 可以做相应的操作，比如下载用户信息
	 */
	private BroadcastReceiver offlineMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String[] users = intent.getStringArrayExtra("fromuser");
			String[] groups = intent.getStringArrayExtra("fromgroup");
			if (users != null) {
				for (String user : users) {
					System.out.println("收到user离线消息：" + user);
				}
			}
			if (groups != null) {
				for (String group : groups) {
					System.out.println("收到group离线消息：" + group);
				}
			}
			abortBroadcast();
		}
	};

	/**
	 * 新消息广播接收者
	 * 
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看
			updateUnreadLabel();
			// 消息id
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

	/**
	 * set head
	 * 
	 * @param username
	 * @return
	 */
	User setUserHead(String username) {
		User user = new User();
		user.setUsername(username);
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
		return user;
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

		}

		@Override
		public void onDisConnected(String errorString) {
			if (errorString != null && errorString.contains("conflict")) {
				// 显示帐号在其他设备登陆dialog
				showConflictDialog();

			}
		}

		@Override
		public void onReConnected() {

		}

		@Override
		public void onReConnecting() {
		}

		@Override
		public void onConnecting(String progress) {
		}

	}

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
						MainActivity.this.finish();
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
	protected void onResume() {
		super.onResume();
		if (LiuLianApplication.current_user != null) {
			if (!isConflict) {
				EMChatManager.getInstance().activityResumed();
				updateUnreadLabel();
			}
		}
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public static int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		return unreadMsgCountTotal;
	}

	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {

			unread_message.setVisibility(View.VISIBLE);
			if (count > 99) {
				unread_message.setText("99+");
			} else {
				unread_message.setText(count + "");
			}

		} else {
			unread_message.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow)
			showConflictDialog();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PathConst.PHOTOHRAPH) {
			if (PersonalFragment.vFile != null && PersonalFragment.vFile.exists()) {
				startCrop(Uri.fromFile(PersonalFragment.vFile).getPath());
			}
		} else if (requestCode == PathConst.CROPIMAGES) {
			if (data != null) {
				String path = data.getStringExtra("path");
				PersonalFragment.getPersonalFragment1().startResult(path);
			}
		} else if (requestCode == REQUEST_CODE_FROM_GAME) {
			LiuLianApplication.PUBLIC_GAME_ID = "";
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	// 裁剪
	public void startCrop(String path) {
		Intent intent = new Intent();
		intent.putExtra("path", path);
		intent.setClass(this, ImageCropActivity.class);
		startActivityForResult(intent, PathConst.CROPIMAGES);
	}

	private class MyPagerAdapter extends FragmentStatePagerAdapter implements IconTabProvider {
		private final int[] icons = { R.drawable.radar_default, R.drawable.test_default, R.drawable.discover_default, R.drawable.personal_default };
		private Fragment[] fragments;

		private MyPagerAdapter(FragmentManager fm) {
			super(fm);
			fragments = new Fragment[TITLES.length];
		}

		@Override     
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public int getPageIconResId(int position) {
			return icons[position];
		}

		@Override
		public Fragment getItem(int position) {
			Log.i("test", "子fragment:" + position);
			if (position == 0) {
				if (fragments[position] == null) {
					fragments[position] = new RadarFragment2();
				}
			} else if (position == 1) {
				if (fragments[position] == null) {
					fragments[position] = new TestFragment();
				}
			} else if (position == 2) {
				if (fragments[position] == null) {
					fragments[position] = new DiscoverFragment();
				}
			} else if (position == 3) {
				if (fragments[position] == null) {
					fragments[position] = new PersonalFragment();
				}
			}
			return fragments[position];
		}
	}

	public static void setTopBar(int position) {
		current_title.setText(TITLES[position]);

		if (position == 2) {
			iv_search.setVisibility(View.VISIBLE);
		} else {
			iv_search.setVisibility(View.GONE);
		}

		if (position == 3) {
			bt_setting.setVisibility(View.VISIBLE);
		} else {
			bt_setting.setVisibility(View.GONE);
		}

	}
}