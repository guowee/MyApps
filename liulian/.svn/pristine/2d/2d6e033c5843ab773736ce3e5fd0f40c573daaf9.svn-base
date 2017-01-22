package com.haomee.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 解决ListView和ScrollView嵌套滚动冲突的问题
 */
public class UnScrollableListView extends ListView {
	public UnScrollableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UnScrollableListView(Context context) {
		super(context);
	}

	public UnScrollableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}