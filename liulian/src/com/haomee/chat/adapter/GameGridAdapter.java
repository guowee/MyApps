package com.haomee.chat.adapter;

import java.util.List;

import com.haomee.liulian.R;
import com.haomee.util.imageloader.ImageLoaderCharles;
import com.haomee.view.CircleImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class GameGridAdapter extends BaseAdapter {
	private Context context;
	private List<String> img_list;
	private LayoutInflater mInflater;
	private int tag = 0;// 0的时候第一个表情不在背景

	public GameGridAdapter(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	public void setData(List<String> img_list, int tag) {
		this.img_list = img_list;
		this.tag = tag;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return img_list == null ? 0 : img_list.size();
	}

	public List<String> getData() {
		return img_list;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return img_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHoler;
		final String img_url=img_list.get(position);
		if (convertView == null) {
			viewHoler = new ViewHolder();
			convertView = mInflater.inflate(R.layout.game2_img_time, null);
			viewHoler.user_img = (CircleImageView) convertView.findViewById(R.id.img_user);
//			viewHoler.user_img.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					if(LiuLianApplication.current_user.getImage().equals(img_url)){
//						viewHoler.user_img.setBackgroundResource(R.drawable.game_background_find_02);
//					}
//					
//				}
//			});
			convertView.setTag(viewHoler);
		} else {
			viewHoler = (ViewHolder) convertView.getTag();
		}
		if (tag != 0 && position == 0) {
			viewHoler.user_img.setBackgroundResource(R.drawable.game_background_find_02);
		}
        ImageLoaderCharles.getInstance(context).addTask(img_list.get(position), viewHoler.user_img);
//		mImageLoader.addTask(img_url, viewHoler.user_img);
		return convertView;
	}

	class ViewHolder {
		CircleImageView user_img;
	}
}
