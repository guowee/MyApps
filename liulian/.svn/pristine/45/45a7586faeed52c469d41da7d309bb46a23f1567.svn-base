package com.haomee.player;

import android.view.ViewGroup.LayoutParams;

/**
 * 播放器接口
 * 用于切换不同播放器时，有统一的适配
 */
public interface MyVideoView {

	public void start();
	public void pause();
	public void seekTo(int position);
	public void stopPlayback();
	public boolean isPlaying();
	public void setVideoPath(String url);
	
	public int getBufferPercentage();
	//public int getCurrentPosition();		// vitamio居然不按照原生的播放器，返回了long
	
	public void setOnPreparedListener(Object obj);
	public void setOnCompletionListener(Object obj);
	public void setOnErrorListener(Object obj);
	
	public void setSizeParams(int videoWidth, int videoHeight);
	public void setVideoScale(int screenWidth, int screenHeight, boolean is_full_screen);
	
	public int getWidth();
	
	public void setLayoutParams(LayoutParams params);
	
	
}
