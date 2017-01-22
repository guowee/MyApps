package com.haomee.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.haomee.entity.TopicType;
import com.haomee.liulian.R;
import com.haomee.util.ViewUtil;
import com.haomee.view.FancyCoverFlow;
import com.haomee.view.FancyCoverFlowAdapter;

public class FlowTypesAdapter extends FancyCoverFlowAdapter {

	public List<TopicType> topicTypes;
	private Context context;
	private int screen_width;
	private int item_width;

	public FlowTypesAdapter() {
		
	}

	public FlowTypesAdapter(Context context, List<TopicType> topicTypes) {
		this.topicTypes = topicTypes;
		this.context = context;

		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_width = dm.widthPixels;
		item_width = ViewUtil.dip2px(context, 80);

	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int i) {
		return topicTypes.get(i % topicTypes.size());
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getCoverFlowItem(final int i, View reuseableView, ViewGroup viewGroup) {

		View view = null;

		if (reuseableView != null) {
			view = reuseableView;
		} else {

			if (topicTypes != null) {

				view = LayoutInflater.from(context).inflate(R.layout.item_topic_type, null);
				ImageView item_img = (ImageView) view.findViewById(R.id.item_img);

				ViewGroup.LayoutParams params_img = item_img.getLayoutParams();
				params_img.width = item_width;
				params_img.height = params_img.width;
				item_img.setLayoutParams(params_img);
				view.setLayoutParams(new FancyCoverFlow.LayoutParams(params_img));

				// mImageLoader.addTask(list.get(i % list.size()), view);
				TopicType type = topicTypes.get(i % topicTypes.size());

				item_img.setImageResource(type.getIcon_res());
			}
		}
		return view;
	}
}
