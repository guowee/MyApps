package com.haomee.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haomee.entity.Movie;
import com.haomee.liulian.R;

public class SearchMovieAdapter extends BaseAdapter{
	
	private Context context;
	private List<Movie> list_movie;
	private LayoutInflater inflater;
	
	public SearchMovieAdapter(Context context){
		this.context =  context;
		 inflater = LayoutInflater.from(context);
	}

	
	public void setData(List<Movie> list_movie){
		this.list_movie =  list_movie;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return list_movie == null ? 0 : list_movie.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list_movie.get(position);
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
		holder.name.setText(list_movie.get(position).getCname());
		holder.tv_author.setText(list_movie.get(position).getEnam());
		return convertView;
	}
	
	 class ViewHolder {
		 TextView name;
		 TextView tv_author;
	}
	

}
