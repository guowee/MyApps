package com.hipad.tracker.adapter;

import java.util.List;


import com.hipad.tracker.MainTestActivity;
import com.hipad.tracker.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;

public class NavigatePagerAdapter extends PagerAdapter {

	private List<View> views = null;
	private Context context = null;

	public NavigatePagerAdapter(List<View> views, Context context) {
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
			
			ImageButton guide_btn = (ImageButton) container.findViewById(R.id.guide_btn);
			
			guide_btn.setVisibility(View.VISIBLE);
			guide_btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(context, MainTestActivity.class);

					context.startActivity(intent);

					((Activity) context).finish();

				}
			});

		}
		return views.get(position);
	}

}
