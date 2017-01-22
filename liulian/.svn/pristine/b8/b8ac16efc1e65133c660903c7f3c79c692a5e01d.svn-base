package com.haomee.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haomee.entity.Topic;
import com.haomee.liulian.R;
import com.haomee.util.imageloader.ImageLoaderCharles;

public class TopicAdapter extends BaseAdapter {

	private List<Topic> list_topic;
	private Context context;
	private LayoutInflater inflater;
	private ViewHolder viewHolder;
	private boolean is_my_send_topic = false;

	public TopicAdapter(Context context, boolean is_my_send_topic) {
		this.context = context;
		this.is_my_send_topic = is_my_send_topic;
		inflater = LayoutInflater.from(context);
	}

	public void setData(List<Topic> list_topic) {
		this.list_topic = list_topic;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return list_topic == null ? 0 : list_topic.size();
	}

	@Override
	public Object getItem(int position) {
		return list_topic.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			if (is_my_send_topic) {
				convertView = inflater.inflate(R.layout.item_my_topic, null);
			} else {
				convertView = inflater.inflate(R.layout.item_new_topic, null);
			}
			viewHolder.topic_pic = (ImageView) convertView.findViewById(R.id.item_image);
			viewHolder.topic_name = (TextView) convertView.findViewById(R.id.item_name);
			viewHolder.topic_desc = (TextView) convertView.findViewById(R.id.item_desc);
			viewHolder.topic_count = (TextView) convertView.findViewById(R.id.item_count);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Topic topic = list_topic.get(position);
		viewHolder.topic_pic.setImageResource(R.drawable.item_icon);

		ImageLoaderCharles.getInstance(context).addTask(list_topic.get(position).getIcon(), viewHolder.topic_pic);
		viewHolder.topic_name.setText(topic.getTitle());
		viewHolder.topic_desc.setText("已有" + topic.getUser_num() + "个LIer聊过它");
		viewHolder.topic_count.setText(topic.getContent_num());
		return convertView;
	}

	private class ViewHolder {

		private ImageView topic_pic;
		private TextView topic_name;
		private TextView topic_desc;
		private TextView topic_count;
	}

}
