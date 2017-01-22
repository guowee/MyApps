package com.example.adapter;

import java.util.List;

import com.example.teabaike.MainActivity;
import com.example.teabaike.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

public class GuidePagerAdapter extends PagerAdapter {

	/** 界面列表 */
	private List<View> views = null;
	/** 上下文内容 */
	private Context context = null;

	public GuidePagerAdapter(List<View> views, Context context) {
		this.views = views;
		this.context = context;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {

		((ViewPager) container).removeView(views.get(position));

	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);

	}

	@Override
	public Object instantiateItem(View container, int position) {

		((ViewPager) container).addView(views.get(position), 0);
		
		if (position == views.size() - 1) {
			/* 若是最后一个界面 */
			Button guide_btn = (Button) container.findViewById(R.id.guide_btn);
			/* 设置图片按钮监听，做跳转操作 */
			guide_btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					Intent intent = new Intent(context, MainActivity.class);
					
					context.startActivity(intent);
					
					((Activity) context).finish();

				}
			});

		}
		return views.get(position);
	}
}
