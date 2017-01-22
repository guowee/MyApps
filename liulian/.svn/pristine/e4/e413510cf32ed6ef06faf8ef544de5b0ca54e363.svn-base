package com.haomee.player;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.haomee.entity.Music;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.MainActivity;
import com.haomee.liulian.R;
import com.haomee.util.StringUtil;
import com.haomee.view.RoundProgressBar;

@SuppressLint("NewApi")
public class MyMusicPlayer {

	private static MyMusicPlayer instance;
	private MediaPlayer mediaPlayer;

	private Music current_music;

	// 播放器当前状态
	private int status;

	// 状态常量
	public static final int STATUS_IDEL = 1; // 空闲
	public static final int STATUS_PREPEARING = 2; // 准备、缓冲
	public static final int STATUS_PREPEARED = 3; // 准备、缓冲
	public static final int STATUS_PLAYING = 4; // 播放
	public static final int STATUS_PAUSED = 5; // 暂停		
	public static final int STATUS_ERROR = -1; // 出错

	public static final String BROADCAST_PLAY_PAUSE = "com.haomee.player.music.playOrPause";
	public static final String BROADCAST_NEXT = "com.haomee.player.music.next";
	public static final String BROADCAST_STOP = "com.haomee.player.music.stop";

	// 通知栏
	private NotificationManager mNotificationManager;

	private Context context;

	private OnPreparedListener onPreparedListener;

	private ArrayList<Music> list_history;

	private int count_error; // 重复错误计数

	private MyMusicPlayer() {

		context = LiuLianApplication.getInstance();

		list_history = new ArrayList<Music>();

		initMediaPlayer();

		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	private BroadcastReceiver playerReceiver;
	private IntentFilter filter;
	
	private void initBroadcastReceiver() {
		
		if(playerReceiver==null){
			filter = new IntentFilter();
			filter.addAction(BROADCAST_PLAY_PAUSE);
			filter.addAction(BROADCAST_NEXT);
			filter.addAction(BROADCAST_STOP);

			playerReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String action = intent.getAction();
					if (action.equals(BROADCAST_PLAY_PAUSE)) {
						Log.i("test", "playOrPause");
						switch_play_pause();
					} else if (action.equals(BROADCAST_NEXT)) {
						playNext(true);
					} else if (action.equals(BROADCAST_STOP)) {
						exit();
					}

				}
			};
		}
		
		context.registerReceiver(playerReceiver, filter);
	}

	private void initMediaPlayer() {

		count_error = 0;

		if (mediaPlayer != null) {
			try {
				mediaPlayer.stop();
				mediaPlayer.release();
				handler_timer.removeMessages(0);
				mediaPlayer = null;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				Log.i("test", "onPrepared");

				count_error = 0;

				mediaPlayer.start();
				status = STATUS_PLAYING;

				if (onPreparedListener != null) {
					onPreparedListener.onPrepared(mp);
				}

			}
		});

		mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				//Log.i("test","onBufferingUpdate "+percent);
			}
		});

		// 自定下一首
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if (current_music != null && getCurrentPosition() > 0) {
					Log.i("test", "播放结束：" + current_music.getTitle());
					count_error = 0;
					playNext(true);
				}
			}
		});

		mediaPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				if (current_music != null) {
					count_error++;
					Log.i("test", "播放出错：" + current_music.getTitle() + "_what:" + what + "_extra:" + extra + "_count:" + count_error);
					//play();

					if (count_error > 12) {
						initMediaPlayer();
						start(current_music);
					}
				}

				return false;
			}
		});

	}

	public static synchronized MyMusicPlayer getInstance() {
		if (instance == null) {
			instance = new MyMusicPlayer();
		}

		return instance;
	}

	public int getStatus() {
		return status;
	}

	public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
		this.onPreparedListener = onPreparedListener;
	}

	// 是否是当前歌曲
	public boolean isCurrentMusic(Music music) {
		return current_music != null && music != null && current_music.getId().equals(music.getId());
	}

	/**
	 *  播放某歌曲，初始为开始播放
	 * @param music
	 * @return 是否成功
	 */
	public boolean start(Music music) {
		count_error = 0;

		if (music != null) {
			try {
				

				initBroadcastReceiver();


				try {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.stop();
					}
				} catch (IllegalStateException e) {
					e.printStackTrace();
					initMediaPlayer();
				}

				try {
					mediaPlayer.reset();
				} catch (IllegalStateException e) {
					e.printStackTrace();
					initMediaPlayer();
				}

				mediaPlayer.setDataSource(music.getUrl());

				mediaPlayer.prepareAsync();
				status = STATUS_PREPEARED;

				current_music = music;

				handler_timer.sendEmptyMessage(0);

				Log.i("test", "开始播放：" + music.getUrl());

				if (!list_history.contains(music)) {
					list_history.add(music);
				}

				return true;

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}

	}

	public void pause() {
		try {
			mediaPlayer.pause();
			status = STATUS_PAUSED;
			handler_timer.removeMessages(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void play() {
		try {
			mediaPlayer.start();
			status = STATUS_PLAYING;
			handler_timer.sendEmptyMessage(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void switch_play_pause() {
		if (mediaPlayer.isPlaying()) {
			pause();
		} else {
			play();
		}
	}

	/**
	 * 播放列表下一首
	 * @param loop 是否循环列表
	 * @return
	 */
	public Music playNext(boolean loop) {

		Music next = null;

		if (current_music == null && list_history.size() > 0) {
			next = list_history.get(0);
		}
		if (current_music != null) {
			int index = list_history.indexOf(current_music);
			index++;
			if (index >= list_history.size()) {
				index = 0;
			}
			next = list_history.get(index);
		}

		if (next != null) {
			if (!next.equals(current_music) || loop) {
				start(next);
			} else {
				next = null; // 如还是当前首不循环，就null
			}

		}

		return next;
	}

	public Music getCurrentMusic() {
		return current_music;
	}

	public int getDuration() {
		int duration = 0;
		try {
			if (mediaPlayer != null) {
				duration = mediaPlayer.getDuration();
			}
		} catch (Exception e) {
			Log.e("test", "getDuration: " + e.getMessage());
		}

		return duration;
	}

	public int getCurrentPosition() {
		int position = 0;
		try {
			if (mediaPlayer != null) {
				position = mediaPlayer.getCurrentPosition();
			}
		} catch (Exception e) {
			Log.e("test", "getCurrentPosition: " + e.getMessage());
		}

		return position;
	}

	public int getProgress() {
		int progress = 0;
		int duration = getDuration();
		if (duration > 0) {
			progress = getCurrentPosition() * 100 / duration;
		}

		return progress;
	}

	private Notification mNotification;
	private static final int notify_requestCode = 1;

	private void addNotify() {
		// 初始化Notification   
		mNotification = new Notification();
		mNotification.flags = Notification.FLAG_NO_CLEAR;
		//mNotification.flags=Notification.FLAG_AUTO_CANCEL;
		mNotification.icon = R.drawable.ic_launcher;

		// ③ 定义notification的消息 和 PendingIntent  

		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(context, notify_requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mNotification.contentIntent = contentIntent;
		mNotification.setLatestEventInfo(context, "", "", contentIntent);

		/*Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(context, notify_requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mNotification.setLatestEventInfo(context, "", "", contentIntent);*/
	}

	private void updateNotify() {

		if (mNotification == null) {
			addNotify();
		}

		// 创建RemoteViews用在Notification中  
		RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.item_notification_music);

		contentView.setTextViewText(R.id.notificationTitle, current_music.getTitle() + " - " + current_music.getAuthor());
		contentView.setProgressBar(R.id.notificationProgress, 100, getProgress(), false);

		contentView.setTextViewText(R.id.notificationPosition, StringUtil.getTimeFormat(getCurrentPosition(), true));
		contentView.setTextViewText(R.id.notificationDuration, StringUtil.getTimeFormat(getDuration(), true));

		// 播放或暂停
		/*Intent intent_broadcast_playOrPause = new Intent(BROADCAST_PLAY_PAUSE);
		PendingIntent playOrPausePendingIntent = PendingIntent.getBroadcast(context, 1, intent_broadcast_playOrPause, PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(R.id.notificationTitle, playOrPausePendingIntent);

		// 下一首
		Intent intent_broadcast_next = new Intent(BROADCAST_NEXT);
		PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 1, intent_broadcast_next, PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(R.id.notificationDuration, nextPendingIntent);*/

		// 退出
		Intent intent_broadcast_stop = new Intent(BROADCAST_STOP);
		PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 1, intent_broadcast_stop, PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(R.id.bt_stop, stopPendingIntent);

				
		mNotification.contentView = contentView;

		mNotificationManager.notify(notify_requestCode, mNotification);

	}

	private void cancelNotify() {
		mNotificationManager.cancel(notify_requestCode);
	}

	private RoundProgressBar progressBar;

	private Handler handler_timer = new Handler() {
		public void handleMessage(Message msg) {

			if (status == STATUS_PLAYING) {
				updateNotify();
			}

			updateProgressBar();

			handler_timer.sendEmptyMessageDelayed(0, 1000);

		};
	};

	public void setProgressBar(RoundProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	private void updateProgressBar() {
		try {
			if (progressBar != null) {
				progressBar.setProgress(getProgress());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void exit() {

		if(playerReceiver!=null){
			context.unregisterReceiver(playerReceiver);
		}
		

		try {
			if (current_music != null) {
				current_music = null;
				mediaPlayer.stop();
				mediaPlayer.release();
				handler_timer.removeMessages(0);
				if (mNotificationManager != null) {
					cancelNotify();
				}
				
				if (progressBar != null) {
					progressBar.setProgress(0);
					progressBar.setImageResource(R.drawable.content_button_music_start);
				}
				
			}

		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

}
