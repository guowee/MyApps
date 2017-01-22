package com.haomee.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.haomee.fragment.PersonalFragment;
import com.haomee.liulian.R;
import com.haomee.util.ViewUtil;
import com.haomee.util.imageloader.ImageLoaderCharles;

public class PicAdapter extends BaseAdapter implements OnClickListener{

	private ArrayList<String> pics_small,pics_big;
	private Activity context;
	private LayoutInflater inflater;

	public PicAdapter(Activity context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void setData(ArrayList<String> pics_small,ArrayList<String> pics_big) {
		this.pics_small = pics_small;
		this.pics_big = pics_big;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return pics_small == null ? 0 : (pics_small.size() - 1 ) /3  + 1;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return pics_small.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	ViewHolder viewHolder;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_person_pic, null);
			
			convertView.setSelected(false);
			convertView.setPressed(false);
			convertView.setClickable(false);
			
			viewHolder.item_image1 = (ImageView) convertView.findViewById(R.id.item_image1);
			viewHolder.item_image2 = (ImageView) convertView.findViewById(R.id.item_image2);
			viewHolder.item_image3 = (ImageView) convertView.findViewById(R.id.item_image3);
			
			ViewGroup.LayoutParams params1 = viewHolder.item_image1.getLayoutParams();
			params1.width = (ViewUtil.getScreenWidth(context) - ViewUtil.dip2px(context, 40)) / 3;
			params1.height = params1.width;
			viewHolder.item_image1.setPadding(10, 10, 10, 10);
			viewHolder.item_image1.setLayoutParams(params1);

			ViewGroup.LayoutParams params2 = viewHolder.item_image2.getLayoutParams();
			params2.width = (ViewUtil.getScreenWidth(context) - ViewUtil.dip2px(context, 40)) / 3;
			params2.height = params2.width;
			viewHolder.item_image2.setPadding(10, 10, 10, 10);
			viewHolder.item_image2.setLayoutParams(params2);

			ViewGroup.LayoutParams params3 = viewHolder.item_image3.getLayoutParams();
			params3.width = (ViewUtil.getScreenWidth(context) - ViewUtil.dip2px(context, 40)) / 3;
			params3.height = params3.width;
			viewHolder.item_image3.setPadding(10, 10, 10, 10);
			viewHolder.item_image3.setLayoutParams(params3);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			convertView.setSelected(false);
			convertView.setPressed(false);
			convertView.setClickable(false);
		}
		
		for (int i = 0; i < 3; i++) {
			String url = "";
			boolean isShow = true;
			if (position * 3 + i < pics_small.size()) {
				url = pics_small.get(position * 3 + i);
			}else{
				isShow = false;
			}
			switch (i) {
			case 0:
				if(isShow){
					viewHolder.item_image1.setVisibility(View.VISIBLE);
					viewHolder.item_image1.setTag(R.id.img_loaded, position * 3 + i);
					viewHolder.item_image1.setOnClickListener(this);
					ImageLoaderCharles.getInstance(context).addTask(url, viewHolder.item_image1);
				}else{
					viewHolder.item_image1.setVisibility(View.INVISIBLE);
				}
				
				break;
			case 1:
				if(isShow){
					viewHolder.item_image2.setVisibility(View.VISIBLE);
                    ImageLoaderCharles.getInstance(context).addTask(url, viewHolder.item_image2);
					viewHolder.item_image2.setTag(R.id.img_loaded, position * 3 + i);
					viewHolder.item_image2.setOnClickListener(this);
				}else{
					viewHolder.item_image2.setVisibility(View.INVISIBLE);
				}
				break;
			case 2:
				if(isShow){
					viewHolder.item_image3.setVisibility(View.VISIBLE);
					viewHolder.item_image3.setTag(R.id.img_loaded, position * 3 + i);
					viewHolder.item_image3.setOnClickListener(this);
                    ImageLoaderCharles.getInstance(context).addTask(url, viewHolder.item_image3);
				}else{
					viewHolder.item_image3.setVisibility(View.INVISIBLE);
				}
				break;
			default:
				break;
			}
		}

		return convertView;
	}
	
	
	@Override
	public void onClick(View v) {
		
		
		int index = (Integer) v.getTag(R.id.img_loaded);
		
		if(index == 0){
		
			PersonalFragment.getPersonalFragment1().showImage(context);
		
			}else{
		
			PersonalFragment.getPersonalFragment1().showDel(context, pics_big,index);
		}
		
		
	}
	

	private class ViewHolder {
		private ImageView item_image1;
		private ImageView item_image2;
		private ImageView item_image3;
	}
	
	

}
