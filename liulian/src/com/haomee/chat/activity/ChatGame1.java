package com.haomee.chat.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.haomee.util.imageloader.ImageLoaderCharles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.liulian.BaseActivity;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.R;
import com.haomee.player.MySoundPlayer;
import com.haomee.util.NetworkUtil;
import com.haomee.view.CircleImageView;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ChatGame1 extends BaseActivity {

	private Timer timer;
	private MyTask myTask;
	private int recLen;
	private Context context;
	private int click_num = 0;
	private CircleImageView img_icon_myself, img_icon_other;
	private LinearLayout ll_clock_timer, ll_result_number;
	private LinearLayout ll_myself, ll_other;
	private TextView myself_number, other_number;
	private LinearLayout ll_notice, ll_play_game;
	private ImageView start_game_button, touch_me;
	private TextView start_game_clock_timer;
	private TextView clock_timer;
	private TextView return_to_chat;
	private TextView game_notice_message;
	private TextView get_num;
	private boolean is_result = false;
	private FrameLayout game_bottom;
	private int myScore, otherScore;
	private SharedPreferences preferences_chat_user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivity_test);
		context = this;
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
	
	private void initView() {
		game_bottom = (FrameLayout) findViewById(R.id.game_bottom);
		img_icon_myself = (CircleImageView) findViewById(R.id.img_myself);
		img_icon_other = (CircleImageView) findViewById(R.id.img_other);
		preferences_chat_user = getSharedPreferences(CommonConst.PREFERENCES_SESSION_USERS, Context.MODE_PRIVATE);
		String pre_otherid = preferences_chat_user.getString(getIntent().getStringExtra("other_id"), "");
		
		ImageLoaderCharles.getInstance(context).addTask(LiuLianApplication.current_user.getImage(), img_icon_myself);
		if (pre_otherid.equals("")) {
			PostUserInfo(getIntent().getStringExtra("other_id"),img_icon_other);
		} else {
			String[] temp = pre_otherid.split("######");
			img_icon_other.setBackgroundResource(CommonConst.user_sex[Integer.parseInt(temp[3])]);
			ImageLoaderCharles.getInstance(context).addTask(temp[1], img_icon_other);
		}

        ImageLoaderCharles.getInstance(context).addTask(LiuLianApplication.current_user.getImage(), img_icon_myself);
        ImageLoaderCharles.getInstance(context).addTask(getIntent().getStringExtra("other_image"), img_icon_other);
		img_icon_other.setBackgroundResource(CommonConst.user_sex[getIntent().getIntExtra("other_sex", 0)]);
		img_icon_myself.setBackgroundResource(CommonConst.user_sex[LiuLianApplication.current_user.getSex()]);
		get_num = (TextView) findViewById(R.id.get_num);
		ll_clock_timer = (LinearLayout) findViewById(R.id.ll_clock_timer);
		ll_result_number = (LinearLayout) findViewById(R.id.ll_result_number);
		ll_myself = (LinearLayout) findViewById(R.id.ll_myself);
		ll_other = (LinearLayout) findViewById(R.id.ll_other);
		myself_number = (TextView) findViewById(R.id.myself_number);
		other_number = (TextView) findViewById(R.id.other_number);
		ll_notice = (LinearLayout) findViewById(R.id.ll_notice);
		ll_play_game = (LinearLayout) findViewById(R.id.ll_play_game);
		start_game_button = (ImageView) findViewById(R.id.start_game_button);
		touch_me = (ImageView) findViewById(R.id.touch_me);
		start_game_clock_timer = (TextView) findViewById(R.id.start_game_clock_timer);
		return_to_chat = (TextView) findViewById(R.id.return_to_chat);
		clock_timer = (TextView) findViewById(R.id.clock_timer);
		game_notice_message = (TextView) findViewById(R.id.game_notice_message);

		return_to_chat.setOnClickListener(clickListener);
		start_game_button.setOnClickListener(clickListener);
		touch_me.setOnClickListener(clickListener);
		touch_me.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (recLen > 0 && recLen < 11) {
					if (event.getAction() == event.ACTION_DOWN) {// 按下
						touch_me.setBackgroundResource(R.drawable.game_button_claw_02);
					} else if (event.getAction() == event.ACTION_UP) {
						touch_me.setBackgroundResource(R.drawable.game_button_claw_01);
					}
				}
				return false;
			}
		});

		// 判断是否为结果页

		if (is_result) {
			ll_result_number.setVisibility(View.VISIBLE);
			ll_clock_timer.setVisibility(View.GONE);
			get_num.setVisibility(View.GONE);
			myself_number.setText(myScore + "");
			other_number.setText(otherScore + "");
			ll_play_game.setVisibility(View.VISIBLE);
			ll_notice.setVisibility(View.GONE);
			start_game_clock_timer.setVisibility(View.GONE);
			if (myScore > otherScore) {
				game_bottom.setBackgroundColor(getResources().getColor(R.color.game_win));
				touch_me.setImageResource(R.drawable.game_button_find_victory);
				game_notice_message.setText("Win");
				ll_myself.setBackgroundColor(getResources().getColor(R.color.game_win));
				ll_other.setBackgroundColor(getResources().getColor(R.color.game_lose));
			} else if (myScore == otherScore) {
				game_bottom.setBackgroundColor(getResources().getColor(R.color.game_draw));
				touch_me.setImageResource(R.drawable.game_button_find_draw);
				game_notice_message.setText("Draw");
				ll_myself.setBackgroundColor(getResources().getColor(R.color.game_draw));
				ll_other.setBackgroundColor(getResources().getColor(R.color.game_draw));
			} else if (myScore < otherScore) {
				game_bottom.setBackgroundColor(getResources().getColor(R.color.game_lose));
				touch_me.setImageResource(R.drawable.game_button_find_loser);
				game_notice_message.setText("Lose");
				ll_myself.setBackgroundColor(getResources().getColor(R.color.game_lose));
				ll_other.setBackgroundColor(getResources().getColor(R.color.game_win));
			}
			return_to_chat.setVisibility(View.VISIBLE);
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
	
	
	/**
	 * 处理点击事件
	 */
	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.return_to_chat:// 返回聊天
				setResult(0);
				finish();
				break;
			case R.id.start_game_button:// 开始准备
				break;
			case R.id.touch_me:// 点击熊掌,进行统计
				if (recLen > 0 && recLen < 11) {
					MySoundPlayer.getInstance(ChatGame1.this).play_background(R.raw.sound_click, false);
					click_num++;
					if (click_num < 10) {
						get_num.setText("0" + click_num);
					} else {
						get_num.setText("" + click_num);
					}
				}
				break;
			}
		}
	};

	/**
	 * 开始准备
	 */
	private void start_game() {

		ll_notice.setVisibility(View.GONE);
		ll_play_game.setVisibility(View.VISIBLE);

		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		if (myTask != null) {
			myTask.cancel();
		}
		recLen = 13;
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
					} else if (recLen > 10) {
						start_game_clock_timer.setText("" + (recLen - 10));
						if (recLen == 11) {
							touch_me.setClickable(true);
						}
					} else {
						if (recLen == 10) {
							start_game_clock_timer.setText("" + (recLen - 10));
							game_notice_message.setVisibility(View.GONE);
							start_game_clock_timer.setVisibility(View.GONE);
						}
						clock_timer.setText("" + recLen);
					}
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
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
}
