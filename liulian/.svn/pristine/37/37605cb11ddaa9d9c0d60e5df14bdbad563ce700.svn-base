package com.haomee.adapter;

import java.util.List;

import com.haomee.entity.TopicType;
import com.haomee.entity.Users;
import com.haomee.liulian.R;
import com.haomee.util.ViewUtil;
import com.haomee.util.imageloader.ImageLoaderCharles;
import com.haomee.view.FancyCoverFlow;
import com.haomee.view.GalleryFlow;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class FilterAdaper extends BaseAdapter {
	private Context mContext;
	private List<Users>list;
	private LayoutInflater mInflater;
	private int mSize;
	private int screen_width,screen_height;
	
	private int item_width;

	public FilterAdaper(Context mContext,List<Users>list) {
		this.mContext = mContext;
		this.list=list;
		this.mSize=list.size();
		mInflater=LayoutInflater.from(mContext);
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_width = dm.widthPixels;
		screen_height=dm.heightPixels;
		item_width = ViewUtil.dip2px(mContext, 80);

	}

	@Override
	public int getCount() {
		if(mSize<GalleryFlow.MIN_CYCLE_NUMS){
			return mSize;
		}
		
		 return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		if(mSize==0){
			return  null;
		}
		position=position%mSize;
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_filtergallery, null);
//			viewHolder.ll_item_filter_root=(LinearLayout) convertView.findViewById(R.id.ll_item_filter_root);
			
			viewHolder.iv_item_filter_bg = (ImageView) convertView.findViewById(R.id.iv_item_filter_bg);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		FrameLayout.LayoutParams llPram=(LayoutParams) viewHolder.ll_item_filter_root.getLayoutParams();
		
		llPram.width=screen_width;
		llPram.height=(int)(screen_height*1f/5*4);
		ViewGroup.LayoutParams params_img = viewHolder.iv_item_filter_bg.getLayoutParams();
		params_img.width = item_width;
		params_img.height = params_img.width;
		convertView.setLayoutParams(params_img);
		convertView.setLayoutParams(new FancyCoverFlow.LayoutParams(params_img));
		Users users  = list.get(position);
        ImageLoaderCharles.getInstance(mContext).addTask(users.getBack_pic(), viewHolder.iv_item_filter_bg);
		
		return convertView;
	}

	class ViewHolder {
		private ImageView iv_item_filter_bg;
		private LinearLayout ll_item_filter_root;
		
		//		private ImageView iv_item_
	}

}
