package com.haomee.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.haomee.entity.TopicType;
import com.haomee.liulian.R;

public class TopicTypeAdapter extends BaseAdapter {

	private List<TopicType> list_types;
	private Context context;
	private LayoutInflater inflater;

	public TopicTypeAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void setData(List<TopicType> list_types) {
		this.list_types = list_types;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return list_types == null ? 0 : list_types.size();
	}

	@Override
	public Object getItem(int position) {
		return list_types.get(position);
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
			convertView = inflater.inflate(R.layout.item_topic_type_home, null);
			viewHolder.item_img = (ImageView) convertView.findViewById(R.id.item_img);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		TopicType type = list_types.get(position);
		viewHolder.item_img.setImageResource(type.getIcon_res());

		return convertView;
	}

	private class ViewHolder {
		private ImageView item_img;
	}

}
