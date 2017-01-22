package com.haomee.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haomee.consts.CommonConst;
import com.haomee.entity.Users;
import com.haomee.liulian.R;
import com.haomee.util.imageloader.ImageLoaderCharles;

public class MyIdolAdapter extends BaseAdapter {

	private Context mContext;
	List<Users> list_idol;
	private ViewHolder viewHolder;
	private LayoutInflater inflater;

	public MyIdolAdapter(Context mContext) {
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
	}

	public void setData(List<Users> list_idol) {
		this.list_idol = list_idol;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_idol == null ? 0 : list_idol.size();
	}

	@Override
	public Object getItem(int position) {
		return list_idol.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_my_idols, null);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.message = (TextView) convertView.findViewById(R.id.message);
			viewHolder.avatar = (ImageView) convertView.findViewById(R.id.item_image);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final Users idol = list_idol.get(position);
		viewHolder.avatar.setImageResource(R.drawable.item_icon);
		viewHolder.avatar.setBackgroundResource(CommonConst.user_sex[idol.getSex()]);
        ImageLoaderCharles.getInstance(mContext).addTask(idol.getHead_pic_small(), viewHolder.avatar);
		viewHolder.name.setText(idol.getName());
		viewHolder.message.setText(idol.getTime());
		/*
		 * convertView.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent intent = new
		 * Intent(mContext, ChatActivity.class); intent.putExtra("userId",
		 * username); intent.putExtra("uId", to_uid);
		 * intent.putExtra("nickname", to_nickname);
		 * mContext.startActivity(intent); } });
		 */
		return convertView;
	}

	private class ViewHolder {
		TextView name, message;
		ImageView avatar;
	}
}
