package com.haomee.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 解决GridView和ScrollView嵌套滚动冲突的问题
 */
public class UnScrollableGridView extends GridView {
	
	public UnScrollableGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UnScrollableGridView(Context context) {
		super(context);
	}

	public UnScrollableGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}