package com.haomee.chat.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.haomee.liulian.R;

public class NewExpressAdapter2 extends BaseAdapter {
	private List<String> emotios_path;
	private Context context;
	private LayoutInflater mInflater;
	private List<String> emotions_name;
	private int screen_width, backup_packageId, type_size;
	
	public NewExpressAdapter2(Activity context){
		this.context = context;
		mInflater = LayoutInflater.from(context);
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_width = dm.widthPixels;
	}
	
	public void setData(List<String> emotios_path,List<String> emotions_name,int type_size) {
		this.emotios_path = emotios_path;
		this.emotions_name=emotions_name;
		this.type_size=type_size;
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return emotios_path == null ? 0 : emotios_path.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return emotios_path.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderAnimActions holder = null;
		if (convertView == null) {
			holder = new ViewHolderAnimActions();
			convertView = mInflater.inflate(R.layout.item_newexpression_grid, null);
			holder.image = (ImageView) convertView.findViewById(R.id.item_image);
			holder.text = (TextView) convertView.findViewById(R.id.item_text);
			LayoutParams params = (LayoutParams) holder.image.getLayoutParams();
			if (type_size == 2) {//加载小表情
				params.width = screen_width / 12;
				params.height = screen_width / 12;
			} else {
				params.width = screen_width / 6;
				params.height = screen_width / 6;

			}
			holder.image.setLayoutParams(params);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderAnimActions) convertView.getTag();
		}
		holder.image.setImageBitmap(BitmapFactory.decodeFile(emotios_path.get(position)));
		if(type_size == 2){//下表情不显示文字
			holder.text.setVisibility(View.GONE);
		}else {
			holder.text.setVisibility(View.VISIBLE);
			holder.text.setText(emotions_name.get(position));
		}
		return convertView;
	}
	private final class ViewHolderAnimActions {
		private ImageView image;
		private TextView text;
	}
}

