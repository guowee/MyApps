package com.haomee.player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.haomee.entity.Movie;
import com.haomee.liulian.BaseActivity;
import com.haomee.liulian.R;
import com.haomee.util.NetworkUtil;
import com.haomee.util.StringUtil;
import com.haomee.view.MyToast;

/**
 * 播放界面
 * 
 * @author Administrator
 * 
 */
public class MediaPlayerActivity extends BaseActivity {

	public static int RESOURCE_TYPE_LOCAL = 2;
	public static int RESOURCE_TYPE_HTTP = 3;

	/**
	 * 播放器状态
	 */
	public static final int PLAYER_STATUS_UNKNOW = -1;
	public static final int PLAYER_STATUS_LOADING = 1;
	public static final int PLAYER_STATUS_PLAYING = 2;
	public static final int PLAYER_STATUS_PAUSE = 3;
	public static final int PLAYER_STATUS_END = 4;
	public static final int PLAYER_STATUS_ERROR = 5;

	/****************** 界面元素： */
	private MyVideoView videoView;
	private ViewGroup layout_top, layout_bottom, layout_player;
	private ImageView bt_play, bt_back, img_battery, bt_lock, bt_unlock; // ,
	// bt_light,
	// bt_volume;
	private TextView txt_title, txt_current_position, txt_total_duration, txt_clock;
	private TextView scrolling_current_time, scrolling_offset, progress_light_volume;

	private View frame_loading, frame_lock;

	private View layout_controllers, frame_process, layout_scrolling_progress, layout_scrolling_time;
	private SeekBar videoSeekBar;
	private ImageView img_loading;

	// public static final int verison_decoder=1; // 是否是使用了解码器的版本0,1 // Mark
	// Vitamio
	public static final int MESSAGE_DISPLAYTOOLBAR = 0;
	public static final int MESSAGE_REFRESH = 0;
	public static final int LENGTH_DISPLAYTOOLBAR = 4000; // 工具条显示的时间长度。
	public static final int LENGTH_DISPLAYLOCK = 3000; // 工具条显示的时间长度。
	public static final int RETRY_MAX = 2; // 最大重试次数
	private int pre_position_touch; // 触屏之前的位置，用来判断进度条是否改变了，以及判断是否卡在了某个地方。
	private long pre_position_refresh; // 自动刷新上次的位置
	private int retry_count; // 重试次数
	private int error_count; // 播放失败次数
	private int wait_count; // 卡住后等待次数
	private boolean isVisible_toolbar_clicked = false; // 解决点一下工具条消失

	// private MyScrollView scroll_lock;
	/******************* 视频信息 */
	private int resource_type; // 源视频格式，（从打开方式中打开，本地，在线）
	private String resource_url; // 载入时的源地址
	private M3u8Info m3u8;
	private String video_url; // 单个文件的视频

	private int video_from; // 视频来源
	private long video_duration; // 视频长度
	private String id, vid, seriesId, video_name; // id:我们自己的id,vid：外站id
	// private String video_format; // 将来进行标清/高清切换时使用
	private String txt_duration = "00:00:00";
	// private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm",
	// Locale.CHINA);
	private AudioManager mAudioManager;
	private int mMaxVolume; // 最大声音
	private int current_volume = -1;
	private float current_brightness = -1f;

	private GestureDetector gestureDetector;

	private static Handler handler_displayLock, handler_displayToolbar, handler_refresh, handler_get_video;

	private int status = PLAYER_STATUS_UNKNOW;; // 播放器状态
	private boolean isLockScreen = false; // 是否锁屏
	private boolean isTouched = false; // 屏幕是否被按住了。
	// private boolean isFullScreen = false;
	private int screenWidth;
	private int screenHeight;
	private int videoWidth;
	private int videoHeight;

	private int current_episode_index; // 在剧列表中的顺序，注意：不是第几集

	private char scrolling_orientation = '0'; // 拖动屏幕的方向，0：未拖动，x：x方向，y：y方向，（防止调节音量和拖动播放之间干扰）
	private char scrolling_orientation_light_volume = '0'; // 拖动事件，0：未拖动，l：亮度，v：音量

	// 记录播放历史
	private long video_position; // 视频当前播放位置, 单位毫秒
	private boolean isFirstLoad = true; // 是否第一次进入

	private BroadcastReceiver broadcastReceiver; // 监听分片缓冲更新进度
	private boolean isStartedCacheNext = false; // 是否已经开启缓冲下一个
	private boolean isCacheCompleted = false; // 是否已经完成下一分片的缓冲
	private int m3u8_cache_index; // 缓冲分片index
	private int m3u8_cache_percent; // 缓冲分片进度

	private long seek_position;

	private ConnectivityManager connectivityManager;

	private boolean is_m3u8_local = false;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);

	private int video_clear = 2; // 默认清晰度

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (android.os.Build.VERSION.SDK_INT >= 9) { // Android版本低于2.3的不识别该设置
			// 让当前activity可以在横屏方向转 （在配置里面设置就可以了）
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		this.setContentView(R.layout.player);

		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;

		initViews();
		initListeners();
		initReceiver();
		startPlay();

	}

	/**
	 * 初始化视图
	 */
	private void initViews() {

		// this.linear_guide = this.findViewById(R.id.frame_guide);

		// this.videoView = (MyVideoView_Android)
		// this.findViewById(R.id.videoView);
		this.layout_player = (ViewGroup) this.findViewById(R.id.layout_player);
		/*
		 * this.videoView = new MyVideoView_Android(this);
		 * layout_player.addView(videoView);
		 */

		switchDecoder();

		this.layout_top = (ViewGroup) this.findViewById(R.id.layout_top);
		this.layout_bottom = (ViewGroup) this.findViewById(R.id.layout_bottom);
		this.bt_play = (ImageView) this.findViewById(R.id.bt_play);
		/*this.bt_prev = (ImageView) this.findViewById(R.id.bt_prev);
		this.bt_next = (ImageView) this.findViewById(R.id.bt_next);*/
		this.bt_back = (ImageView) this.findViewById(R.id.bt_back);
		this.txt_title = (TextView) this.findViewById(R.id.video_title);
		this.videoSeekBar = (SeekBar) this.findViewById(R.id.videoSeekBar);
		this.txt_clock = (TextView) this.findViewById(R.id.txt_clock);
		this.img_battery = (ImageView) this.findViewById(R.id.img_battery);
		this.txt_current_position = (TextView) this.findViewById(R.id.txt_current_position);
		this.txt_total_duration = (TextView) this.findViewById(R.id.txt_total_duration);

		this.layout_controllers = this.findViewById(R.id.layout_controllers);
		this.frame_loading = this.findViewById(R.id.frame_loading);
		this.img_loading = (ImageView) this.findViewById(R.id.img_loading);
		this.frame_lock = this.findViewById(R.id.frame_lock);

		this.frame_process = this.findViewById(R.id.frame_process);
		this.progress_light_volume = (TextView) this.findViewById(R.id.progress_light_volume);
		this.layout_scrolling_progress = this.findViewById(R.id.layout_scrolling_progress);
		this.layout_scrolling_time = this.findViewById(R.id.layout_scrolling_time);
		this.scrolling_current_time = (TextView) this.findViewById(R.id.scrolling_current_time);
		this.scrolling_offset = (TextView) this.findViewById(R.id.scrolling_offset);

		this.bt_lock = (ImageView) this.findViewById(R.id.bt_lock);
		this.bt_unlock = (ImageView) this.findViewById(R.id.bt_unlock);
	}

	/**
	 * 初始化控件的事件监听
	 */
	private void initListeners() {
		// 音量控制
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		// 手势
		this.gestureDetector = new GestureDetector(this, this.onGestureListener);

		// 播放进度条控制
		videoSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

		// 给控制按钮加监听器
		this.bt_play.setOnClickListener(btOnClickListener);
		this.bt_back.setOnClickListener(btOnClickListener);
		this.bt_lock.setOnClickListener(btOnClickListener);
		this.bt_unlock.setOnClickListener(btOnClickListener);
		/*this.bt_next.setOnClickListener(btOnClickListener);
		this.bt_prev.setOnClickListener(btOnClickListener);*/

		layout_top.setOnTouchListener(doNotingOnTouchListener);
		layout_bottom.setOnTouchListener(doNotingOnTouchListener);

		// 锁屏控制，是否消耗掉事件
		this.frame_lock.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (bt_unlock.getVisibility() == View.VISIBLE) {
						bt_unlock.setVisibility(View.GONE);
					} else {
						bt_unlock.setVisibility(View.VISIBLE);
						// 工具条5秒后消失
						handler_displayLock.removeMessages(0);
						handler_displayLock.sendEmptyMessageDelayed(0, LENGTH_DISPLAYLOCK);
					}
				}

				return isLockScreen;
			}
		});

	}

	/**
	 * 触屏事件处理
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// Log.i("test", "ACTION_DOWN");

			isTouched = true;

			isVisible_toolbar_clicked = !layout_controllers.isShown();
			pre_position_touch = videoSeekBar.getProgress();

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			// Log.i("test", "ACTION_UP");

			isTouched = false;
			scrolling_orientation = '0';
			scrolling_orientation_light_volume = '0';

			if (this.video_duration > 0) { // 如果视频加载了才可以拖

				int position = videoSeekBar.getProgress();
				if (pre_position_touch == position) {

				} else {
					seekAndPlay(position);

					if (status == PLAYER_STATUS_PLAYING) {
						videoView.start();
						bt_play.setImageResource(R.drawable.play_bt_pause);
					}

				}
			}

			layout_scrolling_progress.setVisibility(View.INVISIBLE);
			frame_process.setVisibility(View.GONE);

			if (layout_controllers.isShown()) {
				handler_displayToolbar.removeMessages(MESSAGE_DISPLAYTOOLBAR);
				handler_displayToolbar.sendEmptyMessageDelayed(MESSAGE_DISPLAYTOOLBAR, LENGTH_DISPLAYTOOLBAR);
			}

		}

		return this.gestureDetector.onTouchEvent(event);
	}

	// 手势事件处理
	private OnGestureListener onGestureListener = new OnGestureListener() {

		@Override
		public boolean onDown(MotionEvent arg0) {
			// Log.i("test", "onDown");
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			// Log.i("test", "onFling");
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// Log.i("test", "onLongPress");
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			// Log.i("test", "onScroll:"+distanceX);

			if (e1 == null || e2 == null) { // 不知道是android的什么bug...居然有为空的时候。
				return false;
			}

			// 确定是横行还是竖向拖动，滑动中途不能变
			if (scrolling_orientation == '0') {
				if (Math.abs(distanceX) > Math.abs(distanceY)) {
					scrolling_orientation = 'x';
					frame_process.setVisibility(View.GONE);
					layout_scrolling_time.setVisibility(View.VISIBLE);
					handler_displayToolbar.removeMessages(MESSAGE_DISPLAYTOOLBAR);
				} else {
					scrolling_orientation = 'y';
					frame_process.setVisibility(View.VISIBLE);
					layout_scrolling_time.setVisibility(View.GONE);
				}

			}

			layout_scrolling_progress.setVisibility(View.VISIBLE);

			if (scrolling_orientation == 'x') {

				layout_controllers.setVisibility(View.VISIBLE);

				// Log.i("test","videoSeekBar.getMax():"+videoSeekBar.getMax());
				if (videoSeekBar.getMax() > 100) { // 不载入不让拖动
					int per = (int) (getStepLength(video_duration) * distanceX); // 每次拖动的距离
					int position = videoSeekBar.getProgress() - per;

					if (position < 0) {
						position = 0;
					}
					if (position > videoSeekBar.getMax()) {
						position = videoSeekBar.getMax();
					}

					videoSeekBar.setProgress(position);
				}

			} else {

				frame_process.setVisibility(View.VISIBLE);

				float x_old = e1.getX(); // 触屏的位置
				int windowWidth = videoView.getWidth();
				float per = distanceY * 0.0025f; // 拖动距离和百分比直接关系
				// float per = 0.02f; // 每次拖动的距离
				// float percent = distanceY > 0 ? per:-per; // 上下方向，改变百分比

				if (x_old > windowWidth / 2) {// 右边滑动
					changeVolumeSlide(per);
				} else {// 左边滑动
					changeBrightnessSlide(per);
				}

				// 区分声音和亮度的统计
				if (scrolling_orientation_light_volume == '0') {
					if (x_old > windowWidth / 2) {// 右边滑动
						scrolling_orientation_light_volume = 'v';
					} else {// 左边滑动
						scrolling_orientation_light_volume = 'l';
					}

				}

			}

			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			Log.i("test", "onShowPress");
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// Log.i("test", "onSingleTapUp" + isVisible_toolbar_clicked);

			if (!layout_controllers.isShown()) {
				layout_controllers.setVisibility(View.VISIBLE);
			} else if (!isVisible_toolbar_clicked && layout_controllers.isShown()) {

				layout_controllers.setVisibility(View.GONE);
				isVisible_toolbar_clicked = false;

			}

			if (layout_controllers.isShown()) {
				handler_displayToolbar.removeMessages(MESSAGE_DISPLAYTOOLBAR);
				handler_displayToolbar.sendEmptyMessageDelayed(MESSAGE_DISPLAYTOOLBAR, LENGTH_DISPLAYTOOLBAR);
			}

			return false;
		}

	};

	// 根据视频长度获取每次拖动的时候的长度
	private int getStepLength(long video_length) {
		// Log.i("test","video_length:"+video_length);
		int step = 80; // 默认
		if (video_length > 6000000) {
			step = 300;
		} else if (video_length > 4000000) {
			step = 240;
		} else if (video_length > 2000000) {
			step = 180;
		} else if (video_length > 1000000) {
			step = 120;
		}
		return step;
	}

	private float volume_changed = 0; // 由于音量用index表示，这里采用累积的方式

	/**
	 * 滑动改变声音大小
	 * 
	 * @param percent
	 *            每次更新的比率
	 */
	private void changeVolumeSlide(float percent) {
		current_volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		// Log.i("test", "音量：" + current_volume + "/" + mMaxVolume + "/" +
		// volume_changed);

		volume_changed += percent * mMaxVolume;
		int current_progress = (int) ((volume_changed + current_volume) * 100 / mMaxVolume);
		if (current_progress > 100) {
			current_progress = 100;
		} else if (current_progress < 0) {
			current_progress = 0;
		}
		// 变更进度条
		progress_light_volume.setText("音量：" + current_progress + "%");

		if (Math.abs(volume_changed) > 1) {

			int index = (int) (volume_changed) + current_volume;

			if (index > mMaxVolume) {
				index = mMaxVolume;
			} else if (index < 0) {
				index = 0;
			}

			// 变更声音
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
			volume_changed = 0;

		}

	}

	/**
	 * 滑动改变亮度
	 * 
	 * @param percent
	 *            百分比
	 */
	private void changeBrightnessSlide(float percent) {
		current_brightness = getWindow().getAttributes().screenBrightness;

		// Log.i("test", "当前亮度：" + current_brightness);

		WindowManager.LayoutParams lpa_light = getWindow().getAttributes();
		lpa_light.screenBrightness = current_brightness + percent;
		if (lpa_light.screenBrightness > 1.0f) {
			lpa_light.screenBrightness = 1.0f;
		} else if (lpa_light.screenBrightness < 0.01f) {
			lpa_light.screenBrightness = 0.01f;
		}

		getWindow().setAttributes(lpa_light);

		progress_light_volume.setText("亮度：" + (int) (lpa_light.screenBrightness * 100) + "%");

	}

	// 切换视频解码方式
	private void switchDecoder() {

		Log.i("test", "switchDecoder");

		boolean isStart = (videoView == null);

		// videoView==null 初始的时候
		/*if (isStart || videoView instanceof MyVideoView_Vitamio // Mark Vitamio
		) {
			videoView = new MyVideoView_Android(this);
		} else if (videoView instanceof MyVideoView_Android) { // Mark Vitamio

			Log.i("test", "切换到软解码");
			if (!io.vov.vitamio.LibsChecker2.checkVitamioLibs(this)) {
				return;
			}

			videoView = new MyVideoView_Vitamio(this);

		}*/

		videoView = new MyVideoView_Android(this);

		layout_player.removeAllViews();

		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		videoView.setLayoutParams(params);
		layout_player.addView((View) videoView);

		initPlayerListener();

		if (!isStart) {
			reloadVideo(0, 0);
		}

	}

	private void onVideoPrepared(Object mediaPlayer) {

		Log.i("test", "onVideoPrepared");

		stopRotate_loading();
		videoSeekBar.setEnabled(true);
		// is_start_load = false;

		if (videoView instanceof MyVideoView_Android) {
			android.media.MediaPlayer mPlayer = (android.media.MediaPlayer) mediaPlayer;
			videoWidth = mPlayer.getVideoWidth();
			videoHeight = mPlayer.getVideoHeight();

			// 获取影片时长，m3u8在asycTask里面取得。

			if (m3u8 != null && (video_duration == 0
			//|| m3u8.split_num == 1
					)) {
				video_duration = mPlayer.getDuration();
				m3u8.seconds[0] = video_duration / 1000;
			}

		}
		/*else if (videoView instanceof MyVideoView_Vitamio) { // Mark Vitamio
			io.vov.vitamio.MediaPlayer mPlayer = (io.vov.vitamio.MediaPlayer) mediaPlayer;
			videoWidth = mPlayer.getVideoWidth();
			videoHeight = mPlayer.getVideoHeight();

			// 获取影片时长，m3u8在asycTask里面取得。
			if (m3u8 != null && (video_duration == 0 
					//|| m3u8.split_num == 1
					)) {
				video_duration = mPlayer.getDuration();
				m3u8.seconds[0] = video_duration / 1000;
			}

		}*/

		videoView.setSizeParams(videoWidth, videoHeight);

		Log.i("test", "duration:" + video_duration + ",status:" + status);
		videoSeekBar.setMax((int) video_duration);

		// txt_current_position.setVisibility(View.VISIBLE);
		txt_title.setText(video_name);

		txt_duration = StringUtil.getTimeFormat(video_duration, false);

		if (resource_type == RESOURCE_TYPE_LOCAL && m3u8 != null) {
			if (m3u8.split_num == 1) {
				videoSeekBar.setSecondaryProgress((int) video_duration);
			} else {
				videoSeekBar.setSecondaryProgress((int) (m3u8.total_seconds_downloaded * 1000)); // 已下载多少显示多少
			}

		}

		// 视频第一次载入的时候
		if (isFirstLoad && m3u8 != null) {
			isFirstLoad = false;

			if (video_position > 0) { // 从上次记录处开始播放
				if (video_position < (video_duration - 5000)) {
					MyToast.makeText(MediaPlayerActivity.this, "上次播放到：" + StringUtil.getTimeFormat(video_position, true), Toast.LENGTH_SHORT).show();
				} else {
					video_position = 0;
					seek_position = 0; // 上次快播放结束了，就重头开始播
				}
			}

		}

		// 将seekTo统一放到OnPrepared里面, 注意：全局变量seek_position在之前先设定好
		if (seek_position > 0) {
			Log.i("test", "seek_position:" + seek_position);
			videoView.seekTo((int) seek_position);
		}

		if (status == PLAYER_STATUS_PLAYING) {
			videoView.start();
			bt_play.setImageResource(R.drawable.play_bt_pause);
		}

	}

	private void onVideoCompletion() {
		if (m3u8 != null && m3u8.current_index < m3u8.split_num - 1) {
			if (status != PLAYER_STATUS_ERROR) {
				nextSplit();
			}
		} else {
			// 播放结束

			MyToast.makeText(MediaPlayerActivity.this, "播放结束了", Toast.LENGTH_SHORT).show();
			bt_play.setImageResource(R.drawable.play_bt_play);

		}
	}

	private void onVideoError(int what, int extra) {
		Log.i("test", "播放出错：" + what);
		Log.i("test", "重新加载：" + video_url.toString());

		/*
		 * if(resource_type==RESOURCE_TYPE_HTTP){ reportError("视频播放失败"); }
		 */

		reloadVideo(0, 1); // 重新加载
	}

	private void initPlayerListener() {

		if (videoView instanceof MyVideoView_Android) {

			videoView.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(android.media.MediaPlayer mediaPlayer) {
					onVideoPrepared(mediaPlayer);
				}
			});
			videoView.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(android.media.MediaPlayer mp) {
					onVideoCompletion();
				}
			});
			videoView.setOnErrorListener(new android.media.MediaPlayer.OnErrorListener() {
				@Override
				public boolean onError(android.media.MediaPlayer mp, int what, int extra) {
					onVideoError(what, extra);
					return true; // 不让下层处理弹出系统默认错误提示。
				}
			});
		}
		/*else if (videoView instanceof MyVideoView_Vitamio) { // Mark Vitamio

			videoView
					.setOnPreparedListener(new io.vov.vitamio.MediaPlayer.OnPreparedListener() {
						@Override
						public void onPrepared(
								io.vov.vitamio.MediaPlayer mediaPlayer) {
							onVideoPrepared(mediaPlayer);
						}
					});
			videoView
					.setOnCompletionListener(new io.vov.vitamio.MediaPlayer.OnCompletionListener() {

						@Override
						public void onCompletion(io.vov.vitamio.MediaPlayer mp) {
							onVideoCompletion();
						}
					});
			videoView
					.setOnErrorListener(new io.vov.vitamio.MediaPlayer.OnErrorListener() {
						@Override
						public boolean onError(io.vov.vitamio.MediaPlayer mp,
								int what, int extra) {
							onVideoError(what, extra);
							return true; // 不让下层处理弹出系统默认错误提示。
						}
					});
		}*/

	}

	// 屏蔽掉控件之下的事件
	private OnTouchListener doNotingOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				handler_displayToolbar.removeMessages(MESSAGE_DISPLAYTOOLBAR);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				handler_displayToolbar.sendEmptyMessageDelayed(MESSAGE_DISPLAYTOOLBAR, LENGTH_DISPLAYTOOLBAR);
			}
			return true;
		}
	};

	/**
	 * 初始化广播和handler消息接收
	 */
	private void initReceiver() {

		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		// 广播接收,前端传过来的暂停或取消下载任务。,启动任务用startService
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); // 网络更改广播
		filter.addAction(Intent.ACTION_BATTERY_CHANGED); // 电量更新广播
		broadcastReceiver = new BroadcastReceiver() {
			// int split_position = 0;
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();

				if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

					if (resource_type == RESOURCE_TYPE_HTTP) {

						NetworkInfo info = connectivityManager.getActiveNetworkInfo();

						if (info == null || info.getState() != NetworkInfo.State.CONNECTED || !info.isAvailable()) {
							MyToast.makeText(MediaPlayerActivity.this, MediaPlayerActivity.this.getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
						} else if (info.getType() != ConnectivityManager.TYPE_WIFI) {
							MyToast.makeText(MediaPlayerActivity.this, "主淫，当前网络为移动网络，注意流量哦(∩_∩)", Toast.LENGTH_SHORT).show();
						}
					}

				} else if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
					int rawlevel = intent.getIntExtra("level", -1);// 获得当前电量
					int scale = intent.getIntExtra("scale", -1);
					// 获得总电量
					int level = -1;
					if (rawlevel >= 0 && scale > 0) {
						level = (rawlevel * 100) / scale;
					}

					if (level > 95) {
						img_battery.setImageResource(R.drawable.battery_100);
					} else if (level > 65) {
						img_battery.setImageResource(R.drawable.battery_75);
					} else if (level > 40) {
						img_battery.setImageResource(R.drawable.battery_50);
					} else if (level > 25) {
						img_battery.setImageResource(R.drawable.battery_25);
					} else if (level > 10) {
						img_battery.setImageResource(R.drawable.battery_4);
					} else {
						img_battery.setImageResource(R.drawable.bg_battery);
					}

					// txt_battery.setText(level + "%");
				}
			}
		};
		this.registerReceiver(broadcastReceiver, filter);
		// 用来控制工具条的消失
		handler_displayToolbar = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				layout_controllers.setVisibility(View.GONE);
			}
		};

		// 用来控制锁屏按钮的消失
		handler_displayLock = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				bt_unlock.setVisibility(View.GONE);
			}
		};

		// 用来刷新时间和进度条
		handler_refresh = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				refreshTime();
				handler_refresh.sendEmptyMessageDelayed(MESSAGE_REFRESH, 1000);
			}
		};

		// 视频地址获取完成后，进行播放回调
		handler_get_video = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				m3u8 = (M3u8Info) msg.getData().getSerializable("m3u8");
				if (M3u8Parser.check(m3u8)) {

					resource_url = m3u8.resource_url;

					video_duration = (int) (m3u8.total_seconds * 1000);
					Log.i("test", "获取m3u8,片段数:" + m3u8.split_num + "/时长：" + m3u8.total_seconds);

					m3u8.switchSplitIndexByPostion(video_position);
					loadUri(m3u8.getCurrentUrl(true), m3u8.split_offset); // 跳转在prepared事件里面做

				} else {
					// 播放失败，向后台报告
					if (resource_type == RESOURCE_TYPE_HTTP) {
						m3u8 = null;
						MyToast.makeText(MediaPlayerActivity.this, "获取播放地址失败！", Toast.LENGTH_SHORT).show();
					} else {
						MyToast.makeText(MediaPlayerActivity.this, "视频加载失败！", Toast.LENGTH_SHORT).show();
					}

				}
			}

		};
	}

	/**
	 * 开始载入视频
	 */
	private void startPlay() {

		boolean dataConnected = checkNetwork();
		if (!dataConnected) {
			return;
		}

		isFirstLoad = true;

		status = PLAYER_STATUS_PLAYING;
		this.startRotate_loading();

		Movie movie = (Movie) this.getIntent().getSerializableExtra("movie");
		/*Movie movie = new Movie();
		movie.setUrl("http://vf1.mtime.cn/Video/2014/10/15/mp4/141015164125451608.mp4");
		movie.setCname("测试视频");*/

		video_url = movie.getUrl();
		this.m3u8 = new M3u8Info(video_url, video_url);
		this.video_name = movie.getCname();
		
		txt_title.setText(video_name);
		startRotate_loading();
		status = PLAYER_STATUS_PLAYING;
		resource_type = RESOURCE_TYPE_HTTP;

		Message msg = new Message();
		Bundle data = new Bundle();
		data.putSerializable("m3u8", m3u8);
		msg.setData(data);
		handler_get_video.sendMessage(msg);

		// startRotate_loading();

		// 工具条5秒后消失
		handler_displayToolbar.sendEmptyMessageDelayed(MESSAGE_DISPLAYTOOLBAR, LENGTH_DISPLAYTOOLBAR);

		// 默认铺满
		switchVideoScale(true);

	}

	/**
	 * 检查网络
	 */
	private boolean checkNetwork() {
		boolean dataConnected = NetworkUtil.dataConnected(this);
		if (!dataConnected) {
			MyToast.makeText(this, this.getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
			status = PLAYER_STATUS_ERROR;
		}
		return dataConnected;
	}

	// 控制条上的按钮事件处理
	private OnClickListener btOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int vId = v.getId();
			switch (vId) {
			case R.id.bt_play: {
				if (videoView.isPlaying()) {
					bt_play.setImageResource(R.drawable.play_bt_play);
					videoView.pause();
					status = PLAYER_STATUS_PAUSE;
				} else {
					bt_play.setImageResource(R.drawable.play_bt_pause);
					videoView.start();
					status = PLAYER_STATUS_PLAYING;
				}
				break;
			}

			case R.id.bt_back: {
				MediaPlayerActivity.this.finish();
				break;
			}

			case R.id.bt_lock: {
				isLockScreen = true;
				MyToast.makeText(MediaPlayerActivity.this, "屏幕锁定", Toast.LENGTH_SHORT).show();
				layout_controllers.setVisibility(View.GONE);
				frame_lock.setVisibility(View.VISIBLE);
				bt_unlock.setVisibility(View.VISIBLE);
				handler_displayLock.removeMessages(0);
				handler_displayLock.sendEmptyMessageDelayed(0, LENGTH_DISPLAYLOCK);
				break;
			}
			case R.id.bt_unlock: {
				isLockScreen = false;
				MyToast.makeText(MediaPlayerActivity.this, "解除屏幕锁定", Toast.LENGTH_SHORT).show();
				layout_controllers.setVisibility(View.VISIBLE);
				frame_lock.setVisibility(View.GONE);
				break;
			}

			}

			// 工具条5秒后消失
			handler_displayToolbar.removeMessages(MESSAGE_DISPLAYTOOLBAR);
			handler_displayToolbar.sendEmptyMessageDelayed(MESSAGE_DISPLAYTOOLBAR, LENGTH_DISPLAYTOOLBAR);

		}
	};

	// 进度条监听
	private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			Log.i("test", "onStartTrackingTouch");
			isTouched = true;

			if (video_duration > 0 && seekBar.getMax() > 100) { // 如果视频加载了才可以拖
				frame_process.setVisibility(View.GONE);
				layout_scrolling_progress.setVisibility(View.VISIBLE);
				layout_scrolling_time.setVisibility(View.VISIBLE);
			}

			handler_displayToolbar.removeMessages(MESSAGE_DISPLAYTOOLBAR);

		}

		// 在结束滑动的时候跳转
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			Log.i("test", "onStopTrackingTouch");

			isTouched = false;

			if (video_duration > 0 && seekBar.getMax() > 100) { // 如果视频加载了才可以拖
				int progress = seekBar.getProgress();
				seekAndPlay(progress);
			}

			layout_scrolling_progress.setVisibility(View.GONE);
			handler_displayToolbar.sendEmptyMessageDelayed(MESSAGE_DISPLAYTOOLBAR, LENGTH_DISPLAYTOOLBAR);

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// Log.i("test", "onProgressChanged  "+progress);

			if (video_duration > 0 && seekBar.getMax() > 100) { // 如果视频加载了才可以拖

				String str_time = StringUtil.getTimeFormat(progress, true);
				txt_current_position.setText(str_time);
				txt_total_duration.setText(txt_duration);
			}

			if (video_duration > 0 && seekBar.getMax() > 100) { // 如果视频加载了才可以拖

				String str_time = StringUtil.getTimeFormat(progress, false);
				txt_current_position.setText(str_time);
				txt_total_duration.setText(txt_duration);

				if (layout_scrolling_progress.isShown()) {
					String txt_offset = StringUtil.getTimeFormat_scrolled(progress - pre_position_touch);
					String str_time_short = StringUtil.getTimeFormat(progress, true);
					scrolling_current_time.setText(str_time_short);
					scrolling_offset.setText(txt_offset);
				}

			}

		}
	};

	/**
	 * 拖动的时候，切换分片和播放
	 * 
	 * @param progress
	 *            毫秒
	 */
	private void seekAndPlay(long progress) {

		if (m3u8 == null) {
			videoView.seekTo((int) progress);
			return;
		}

		this.startRotate_loading();

		boolean isSwitch = m3u8.switchSplitIndexByPostion(progress);

		if (this.video_url == null) { // 如果是第一次加载
			loadUri(m3u8.getCurrentUrl(true), m3u8.split_offset);
		} else if (isSwitch) { // 需要跳转分片

			loadUri(m3u8.getCurrentUrl(true), m3u8.split_offset);

		} else {
			videoView.seekTo((int) m3u8.split_offset);
		}

		if (status == PLAYER_STATUS_PLAYING) {
			videoView.start();
			bt_play.setImageResource(R.drawable.play_bt_pause);
		}

	}

	/**
	 * 播放下一分片，自动顺序切换
	 */
	private void nextSplit() {

		if (m3u8 == null) {
			return;
		}

		startRotate_loading();

		m3u8.current_index++;
		if (m3u8.current_index >= m3u8.split_num) { // 播放结束
			m3u8.current_index = m3u8.split_num - 1;
			return;
		}

		loadUri(m3u8.getCurrentUrl(true), 0);

		if (status == PLAYER_STATUS_PLAYING) {
			videoView.start();
			bt_play.setImageResource(R.drawable.play_bt_pause);
		}

		if (isCacheCompleted) { // 如果上一次的缓冲已经完成
			isCacheCompleted = false;
			isStartedCacheNext = false;
		}

	}

	// private int load_video_duration; // 加载视频用时，如果太长就重新load

	/**
	 * 加载视频源，注意这里的seek_split_position不是整个播放进度
	 */
	private void loadUri(String video_url, long seek_split_position) {
		this.video_url = video_url;
		this.seek_position = seek_split_position;
		videoSeekBar.setEnabled(false);

		// load_video_duration = 0;

		if (video_url == null || video_url.equals("")) {
			MyToast.makeText(this, "视频播放失败", Toast.LENGTH_LONG).show();
		} else {
			Log.i("test", "加载视频:" + video_url);

			startRotate_loading();
			// is_start_load = true;

			videoView.setVideoPath(video_url);

		}

	}

	/**
	 * 失败之后reload，并跳转到上一次的进度 seek_offset, 如果有错误，往后跳一段距离 error_type 1：播放失败
	 */
	private void reloadVideo(int seek_offset, int error_type) {

		retry_count++;
		if (error_type == 1) {
			error_count++;
		}

		if (error_count >= RETRY_MAX) {

			// Mark Vitamio
			/*if (videoView instanceof MyVideoView_Android) {
				MyToast.makeText(MediaPlayerActivity.this, "使用软解码播放", Toast.LENGTH_SHORT).show();
				retry_count = 0;
				error_count = 0;
				switchDecoder();
				return;
			}*/

			// 降低清晰度进行播放
			/*
			 * if(video_clear!=1){ video_clear=1; }
			 */

			MyToast.makeText(MediaPlayerActivity.this, "视频播放失败\n" + video_url, Toast.LENGTH_LONG).show();
			videoView.pause();
			stopRotate_loading();
			status = PLAYER_STATUS_ERROR;
			bt_play.setImageResource(R.drawable.play_bt_play);

			return;
		}

		int seek = 0;
		if (m3u8 != null
		// && !is_start_load
		) {
			m3u8.switchSplitIndexByPostion(video_position);
			seek = (int) (m3u8.split_offset) + seek_offset;
		}

		loadUri(video_url, seek);

	}

	/**
	 * 刷新播放时间、进度、缓冲进度等界面
	 */
	private void refreshTime() {

		txt_clock.setText(dateFormat.format(new Date()));

		int split_position = 0;
		if (m3u8 != null) {
			split_position = m3u8.getCurrentSplitPosition();
		}

		if (videoView.isPlaying()) {

			if (videoView instanceof MyVideoView_Android) {
				video_position = ((MyVideoView_Android) videoView).getCurrentPosition();
			}
			/*else if (videoView instanceof MyVideoView_Vitamio) { // Mark
																	// Vitamio
				video_position = ((MyVideoView_Vitamio) videoView)
						.getCurrentPosition();
			}*/

			if (m3u8 != null) {
				video_position = split_position + video_position; // 当前分片播放位置
			}

			// Log.i("test","video_position:"+video_position);

			// 判断是否卡住
			if (pre_position_refresh == video_position) {

				wait_count++;
				Log.i("test", "卡住了啊。。。。" + wait_count);

				startRotate_loading();

				if (wait_count >= 6) { // 加次数判断，等待一段时间，防止太多次的重新加载
					wait_count = 0;

					Log.i("test", "重新加载：" + video_url.toString());
					reloadVideo(0, 0);

					if (retry_count > RETRY_MAX) {
						return;
					}
				}

			} else {
				retry_count = 0; // 错误计数清零
				stopRotate_loading();

				// 卡的时候不刷新进度条
				// 如果正在手动拖动，就不自动刷进度条。
				if (!isTouched) {
					videoSeekBar.setProgress((int) video_position); // 上一个分片位置+当前分片位置
				}

			}
			pre_position_refresh = video_position;
		}

		// 刷新缓冲进度条, 只有播在线视频才刷
		int buffer_position = 0;
		int current_buffer_percent = 0; // 当前分片缓冲进度百分比（由videoView自动更新）
		if ((resource_type == RESOURCE_TYPE_HTTP) && m3u8 != null) {

			if (m3u8.is_current_split_local) {
				current_buffer_percent = 100;
				isCacheCompleted = true;
			} else {
				current_buffer_percent = videoView.getBufferPercentage();
			}

			// Log.i("test","isLastSplit:"+m3u8.isLastSplit()+",当前分片:"+m3u8.current_index);

			// 下一分片缓冲进度
			if (current_buffer_percent == 100 && !m3u8.isLastSplit()) {
				if (m3u8_cache_percent < 100) {
					buffer_position = (int) (split_position + m3u8.getCurrentMilliSecond() + m3u8_cache_percent * m3u8.seconds[m3u8.current_index + 1] * 10);
				} else {
					int index_cached = m3u8.current_index + 1;
					while (index_cached < m3u8.split_num && m3u8.split_cached[index_cached]) {
						index_cached++;
					}
					buffer_position = m3u8.getSplitPosition(index_cached);
				}

			} else { // 当前分片缓冲
				buffer_position = (int) (split_position + current_buffer_percent * 1.0 / 100 * m3u8.getCurrentMilliSecond());
			}

			videoSeekBar.setSecondaryProgress(buffer_position);
		}

	}

	private void switchVideoScale(boolean isFullScreen) {

		if (isFullScreen) {
			// 满屏（拉伸）
			Log.d("test", "screenWidth: " + screenWidth + " screenHeight: " + screenHeight);
			videoView.setVideoScale(screenWidth, screenHeight, true);
		} else {

			// 按原大小
			int mWidth = screenWidth;
			int mHeight = screenHeight;

			if (videoWidth > 0 && videoHeight > 0) {
				if (videoWidth * mHeight > mWidth * videoHeight) {
					mHeight = mWidth * videoHeight / videoWidth;
				} else if (videoWidth * mHeight < mWidth * videoHeight) {
					mWidth = mHeight * videoWidth / videoHeight;
				}
			}

			Log.d("test", "mWidth: " + mWidth + " mHeight: " + mHeight);

			videoView.setVideoScale(mWidth, mHeight, false);

		}

	}

	// 等待动画
	private RotateAnimation rotateAnimation;
	private boolean isRotating = false;

	private void startRotate_loading() {

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

		frame_loading.setVisibility(View.VISIBLE);
		if (!isRotating) {
			img_loading.startAnimation(rotateAnimation);
		}

	}

	private void stopRotate_loading() {
		if (rotateAnimation != null) {
			rotateAnimation.cancel();
		}
		frame_loading.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onResume() {
		super.onResume();

		Log.i("test", "onResume");
		handler_refresh.sendEmptyMessage(MESSAGE_REFRESH); // 启动刷新

		// 如果之前屏幕被锁，下次进入的时候解锁
		if (isLockScreen) {
			isLockScreen = false;
			// bt_lock.setImageResource(R.drawable.mov_icon_lock);
			layout_controllers.setVisibility(View.VISIBLE);

		}

		// 应用被打断之后，恢复上次被中断的，第一次载入不会调用这个，status初始值为UNKNOW
		if (videoView != null && status == PLAYER_STATUS_PAUSE) {

			status = PLAYER_STATUS_PLAYING; // 再次进来play
			if (m3u8 != null) {
				m3u8.switchSplitIndexByPostion(video_position);
				seek_position = m3u8.split_offset; // 更新跳转位置
			}

			bt_play.setImageResource(R.drawable.play_bt_play);
			layout_controllers.setVisibility(View.VISIBLE);
			// bt_lock.setVisibility(View.VISIBLE);
			// videoView.start();
		}

	}

	@Override
	public void onPause() {

		super.onPause();

		Log.i("test", "MediaPlayerActivity onPause");

		// 暂停播放
		if (videoView != null && videoView.isPlaying()) {
			videoView.pause();
			status = PLAYER_STATUS_PAUSE;
			bt_play.setImageResource(R.drawable.play_bt_play);
		}
		if (handler_refresh != null) {
			handler_refresh.removeMessages(MESSAGE_REFRESH);
		}
		if (handler_displayToolbar != null) {
			handler_displayToolbar.removeMessages(MESSAGE_DISPLAYTOOLBAR);
		}

	};

	/**
	 * 退出时注意：清除handler、关闭服务、关闭数据库会话、关闭缓冲等等。
	 */
	@Override
	public void onDestroy() {
		Log.i("test", "MediaPlayerActivity onDestroy");

		if (broadcastReceiver != null) {
			this.unregisterReceiver(broadcastReceiver);
		}

		handler_displayToolbar.removeMessages(MESSAGE_DISPLAYTOOLBAR);
		handler_refresh.removeMessages(MESSAGE_REFRESH);

		try {
			videoView.stopPlayback();
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onDestroy();
	}

	/**
	 * 再按一次退出
	 */
	long waitTime = 2000;
	long touchTime = 0;

}
