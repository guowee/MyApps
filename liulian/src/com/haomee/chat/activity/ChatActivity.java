/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.haomee.chat.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.haomee.adapter.NewYanWenZiAdapter;
import com.haomee.chat.Utils.CommonUtils;
import com.haomee.chat.Utils.ImageUtils;
import com.haomee.chat.adapter.MessageAdapter;
import com.haomee.chat.adapter.NewExpressAdapter;
import com.haomee.chat.adapter.NewExpressAdapter2;
import com.haomee.chat.adapter.VoicePlayClickListener;
import com.haomee.chat.db.InviteMessgeDao;
import com.haomee.chat.domain.Constant;
import com.haomee.chat.domain.User;
import com.haomee.chat.task.EmotionDownloadTask;
import com.haomee.chat.widget.PasteEditText;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.NewExpression;
import com.haomee.liulian.BaseActivity;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.LoginPageActivity;
import com.haomee.liulian.R;
import com.haomee.liulian.ReportActivity1;
import com.haomee.player.MySoundPlayer;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.util.ViewUtil;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 聊天页面
 */
public class ChatActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {
	protected static final String TAG = "ChatActivity";
	private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
	public static final int REQUEST_CODE_CONTEXT_MENU = 3;
	private static final int REQUEST_CODE_MAP = 4;
	public static final int REQUEST_CODE_TEXT = 5;
	public static final int REQUEST_CODE_VOICE = 6;
	public static final int REQUEST_CODE_PICTURE = 7;
	public static final int REQUEST_CODE_LOCATION = 8;
	public static final int REQUEST_CODE_NET_DISK = 9;
	public static final int REQUEST_CODE_FILE = 10;
	public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
	public static final int REQUEST_CODE_PICK_VIDEO = 12;
	public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
	public static final int REQUEST_CODE_VIDEO = 14;
	public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
	public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
	public static final int REQUEST_CODE_SEND_USER_CARD = 17;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
	public static final int REQUEST_CODE_GROUP_DETAIL = 21;
	public static final int REQUEST_CODE_SELECT_VIDEO = 23;
	public static final int REQUEST_CODE_SELECT_FILE = 24;
	public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

	public static final int RESULT_CODE_COPY = 1;
	public static final int RESULT_CODE_DELETE = 2;
	public static final int RESULT_CODE_FORWARD = 3;
	public static final int RESULT_CODE_OPEN = 4;
	public static final int RESULT_CODE_DWONLOAD = 5;
	public static final int RESULT_CODE_TO_CLOUD = 6;
	public static final int RESULT_CODE_EXIT_GROUP = 7;

	public static final int REQUEST_CODE_FROM_GAME = 8;

	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;
	public String playMsgId;
	public static final String COPY_IMAGE = "EASEMOBIMG";
	private View recordingContainer, game_container;
	private ImageView micImage;
	private TextView recordingHint;
	private ListView listView;
	private PasteEditText mEditTextContent;
	private ImageView buttonSetModeVoice;
	private View buttonSend;
	private View buttonPressToSpeak;
	private LinearLayout expressionContainer;
	private LinearLayout more_container;
	private View all_container;
	private int position;
	private ClipboardManager clipboard;
	private InputMethodManager manager;
	private List<String> reslist;
	private List<NewExpression> expressImages;
	private Drawable[] micImages;
	private int chatType;
	private String nickname;
	private EMConversation conversation;
	private NewMessageBroadcastReceiver receiver;
	public static ChatActivity activityInstance = null;
	// 给谁发送消息
	private String toChatUsername;
	private String uId;
	private VoiceRecorder voiceRecorder;
	private MessageAdapter adapter;
	private File cameraFile;
	static int resendPos;

	// private GroupListener groupListener;

	private ImageView bt_emoticons;
	private RelativeLayout edittext_layout;
	private ProgressBar loadmorePB;
	private boolean isloading;
	private final int pagesize = 20;
	private boolean haveMoreData = true;
	private ImageView btnMore;

	// 表情
	private int new_expression_page_0, new_expression_page_1, new_expression_page_2 = 0;
	private ImageView[] tips_anim1, tips_anim2, tips_anim3;

	private boolean is_from_content = false;// 判断是否需要携带图文信息
	private TextView attachment_content;
	private ImageView attachment_close;
	private LinearLayout attachment_linear;

	private int package_id = 0;

	private int screen_width;

	private boolean has_initial_exp1 = false;
	private boolean has_initial_exp2 = false;
	private boolean has_initial_exp3 = false;

	private TextView report;
	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			micImage.setImageDrawable(micImages[msg.what]);
		}
	};
	private InviteMessgeDao inviteMessgeDao;

	private boolean is_first = false;

	// 问答
	private ImageView bt_question, btn_game;
	private View questionContainer;

	private ArrayList<String> bad_words_list;// 文件中脏次
	private ArrayList<String> warn_words_list;// 文件中警告词
	private ArrayList<String> bad_words_loca_list;// 本地脏次
	private ArrayList<String> warn_words_loca_list;// 本地警告词
	private SharedPreferences preferences_chat_user;

	private List<String> emotions_package_names_list;
	private String emotions_base_path;
	private List<String> image_path;// 存放表情图片的地址
	private List<String> image_name;// 存放表情的name
	private String big_cover_name = "big";// 大表情
	private String simall_cover_name = "small";// 小表情
	private LinearLayout.LayoutParams layoutParams;
	private List<ImageView> imag_list;// 存放底部表情
	private String selected_pager;
	private ImageView iv_expression_emoji;
	private LinearLayout ll_emotions_content, ll_points;
	private View viewpager;
	private String tab_emotions_tag;
	private LayoutInflater inflater_bottom_expression;
	private static int TYPE_IMAGE = 0;
	private static int TYPE_GIF = 1;
	private ViewPager expressionViewpager;
	private MyPagerAdapter pager_adapter;
	private ImageView tips_anim_emoji[];
	private String new_emotion_file_path;// 新表情文件路径
	private FrameLayout fl_emotions_list;
	private boolean is_fist_loading = true;
	private MyDownloadReceiver download_receiver;
	private LinearLayout ll_tab_emtoins_content;
	private static int TYPE_BIG = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity_chat);
		activityInstance = this;

		this.screen_width = ViewUtil.getScreenWidth(activityInstance);
		if (EMChat.getInstance().isLoggedIn()) {
			activityInstance = ChatActivity.this;
			this.screen_width = ViewUtil.getScreenWidth(activityInstance);
			initView();
			setUpView();
			initData();
		} else {
			LiuLianApplication.getInstance().logout();
			Intent intent = new Intent();
			intent.setClass(this, LoginPageActivity.class);
			startActivity(intent);
			this.finish();
		}

		/**
		 * 直接say hi
		 */
		if (getIntent().getBooleanExtra("is_from_hi", false)) {
			sendText("hi");
		}

		getBadWords2();// 获取臧词
		initLocaBadWords2();// 添加本地臧词

		/**
		 * 添加表情
		 */
		findViewById(R.id.more_emotions).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ChatActivity.this, MoreEmotionsActivity.class);
				startActivity(intent);

			}
		});
		download_receiver = new MyDownloadReceiver();
		IntentFilter filter = new IntentFilter("MyReceiver_Emotion_download");
		registerReceiver(download_receiver, filter);
		init_emotions_view();

		is_first_load_emotions();// 加载一次表情
	}

	private void init_emotions_view() {
		inflater_bottom_expression = LayoutInflater.from(ChatActivity.this);
		ll_emotions_content = (LinearLayout) findViewById(R.id.ll_emotions_content);

		emotions_package_names_list = new ArrayList<String>();
		emotions_base_path = FileDownloadUtil.getDefaultLocalDir(PathConst.DOWNLOAD_EMOTIONS);
		layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		imag_list = new ArrayList<ImageView>();

		ll_tab_emtoins_content = (LinearLayout) findViewById(R.id.ll_tab_emtoins_content);

		fl_emotions_list = (FrameLayout) findViewById(R.id.fl_emotions_list);// 表情列表
		viewpager = inflater_bottom_expression.inflate(R.layout.chat_emotions_viewpager, null);
		expressionViewpager = (ViewPager) viewpager.findViewById(R.id.pager_list);
		expressionViewpager.setOnPageChangeListener(this);

		pager_adapter = new MyPagerAdapter();
		expressionViewpager.setAdapter(pager_adapter);
		ll_points = (LinearLayout) viewpager.findViewById(R.id.ll_points);
		fl_emotions_list.addView(viewpager);

		iv_expression_emoji = (ImageView) findViewById(R.id.expression_emoji);
		iv_expression_emoji.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				return_normal();// 恢复未选中状态
				init_yanwenzi();// 显示颜文字
				iv_expression_emoji.setBackgroundResource(R.drawable.grid_line_press);
			}
		});

	}

	/**
	 * 获取本地表情文件包名
	 */
	private List<String> get_local_emotions_package_name() {
		List<String> package_name_list = new ArrayList<String>();
		String emotions_base_path = FileDownloadUtil.getDefaultLocalDir(PathConst.DOWNLOAD_EMOTIONS);
		File file = new File(emotions_base_path);
		File[] files = file.listFiles();// 获取所有表情目录文件
		for (File f : files) {
			package_name_list.add(f.getName());
		}
		return package_name_list;
	}

	/**
	 * 获取要下载的表情包的名字
	 */
	private String get_package_name(String package_path) {
		String[] split = package_path.split("/");
		String[] split2 = split[split.length - 1].split("\\.");
		return split2[0];
	}

	/**
	 * 加载一次表情
	 */
	private void is_first_load_emotions() {

		List<String> package_emotions_list = get_local_emotions_package_name();// 获取本地表情包名

		final SharedPreferences share_prefenrence_load = getSharedPreferences("is_first_load", Context.MODE_PRIVATE);
		boolean is_first_load = share_prefenrence_load.getBoolean("is_first_load_flag", true);// 判断是否是第一次加载
		if (is_first_load) {// 如果是第一次
			if (!NetworkUtil.dataConnected(this)) {
				MyToast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
				return;
			}
			if (package_emotions_list != null) {
				if (!package_emotions_list.contains(get_package_name(PathConst.URL_EMOTION_FIRST))) {
					new EmotionDownloadTask(ChatActivity.this, PathConst.URL_EMOTION_FIRST, null, null, 0).execute();
				}
				if (!package_emotions_list.contains(get_package_name(PathConst.URL_EMOTION_SECOND))) {
					new EmotionDownloadTask(ChatActivity.this, PathConst.URL_EMOTION_SECOND, null, null, 0).execute();
				}
				if (!package_emotions_list.contains(get_package_name(PathConst.URL_EMOTION_THRID))) {
					new EmotionDownloadTask(ChatActivity.this, PathConst.URL_EMOTION_THRID, null, null, 0).execute();
				}
			}

			Editor editor = share_prefenrence_load.edit();
			editor.putBoolean("is_first_load_flag", false);
			editor.commit();
		}
	}

	public void initData() {
		// 注册一个离线消息的BroadcastReceiver
		IntentFilter offlineMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getOfflineMessageBroadcastAction());
		registerReceiver(offlineMessageReceiver, offlineMessageIntentFilter);

	}

	public String getToChatUsername() {
		return toChatUsername;
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
	 * initView
	 */
	protected void initView() {
		report = (TextView) findViewById(R.id.report);
		report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(ChatActivity.this, ReportActivity1.class);
				intent.putExtra("uid", uId);
				intent.putExtra("nickname", nickname);
				ChatActivity.this.startActivity(intent);
			}
		});
		attachment_linear = (LinearLayout) findViewById(R.id.attachment_linear);
		attachment_content = (TextView) findViewById(R.id.attachment_content);
		attachment_close = (ImageView) findViewById(R.id.attachment_close);
		attachment_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attachment_linear.setVisibility(View.GONE);
			}
		});
		recordingContainer = findViewById(R.id.recording_container);
		game_container = findViewById(R.id.game_container);
		micImage = (ImageView) findViewById(R.id.mic_image);
		recordingHint = (TextView) findViewById(R.id.recording_hint);
		listView = (ListView) findViewById(R.id.list);
		mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
		// buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
		edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
		buttonSetModeVoice = (ImageView) findViewById(R.id.btn_set_mode_voice);

		buttonSend = findViewById(R.id.btn_send);
		buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
		expressionContainer = (LinearLayout) findViewById(R.id.ll_face_container);
		more_container = (LinearLayout) findViewById(R.id.more_container);
		// locationImgview = (ImageView) findViewById(R.id.btn_location);
		bt_emoticons = (ImageView) findViewById(R.id.bt_emoticons);
		loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
		btnMore = (ImageView) findViewById(R.id.btn_more);
		all_container = findViewById(R.id.all_container);
		edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);

		bt_question = (ImageView) findViewById(R.id.bt_question);
		btn_game = (ImageView) findViewById(R.id.btn_game);
		questionContainer = findViewById(R.id.question_container);

		// 动画资源文件,用于录制语音时
		micImages = new Drawable[] { getResources().getDrawable(R.drawable.record_animate_01), getResources().getDrawable(R.drawable.record_animate_02), getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_04), getResources().getDrawable(R.drawable.record_animate_05), getResources().getDrawable(R.drawable.record_animate_06), getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08), getResources().getDrawable(R.drawable.record_animate_09), getResources().getDrawable(R.drawable.record_animate_10), getResources().getDrawable(R.drawable.record_animate_11),
				getResources().getDrawable(R.drawable.record_animate_12), getResources().getDrawable(R.drawable.record_animate_13), getResources().getDrawable(R.drawable.record_animate_14) };

		// expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
		edittext_layout.requestFocus();
		voiceRecorder = new VoiceRecorder(micImageHandler);
		buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
		mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
				} else {
					edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}
			}
		});

		// 监听文字框
		mEditTextContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(s)) {
					buttonSetModeVoice.setVisibility(View.GONE);
					buttonSend.setVisibility(View.VISIBLE);
				} else {
					buttonSetModeVoice.setVisibility(View.VISIBLE);
					buttonSend.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private void init_from_data() {
		chatType = getIntent().getIntExtra("chatType", CHATTYPE_SINGLE);
		is_from_content = getIntent().getBooleanExtra("is_from_content", false);
		toChatUsername = getIntent().getStringExtra("userId");// 聊天对象的环信id用户和环信用户聊天
		uId = getIntent().getStringExtra("uId");// 聊天对象的UID
		nickname = getIntent().getStringExtra("nickname");
		((TextView) findViewById(R.id.name)).setText(nickname);
		if (is_from_content) {
			attachment_linear.setVisibility(View.VISIBLE);

			String temp_title = getIntent().getStringExtra("attachmentThemeTitle");
			if (!temp_title.equals("")) {
				attachment_content.setText(temp_title);
			} else {
				attachment_content.setText("你的内容很赞哦！");
			}
		}
	}

	private void setUpView() {
		// position = getIntent().getIntExtra("position", -1);
		clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
		init_from_data();

		// conversation =
		// EMChatManager.getInstance().getConversation(toChatUsername,false);
		conversation = EMChatManager.getInstance().getConversation(toChatUsername);
		// 把此会话的未读数置为0
		conversation.resetUnsetMsgCount();
		adapter = new MessageAdapter(this, toChatUsername, chatType, uId);
		// 显示消息
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new ListScrollListener());
		int count = listView.getCount();
		if (count > 0) {
			listView.setSelection(count - 1);
		}

		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				hideBottom();
				return false;
			}
		});
		// 注册接收消息广播
		receiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		// 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
		intentFilter.setPriority(5);
		registerReceiver(receiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(5);
		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

		String forward_msg_id = getIntent().getStringExtra("forward_msg_id");
		if (forward_msg_id != null) {
			// 显示发送要转发的消息
			forwardMessage(forward_msg_id);
		}

	}

	/**
	 * 转发消息
	 * 
	 * @param forward_msg_id
	 */
	protected void forwardMessage(String forward_msg_id) {
		EMMessage forward_msg = EMChatManager.getInstance().getMessage(forward_msg_id);
		EMMessage.Type type = forward_msg.getType();
		switch (type) {
		case TXT:
			// 获取消息内容，发送消息
			String content = ((TextMessageBody) forward_msg.getBody()).getMessage();
			sendText(content);
			break;
		case IMAGE:
			// 发送图片
			String filePath = ((ImageMessageBody) forward_msg.getBody()).getLocalUrl();
			if (filePath != null) {
				File file = new File(filePath);
				if (!file.exists()) {
					// 不存在大图发送缩略图
					filePath = ImageUtils.getThumbnailImagePath(filePath);
				}
				sendPicture(filePath);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * onActivityResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CODE_EXIT_GROUP) {
			setResult(RESULT_OK);
			finish();
			return;
		}
		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
			switch (resultCode) {
			case RESULT_CODE_COPY: // 复制消息
				EMMessage copyMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));
				if (copyMsg.getType() == EMMessage.Type.IMAGE) {
					ImageMessageBody imageBody = (ImageMessageBody) copyMsg.getBody();
					// 加上一个特定前缀，粘贴时知道这是要粘贴一个图片
					clipboard.setText(COPY_IMAGE + imageBody.getLocalUrl());
				} else {
					// clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
					// ((TextMessageBody) copyMsg.getBody()).getMessage()));
					clipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
				}
				break;
			case RESULT_CODE_DELETE: // 删除消息
				EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
				conversation.removeMessage(deleteMsg.getMsgId());
				adapter.refresh();
				listView.setSelection(data.getIntExtra("position", adapter.getCount()) - 1);
				break;
			/*
			 * case RESULT_CODE_FORWARD: // 转发消息 EMMessage forwardMsg =
			 * (EMMessage) adapter.getItem(data.getIntExtra("position", 0));
			 * Intent intent = new Intent(this, ForwardMessageActivity.class);
			 * intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
			 * startActivity(intent);
			 * 
			 * break;
			 */
			default:
				break;
			}
		} else if (requestCode == REQUEST_CODE_FROM_GAME) {
			LiuLianApplication.PUBLIC_GAME_ID = "";
		}
		if (resultCode == RESULT_OK) { // 清空消息
			if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
				// 清空会话
				EMChatManager.getInstance().clearConversation(toChatUsername);
				adapter.refresh();
			} else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
				if (cameraFile != null && cameraFile.exists())
					sendPicture(cameraFile.getAbsolutePath());
			} else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // 发送本地选择的视频

				int duration = data.getIntExtra("dur", 0);
				String videoPath = data.getStringExtra("path");
				File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
				Bitmap bitmap = null;
				FileOutputStream fos = null;
				try {
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
					if (bitmap == null) {
						EMLog.d("chatactivity", "problem load video thumbnail bitmap,use default icon");
						bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_panel_video_icon);
					}
					fos = new FileOutputStream(file);

					bitmap.compress(CompressFormat.JPEG, 100, fos);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						fos = null;
					}
					if (bitmap != null) {
						bitmap.recycle();
						bitmap = null;
					}

				}
				sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);

			} else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
					}
				}
			} else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
				if (data != null) {
					Uri uri = data.getData();
					if (uri != null) {
						sendFile(uri);
					}
				}

			} else if (requestCode == REQUEST_CODE_MAP) { // 地图
				double latitude = data.getDoubleExtra("latitude", 0);
				double longitude = data.getDoubleExtra("longitude", 0);
				String locationAddress = data.getStringExtra("address");
				if (locationAddress != null && !locationAddress.equals("")) {
					more(all_container);
					sendLocationMsg(latitude, longitude, "", locationAddress);
				} else {
					MyToast.makeText(this, "无法获取到您的位置信息！", 0).show();
				}
				// 重发消息
			} else if (requestCode == REQUEST_CODE_TEXT) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_VOICE) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_PICTURE) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_LOCATION) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_VIDEO || requestCode == REQUEST_CODE_FILE) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
				// 粘贴
				if (!TextUtils.isEmpty(clipboard.getText())) {
					String pasteText = clipboard.getText().toString();
					if (pasteText.startsWith(COPY_IMAGE)) {
						// 把图片前缀去掉，还原成正常的path
						sendPicture(pasteText.replace(COPY_IMAGE, ""));
					}

				}
			} else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
				EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
				addUserToBlacklist(deleteMsg.getFrom());
			} else if (conversation.getMsgCount() > 0) {
				adapter.refresh();
				setResult(RESULT_OK);
			} else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
				adapter.refresh();
			}
		}
	}

	/**
	 * 消息图标点击事件
	 * 
	 * @param view
	 */
	@Override
	public void onClick(View view) {

		int id = view.getId();
		if (id == R.id.btn_send) {// 点击发送按钮(发文字和表情)
			String s = mEditTextContent.getText().toString();
			if (bad_words_list == null || bad_words_list.size() == 0) {
				boolean is_bad = bad_words_loca_list.contains(s);
				if (is_bad) {
					MyToast.makeText(this, "您发送的信息包含敏感词", Toast.LENGTH_SHORT).show();
				} else {
					sendText(s);
				}
			} else {
				boolean is_also_bad = bad_words_list.contains(s);
				if (is_also_bad) {
					MyToast.makeText(this, "您发送的信息包含敏感词", Toast.LENGTH_SHORT).show();
				} else {
					sendText(s);
				}
			}
		} else if (id == R.id.btn_take_picture) {
			selectPicFromCamera();// 点击照相图标
		} else if (id == R.id.btn_picture) {
			selectPicFromLocal(); // 点击图片图标
		}
		/*
		 * else if (id == R.id.btn_location) { // 位置
		 * //startActivityForResult(new Intent(this, BaiduMapActivity.class),
		 * REQUEST_CODE_MAP); } else if (id == R.id.btn_video) { // 点击摄像图标
		 * Intent intent = new Intent(ChatActivity.this,
		 * ImageGridActivity.class); startActivityForResult(intent,
		 * REQUEST_CODE_SELECT_VIDEO); }
		 */
		else if (id == R.id.btn_file) { // 点击文件图标
			selectFileFromLocal();
		} else if (id == R.id.game_0) {// 点击第一个游戏
			// 发送游戏1
			sendGame("0");
			StatService.onEvent(ChatActivity.this, "count_of_send_game", "发送游戏次数", 1);
		} else if (id == R.id.game_1) {// 点击第2个游戏
			// 发送游戏
			sendGame("1");
			StatService.onEvent(ChatActivity.this, "count_of_send_game", "发送游戏次数", 1);
		} else if (id == R.id.game_2) {// 点击第3个游戏
			MyToast.makeText(ChatActivity.this, "游戏尚未开启，敬请期待", 1).show();
		} else if (id == R.id.game_3) {// 点击第4个游戏
			MyToast.makeText(ChatActivity.this, "游戏尚未开启，敬请期待", 1).show();
		}
	}

	public void sendGame(final String level) {
		post_two_first_chat();
		if (NetworkUtil.dataConnected(this)) {

			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams re = new RequestParams();
			re.put("myself", LiuLianApplication.current_user.getHx_username());
			re.put("other", toChatUsername);
			re.put("level", level);
			client.post(PathConst.URL_CREATE_GAME, re, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg0) {
					try {
						if (arg0 == null || arg0.equals("")) {
							return;
						}
						JSONObject json_obj = new JSONObject(arg0);
						String flag = json_obj.optString("flag");
						if (flag.equals("1")) {
							LiuLianApplication.PUBLIC_GAME_ID = json_obj.optString("id");
							EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
							// 如果是群聊，设置chattype,默认是单聊
							TextMessageBody txtBody = new TextMessageBody("");
							// 设置消息body
							message.addBody(txtBody);
							message.setAttribute("chatGameId", json_obj.optString("id"));
							message.setAttribute("chatGameLevel", level);
							message.setAttribute("chatGameState", "chatGameStateStart");
							message.setReceipt(toChatUsername);
							conversation.addMessage(message);
							adapter.refresh();
							listView.setSelection(listView.getCount() - 1);
							mEditTextContent.setText("");
							attachment_linear.setVisibility(View.GONE);
							setResult(RESULT_OK);
							startRefresh();
						} else {
							MyToast.makeText(ChatActivity.this, json_obj.optString("msg"), 1).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			MyToast.makeText(this, getResources().getString(R.string.no_network), 1).show();
		}
	}

	public void sendGame(String level, String game_id, int state) {
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		// 如果是群聊，设置chattype,默认是单聊
		TextMessageBody txtBody = new TextMessageBody("");
		// 设置消息body
		message.addBody(txtBody);
		message.setAttribute("chatGameId", game_id);
		message.setAttribute("chatGameLevel", level);
		if (state == 0) {
			message.setAttribute("chatGameState", "chatGameStateStart");
		} else if (state == 1) {
			message.setAttribute("chatGameState", "chatGameStateAgree");
		}
		message.setReceipt(toChatUsername);
		conversation.addMessage(message);
		adapter.refresh();
		listView.setSelection(listView.getCount() - 1);
		mEditTextContent.setText("");
		attachment_linear.setVisibility(View.GONE);
		setResult(RESULT_OK);
		startRefresh();
	}

	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
			MyToast.makeText(getApplicationContext(), "SD卡不存在，不能拍照", 0).show();
			return;
		}

		cameraFile = new File(PathUtil.getInstance().getImagePath(), LiuLianApplication.current_user.getHx_username() + System.currentTimeMillis() + ".jpg");
		cameraFile.getParentFile().mkdirs();
		startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)), REQUEST_CODE_CAMERA);
	}

	/**
	 * 选择文件
	 */
	private void selectFileFromLocal() {
		Intent intent = null;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}

	/***
	 * 两个人第一次聊天
	 */

	private void post_two_first_chat() {
		if (!is_first) {
			// 请求接口
			if (NetworkUtil.dataConnected(this)) {
				// 获取会话好友头像和昵称
				AsyncHttpClient client = new AsyncHttpClient();
				RequestParams re = new RequestParams();
				re.put("uid1", LiuLianApplication.current_user.getHx_username());
				re.put("uid2", toChatUsername);
				client.get(PathConst.URL_TWO_PEOPLE__FIRST_CHAT, re, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String arg0) {
						if (arg0 == null || arg0.equals("")) {
							return;
						}
						try {
							JSONObject json_obj = new JSONObject("arg0");
							if (json_obj.optString("flag").equals("1")) {
								is_first = true;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});
			}
		}
	}

	/**
	 * 发送文本消息
	 * 
	 * @param content
	 *            message content
	 * @param isResend
	 *            boolean resend
	 */
	private void sendText(String content) {
		post_two_first_chat();
		if (content.length() > 0) {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			// 如果是群聊，设置chattype,默认是单聊
			TextMessageBody txtBody = new TextMessageBody(content);
			// 设置消息body
			message.addBody(txtBody);
			// 增加自己特定的属性,目前sdk支持int,boolean,String这三种属性，可以设置多个扩展属性
			if (attachment_linear.isShown()) {

				/*
				 * if
				 * (!getIntent().getStringExtra("attachmentImage").equals(""))
				 * {// 显示图片地址 message.setAttribute("attachmentImage",
				 * getIntent().getStringExtra("attachmentImage")); } if
				 * (!getIntent().getStringExtra("attachmentContent").equals(""))
				 * {// 显示原图内容 message.setAttribute("attachmentContent",
				 * getIntent().getStringExtra("attachmentContent")); }
				 */
				if (!getIntent().getStringExtra("attachmentThemeTitle").equals("")) {// 显示原图内容
					message.setAttribute("attachmentThemeTitle", getIntent().getStringExtra("attachmentThemeTitle"));
				}
				if (!getIntent().getStringExtra("attachmentId").equals("")) {// 显示原图内容
					message.setAttribute("attachmentId", getIntent().getStringExtra("attachmentId"));
				}
				StatService.onEvent(ChatActivity.this, "chat_send_content", "发送带context内容的文字", 1);
			} else {// 不带任何内容
				message.setAttribute("attachmentId", "");
				StatService.onEvent(ChatActivity.this, "chat_send_text", "发送文本", 1);
			}

			// 设置要发给谁,用户username或者群聊groupid
			message.setReceipt(toChatUsername);
			// 把messgage加到conversation中
			conversation.addMessage(message);
			// 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);
			mEditTextContent.setText("");
			attachment_linear.setVisibility(View.GONE);
			setResult(RESULT_OK);

		}
	}

	/**
	 * 发送语音
	 * 
	 * @param filePath
	 * @param fileName
	 * @param length
	 * @param isResend
	 */
	private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
		post_two_first_chat();
		if (!(new File(filePath).exists())) {
			return;
		}
		try {
			final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
			// 如果是群聊，设置chattype,默认是单聊
			if (chatType == CHATTYPE_GROUP)
				message.setChatType(ChatType.GroupChat);
			message.setReceipt(toChatUsername);
			int len = Integer.parseInt(length);
			VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
			message.addBody(body);

			conversation.addMessage(message);
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);
			setResult(RESULT_OK);
			StatService.onEvent(ChatActivity.this, "chat_send_voice", "发送语音", 1);
			// send file
			// sendVoiceSub(filePath, fileName, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送图片
	 * 
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {
		post_two_first_chat();
		String to = toChatUsername;
		// create and add image message in view
		final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == CHATTYPE_GROUP)
			message.setChatType(ChatType.GroupChat);

		message.setReceipt(to);
		ImageMessageBody body = new ImageMessageBody(new File(filePath));
		// 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
		// body.setSendOriginalImage(true)
		message.addBody(body);
		conversation.addMessage(message);

		listView.setAdapter(adapter);
		adapter.refresh();
		listView.setSelection(listView.getCount() - 1);
		setResult(RESULT_OK);
		StatService.onEvent(ChatActivity.this, "chat_send_image", "发送图片", 1);
		// more(more);
	}

	/**
	 * 发送视频消息
	 */
	private void sendVideo(final String filePath, final String thumbPath, final int length) {
		final File videoFile = new File(filePath);
		if (!videoFile.exists()) {
			return;
		}
		try {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VIDEO);
			// 如果是群聊，设置chattype,默认是单聊
			if (chatType == CHATTYPE_GROUP)
				message.setChatType(ChatType.GroupChat);
			String to = toChatUsername;
			message.setReceipt(to);
			VideoMessageBody body = new VideoMessageBody(videoFile, thumbPath, length, videoFile.length());
			message.addBody(body);
			conversation.addMessage(message);
			listView.setAdapter(adapter);
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);
			setResult(RESULT_OK);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 根据图库图片uri发送图片
	 * 
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			sendPicture(file.getAbsolutePath());
		}

	}

	/**
	 * 发送位置信息
	 * 
	 * @param latitude
	 * @param longitude
	 * @param imagePath
	 * @param locationAddress
	 */
	private void sendLocationMsg(double latitude, double longitude, String imagePath, String locationAddress) {
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == CHATTYPE_GROUP)
			message.setChatType(ChatType.GroupChat);
		LocationMessageBody locBody = new LocationMessageBody(locationAddress, latitude, longitude);
		message.addBody(locBody);
		message.setReceipt(toChatUsername);
		conversation.addMessage(message);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listView.setSelection(listView.getCount() - 1);
		setResult(RESULT_OK);

	}

	/**
	 * 发送文件
	 * 
	 * @param uri
	 */
	private void sendFile(Uri uri) {
		String filePath = null;
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					filePath = cursor.getString(column_index);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			filePath = uri.getPath();
		}
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			MyToast.makeText(getApplicationContext(), "文件不存在", 0).show();
			return;
		}
		if (file.length() > 10 * 1024 * 1024) {
			MyToast.makeText(getApplicationContext(), "文件不能大于10M", 0).show();
			return;
		}

		// 创建一个文件消息
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == CHATTYPE_GROUP)
			message.setChatType(ChatType.GroupChat);

		message.setReceipt(toChatUsername);
		// add message body
		NormalFileMessageBody body = new NormalFileMessageBody(new File(filePath));
		message.addBody(body);

		conversation.addMessage(message);
		listView.setAdapter(adapter);
		adapter.refresh();
		listView.setSelection(listView.getCount() - 1);
		setResult(RESULT_OK);
	}

	/**
	 * 重发消息
	 */
	private void resendMessage() {
		EMMessage msg = null;
		msg = conversation.getMessage(resendPos);
		// msg.setBackSend(true);
		msg.status = EMMessage.Status.CREATE;

		adapter.refresh();
		listView.setSelection(resendPos);
	}

	/**
	 * 显示语音图标按钮
	 * 
	 * @param view
	 */
	public void setModeVoice(View view) {
		hideKeyboard();
		all_container.setVisibility(View.VISIBLE);
		recordingContainer.setVisibility(View.VISIBLE);
		more_container.setVisibility(View.GONE);
		expressionContainer.setVisibility(View.GONE);
		questionContainer.setVisibility(View.GONE);
		btn_game.setImageResource(R.drawable.chat_game_default);
		buttonSetModeVoice.setImageResource(R.drawable.talk_button_voice_pressed);
		bt_question.setImageResource(R.drawable.chatting_wenda_btn);
		bt_emoticons.setImageResource(R.drawable.chatting_biaoqing_btn);
		btnMore.setImageResource(R.drawable.type_select_btn_default);

		// all_container.setVisibility(View.GONE);

	}

	/**
	 * 隐藏底部模块
	 */
	private void hideBottom() {
		all_container.setVisibility(View.GONE);

		recordingContainer.setVisibility(View.GONE);
		more_container.setVisibility(View.GONE);
		expressionContainer.setVisibility(View.GONE);
		questionContainer.setVisibility(View.GONE);
		game_container.setVisibility(View.GONE);
		btn_game.setImageResource(R.drawable.chat_game_default);
		bt_question.setImageResource(R.drawable.chatting_wenda_btn);
		bt_emoticons.setImageResource(R.drawable.chatting_biaoqing_btn);
		btnMore.setImageResource(R.drawable.type_select_btn_default);
	}

	/**
	 * 点击清空聊天记录
	 * 
	 * @param view
	 */
	public void emptyHistory(View view) {
		startActivityForResult(new Intent(this, AlertDialog.class).putExtra("titleIsCancel", true).putExtra("msg", "是否清空所有聊天记录").putExtra("cancel", true), REQUEST_CODE_EMPTY_HISTORY);
	}

	/**
	 * 点击进入群组详情
	 * 
	 * @param view
	 */
	/*
	 * public void toGroupDetails(View view) { startActivityForResult((new
	 * Intent(this, GroupDetailsActivity.class).putExtra("groupId",
	 * toChatUsername)), REQUEST_CODE_GROUP_DETAIL); }
	 */
	/**
	 * 显示或隐藏图标按钮页
	 * 
	 * @param view
	 */
	public void more(View view) {

		hideKeyboard();
		all_container.setVisibility(View.VISIBLE);
		recordingContainer.setVisibility(View.GONE);
		more_container.setVisibility(View.VISIBLE);
		expressionContainer.setVisibility(View.GONE);
		questionContainer.setVisibility(View.GONE);
		game_container.setVisibility(View.GONE);
		btn_game.setImageResource(R.drawable.chat_game_default);
		bt_question.setImageResource(R.drawable.chatting_wenda_btn);
		bt_emoticons.setImageResource(R.drawable.chatting_biaoqing_btn);
		btnMore.setImageResource(R.drawable.type_select_btn_pressed);
	}

	/**
	 * 显示或隐藏图标按钮页
	 * 
	 * @param view
	 */
	public void question(View view) {

		hideKeyboard();
		all_container.setVisibility(View.VISIBLE);
		recordingContainer.setVisibility(View.GONE);
		more_container.setVisibility(View.GONE);
		expressionContainer.setVisibility(View.GONE);
		questionContainer.setVisibility(View.VISIBLE);
		btn_game.setImageResource(R.drawable.chat_game_default);
		bt_question.setImageResource(R.drawable.chatting_wenda_btn_enable);
		bt_emoticons.setImageResource(R.drawable.chatting_biaoqing_btn);
		btnMore.setImageResource(R.drawable.type_select_btn_default);

		startLiulianAnim();
	}

	public void showEmoticons(View view) {
		hideKeyboard();
		// if (!has_initial_exp1) {
		// init_expression1();
		//
		//
		// }
		if (is_fist_loading) {
			init_local_emotions();
		}
		ll_tab_emtoins_content.setVisibility(View.VISIBLE);
		all_container.setVisibility(View.VISIBLE);
		recordingContainer.setVisibility(View.GONE);
		more_container.setVisibility(View.GONE);
		questionContainer.setVisibility(View.GONE);
		expressionContainer.setVisibility(View.VISIBLE);
		btn_game.setImageResource(R.drawable.chat_game_default);
		bt_question.setImageResource(R.drawable.chatting_wenda_btn);
		bt_emoticons.setImageResource(R.drawable.chatting_biaoqing_btn_enable);
		btnMore.setImageResource(R.drawable.type_select_btn_default);

	}

	/**
	 * 加载本地表情
	 */
	private void init_local_emotions() {
		List local_emotins = search_local_emotins();
		if (local_emotins != null && local_emotins.size() != 0) {
			for (int index = 0; index < local_emotins.size(); index++) {
				init_new_emotions((String) local_emotins.get(index));
			}
			is_fist_loading = false;

		}
		init_yanwenzi();// 显示颜文字
	}

	public List<String> getYanWenZi() {
		List<String> reslist = new ArrayList<String>();
		for (int i = 0; i < CommonConst.YANWENZI.length; i++) {
			reslist.add(CommonConst.YANWENZI[i]);
		}
		return reslist;
	}

	/**
	 * 显示颜文字
	 */
	private void init_yanwenzi() {

		List<View> views_0 = new ArrayList<View>();
		List<String> yanWenZi_list = getYanWenZi();
		int total_page = (yanWenZi_list.size() - 1) / 12 + 1;
		for (int cur_page_anim = 0; cur_page_anim < total_page; cur_page_anim++) {
			views_0.add(inflateYanWenZi(cur_page_anim, yanWenZi_list));
		}
		ll_points.removeAllViews();
		if (total_page > 1) {
			add_points(total_page, ll_points);// 添加小圆点
		}
		expressionViewpager.setAdapter(pager_adapter);
		pager_adapter.setData(views_0, image_path, 1);

	}

	/**
	 * 初始化颜文字
	 */
	private View inflateYanWenZi(int current_page, List<String> list) {

		View view = LayoutInflater.from(this).inflate(R.layout.yanwenzi_grid, null);
		GridView grid = (GridView) view.findViewById(R.id.gridView1);
		grid.setSelector(new ColorDrawable(Color.TRANSPARENT));

		final NewYanWenZiAdapter new_express_adapter = new NewYanWenZiAdapter(ChatActivity.this);
		grid.setAdapter(new_express_adapter);

		int total_page = (list.size() - 1) / 12 + 1;
		if (current_page < total_page - 1) {
			new_express_adapter.setData(list.subList(current_page * 12, (current_page + 1) * 12));
		} else {
			new_express_adapter.setData(list.subList(current_page * 12, list.size()));
		}

		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				String wenzi = new_express_adapter.getData().get(position);

				if (!mEditTextContent.getText().toString().equals("")) {
					mEditTextContent.setText(mEditTextContent.getText().toString() + wenzi);
				} else {
					mEditTextContent.setText(wenzi);
				}
				mEditTextContent.setSelection(mEditTextContent.getText().toString().length());
			}
		});

		return view;
	}

	public void showGame(View view) {

		if (LiuLianApplication.GAME_TIME_SEND_RECEIVE == 30) {
			hideKeyboard();
			all_container.setVisibility(View.VISIBLE);
			recordingContainer.setVisibility(View.GONE);
			more_container.setVisibility(View.GONE);
			questionContainer.setVisibility(View.GONE);
			game_container.setVisibility(View.VISIBLE);
			expressionContainer.setVisibility(View.GONE);
			bt_question.setImageResource(R.drawable.chatting_wenda_btn);
			bt_emoticons.setImageResource(R.drawable.chatting_biaoqing_btn);
			btn_game.setImageResource(R.drawable.chat_game_pressed);
			btnMore.setImageResource(R.drawable.type_select_btn_default);
		} else {
			MyToast.makeText(ChatActivity.this, "你还有" + LiuLianApplication.GAME_TIME_SEND_RECEIVE + "秒，才可以继续游戏哦!", 1).show();
		}

	}

	private View q1, q2, q3, q4;
	private ImageView bt_liulian_question, icon_arrow;
	private Animation anim_q1, anim_q2, anim_q3, anim_q4;
	private AnimationDrawable anim_liulian, anim_arrow;

	private void startLiulianAnim() {
		if (bt_liulian_question == null) {

			bt_liulian_question = (ImageView) questionContainer.findViewById(R.id.bt_liulian_question);
			icon_arrow = (ImageView) questionContainer.findViewById(R.id.icon_arrow);
			anim_liulian = (AnimationDrawable) bt_liulian_question.getDrawable();
			anim_arrow = (AnimationDrawable) icon_arrow.getDrawable();

			bt_liulian_question.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// icon_arrow.clearAnimation();
					// icon_arrow.setVisibility(View.GONE);

					anim_liulian.start();
					startQuestionAnim();
				}
			});

		}

		// icon_arrow.setVisibility(View.VISIBLE);

		anim_arrow.start();

	}

	private boolean is_anim_started; // 防止连续快速点击

	private void startQuestionAnim() {

		if (is_anim_started) {
			return;
		}

		MySoundPlayer.getInstance(this).play_background(R.raw.sound_question, false);

		if (q1 == null) {
			q1 = questionContainer.findViewById(R.id.q1);
			q2 = questionContainer.findViewById(R.id.q2);
			q3 = questionContainer.findViewById(R.id.q3);
			q4 = questionContainer.findViewById(R.id.q4);
			anim_liulian = (AnimationDrawable) bt_liulian_question.getDrawable();
			anim_arrow = (AnimationDrawable) ((ImageView) questionContainer.findViewById(R.id.icon_arrow)).getDrawable();

			q1.setVisibility(View.INVISIBLE);
			q2.setVisibility(View.INVISIBLE);
			q3.setVisibility(View.INVISIBLE);
			q4.setVisibility(View.INVISIBLE);

			anim_q1 = AnimationUtils.loadAnimation(this, R.anim.fade_in_fade_out);
			anim_q2 = AnimationUtils.loadAnimation(this, R.anim.fade_in_fade_out);
			anim_q3 = AnimationUtils.loadAnimation(this, R.anim.fade_in_fade_out);
			anim_q4 = AnimationUtils.loadAnimation(this, R.anim.fade_in_fade_out);

			anim_q4.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					is_anim_started = true;
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					sendRandomQuestion();
					is_anim_started = false;

					if (anim_liulian != null) {
						anim_liulian.stop();
					}

				}
			});

			anim_q2.setStartOffset(500);
			anim_q3.setStartOffset(1000);
			anim_q4.setStartOffset(1500);
		}

		q1.startAnimation(anim_q1);
		q2.startAnimation(anim_q2);
		q3.startAnimation(anim_q3);
		q4.startAnimation(anim_q4);

		anim_liulian.start();
		anim_arrow.start();

	}

	private MediaPlayer mediaPlayer_background;

	// 播放背景音乐的
	private void playBackgroundMusic(int resId) {
		try {
			if (mediaPlayer_background == null) {
				mediaPlayer_background = new MediaPlayer();
			}
			mediaPlayer_background.reset();
			AssetFileDescriptor afd = this.getResources().openRawResourceFd(resId);
			if (afd != null) {
				mediaPlayer_background.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
				afd.close();
				mediaPlayer_background.setLooping(false);
				mediaPlayer_background.prepare();
				mediaPlayer_background.start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 点击文字输入框
	 * 
	 * @param v
	 */
	public void editClick(View v) {
		hideBottom();

		listView.setSelection(listView.getCount() - 1);
		if (all_container.getVisibility() == View.VISIBLE) {
			all_container.setVisibility(View.GONE);
			bt_emoticons.setVisibility(View.VISIBLE);
		}

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 隐藏软键盘
		// imm.hideSoftInputFromWindow(editView.getWindowToken(), 0);
		// 显示软键盘
		imm.showSoftInputFromInputMethod(mEditTextContent.getWindowToken(), 0);
		// 切换软键盘的显示与隐藏
		// imm.toggleSoftInputFromWindow(mEditTextContent.getWindowToken(), 0,
		// InputMethodManager.HIDE_NOT_ALWAYS);

	}

	/**
	 * 消息广播接收者
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			String username = intent.getStringExtra("from");

			String msgid = intent.getStringExtra("msgid");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			EMMessage message = EMChatManager.getInstance().getMessage(msgid);
			message.getBooleanAttribute("hasContent", false);
			message.getStringAttribute("attachmentId", "");
			message.getStringAttribute("attachmentThemeTitle", "");

			// 表情attachment

			message.getStringAttribute("chatCustomFacePackageId", "");
			message.getStringAttribute("chatCustomFaceId", "");
			message.getStringAttribute("chatCustomFaceImage", "");
			message.getStringAttribute("chatCustomFaceName", "");
			message.getStringAttribute("chatCustomFaceWidth", "");
			message.getStringAttribute("chatCustomFaceHeight", "");

			// 如果是群聊消息，获取到group id
			if (message.getChatType() == ChatType.GroupChat) {
				username = message.getTo();
			}
			if (!username.equals(toChatUsername)) {
				// 消息不是发给当前会话，return
				return;
			}
			// conversation =
			// EMChatManager.getInstance().getConversation(toChatUsername);
			// 通知adapter有新消息，更新ui
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);
			// 记得把广播给终结掉

			try {
				String chatGameLevel = message.getStringAttribute("chatGameLevel");
				// 表示对方同意
				if (message.getStringAttribute("chatGameState").equals("chatGameStateAgree")) {
					MyToast.makeText(context, "游戏即将开始", 1).show();
					intent.putExtra("game_id", message.getStringAttribute("chatGameId"));
					intent.putExtra("other_id", toChatUsername);
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
				} else if (message.getStringAttribute("chatGameState").equals("chatGameStateResult")) {
					// 返回结果
					intent.putExtra("game_id", message.getStringAttribute("chatGameId"));
					intent.putExtra("chatMyselfScore", message.getStringAttribute("chatMyselfScore"));
					intent.putExtra("chatOtherScore", message.getStringAttribute("chatOtherScore"));
					intent.putExtra("other_id", toChatUsername);
					intent.putExtra("is_result", true);
					if (chatGameLevel.equals("0")) {
						intent.setClass(context, ChatGame1.class);
					} else if (chatGameLevel.equals("1")) {
						intent.setClass(context, ChatGame2.class);
					}
					startActivity(intent);
				}
			} catch (EaseMobException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			abortBroadcast();
		}
	}

	public void startRefresh(final TextView textView) {
		if (LiuLianApplication.GAME_TIMER != null) {
			LiuLianApplication.GAME_TIMER.cancel();
		}
		LiuLianApplication.GAME_TIMER = new Timer();
		LiuLianApplication.GAME_TIMER.schedule(new TimerTask() {
			public void run() {
				if (LiuLianApplication.GAME_TIME_SEND_RECEIVE > 0) {
					LiuLianApplication.GAME_TIME_SEND_RECEIVE--;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(LiuLianApplication.GAME_TIME_SEND_RECEIVE==0){
								textView.setText("已过期");
							}else{
								textView.setText(LiuLianApplication.GAME_TIME_SEND_RECEIVE + "");
							}
							
						}
					});

				} else {
					stopRefresh();
				}
			}
		}, 0, 1000);
	}

	public void startRefresh() {
		hideKeyboard();
		hideBottom();
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
			adapter.notifyDataSetChanged();
		}
	};
	private PowerManager.WakeLock wakeLock;

	/**
	 * 按住说话listener
	 * 
	 */
	class PressToSpeakListen implements View.OnTouchListener {
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.isExitsSdcard()) {
					MyToast.makeText(ChatActivity.this, "发送语音需要sdcard支持！", Toast.LENGTH_SHORT).show();
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();
					if (VoicePlayClickListener.isPlaying)
						VoicePlayClickListener.currentPlayListener.stopPlayVoice();
					recordingContainer.setVisibility(View.VISIBLE);
					recordingHint.setVisibility(View.VISIBLE);
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
					voiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					recordingContainer.setVisibility(View.GONE);
					MyToast.makeText(ChatActivity.this, getResources().getString(R.string.recoding_fail), Toast.LENGTH_SHORT).show();
					return false;
				}
				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					recordingHint.setText(getString(R.string.release_to_cancel));
					// recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
				} else {
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				recordingHint.setVisibility(View.INVISIBLE);
				if (wakeLock.isHeld())
					wakeLock.release();
				if (event.getY() < 0) {
					// discard the recorded audio.
					voiceRecorder.discardRecording();

				} else {
					// stop recording and send voice file
					try {
						int length = voiceRecorder.stopRecoding();
						if (length > 0) {
							sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername), Integer.toString(length), false);
						} else {
							MyToast.makeText(getApplicationContext(), "录音时间太短", 0).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						MyToast.makeText(ChatActivity.this, "发送失败，请检测服务器是否连接", Toast.LENGTH_SHORT).show();
					}

				}
				return true;
			default:
				return false;
			}
		}
	}

	// private View inflatePage(int package_id, int current_page,
	// List<NewExpression> list) {
	//
	// View view = LayoutInflater.from(this).inflate(R.layout.express_grid,
	// null);
	// GridView grid = (GridView) view.findViewById(R.id.gridView1);
	// grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
	//
	// final NewExpressAdapter new_express_adapter = new
	// NewExpressAdapter(ChatActivity.this, package_id);
	// grid.setAdapter(new_express_adapter);
	//
	// int total_page = (list.size() - 1) / 10 + 1;
	// if (current_page < total_page - 1) {
	// new_express_adapter.setData(list.subList(current_page * 10, (current_page
	// + 1) * 10));
	// } else {
	// new_express_adapter.setData(list.subList(current_page * 10,
	// list.size()));
	// }
	// grid.setOnItemClickListener(new OnItemClickListener() {
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View view, int position,
	// long arg3) {
	//
	// NewExpression expression = new_express_adapter.getData().get(position);
	// sendTextByExpression1(expression.getPackageName(),
	// expression.getExpressionId(), expression.getExpressionName(),
	// expression.getExpressionUrl(), expression.getExpressionWidth(),
	// expression.getExpressionHeight());
	//
	// }
	// });
	// return view;
	// }

	public List<NewExpression> getExpressionImages(int package_id) {
		List<NewExpression> reslist = new ArrayList<NewExpression>();
		for (int i = 0; i < 17; i++) {
			NewExpression expression = new NewExpression();
			expression.setExpressionId(i + "");
			expression.setPackageName(package_id + "");
			// expression.setExpressionName(CommonConst.FACE_DESC[package_id][i]);
			expression.setExpressionWidth(100 + "");
			expression.setExpressionHeight(100 + "");
			expression.setExpressionUrl(CommonConst.EXPRESS_URL + package_id + "_" + i + ".png");
			reslist.add(expression);
		}
		return reslist;

	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityInstance = null;
		// EMGroupManager.getInstance().removeGroupChangeListener(groupListener);
		// 注销广播
		try {
			unregisterReceiver(receiver);
			unregisterReceiver(ackMessageReceiver);
			unregisterReceiver(offlineMessageReceiver);
			unregisterReceiver(download_receiver);
			receiver = null;
			ackMessageReceiver = null;
			offlineMessageReceiver = null;
			download_receiver = null;
		} catch (Exception e) {
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.refresh();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (wakeLock.isHeld())
			wakeLock.release();
		if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
			// 停止语音播放
			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
		}

		try {
			// 停止录音
			if (voiceRecorder.isRecording()) {
				voiceRecorder.discardRecording();
				recordingContainer.setVisibility(View.GONE);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 加入到黑名单
	 * 
	 * @param username
	 */
	private void addUserToBlacklist(String username) {
		try {
			EMContactManager.getInstance().addUserToBlackList(username, true);
			MyToast.makeText(getApplicationContext(), "移入黑名单成功", 0).show();
		} catch (EaseMobException e) {
			e.printStackTrace();
			MyToast.makeText(getApplicationContext(), "移入黑名单失败", 0).show();
		}
	}

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}

	/**
	 * 覆盖手机返回键
	 */
	@Override
	public void onBackPressed() {
		if (all_container.getVisibility() == View.VISIBLE) {
			all_container.setVisibility(View.GONE);
			bt_emoticons.setVisibility(View.VISIBLE);
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * listview滑动监听listener
	 * 
	 */
	private class ListScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
					loadmorePB.setVisibility(View.VISIBLE);
					// sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
					List<EMMessage> messages;
					try {
						// 获取更多messges，调用此方法的时候从db获取的messages
						// sdk会自动存入到此conversation中
						if (chatType == CHATTYPE_SINGLE)
							messages = conversation.loadMoreMsgFromDB(adapter.getItem(0).getMsgId(), pagesize);
						else
							messages = conversation.loadMoreGroupMsgFromDB(adapter.getItem(0).getMsgId(), pagesize);
					} catch (Exception e1) {
						loadmorePB.setVisibility(View.GONE);
						return;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
					if (messages.size() != 0) {
						// 刷新ui
						adapter.notifyDataSetChanged();
						listView.setSelection(messages.size() - 1);
						if (messages.size() != pagesize)
							haveMoreData = false;
					} else {
						haveMoreData = false;
					}
					loadmorePB.setVisibility(View.GONE);
					isloading = false;

				}
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// 点击notification bar进入聊天页面，保证只有一个聊天页面
		String username = intent.getStringExtra("userId");
		if (toChatUsername.equals(username))
			super.onNewIntent(intent);
		else {
			finish();
			startActivity(intent);
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		if (package_id == 0) {
			if (new_expression_page_0 != 0) {
				setImageBackground1(arg0 % new_expression_page_0);
			}
		}
		if (package_id == 1) {
			if (new_expression_page_1 != 0) {
				setImageBackground2(arg0 % new_expression_page_1);
			}
		}
		if (package_id == 2) {
			if (new_expression_page_2 != 0) {
				setImageBackground3(arg0 % new_expression_page_2);
			}
		}

		setImageBackground_emoji(arg0);

	}

	/**
	 * 设置选中的tip的背景
	 * 
	 * @param selectItems
	 */
	private void setImageBackground_emoji(int selectItems) {
		for (int i = 0; i < tips_anim_emoji.length; i++) {
			if (i == selectItems) {
				tips_anim_emoji[i].setBackgroundResource(R.drawable.dot);
			} else {
				tips_anim_emoji[i].setBackgroundResource(R.drawable.dot_normal);
			}
		}
	}

	/**
	 * 设置选中的tip的背景
	 * 
	 * @param selectItems
	 */
	private void setImageBackground1(int selectItems) {
		for (int i = 0; i < tips_anim1.length; i++) {
			if (i == selectItems) {
				tips_anim1[i].setBackgroundResource(R.drawable.dot);
			} else {
				tips_anim1[i].setBackgroundResource(R.drawable.dot_normal);
			}
		}
	}

	private void setImageBackground2(int selectItems) {
		for (int i = 0; i < tips_anim2.length; i++) {
			if (i == selectItems) {
				tips_anim2[i].setBackgroundResource(R.drawable.dot);
			} else {
				tips_anim2[i].setBackgroundResource(R.drawable.dot_normal);
			}
		}
	}

	private void setImageBackground3(int selectItems) {
		for (int i = 0; i < tips_anim3.length; i++) {
			if (i == selectItems) {
				tips_anim3[i].setBackgroundResource(R.drawable.dot);
			} else {
				tips_anim3[i].setBackgroundResource(R.drawable.dot_normal);
			}
		}
	}

	private ArrayList<String> list_questions;

	// 随机提问
	private void sendRandomQuestion() {
		new AsyncTask<Object, Object, String>() {
			Random rdm = new Random();

			@Override
			protected String doInBackground(Object... params) {

				String question = null;
				try {
					if (list_questions == null || list_questions.size() == 0) {
						String urlPath = PathConst.URL_QUESTIONS_RANDOM;
						JSONArray list = NetworkUtil.getJsonArray(urlPath, null, 5000);

						list_questions = new ArrayList<String>();
						for (int i = 0; i < list.length(); i++) {
							list_questions.add(list.getString(i));
						}
					}

					question = list_questions.get(rdm.nextInt(list_questions.size()));

				} catch (Exception e) {
					e.printStackTrace();
				}

				return question;
			}

			protected void onPostExecute(String result) {
				if (result != null && !result.equals("")) {
					sendText("榴莲君替TA问你——" + result);
				}
			};
		}.execute();
	}

	private void getBadWords2() {
		String dir_offline = FileDownloadUtil.getDefaultLocalDir(PathConst.BAD_WORDS_PATH);
		File file_local = new File(dir_offline + PathConst.BAD_WORDS_FILE);
		String str_json_bad_words = null;// 本地json数据
		str_json_bad_words = FileDownloadUtil.getLocalString(file_local);

		JSONObject json = null;
		try {
			if (str_json_bad_words != null) {
				json = new JSONObject(str_json_bad_words);
			} else {
				json = null;
			}
			if (json != null) {
				// 获取臧词
				bad_words_list = new ArrayList<String>();
				JSONArray array_deny = json.getJSONArray("deny");
				if (array_deny != null && array_deny.length() != 0) {
					for (int i = 0; i < array_deny.length(); i++) {
						bad_words_list.add(array_deny.getString(i));
					}
				}
				// 获取警告词
				warn_words_list = new ArrayList<String>();
				JSONArray array_warn = json.getJSONArray("warn");
				if (array_warn != null && array_warn.length() != 0) {
					for (int i = 0; i < array_warn.length(); i++) {
						warn_words_list.add(array_warn.getString(i));
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initLocaBadWords2() {
		bad_words_loca_list = new ArrayList<String>();
		String[] bad_words_array = new String[] { "法轮功", "法轮大法", "赵紫阳", "新疆叛", "习近平", "我搞台独", "我的西域", "瓮安", "溫家寶", "温影帝", "围攻上海", "万人骚动", "死要见毛", "死法分布", "司法黑", "售手枪", "售五四", "售信用", "售冒名", "售麻醉", "售猎枪", "守所死法", "手木仓", "士康事件", "十七大幕", "十类人不", "十大谎",
				"十八等", "狮子旗", "烧公安局", "煽动群众", "煽动不明", "杀指南", "三唑", "群体性事", "群起抗暴", "情聊天室", "清純壆", "枪决女犯", "拟涛哥", "民九亿商", "明慧网", "媒体封锁", "每周一死", "蟆叫专家", "轮手枪", "轮功", "龙湾事件", "六月联盟", "六四事", "六合彩", "领土拿", "两会又三", "两会代", "炼大法", "聯繫電", "利他林", "丽媛离", "力月西",
				"力骗中央", "理做帐报", "理证件", "理是影帝", "李洪志", "康跳楼", "康没有不", "砍伤儿", "砍杀幼", "九评共", "九龙论坛", "警方包庇", "警察的幌", "江贼民", "疆獨", "江系人", "江太上", "江胡内斗", "激情炮", "还看锦涛", "华国锋", "胡耀邦", "胡适眼", "胡錦濤", "胡江内斗", "胡紧套", "红色恐怖", "紅色恐", "海访民", "國內美", "国一九五七", "国家妓",
				"共王储", "公安网监", "搞媛交", "高就在政", "府包庇", "佛同修", "封锁消", "法院给废", "法一轮", "法轮", "法伦功", "法轮佛", "法车仑" };

		for (int i = 0; i < bad_words_array.length; i++) {
			bad_words_loca_list.add(bad_words_array[i]);
		}

		warn_words_loca_list = new ArrayList<String>();
		String[] worn_words_array = new String[] { "阿扁推翻", "阿宾", "阿賓", "挨了一炮", "爱液横流", "安街逆", "安局办公楼", "安局豪华", "安门事", "安眠藥", "案的准确", "八九民", "八九学", "八九政治", "把病人整", "把邓小平", "把学生整", "罢工门", "白黄牙签", "败培训", "办本科", "办理本科", "办理各种", "办理票据", "办理文凭", "办理真实",
				"办理证书", "办理资格", "办文凭", "办怔", "办证", "半刺刀", "辦毕业", "辦證", "谤罪获刑", "磅解码器", "磅遥控器", "宝在甘肃修", "保过答案", "报复执法", "爆发骚", "北省委门", "被打死", "被指抄袭", "被中共", "本公司担", "本无码", "变牌绝", "辩词与梦", "冰毒", "冰火毒", "冰火佳", "冰火九重", "冰火漫", "冰淫传", "冰在火上", "波推龙", "博彩娱",
				"博会暂停", "博园区伪", "不查都", "不查全", "不思四化", "布卖淫女", "部忙组阁", "部是这样", "才知道只生", "财众科技", "采花堂", "踩踏事", "苍山兰", "苍蝇水", "藏春阁", "藏獨", "操了嫂", "操嫂子", "策没有不", "插屁屁", "察象蚂", "拆迁灭", "车牌隐", "成人电", "成人聊", "成人片", "成人视", "成人图", "成人文", "成人小", "城管灭", "惩公安",
				"惩贪难", "冲凉死", "抽着大中", "抽着芙蓉", "出成绩付", "出售发票", "出售军", "穿透仪器", "春水横溢", "纯度白", "纯度黄", "次通过考" };
		for (int i = 0; i < worn_words_array.length; i++) {
			warn_words_loca_list.add(bad_words_array[i]);
		}
	}

	/**
	 * 遍历本地表情文件展示
	 */
	private List<String> search_local_emotins() {
		String emotions_base_path = FileDownloadUtil.getDefaultLocalDir(PathConst.DOWNLOAD_EMOTIONS);
		File file = new File(emotions_base_path);
		File[] files = file.listFiles();// 获取所有表情目录文件
		for (File f : files) {
			emotions_package_names_list.add(f.getName());
		}
		return emotions_package_names_list;
	}

	/**
	 * 加载新表情页
	 */
	private void init_new_emotions(final String path) {
		File file = new File(emotions_base_path);
		File[] files = file.listFiles();// 获取所有表情目录文件
		for (File f : files) {
			if (path == null) {
				return;
			}
			if (f == null) {
				return;
			}
			if (!file.exists() || !file.isDirectory()) {
				return;
			}
			// if (f.listFiles().length == 0) {
			// return;
			// }
			if (path.equals(f.getName())) {// 根据后缀判断
				File[] file_image_list = f.listFiles();
				image_path = new ArrayList<String>();
				image_name = new ArrayList<String>();
				if (file_image_list == null) {
					return;
				}
				for (int j = 0; j < file_image_list.length; j++) {
					File file_image = file_image_list[j];
					try {
						String newFileName = new String(file_image.getName().getBytes(), "UTF-8");
						if (newFileName.contains(big_cover_name) || newFileName.contains(simall_cover_name)) {
							// 底部表情分类
							final ImageView iv_bottom_emotion = new ImageView(ChatActivity.this);
							Bitmap decodeFile = BitmapFactory.decodeFile(file_image.getAbsolutePath());
							iv_bottom_emotion.setImageBitmap(decodeFile);
							int screen_width = ViewUtil.getScreenWidth(ChatActivity.this);
							layoutParams.width = screen_width / 8;
							layoutParams.height = screen_width / 8;
							iv_bottom_emotion.setLayoutParams(layoutParams);
							iv_bottom_emotion.setBackgroundResource(R.drawable.grid_line);
							iv_bottom_emotion.setTag(path);
							// imag_list.add(iv_bottom_emotion);
							imag_list.add(0, iv_bottom_emotion);
							iv_bottom_emotion.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									selected_pager = (String) iv_bottom_emotion.getTag();
									iv_expression_emoji.setBackgroundResource(R.drawable.grid_line);
									for (ImageView iv : imag_list) {
										if (selected_pager.equals(iv.getTag())) {
											iv.setBackgroundResource(R.drawable.grid_line_press);
										} else {
											iv.setBackgroundResource(R.drawable.grid_line);
										}
									}
									search_selected_emotions((String) iv_bottom_emotion.getTag());
								}
							});

							ll_emotions_content.addView(iv_bottom_emotion);
							viewpager.setTag(tab_emotions_tag);// 当前表情包的标示
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		}
	}

	/**
	 * 查找显示选中表情
	 */
	public void search_selected_emotions(String path) {
		int express_type = TYPE_IMAGE;
		File file = new File(emotions_base_path);
		File[] files = file.listFiles();// 获取所有表情目录文件
		for (File f : files) {
			if (path == null) {
				return;
			}
			if (!file.exists() || !file.isDirectory()) {
				return;
			}
			// if (f.listFiles().length == 0) {// 文件为空时
			// return;
			// }
			if (path.equals(f.getName())) {// 根据后缀判断
				File[] file_image_list = f.listFiles();
				image_path = new ArrayList<String>();
				image_name = new ArrayList<String>();
				int total_page = 0;// 默认值
				int type_size = 0;// 默认值
				if (file_image_list == null) {
					return;
				}
				for (int j = 0; j < file_image_list.length; j++) {
					File file_image = file_image_list[j];
					if (j == 0) {
						if (file_image.getName().contains("gif")) {
							express_type = TYPE_GIF;
						}
					}
					try {
						String newFileName = new String(file_image.getName().getBytes(), "UTF-8");
						if (newFileName.contains(big_cover_name)) {// 大表情
							type_size = 1;
						} else if (newFileName.contains(simall_cover_name)) {// 小表情
							type_size = 2;
						} else {
							image_path.add(file_image.getAbsolutePath());// 获取对应表情文件夹下图片的绝对路径
							String[] split = newFileName.split("#");
							image_name.add(split[split.length - 1]);// 获取对应表情的名字
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (type_size == 1) {// 大表情
					total_page = (image_path.size() - 1) / 10 + 1;
				} else {// 小表情
					total_page = (image_path.size() - 1) / 20 + 1;
				}

				List<View> views_0 = new ArrayList<View>();
				for (int cur_page_anim = 0; cur_page_anim < total_page; cur_page_anim++) {
					views_0.add(init_grid_viewpager_data(image_path, image_name, cur_page_anim, type_size, path, express_type));
				}
				ll_points.removeAllViews();
				// 只有一页不用添加
				if (total_page > 1) {
					add_points(total_page, ll_points);// 添加小圆点
				}
				expressionViewpager.setAdapter(pager_adapter);
				pager_adapter.setData(views_0, image_path, type_size);
			}
		}
	}

	class MyPagerAdapter extends PagerAdapter {
		private List<View> pager_list;
		private int page_size;
		SparseArray<View> views = new SparseArray<View>();

		public MyPagerAdapter() {
		}

		public void setData(List<View> pager_list, List<String> images_path_list, int type_size) {
			this.pager_list = pager_list;
			notifyDataSetChanged();
		}

		@Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub
			int key = 0;
			for (int i = 0; i < views.size(); i++) {
				key = views.keyAt(i);
				View view = views.get(key);
			}
			super.notifyDataSetChanged();

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pager_list == null ? 0 : pager_list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = pager_list.get(position % pager_list.size());
			((ViewPager) container).addView(view, 0);
			views.put(position, view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;
			((ViewPager) container).removeView(view);
			views.remove(position);
			view = null;
		}

		@Override
		public int getItemPosition(Object object) {
			if (page_size > 0) {
				page_size--;
				return POSITION_NONE;
			}
			return PagerAdapter.POSITION_NONE;
		}
	}

	/**
	 * 添加底部小圆点
	 */

	public void add_points(int total_number, View container) {
		// 将小圆点加入到ViewGroup中
		tips_anim_emoji = new ImageView[total_number];
		Map<String, ImageView[]> map = new HashMap<String, ImageView[]>();
		for (int i = 0; i < tips_anim_emoji.length; i++) {
			ImageView imageView = new ImageView(ChatActivity.this);
			LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layout.setMargins(10, 0, 10, 0);
			imageView.setTag(tab_emotions_tag);
			imageView.setLayoutParams(layout);
			tips_anim_emoji[i] = imageView;
			if (i == 0) {
				tips_anim_emoji[i].setBackgroundResource(R.drawable.dot);
			} else {
				tips_anim_emoji[i].setBackgroundResource(R.drawable.dot_normal);
			}

			((LinearLayout) container).setGravity(Gravity.CENTER);
			((ViewGroup) container).addView(imageView);
		}
	}

	/**
	 * 监听表情下载情况的广播
	 */
	class MyDownloadReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int tag = intent.getIntExtra("tag", 0);
			String action = intent.getAction();
			if ("MyReceiver_Emotion_download".equals(action)) {
				new_emotion_file_path = intent.getStringExtra("emotions");// 获取当前新下载的表情文件路径
				tab_emotions_tag = new_emotion_file_path;// 当前表情的标示
				if (tag == 1) {// 删除表情
					delete_select_emotions(tab_emotions_tag);
				} else {
					init_new_emotions(new_emotion_file_path);
				}
				is_fist_loading = false;
			}

		}

	}

	/**
	 * 删除表情
	 */

	private void delete_select_emotions(String package_name) {

		return_normal();// 先恢复未选中状态
		int childCount = ll_emotions_content.getChildCount();
		for (int index = 0; index < childCount; index++) {
			View childAt = ll_emotions_content.getChildAt(index);
			if (childAt != null) {
				if (package_name.equals(childAt.getTag())) {
					ll_emotions_content.removeView(childAt);
					if (index >= 0) {
						View childAt2 = ll_emotions_content.getChildAt(index - 1);
						if (childAt2 != null) {
							search_selected_emotions((String) childAt2.getTag());//
							childAt2.setBackgroundResource(R.drawable.grid_line_press);
							iv_expression_emoji.setBackgroundResource(R.drawable.grid_line);
						}
						// 有多个
						if (index == 0) {
							return_normal();// 恢复未选中状态
							init_yanwenzi();// 显示颜文字
							iv_expression_emoji.setBackgroundResource(R.drawable.grid_line_press);
						}
					}
				}
			}
		}
		if (childCount == 1) {
			return_normal();// 恢复未选中状态
			init_yanwenzi();// 显示颜文字
			iv_expression_emoji.setBackgroundResource(R.drawable.grid_line_press);
		}
	}

	/**
	 * 恢复未选中的状态
	 */
	private void return_normal() {
		int childCount = ll_emotions_content.getChildCount();

		for (int index = 0; index < childCount; index++) {
			View childAt = ll_emotions_content.getChildAt(index);
			if (childAt != null) {
				childAt.setBackgroundResource(R.drawable.grid_line);
			}
		}
	}

	/**
	 * gridview填充数据
	 */
	int page_size = 0;

	public View init_grid_viewpager_data(List<String> images_path_list, List<String> iamges_name_list, final int current_page, final int type_size, final String package_id, final int type) {

		View view = LayoutInflater.from(this).inflate(R.layout.express_grid, null);
		GridView grid = (GridView) view.findViewById(R.id.gridView1);
		grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
		final NewExpressAdapter2 grid_adapter = new NewExpressAdapter2(ChatActivity.this);
		grid.setAdapter(grid_adapter);
		final List<String> current_page_path;
		final List<String> current_page_name;
		int total_page = 0;
		if (type_size == 1) {// 加载大表情
			total_page = (images_path_list.size() - 1) / 10 + 1;
			page_size = 10;
		} else if (type_size == 2) {// 加载小表情
			total_page = (images_path_list.size() - 1) / 20 + 1;
			page_size = 20;
		}
		if (current_page < total_page - 1) {
			current_page_path = images_path_list.subList(current_page * page_size, (current_page + 1) * page_size);
			current_page_name = iamges_name_list.subList(current_page * page_size, (current_page + 1) * page_size);
		} else {
			current_page_path = images_path_list.subList(current_page * page_size, images_path_list.size());
			current_page_name = iamges_name_list.subList(current_page * page_size, images_path_list.size());
		}
		grid_adapter.setData(current_page_path, current_page_name, type_size);
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String[] path_list = current_page_path.get(position).split("/");
				String[] file_name = path_list[path_list.length - 1].split("#");
				String[] current_id = file_name[0].split("\\.");
				String temp_type = "";
				if (type == TYPE_GIF) {
					temp_type = ".gif";
				} else {
					temp_type = ".png";
				}
				sendTextByExpression(type, package_id, current_id[0], current_page_name.get(position), PathConst.URL_EXPRESS_PREFIX + package_id + "/" + current_id[0] + temp_type, type_size);
			}
		});
		return view;
	}

	/**
	 * 发送表情消息
	 * 
	 * @param content
	 *            #define KChatCustomFacePackageId @"chatCustomFacePackageId"
	 *            #define KChatCustomFaceId @"chatCustomFaceId" #define
	 *            KChatCustomFaceImageURL @"chatCustomFaceImage" #define
	 *            KChatCustomFaceName @"chatCustomFaceName" #define
	 *            KChatCustomFaceWidth @"chatCustomFaceWidth" #define
	 *            KChatCustomFaceHeight @"chatCustomFaceHeight"
	 */
	private void sendTextByExpression(int type, String chatCustomFacePackageId, String chatCustomFaceId, String chatCustomFaceName, String chatCustomFaceImage, int page_size) {
		post_two_first_chat();
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		// if (chatType == CHATTYPE_GROUP) {
		// message.setChatType(ChatType.GroupChat);
		// post_active();
		// }
		String chatCustomFaceWidth = 50 + "";
		String chatCustomFaceHeight = 50 + "";
		if (page_size == TYPE_BIG) {
			chatCustomFaceWidth = 100 + "";
			chatCustomFaceHeight = 100 + "";
		}

		TextMessageBody txtBody = new TextMessageBody("");
		// 设置消息body
		message.addBody(txtBody);
		message.setAttribute("chatCustomFacePackageId", chatCustomFacePackageId);//
		message.setAttribute("chatCustomFaceId", chatCustomFaceId);//
		message.setAttribute("chatCustomFaceIsGif", type + "");//
		message.setAttribute("chatCustomFaceImage", chatCustomFaceImage);//
		message.setAttribute("chatCustomFaceName", chatCustomFaceName);
		message.setAttribute("chatCustomFaceWidth", chatCustomFaceWidth);
		message.setAttribute("chatCustomFaceHeight", chatCustomFaceHeight);
		message.setAttribute("chatCustomFacePackageId", chatCustomFacePackageId);
		// 设置要发给谁,用户username或者群聊groupid
		message.setReceipt(toChatUsername);
		// 把messgage加到conversation中
		conversation.addMessage(message);
		// 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
		listView.requestLayout();
		adapter.refresh();
		listView.setSelection(listView.getCount() - 1);
		setResult(RESULT_OK);
	}

}
