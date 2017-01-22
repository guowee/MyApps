package com.haomee.adapter;

import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haomee.consts.CommonConst;
import com.haomee.entity.Topic;
import com.haomee.liulian.R;
import com.haomee.util.ViewUtil;

public class InterestAdapter extends BaseAdapter {

	private List<Topic> list_topic;
	private Activity context;
	private LayoutInflater inflater;
	private ViewHolder viewHolder;
	private int screen_width;
	private Random rdm;

	public InterestAdapter(Activity context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		screen_width = ViewUtil.getScreenWidth(context);
		rdm = new Random();
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
			convertView = inflater.inflate(R.layout.item_interest, null);
			viewHolder.tv_left_time = (TextView) convertView.findViewById(R.id.tv_left_time);
			viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			viewHolder.tv_view_num = (TextView) convertView.findViewById(R.id.tv_view_num);
			viewHolder.ll_person_bg = (RelativeLayout) convertView.findViewById(R.id.item_img);
			viewHolder.tip_mine = convertView.findViewById(R.id.tip_mine);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Topic topic = list_topic.get(position);
		
		viewHolder.tip_mine.setVisibility(topic.isMy()?View.VISIBLE:View.GONE);
		viewHolder.tv_left_time.setText(topic.getLeft_time() + "	消失");
		viewHolder.tv_title.setText(topic.getTitle());
		viewHolder.tv_view_num.setText("已+" + topic.getView_user_num() + "人");
		viewHolder.ll_person_bg.setBackgroundResource(CommonConst.bg_round_colors[rdm.nextInt(CommonConst.bg_round_colors.length)]);
		return convertView;
	}

	private class ViewHolder {

		private TextView tv_left_time;
		private TextView tv_title;
		private TextView tv_view_num;
		private RelativeLayout ll_person_bg;
		private View tip_mine;
	}

}
