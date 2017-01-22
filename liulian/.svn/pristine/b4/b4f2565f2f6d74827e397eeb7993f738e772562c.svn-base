package com.haomee.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.haomee.consts.CommonConst;
import com.haomee.entity.Users;
import com.haomee.liulian.R;
import com.haomee.util.imageloader.ImageLoaderCharles;

public class LookUserAdapter extends BaseAdapter {

	private List<Users> list_users;
	private Context context;
	private LayoutInflater inflater;
	private ViewHolder viewHolder;

	public LookUserAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void setData(List<Users> list_users) {
		this.list_users = list_users;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_users == null ? 0 : list_users.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list_users.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_user, null);
			viewHolder.user_img = (ImageView) convertView.findViewById(R.id.user_img);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.user_img.setImageResource(R.drawable.item_icon);
		viewHolder.user_img.setBackgroundResource(CommonConst.user_sex[list_users.get(position).getSex()]);
        ImageLoaderCharles.getInstance(context).addTask(list_users.get(position).getImage(), viewHolder.user_img);
		return convertView;
	}

	private class ViewHolder {

		private ImageView user_img;
	}
}
