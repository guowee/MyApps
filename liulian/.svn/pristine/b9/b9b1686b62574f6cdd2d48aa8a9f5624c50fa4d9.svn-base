package com.haomee.fragment;

import com.haomee.liulian.R;
import com.haomee.util.ViewUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class BlankFragment extends Fragment{

	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		

		if (view == null) {
			view = inflater.inflate(R.layout.layout_loading, null);
			
			view.setLayoutParams(new ViewGroup.LayoutParams(
					ViewUtil.getScreenWidth(this.getActivity()),
					ViewUtil.getScreenHeight(this.getActivity())-ViewUtil.dip2px(this.getActivity(), 150)));
			
			RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			rotateAnimation.setDuration(1000);
			rotateAnimation.setRepeatCount(-1);
			rotateAnimation.setInterpolator(new LinearInterpolator());
			
			ImageView icon_loading = (ImageView) view.findViewById(R.id.icon_loading);
			icon_loading.startAnimation(rotateAnimation);
			
			
		}else{
			((ViewGroup) view.getParent()).removeView(view);
		}
			
		
		
		return view;
	}
}
