package com.hipad.tracker.adapter;

import java.util.List;

import com.google.android.gms.drive.internal.SetDrivePreferencesRequest;
import com.hipad.tracker.R;
import com.hipad.tracker.widget.ArrowAnimation;
import com.hipad.tracker.widget.TouchAnimation;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class GuidePagerAdapter extends PagerAdapter{
	private List<View> views = null;
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
		
		if(position==0){
			
			TouchAnimation watchView=(TouchAnimation) container.findViewById(R.id.ico_watch_anim);
			watchView.restart(true);
			
			ArrowAnimation arrowView=(ArrowAnimation) container.findViewById(R.id.ico_arrow_anim);
			arrowView.restart(true);
			
		}
		
		return views.get(position);
	}

}
