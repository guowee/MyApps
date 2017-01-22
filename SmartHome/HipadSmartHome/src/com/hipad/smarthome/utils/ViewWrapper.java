package com.hipad.smarthome.utils;

import android.view.View;

public class ViewWrapper {

	private View mTarget;
	 
    public ViewWrapper(View target) {
        mTarget = target;
    }
 
    public int getWidth() {
        return mTarget.getLayoutParams().width;
    }
 
    public void setWidth(int width) {
        mTarget.getLayoutParams().width = width;
        mTarget.requestLayout();
    }
}
