package com.hipad.smarthome.adapter;

import java.util.List;
import java.util.Map;

import com.hipad.smarthome.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListviewAdapter extends BaseAdapter {
	private Context context; 
	private List<Map<String, Object>> listItems; 
	private LayoutInflater listContainer;

	
	public ListviewAdapter(Context context,List<Map<String, Object>> list) {
		this.context = context;
		listItems = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = listContainer.from(context).inflate(R.layout.listview_item,null); 
			
		}
		return convertView;
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	
	private  class ListItemView{
        public ImageView image;
        public TextView title;
 }    
}
