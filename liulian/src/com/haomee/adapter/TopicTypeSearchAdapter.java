package com.haomee.adapter;

import java.util.List;

import com.haomee.consts.CommonConst;
import com.haomee.entity.TopicType_Search_User;
import com.haomee.liulian.R;
import com.haomee.util.imageloader.ImageLoaderCharles;
import com.haomee.view.CircleImageView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TopicTypeSearchAdapter extends BaseAdapter {

	private List<TopicType_Search_User> search_user_list;
	private Activity context;
	private LayoutInflater inflater;
	private ViewHolder viewHolder;

	public TopicTypeSearchAdapter(Activity context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void setData(List<TopicType_Search_User> search_user_list) {
		this.search_user_list = search_user_list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return search_user_list == null ? 0 : search_user_list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return search_user_list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.topic_search_user_item, null);
			viewHolder.iv_image = (CircleImageView) convertView.findViewById(R.id.item_image);
			viewHolder.tv_title = (TextView) convertView.findViewById(R.id.name);
			viewHolder.tv_message = (TextView) convertView.findViewById(R.id.message);
			viewHolder.iv_line = (ImageView) convertView.findViewById(R.id.message_from);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		TopicType_Search_User info = search_user_list.get(position);
		viewHolder.tv_title.setText(info.getUsername());
		viewHolder.tv_message.setText(info.getTime());
		if (info.isIs_online()) {
			viewHolder.iv_line.setBackgroundResource(R.drawable.image_icon_online);
		}
        ImageLoaderCharles.getInstance(context).addTask(info.getHead_pic(), viewHolder.iv_image);
		viewHolder.iv_image.setBackgroundResource(CommonConst.user_sex[Integer.parseInt(info.getSex())]);
		return convertView;
	}

	private class ViewHolder {
		private CircleImageView iv_image;
		private TextView tv_title, tv_message;
		private ImageView iv_line;
	}
}
