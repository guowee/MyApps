package com.haomee.adapter;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.haomee.entity.Music;
import com.haomee.entity.Report;
import com.haomee.liulian.R;

public class SearchMusicAdapter extends BaseAdapter {

	private Context context;
	private List<Music> list_music;
	private LayoutInflater inflater;

	public SearchMusicAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void setData(List<Music> list_music) {
		this.list_music = list_music;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_music == null ? 0 : list_music.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list_music.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	ViewHolder holder;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_music, null);
			holder.name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_author = (TextView) convertView.findViewById(R.id.tv_author);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(list_music.get(position).getTitle());
		holder.tv_author.setText(list_music.get(position).getAuthor() + "-" + list_music.get(position).getAlbum());
		return convertView;
	}

	class ViewHolder {
		TextView name;
		TextView tv_author;
	}

}
