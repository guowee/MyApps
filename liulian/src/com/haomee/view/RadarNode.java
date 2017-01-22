package com.haomee.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.haomee.entity.Users;
import com.haomee.util.ViewUtil;

public class RadarNode extends ImageView{
	
	public int node_size = 50;	// 点的大小
	
	private FrameLayout.LayoutParams params;
	public int x,y;
	public Users user;
	
	public RadarNode(Context context) {
		super(context);
	}
	public RadarNode(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public RadarNode(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	// 初始化
	public void init(int center_x, int center_y, int radius_start, double degree){
		this.center_x = center_x;
		this.center_y = center_y;
		this.radius_start = radius_start;
		this.degree = degree;
		
		node_size = ViewUtil.dip2px(this.getContext(), 20);
		
		params = new FrameLayout.LayoutParams(node_size,node_size);	
		params.gravity = Gravity.LEFT|Gravity.TOP;
		
		this.setPadding(4, 4, 4, 4);
		this.setScaleType(ScaleType.FIT_XY);		
		this.setLayoutParams(params);
	}


	private int center_x, center_y;
	//public int x,y;
	public int radius;
	public int radius_start;
	public double degree;
	
	public void setRadius(int radius){
		
		this.radius = radius;
		x = (int) (radius*Math.cos(Math.toRadians(degree)) + center_x -node_size/2);
		y = (int) (center_y - radius*Math.sin(Math.toRadians(degree)) -node_size/2);
		
		
		params.leftMargin = x;
		params.topMargin = y;				
		this.setLayoutParams(params);
	}


	// 半径和角度
/*	public void setPosition(int radius_start, double degree){
		
		this.radius_start = radius_start;
		this.degree = degree;
		
		setRadius(radius_start);
	}*/
	
	/*public void setPosition(int x, int y){
		
		this.x = x;
		this.y = y;
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		params.leftMargin = x;
		params.topMargin = y;
		
		this.setLayoutParams(params);
	}*/
	

	
	public boolean is_anim_started = false;
	
	public void startAnim(){
		
		if(is_anim_started){
			return;
		}
		
		AnimationSet animationSet = new AnimationSet(true);


		Animation scaleAnimation1 = new ScaleAnimation(1f, 1.3f, 1f, 1.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		Animation scaleAnimation2 = new ScaleAnimation(1.3f, 1f, 1.3f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

		scaleAnimation1.setDuration(200);
		scaleAnimation2.setDuration(200);
		scaleAnimation2.setStartOffset(200);
		
		animationSet.addAnimation(scaleAnimation1);
		animationSet.addAnimation(scaleAnimation2);


		animationSet.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				is_anim_started = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				is_anim_started = false;
			}
		});

		this.startAnimation(animationSet);
	}

}
