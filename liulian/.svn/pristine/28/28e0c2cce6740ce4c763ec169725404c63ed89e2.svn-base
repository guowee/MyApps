package com.haomee.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haomee.chat.task.LevelTask;
import com.haomee.consts.CommonConst;
import com.haomee.entity.Users;
import com.haomee.liulian.R;
import com.haomee.liulian.UserInfoDetail;
import com.haomee.util.imageloader.ImageLoaderCharles;

public class MyIdolAdapters extends BaseAdapter {

	private Context context;

	LayoutInflater inflater;

	private List<Users> list_idols;

	private ViewHolder viewHolder;
	private PullToRefreshListView listview_idols;
	public MyIdolAdapters(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void setData(List<Users> list_idols,PullToRefreshListView listview_idols) {
		this.list_idols = list_idols;
		this.listview_idols=listview_idols;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_idols == null ? 0 : list_idols.size();
	}

	@Override
	public Object getItem(int position) {
		return list_idols.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Users idol = list_idols.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_my_idols, null);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.message = (TextView) convertView.findViewById(R.id.message);
			viewHolder.avatar = (ImageView) convertView.findViewById(R.id.item_image);
			viewHolder.is_on_line=(TextView) convertView.findViewById(R.id.is_on_line);
			viewHolder.user_levle_icon=(ImageView) convertView.findViewById(R.id.user_level_icon);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		
		viewHolder.avatar.setImageResource(R.drawable.item_icon);
		viewHolder.avatar.setBackgroundResource(CommonConst.user_sex[idol.getSex()]);
        ImageLoaderCharles.getInstance(context).addTask(idol.getHead_pic_small(), viewHolder.avatar);
        ImageLoaderCharles.getInstance(context).addTask(idol.getUser_level_icon(), viewHolder.user_levle_icon);
		if(idol.isIs_online()){//在线
			viewHolder.is_on_line.setText("在线");
		}else {
			viewHolder.is_on_line.setText("离线");
		}
		
		viewHolder.name.setSpannableFactory(null);
		viewHolder.name.setText(idol.getName());

//		new LevelTask(idol.getUser_level_icon(),  viewHolder.name,context).execute();
		viewHolder.message.setText(idol.getTime() + "刷新兴趣");

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(context, UserInfoDetail.class);
				intent.putExtra("uid", idol.getUid());
				context.startActivity(intent);
				StatService.onEvent(context, "count_of_into_ta_from_care", "消息页从关注进入他人页面", 1);
			}
		});
		return convertView;
	}

	private class ViewHolder {
		TextView name, message,is_on_line;
		ImageView avatar,user_levle_icon;
	}

}
