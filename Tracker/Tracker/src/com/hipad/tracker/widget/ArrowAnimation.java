package com.hipad.tracker.widget;

import com.hipad.tracker.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ArrowAnimation extends FrameLayout {
	 private ImageView mPromptImage;
	   
	    private  AnimationDrawable drawable;
	    
	    public ArrowAnimation(Context context, AttributeSet attrs) {
	        super( context, attrs);
	        this.drawable=(AnimationDrawable)getResources().getDrawable( R.drawable.arrow_animation);
	        init();
	    }

	    private void init() {
	        LayoutInflater inflater = LayoutInflater.from(getContext());
	        inflater.inflate( R.layout.request_loading, this, true);
	        mPromptImage = (ImageView)findViewById( R.id.loading_prompt);
	        setClickable( true);
	    }

	   

	    public void restart(boolean start) {
	        mPromptImage.setBackground( drawable);
	        if(start) {
	            drawable.start();
	        }
	        else {
	            drawable.stop();
	        }
	    }

	    public void updateLoading(boolean success, String tips) {
	        if(success) {
	            restart( false);
	        }
	        else {
	            Drawable d = mPromptImage.getBackground();
	            if(d != null) {
	                if(d instanceof AnimationDrawable) {
	                    ((AnimationDrawable)d).stop();
	                }
	                d.setCallback( null);
	            }
	            mPromptImage.setBackgroundResource( R.drawable.loading_00);
	        }
	    }

	    public void updateLoading(boolean success) {
	        updateLoading( success, null);
	    }

}
