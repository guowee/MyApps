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
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.easemob.util.NetUtils;
import com.haomee.baidu.push.Utils;
import com.haomee.chat.activity.ChatGame1;
import com.haomee.chat.activity.ChatGame2;
import com.haomee.chat.domain.Constant;
import com.haomee.chat.domain.User;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.Music;
import com.haomee.fragment.DiscoverFragment;
import com.haomee.fragment.MessageFragment;
import com.haomee.fragment.HiFragment;
import com.haomee.fragment.PersonalFragment;
import com.haomee.fragment.RadarFragment;
import com.haomee.fragment.RadarFragment2;
import com.haomee.fragment.TestFragment;
import com.haomee.player.MyMusicPlayer;
import com.haomee.util.NetworkUtil;
import com.haomee.util.UpdateUtil;
import com.haomee.view.MyToast;
import com.haomee.view.RoundProgressBar;

public class MainActivity1 extends BaseFragmentActivity {

	private int menu_text_color, menu_text_color_hover;
	private int menu_index_current, menu_index_pre = -1;
	private View fragment_container;

	private Context context;
	private NewMessageBroadcastReceiver msgReceiver;
	private String[] good;

	private static int REQUEST_CODE_FROM_GAME = 1;

	// private String[] menu_titles;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 百度推送每次都要startwork，否则长连接会中断
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(this, "api_key"));
		context = this;

		fragment_container = this.findViewById(R.id.fragment_container);

		initMenus();

		menu_index_current = 2;
		switchPager(menu_index_current);

		initMusicPanel();

		// 显示小红点
		if (NetworkUtil.dataConnected(this)) {
			if (LiuLianApplication.current_user != null) {
				init_huanxin_receiver();
			}
		}

		ViewTreeObserver vto = fragment_container.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			int height = 0;

			@Override
			public void onGlobalLayout() {
				if (height != 0) {
					return;
				}
				height = fragment_container.getMeasuredHeight();
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
		// 取xml文件格式的字符数组
		good = getResources().getStringArray(R.array.names);
		// menu_titles=good;
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

	private String[] menu_titles = { "探索", "测试", "发现", "招呼", "我" };

	private int[] menu_icons_unselect = { R.drawable.tabbar_button_1_default, R.drawable.tabbar_button_2_default, R.drawable.tabbar_button_2_default, R.drawable.tabbar_button_3_default, R.drawable.tabbar_button_4_default };
	private int[] menu_icons_select = { R.drawable.tabbar_button_1_pressed, R.drawable.tabbar_button_2_pressed, R.drawable.tabbar_button_2_pressed, R.drawable.tabbar_button_3_pressed, R.drawable.tabbar_button_4_pressed };
	private Fragment[] fragments = new Fragment[menu_titles.length];
	private View[] menus = new View[menu_titles.length];

	/*
	 * private String[] red_dots = new String[menu_titles.length]; //
	 * 小红点标识位，如果后台和本地记录不一样，就显示小红点 private int[] red_dots_flat = new
	 * int[menu_titles.length]; // 小红点标识位，如果后台和本地记录不一样，就显示小红点
	 */
	// 初始化底部菜单
	private void initMenus() {

		menu_text_color = Color.parseColor("#999999");
		menu_text_color_hover = Color.parseColor("#fe7777");

		LinearLayout container = (LinearLayout) this.findViewById(R.id.layout_menus);
		container.removeAllViews();

		LayoutInflater inflater = LayoutInflater.from(this);

		for (int i = 0; i < menu_titles.length; i++) {
			View menu = inflater.inflate(R.layout.item_main_menu, null);

			ImageView tempImage = (ImageView) menu.findViewById(R.id.item_icon);
			int temp = menu_icons_unselect[i];
			tempImage.setImageResource(temp);
			((TextView) menu.findViewById(R.id.item_text)).setText(menu_titles[i]);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.weight = 1;
			menu.setLayoutParams(params);
			container.addView(menu);

			menu.setTag(i);

			menus[i] = menu;
		}

		// 解决开始点击太快引起崩溃的Bug
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				for (View menu : menus) {
					menu.setOnClickListener(menuOnClickListener);
				}
			}
		}, 1000);
	}

	private View layout_player_bts;
	private boolean is_music_bar_visible = false;
	private boolean is_music_bar_open = true;
	// private TranslateAnimation anim_open, anim_close;
	private RoundProgressBar bt_play;

	private void initMusicPanel() {
		layout_player_bts = this.findViewById(R.id.layout_player_bts);
		bt_play = (RoundProgressBar) this.findViewById(R.id.bt_play);
		ImageView bt_next = (ImageView) this.findViewById(R.id.bt_next);

		/*
		 * anim_close = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
		 * Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.0f,
		 * Animation.RELATIVE_TO_SELF, 0.0f); anim_open = new
		 * TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.7f,
		 * Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
		 * Animation.RELATIVE_TO_SELF, 0.0f);
		 * 
		 * anim_close.setFillAfter(true); anim_close.setDuration(1000);
		 * anim_open.setFillAfter(true); anim_open.setDuration(1000);
		 */

		bt_play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (is_music_bar_open) {
					if (MyMusicPlayer.getInstance().getCurrentMusic() != null) {

						if (MyMusicPlayer.getInstance().getStatus() == MyMusicPlayer.STATUS_PLAYING) {
							bt_play.setImageResource(R.drawable.music_float_play);
							MyMusicPlayer.getInstance().pause();
						} else {
							bt_play.setImageResource(R.drawable.music_float_pause);
							MyMusicPlayer.getInstance().play();
						}

						autoCloseMusicBar();
					}

				} else {
					openMusicBar();
				}

			}
		});

		bt_next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (is_music_bar_open) {
					if (MyMusicPlayer.getInstance().getCurrentMusic() != null) {

						Music next = MyMusicPlayer.getInstance().playNext(false);

						if (next != null) {

							final RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
							rotateAnimation.setDuration(1000);
							rotateAnimation.setRepeatCount(-1);
							rotateAnimation.setInterpolator(new LinearInterpolator());

							((ImageView) bt_play).setImageResource(R.drawable.music_float_play);
							bt_play.setProgress(0);
							bt_play.startAnimation(rotateAnimation);

							MyMusicPlayer.getInstance().setOnPreparedListener(new OnPreparedListener() {
								@Override
								public void onPrepared(MediaPlayer mp) {
									rotateAnimation.cancel();

									((ImageView) bt_play).setImageResource(R.drawable.music_float_pause);

								}
							});
						}

					}

					autoCloseMusicBar();

				} else {
					openMusicBar();
				}

			}
		});

		layout_player_bts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!is_music_bar_open) {
					openMusicBar();
				}
			}
		});

		autoCloseMusicBar();

		handler_current_music.sendEmptyMessageDelayed(0, 1000);

	}

	private int move_step = 32; // 每次移动距离
	private int move_rightMargin_music;
	private int move_window_width_music;

	Handler handler_move_window_music = new Handler() {
		public void handleMessage(Message msg) {
			if (is_music_bar_open) {
				move_rightMargin_music += move_step;
				if (move_rightMargin_music < 0) {
					this.sendEmptyMessageDelayed(0, 60); // 自己不停循环
				} else {
					move_rightMargin_music = 0;
				}

			} else {
				move_rightMargin_music -= move_step;
				if (move_rightMargin_music > -move_window_width_music * 2 / 3) {
					this.sendEmptyMessageDelayed(0, 60); // 自己不停循环
				} else {
					move_rightMargin_music = -move_window_width_music * 2 / 3;
				}
			}

			RelativeLayout.LayoutParams layoutParams_move = (RelativeLayout.LayoutParams) layout_player_bts.getLayoutParams();
			layoutParams_move.rightMargin = move_rightMargin_music;
			layout_player_bts.setLayoutParams(layoutParams_move);

			// Log.i("test", "rightMargin:" + move_rightMargin_music);
		};
	};

	// 移动界面，相对x移动多少
	private void moveWindow_music() {
		RelativeLayout.LayoutParams layoutParams_move = (RelativeLayout.LayoutParams) layout_player_bts.getLayoutParams();
		move_rightMargin_music = layoutParams_move.rightMargin;
		handler_move_window_music.removeMessages(0);
		handler_move_window_music.sendEmptyMessage(0);
		move_window_width_music = layout_player_bts.getWidth();
	}

	private void openMusicBar() {
		is_music_bar_open = true;
		// layout_player_bts.startAnimation(anim_open);
		moveWindow_music();

		autoCloseMusicBar();
	}

	private void autoCloseMusicBar() {
		handler_auto_close.removeMessages(0);
		handler_auto_close.sendEmptyMessageDelayed(0, 5000);
	}

	private Handler handler_auto_close = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (is_music_bar_visible) {
				// layout_player_bts.startAnimation(anim_close);
				is_music_bar_open = false;
				moveWindow_music();
			}
		}
	};

	// 检测是否有当前歌曲,更新进度条
	private Handler handler_current_music = new Handler() {

		private boolean is_first = true;

		@Override
		public void handleMessage(Message msg) {

			if (MyMusicPlayer.getInstance().getCurrentMusic() == null) {
				layout_player_bts.setVisibility(View.GONE);
				is_music_bar_visible = false;
			} else {
				layout_player_bts.setVisibility(View.VISIBLE);
				is_music_bar_visible = true;

				if (is_first) {
					if (is_music_bar_open) {
						autoCloseMusicBar();
						is_first = false;
					}
				}

				if (MyMusicPlayer.getInstance().getStatus() == MyMusicPlayer.STATUS_PLAYING) {
					bt_play.setImageResource(R.drawable.music_float_pause);

					int progress = MyMusicPlayer.getInstance().getProgress();
					bt_play.setProgress(progress);

				} else {
					bt_play.setImageResource(R.drawable.music_float_play);
				}

			}

			handler_current_music.sendEmptyMessageDelayed(0, 1000);
		}
	};

	private OnClickListener menuOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			int index = (Integer) v.getTag();

			// 没登陆都跳转登陆页(发现页除外)
			if (index != 1 && LiuLianApplication.current_user == null) {
				Intent intent = new Intent();
				intent.setClass(MainActivity1.this, LoginPageActivity.class);
				startActivity(intent);
				MainActivity1.this.finish();
				MyToast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
				return;
			}
			switchPager(index);
			hideMenuTip(index);
		}
	};

	/**
	 * 切换到第index个页面
	 */
	private void switchPager(int index) {

		View menu = menus[index];

		if (menu_index_pre != -1) {

			if (menu_index_pre == index) { // 如果是当前，不重复操作
				return;
			}

			View menu_pre = menus[menu_index_pre];
			((ImageView) menu_pre.findViewById(R.id.item_icon)).setImageResource(menu_icons_unselect[menu_index_pre]);
			((TextView) menu_pre.findViewById(R.id.item_text)).setTextColor(menu_text_color);
		}

		menu_index_current = index;
		menu_index_pre = index;

		((ImageView) menu.findViewById(R.id.item_icon)).setImageResource(menu_icons_select[index]);
		((TextView) menu.findViewById(R.id.item_text)).setTextColor(menu_text_color_hover);

		if (fragments[index] == null) {
			switch (index) {
			case 0:
				// fragments[index] = new RadarFragment();
				fragments[index] = new RadarFragment2();
				break;
			case 1:// 测试
				fragments[index] = new TestFragment();
				break;
			case 2:
				fragments[index] = new DiscoverFragment();
				break;
			case 3:
				// fragments[index] = new HiFragment();
				break;
			case 4:
				fragments[index] = new PersonalFragment();
				break;
			default:
				return;
			}
		}

		try {
			FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.fragment_container, fragments[index]);
			ft.commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}

		switch (index) {
		case 0:
			StatService.onEvent(context, "main_bottom_radar", "底部菜单探索点击次数", 1);
			break;
		case 1:
			StatService.onEvent(context, "main_bottom_discover", "底部菜单发现点击次数", 1);
			break;
		case 2:
			StatService.onEvent(context, "main_bottom_hi", "底部菜单招呼点击次数", 1);
			break;
		case 3:
			StatService.onEvent(context, "main_bottom_mine", "底部菜单我的点击次数", 1);
			break;
		}

	}

	/**
	 * 菜单上的小红点提示
	 */
	private void showMenuTip(int index) {

		if (!menus[index].findViewById(R.id.item_tip).isShown()) {
			((ImageView) menus[index].findViewById(R.id.item_tip)).setVisibility(View.VISIBLE);
		}

	}

	@SuppressWarnings("unused")
	private void hideMenuTip(int index) {
		View menu = menus[index];
		((ImageView) menu.findViewById(R.id.item_tip)).setVisibility(View.GONE);
	}

	@Override
	protected void onDestroy() {
		// 退出应用
		LiuLianApplication application = (LiuLianApplication) this.getApplication();
		application.exit();
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
		super.onDestroy();
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
			Toast.makeText(MainActivity1.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
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
					Intent intent = new Intent(MainActivity1.this, UpdateActivity.class);
					intent.putExtras(bundle_update);
					MainActivity1.this.startActivity(intent);
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

			// 消息id
			String msgId = intent.getStringExtra("msgid");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			// EMMessage message =
			// EMChatManager.getInstance().getMessage(msgId);
			// 刷新bottom bar消息未读数
			updateUnreadLabel();
			if (menu_index_current == 3) {
				// 当前页面如果为聊天历史页面，刷新此页面
				if (fragments[3] != null) {
					// HiFragment hiFragment = (HiFragment) fragments[3];
					// hiFragment.refresh();
				}
			}
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
			if (fragments[3] != null) {
				/*
				 * HiFragment hiFragment = (HiFragment) fragments[3]; if
				 * (hiFragment != null) {
				 * hiFragment.errorItem.setVisibility(View.GONE); }
				 */
			}
		}

		@Override
		public void onDisConnected(String errorString) {
			if (errorString != null && errorString.contains("conflict")) {
				// 显示帐号在其他设备登陆dialog
				showConflictDialog();

				if (fragments[3] != null) {
					/*
					 * HiFragment hiFragment = (HiFragment) fragments[3];
					 * hiFragment.errorItem.setVisibility(View.VISIBLE); if
					 * (NetUtils.hasNetwork(MainActivity1.this))
					 * hiFragment.errorText.setText("同一账号在其他设备上登陆"); else
					 * hiFragment.errorText.setText("当前网络不可用，请检查网络设置");
					 */
				}

			} else {
				if (fragments[3] != null) {
					/*
					 * HiFragment hiFragment = (HiFragment) fragments[3];
					 * hiFragment.errorItem.setVisibility(View.VISIBLE); if
					 * (NetUtils.hasNetwork(MainActivity1.this))
					 * hiFragment.errorText.setText("连接不到聊天服务器"); else
					 * hiFragment.errorText.setText("当前网络不可用，请检查网络设置");
					 */

				}
			}
		}

		@Override
		public void onReConnected() {
			/*
			 * if (fragments[3] != null) { HiFragment hiFragment = (HiFragment)
			 * fragments[3]; hiFragment.errorItem.setVisibility(View.GONE); }
			 */
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
						MainActivity1.this.finish();
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
				// updateUnreadLabel();
				// updateUnreadAddressLable();
				EMChatManager.getInstance().activityResumed();
			}
		}

		if (is_music_bar_visible) {
			autoCloseMusicBar();
		}
	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (menu_index_current != 3) {
			if (count > 0) {
				showMenuTip(3);
			} else {
				hideMenuTip(3);
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

}