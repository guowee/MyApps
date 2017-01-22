package com.haomee.player;

import android.content.Context;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.VideoView;

public class MyVideoView_Android extends VideoView implements MyVideoView {
	
	private int videoWidth;
	private int videoHeight;
	
	public MyVideoView_Android(Context context) {
		super(context);
	}
	public MyVideoView_Android(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public MyVideoView_Android(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	

	public void setSizeParams(int videoWidth, int videoHeight){
		this.videoHeight = videoHeight;
		this.videoWidth = videoWidth;
	}
	
	private boolean is_full_screen;
	

	
    public void setVideoScale(int width , int height, boolean is_full_screen){
    	this.is_full_screen = is_full_screen;
    	LayoutParams lp = getLayoutParams();
    	lp.height = height;
		lp.width = width;
		setLayoutParams(lp);
    }
	
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(is_full_screen){
        	 int width = getDefaultSize(videoWidth, widthMeasureSpec);
        	 int height = getDefaultSize(videoHeight, heightMeasureSpec);
        	 setMeasuredDimension(width,height);
        }else{
        	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
    
    
    
    @Override
	public void seekTo(int position) {
    	try{
    		super.seekTo(position);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}
    
    
	@Override
	public void setOnPreparedListener(Object obj) {
		super.setOnPreparedListener((OnPreparedListener) obj);
	}
	@Override
	public void setOnCompletionListener(Object obj) {
		super.setOnCompletionListener((OnCompletionListener) obj);
	}
	@Override
	public void setOnErrorListener(Object obj) {
		super.setOnErrorListener((OnErrorListener) obj);
	}
	
}
