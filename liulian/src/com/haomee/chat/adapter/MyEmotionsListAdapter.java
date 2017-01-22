package com.haomee.chat.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haomee.chat.task.EmotionDownloadTask;
import com.haomee.entity.EmotionItem;
import com.haomee.liulian.R;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.imageloader.ImageLoaderCharles;

public class MyEmotionsListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<String> local_package_list;//本地存在的表情包
	private List<EmotionItem> emotions_list;
	private ImageLoaderCharles imageLoader;
	private int current_posiont;
	private ListView lv_emotions;
	private String emotions_base_path;
	public MyEmotionsListAdapter(Context context,String emotions_base_path){
		this.context=context;
		this.emotions_base_path=emotions_base_path;
		mInflater = LayoutInflater.from(context);
		this.imageLoader = ImageLoaderCharles.getInstance(context);
	}

	public void setData(List<String> local_package_list,List<EmotionItem> emotions_list,ListView lv_emotions){
		this.local_package_list=local_package_list;
		this.emotions_list=emotions_list;
		this.lv_emotions=lv_emotions;
		notifyDataSetChanged();
	}
	public List<EmotionItem> getData(){
		return emotions_list;
		
	}
	@Override
	public int getCount() {
		return emotions_list == null ? 0 : emotions_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return emotions_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView( final int position, View convertView, ViewGroup parent) {
		final ViewHolderEmotion viewHolder;
		if(convertView==null){
			viewHolder=new ViewHolderEmotion();
			convertView = mInflater.inflate(R.layout.emotions_download_item, null);

			viewHolder.iv_cover=(ImageView) convertView.findViewById(R.id.iv_emotions_cover);
			viewHolder.tv_name=(TextView) convertView.findViewById(R.id.tv_emotions_name);
			viewHolder.pb_loading=(ProgressBar) convertView.findViewById(R.id.pb_emotions_progress);
			viewHolder.tv_load=(TextView) convertView.findViewById(R.id.tv_emotions_load);
			viewHolder.iv_emotions_load=(ImageView) convertView.findViewById(R.id.iv_emotions_load);
			viewHolder.is_new_emotions=(ImageView) convertView.findViewById(R.id.is_new_emotions);
			viewHolder.tv_emotions_context=(TextView) convertView.findViewById(R.id.tv_emotions_context);
			viewHolder.iv_emotions_load.setOnClickListener(new OnClickListener() {
				EmotionItem emotionItem = emotions_list.get(position);
				@Override
				public void onClick(View v) {
					if("下载".equals(viewHolder.tv_load.getText().toString())){
						current_posiont=position;
						viewHolder.iv_emotions_load.setVisibility(View.GONE);
						viewHolder.pb_loading.setVisibility(View.VISIBLE);
						viewHolder.pb_loading.incrementProgressBy(0);
						new EmotionDownloadTask(context, emotionItem.getUrl(),emotionItem.getCover(), handler_download,current_posiont).execute();
					}else if("移除".equals(viewHolder.tv_load.getText().toString())){
						String delete_packae_name=get_package_name(emotionItem.getUrl());
						FileDownloadUtil.deleteDownloadFiles(emotions_base_path+File.separator+delete_packae_name);
						viewHolder.tv_load.setText("下载");
						viewHolder.iv_emotions_load.setImageResource(R.drawable.face_button_download);
						Intent intent = new Intent("MyReceiver_Emotion_download");
						// 可通过Intent携带消息
						intent.putExtra("tag", 1);
						intent.putExtra("emotions", delete_packae_name);
						// 发送广播消息
						context.sendBroadcast(intent);
					}
				}
			});

			convertView.setTag(viewHolder);

		}else {
			viewHolder=(ViewHolderEmotion) convertView.getTag();
		}
		EmotionItem emotionItem = emotions_list.get(position);
		if(emotionItem!=null){
			viewHolder.tv_name.setText(emotionItem.getName());
			if(init_load_state(emotionItem.getUrl())){
				viewHolder.tv_load.setText("移除");
				viewHolder.iv_emotions_load.setImageResource(R.drawable.face_button_delete);
			}else {
				viewHolder.tv_load.setText("下载");
				viewHolder.iv_emotions_load.setImageResource(R.drawable.face_button_download);
			}
			if(emotionItem.isIs_new()){
				viewHolder.is_new_emotions.setVisibility(View.VISIBLE);
			}else {
				viewHolder.is_new_emotions.setVisibility(View.GONE);
			}
			viewHolder.tv_emotions_context.setText(emotionItem.getDesc());
			imageLoader.addTask(emotionItem.getIcon(), viewHolder.iv_cover);
		}
		return convertView;
	}

	private final class ViewHolderEmotion {
		private ImageView iv_cover,iv_emotions_load,is_new_emotions;
		private TextView tv_name,tv_load,tv_emotions_context;
		private ProgressBar pb_loading;
	}

	/**
	 * 
	 */
	private boolean init_load_state(String packag_name){
		if(local_package_list!=null){
			boolean contains = local_package_list.contains(get_package_name(packag_name));
			return contains;
		}
		return false;
	}

	/**
	 * 获取包名
	 */
	private String get_package_name(String package_path){
		String[] split = package_path.split("/");
		String[] split2 = split[split.length-1].split("\\.");
		return split2[0];
	}

	private Handler handler_download = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			show_progress(msg,msg.what);
		}
	};

	/**
	 * @param package_path
	 * @return
	 */
	private void show_progress(Message msg,int position){
		int progress = msg.arg1;
		ViewHolderEmotion viewHolder=(ViewHolderEmotion) lv_emotions.getChildAt(position).getTag();
		
		if (progress == 100) {
			viewHolder.iv_emotions_load.setVisibility(View.VISIBLE);
			viewHolder.pb_loading.setVisibility(View.GONE);
			viewHolder.tv_load.setText("移除");
			viewHolder.iv_emotions_load.setImageResource(R.drawable.face_button_delete);
			viewHolder.tv_load.setClickable(true);

		} else {
			viewHolder.pb_loading.incrementProgressBy(progress);
		}
	}
}
