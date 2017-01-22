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

import com.haomee.entity.Report;
import com.haomee.liulian.R;

public class ReportAdapter extends BaseAdapter{
	
	private Context context;
	private List<Report> list_report;
	private Vector<Boolean> mImage_bs = new Vector<Boolean>();
	private Vector<Integer> mImageIds = new Vector<Integer>();
	private int lastPosition = -1;  
	private LayoutInflater inflater;
	
	public ReportAdapter(Context context,List<Report> list_report){
		this.context =  context;
		this.list_report =  list_report;
		 for (int i = 0; i < list_report.size(); i++) {
				mImageIds.add(Integer.parseInt(list_report.get(i).getId()));
				mImage_bs.add(false);
		 }
		 inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_report == null ? 0 : list_report.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list_report.get(position);
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
			convertView = inflater.inflate(R.layout.item_report, null);
			holder.name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.image = (ImageView) convertView.findViewById(R.id.tv_images);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		setViewBg(mImageIds.elementAt(position),mImage_bs.elementAt(position));
		holder.name.setText(list_report.get(position).getName());
		return convertView;
	}
	
	public void setViewBg(int position, boolean isChosen){
		if(isChosen){
			
			
			holder.image.setImageResource(R.drawable.report_button_pressed);
			
//			holder.image.setBackgroundResource(R.drawable.report_button_pressed);
			
		}else{
			holder.image.setImageResource(R.drawable.report_button_default);
//			holder.image.setBackgroundResource(R.drawable.report_button_default);
		}
	}
	
	
	 class ViewHolder {
		 TextView name;
		 ImageView image;
	}
	
	//修改选中的状态
	public void changeState(int position){
	
		if (lastPosition != -1){
					mImage_bs.setElementAt(false, lastPosition);
					list_report.get(position).setIs_select(false);
			}
			list_report.get(position).setIs_select(false);
			// 直接取反即可
			mImage_bs.setElementAt(!mImage_bs.elementAt(position), position);
			lastPosition = position; 
			notifyDataSetChanged(); 
	}
	

}
