package com.haomee.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haomee.entity.Chest;
import com.haomee.liulian.LatestLoginActivity;
import com.haomee.liulian.R;
import com.haomee.util.NetworkUtil;
import com.haomee.util.imageloader.ImageLoaderCharles;
import com.haomee.view.MyToast;

public class ChestAdapter extends BaseAdapter {

	private List<Chest> list_chest;
	private Context context;
	private LayoutInflater inflater;
	private ViewHolder viewHolder;
	private int screen_width;

	public ChestAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_width = dm.widthPixels;
	}

	public void setData(List<Chest> list_chest) {
		this.list_chest = list_chest;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return list_chest == null ? 0 : list_chest.size();
	}

	@Override
	public Object getItem(int position) {
		return list_chest.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_chest, null);
			viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_desc);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final Chest chest = list_chest.get(position);
		ViewGroup.LayoutParams params = viewHolder.iv_icon.getLayoutParams();

		params.width = screen_width / 4;
		params.height = params.width;
		viewHolder.iv_icon.setLayoutParams(params);
		viewHolder.iv_icon.setImageResource(R.drawable.item_default);
        ImageLoaderCharles.getInstance(context).addTask(list_chest.get(position).getIcon(), viewHolder.iv_icon);
		viewHolder.tv_name.setText(chest.getName());
		viewHolder.tv_time.setText("参与时间:" + chest.getTime());

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (NetworkUtil.dataConnected(context)) {
					Intent intent = new Intent();
					intent.setClass(context, LatestLoginActivity.class);
					intent.putExtra("category", chest.getId());
					intent.putExtra("category_name", chest.getName());
					context.startActivity(intent);
				} else {
					MyToast.makeText(context, context.getResources().getString(R.string.no_network), 1).show();
				}
			}
		});

		return convertView;
	}

	private class ViewHolder {

		private ImageView iv_icon;
		private TextView tv_name;
		private TextView tv_time;
	}

}
