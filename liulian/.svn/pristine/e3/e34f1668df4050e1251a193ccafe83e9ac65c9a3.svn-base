package com.haomee.player;

import java.io.IOException;

import com.haomee.chat.Utils.PreferenceUtils;
import com.haomee.liulian.SettingActivity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class MySoundPlayer {

	private static MySoundPlayer instance;

	private Context context;

	// 用来播放背景音乐
	private static MediaPlayer mediaPlayer_background;

	// 用来播放很短的声音
	private static SoundPool pool;

	private MySoundPlayer(Context context) {
		this.context = context;
	}

	// 单例
	public static synchronized MySoundPlayer getInstance(Context context) {
		if (instance == null) {
			instance = new MySoundPlayer(context);
		}
		return instance;
	}


	

	// 专门用来播放背景音乐的
	public void play_background(int resId, boolean loop) {
		try {
			
			if (!PreferenceUtils.getInstance(context).getSettingSound_operation()) {
				return;
			}
			
			if(mediaPlayer_background==null){
				mediaPlayer_background = new MediaPlayer();
			}else{
				this.stop_background();
				mediaPlayer_background.reset();
			}
			
			AssetFileDescriptor afd = context.getResources().openRawResourceFd(
					resId);
			if (afd == null)
				return;
			mediaPlayer_background.setDataSource(afd.getFileDescriptor(),
					afd.getStartOffset(), afd.getLength());
			afd.close();
			mediaPlayer_background.setLooping(loop);
			mediaPlayer_background.prepare();
			mediaPlayer_background.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop_background() {
		if (mediaPlayer_background!=null && mediaPlayer_background.isPlaying()) {
			mediaPlayer_background.stop();
			mediaPlayer_background.reset();
		}
	}


	public void play_shortSound(int resId) {
		
		if (!PreferenceUtils.getInstance(context).getSettingSound_operation()) {
			return;
		}
		
		
		if(pool == null){
			pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
			pool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				@Override
				public void onLoadComplete(SoundPool soundPool, int sampleId,
						int status) {
					pool.play(sampleId, 1, 1, 1, 0, 1);
				}
			});
			
		}
		
		pool.load(context, resId, 1);
	}
}
