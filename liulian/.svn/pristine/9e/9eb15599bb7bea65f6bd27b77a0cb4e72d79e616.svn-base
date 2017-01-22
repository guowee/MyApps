package com.haomee.chat.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haomee.chat.adapter.GameGridAdapter;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.liulian.BaseActivity;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.R;
import com.haomee.player.MySoundPlayer;
import com.haomee.util.NetworkUtil;
import com.haomee.util.imageloader.ImageLoaderCharles;
import com.haomee.view.CircleImageView;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ChatGame2 extends BaseActivity {

	private MyTask myTask;
	private Timer timer;
	private int recLen;
	private Context context;
	private int click_num = 0;

	private FrameLayout game_bottom;
	private CircleImageView img_icon_myself, img_icon_other;
	private LinearLayout ll_clock_timer, ll_result_number;
	private LinearLayout ll_myself, ll_other;
	private TextView myself_number, other_number;
	private TextView clock_timer;

	private LinearLayout ll_notice, ll_play_game, ll_play_game_result;
	private GridView start_game_button, grid_game_img;
	private TextView game_clock_num, game_start_timer;
	private ImageView game_result_emotion;
	private TextView game_result_message, return_to_chat;
	private List<String> icon_list;// 存放头像的地址
	private GameGridAdapter game_adapter_start;
	private GameGridAdapter game_adapter_play;

	private int current_click_time = 0;

	private boolean is_result = false;
	private int myScore, otherScore;
	private SharedPreferences preferences_chat_user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game2);
		context = ChatGame2.this;

		if (LiuLianApplication.current_user.getImage() == null || LiuLianApplication.current_user.getImage().equals("")) {
			MyToast.makeText(this, "请先上传自己的头像哦！", 1).show();
			finish();
		}

		is_result = getIntent().getBooleanExtra("is_result", false);
		if (is_result) {
			myScore = Integer.parseInt(getIntent().getStringExtra("chatMyselfScore"));
			otherScore = Integer.parseInt(getIntent().getStringExtra("chatOtherScore"));
			initView();
		} else {
			initView();
			start_game();
		}
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		game_bottom = (FrameLayout) findViewById(R.id.game_bottom);
		img_icon_myself = (CircleImageView) findViewById(R.id.img_myself);
		img_icon_other = (CircleImageView) findViewById(R.id.img_other);

		preferences_chat_user = getSharedPreferences(CommonConst.PREFERENCES_SESSION_USERS, Context.MODE_PRIVATE);
		String pre_otherid = preferences_chat_user.getString(getIntent().getStringExtra("other_id"), "");

		
		
		if (pre_otherid.equals("")) {
			PostUserInfo(getIntent().getStringExtra("other_id"),img_icon_other);
		} else {
			String[] temp = pre_otherid.split("######");
			img_icon_other.setBackgroundResource(CommonConst.user_sex[Integer.parseInt(temp[3])]);
			ImageLoaderCharles.getInstance(context).addTask(temp[1], img_icon_other);
		}
		img_icon_myself.setBackgroundResource(CommonConst.user_sex[LiuLianApplication.current_user.getSex()]);
        ImageLoaderCharles.getInstance(context).addTask(LiuLianApplication.current_user.getImage(), img_icon_myself);
        ImageLoaderCharles.getInstance(context).addTask(getIntent().getStringExtra("other_image"), img_icon_other);
		ll_clock_timer = (LinearLayout) findViewById(R.id.ll_clock_timer);
		ll_result_number = (LinearLayout) findViewById(R.id.ll_result_number);
		ll_myself = (LinearLayout) findViewById(R.id.ll_myself);
		ll_other = (LinearLayout) findViewById(R.id.ll_other);
		myself_number = (TextView) findViewById(R.id.myself_number);
		other_number = (TextView) findViewById(R.id.other_number);
		clock_timer = (TextView) findViewById(R.id.clock_timer);

		ll_notice = (LinearLayout) findViewById(R.id.ll_notice);
		ll_play_game = (LinearLayout) findViewById(R.id.ll_play_game);
		ll_play_game_result = (LinearLayout) findViewById(R.id.ll_play_game_result);
		start_game_button = (GridView) findViewById(R.id.start_game_button);
		grid_game_img = (GridView) findViewById(R.id.grid_game_img);
		game_clock_num = (TextView) findViewById(R.id.game_clock_num);
		game_start_timer = (TextView) findViewById(R.id.game_start_timer);
		game_result_emotion = (ImageView) findViewById(R.id.game_result_emotion);
		game_result_message = (TextView) findViewById(R.id.game_result_message);
		return_to_chat = (TextView) findViewById(R.id.return_to_chat);
		return_to_chat.setOnClickListener(new OnClickListener() {// 返回聊天
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});
		game_adapter_start = new GameGridAdapter(context);
		start_game_button.setAdapter(game_adapter_start);
		// 初始化界面
		icon_list = new ArrayList<String>();// 存放随机数
		icon_list.add(LiuLianApplication.current_user.getImage());
		icon_list.add(PathConst.URL_GAME_IMG + 0 + ".png");
		icon_list.add(PathConst.URL_GAME_IMG + 2 + ".png");
		icon_list.add(PathConst.URL_GAME_IMG + 3 + ".png");
		game_adapter_start.setData(icon_list, 1);
		game_adapter_play = new GameGridAdapter(context);
		grid_game_img.setAdapter(game_adapter_play);
		grid_game_img.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (recLen <= 10 & recLen > 0) {
					MySoundPlayer.getInstance(ChatGame2.this).play_background(R.raw.sound_click, false);
					if (game_adapter_play.getData().get(position).equals(LiuLianApplication.current_user.getImage())) {
						click_num++;
						if (click_num < 10) {
							game_clock_num.setText("0" + click_num);
						} else {
							game_clock_num.setText("" + click_num);
						}
					}
					add_game_icon();
				}
			}
		});
		// 判断是否为结果页

		if (is_result) {
			ll_play_game_result.setVisibility(View.VISIBLE);
			ll_play_game.setVisibility(View.GONE);
			ll_result_number.setVisibility(View.VISIBLE);
			ll_clock_timer.setVisibility(View.GONE);
			myself_number.setText(myScore + "");
			other_number.setText(otherScore + "");
			ll_play_game.setVisibility(View.VISIBLE);
			ll_notice.setVisibility(View.GONE);
			if (myScore > otherScore) {
				game_bottom.setBackgroundColor(getResources().getColor(R.color.game_win));
				game_result_emotion.setImageResource(R.drawable.game_button_find_victory);
				game_result_message.setText("Win");
				ll_myself.setBackgroundColor(getResources().getColor(R.color.game_win));
				ll_other.setBackgroundColor(getResources().getColor(R.color.game_lose));
			} else if (myScore == otherScore) {
				game_bottom.setBackgroundColor(getResources().getColor(R.color.game_draw));
				game_result_emotion.setImageResource(R.drawable.game_button_find_draw);
				game_result_message.setText("Draw");
				ll_myself.setBackgroundColor(getResources().getColor(R.color.game_draw));
				ll_other.setBackgroundColor(getResources().getColor(R.color.game_draw));
			} else if (myScore < otherScore) {
				game_result_emotion.setImageResource(R.drawable.game_button_find_loser);
				game_result_message.setText("Lose");
				game_bottom.setBackgroundColor(getResources().getColor(R.color.game_lose));
				ll_myself.setBackgroundColor(getResources().getColor(R.color.game_lose));
				ll_other.setBackgroundColor(getResources().getColor(R.color.game_win));
			}

			return_to_chat.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 开始准备
	 */
	private void start_game() {
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		if (myTask != null) {
			myTask.cancel();
		}
		recLen = 16;
		myTask = new MyTask();
		timer.schedule(myTask, 1000, 1000);
	}

	class MyTask extends TimerTask {
		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					recLen--;
					if (recLen < 0) {
						if (timer != null) {
							timer.cancel();
							timer = null;
						}
						if (myTask != null) {
							myTask.cancel();
							myTask = null;
						}
						sendGameResult();
					} else if (recLen > 10 && recLen <= 14) {
						if (recLen == 14) {
							ll_notice.setVisibility(View.GONE);
							game_start_timer.setVisibility(View.VISIBLE);
							ll_play_game.setVisibility(View.VISIBLE);
						}
						game_start_timer.setText("" + (recLen - 11));
						if (recLen == 11) {
							game_clock_num.setVisibility(View.VISIBLE);
							grid_game_img.setVisibility(View.VISIBLE);
							game_start_timer.setVisibility(View.GONE);
							// 开始下载头像
							add_game_icon();
						}
					} else if (recLen <= 10) {
						clock_timer.setText("" + recLen);
					}
				}
			});
		}
	}

	private void add_game_icon() {
		game_adapter_play.setData(get_random_list(current_click_time), 0);
	}

	private List<String> get_random_list(int start) {
		if (start == 13) {
			start = 0;
			current_click_time = 0;
		}
		List<String> list = new ArrayList<String>();
		for (int index = start; index < start + 8; index++) {
			list.add(PathConst.URL_GAME_IMG + index + ".png");
		}
		list.add(LiuLianApplication.current_user.getImage());
		Collections.shuffle(list);
		return list;
	}

	public void sendGameResult() {
		if (NetworkUtil.dataConnected(context)) {
			// 获取会话好友头像和昵称
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams re = new RequestParams();
			re.put("hx_uid", LiuLianApplication.current_user.getHx_username());
			re.put("score", click_num + "");
			re.put("id", getIntent().getStringExtra("game_id"));
			client.post(PathConst.URL_GAME_RESULT_SUBMIT, re, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg0) {
					try {
						if (arg0 == null || arg0.equals("")) {
							return;
						}
						JSONObject json_obj = new JSONObject(arg0);
						MyToast.makeText(context, json_obj.getString("msg"), 1).show();
						finish();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}

	}

	private SharedPreferences.Editor editor;

	public void PostUserInfo(final String temp, final ImageView image) {
		if (NetworkUtil.dataConnected(this)) {
			// 获取会话好友头像和昵称
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams re = new RequestParams();
			re.put("hx_username", temp);
			client.post(PathConst.URL_GET_USER_INFO_FROM_HX_NAME, re, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg0) {
					try {
						JSONArray json_arr = new JSONArray(arg0);
						for (int i = 0; i < json_arr.length(); i++) {
							JSONObject json = json_arr.getJSONObject(i);
							image.setImageResource(R.drawable.item_icon);
							ImageLoaderCharles.getInstance(context).addTask(json.getString("head_pic"), image);
							image.setBackgroundResource(CommonConst.user_sex[json.getInt("sex")]);
							editor = preferences_chat_user.edit();
							editor.putString(temp, json.getString("username") + "######" + json.getString("head_pic") + "######" + json.getString("id") + "######" + json.getInt("sex"));
							editor.commit();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
