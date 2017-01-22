package com.haomee.chat.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haomee.liulian.R;

public class NewYanWenZiAdapter extends BaseAdapter {
	private List<String> list_NewExpression;
	private Context context;
	private LayoutInflater mInflater;

	public NewYanWenZiAdapter(Activity context) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	public void setData(List<String> list_NewExpression) {
		this.list_NewExpression = list_NewExpression;
		this.notifyDataSetChanged();
	}

	public List<String> getData() {
		return this.list_NewExpression;
	}

	@Override
	public int getCount() {
		return list_NewExpression == null ? 0 : list_NewExpression.size();
	}

	public Object getItem(int position) {
		return list_NewExpression.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderAnimActions holder = null;
		if (convertView == null) {
			holder = new ViewHolderAnimActions();
			convertView = mInflater.inflate(R.layout.item_newyanwenzi_grid, null);
			holder.text = (TextView) convertView.findViewById(R.id.item_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderAnimActions) convertView.getTag();
		}
		holder.text.setText(list_NewExpression.get(position));
		return convertView;
	}

	private final class ViewHolderAnimActions {
		private TextView text;
	}
}
