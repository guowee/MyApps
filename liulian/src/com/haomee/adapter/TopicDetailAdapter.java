package com.haomee.adapter;

import java.util.List;

import com.haomee.util.imageloader.ImageLoaderCharles;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.haomee.chat.activity.ChatActivity;
import com.haomee.consts.PathConst;
import com.haomee.entity.Users;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.R;
import com.haomee.liulian.TopicDetailActivity;
import com.haomee.player.MySoundPlayer;
import com.haomee.util.NetworkUtil;
import com.haomee.util.ViewUtil;
import com.haomee.view.MyToast;

public class TopicDetailAdapter extends BaseAdapter {

	private List<Users> list_users;
	private Users current_user;
	private Context context;
	private LayoutInflater inflater;
	private ViewHolder viewHolder;
	private int screen_width;
	private boolean is_first = true;
	private int item_width;

	private String topic_id, topic_name;

	private boolean has_data = false;

	private boolean if_shake = false;

	public TopicDetailAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_width = dm.widthPixels;
		item_width = (screen_width - ViewUtil.dip2px(context, 24)) / 2;
	}

	public void setData(List<Users> list_users, String topic_id, String topic_name) {
		if_shake = true;
		has_data = true;
		this.topic_id = topic_id;
		this.topic_name = topic_name;
		this.list_users = list_users;
		notifyDataSetChanged();
	}

	public void setData(List<Users> list_users) {
		if_shake = false;
		has_data = false;
		this.list_users = list_users;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_users == null ? 0 : list_users.size();
	}

	@Override
	public Object getItem(int position) {
		return list_users.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_latest_login, null);
			viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
			viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.tv_age = (TextView) convertView.findViewById(R.id.tv_age);
			viewHolder.iv_online = (ImageView) convertView.findViewById(R.id.iv_online);
			viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			viewHolder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
			viewHolder.bt_chat = (ImageView) convertView.findViewById(R.id.bt_chat);
			convertView.setTag(viewHolder);
			viewHolder.bt_chat.setOnClickListener(chatListener);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Users user = list_users.get(position);
		ViewGroup.LayoutParams params = viewHolder.iv_image.getLayoutParams();
		params.width = item_width;
		params.height = item_width;
		viewHolder.iv_image.setLayoutParams(params);
		viewHolder.iv_image.setImageResource(R.drawable.item_default);
		Users users = list_users.get(position);
		if(users!=null){
			String image=users.getImage();
            ImageLoaderCharles.getInstance(context).addTask(image, viewHolder.iv_image);
		}
//		imageLoader.addTask(list_users.get(position).getImage(), viewHolder.iv_image);
		if (is_first && if_shake) {
			is_first = false;
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					shakeAnim(parent.getChildAt(0));
				}
			}, 500);

		}

		Log.i("test", "getView " + position);

		viewHolder.tv_name.setText(user.getName());
		viewHolder.tv_age.setText(user.getAge());
		viewHolder.tv_time.setText(user.getTime());
		viewHolder.tv_distance.setText(user.getDistance_str());
		if (user.isIs_online()) {
			viewHolder.iv_online.setBackgroundResource(R.drawable.image_icon_online);
		} else {
			viewHolder.iv_online.setBackgroundResource(R.drawable.image_icon_offline);
		}

		viewHolder.bt_chat.setTag(user);
		if(user.getUid().equals(LiuLianApplication.current_user.getUid())){
			
			current_user = user;
			
			if(context instanceof TopicDetailActivity){
				viewHolder.bt_chat.setImageResource(R.drawable.topic_detail_delete);
			}else{
				viewHolder.bt_chat.setImageResource(R.drawable.topic_detail_chat_disabled);
			}
		}else{
			viewHolder.bt_chat.setImageResource(R.drawable.topic_detail_chat);
		}
		

		return convertView;
	}

	private class ViewHolder {

		private ImageView iv_image;
		private TextView tv_name;
		private TextView tv_age;
		private ImageView iv_online;
		private TextView tv_time;
		private TextView tv_distance;
		private ImageView bt_chat;
	}

	public void shakeAnim(View view) {
		// Animation anim = new TranslateAnimation(0, 10, 0, 10);
		Animation anim = new RotateAnimation(0, 1.2f, item_width / 2, item_width / 2);
		// Animation anim = new TranslateAnimation(0, 10, 0, 10);
		view.clearAnimation();
		anim.setInterpolator(new CycleInterpolator(10));
		anim.setDuration(1500);
		view.startAnimation(anim);

		MySoundPlayer.getInstance(context).play_background(R.raw.sound_shake, false);

		Log.i("test", "shakeAnim");
	}

	public void refreshAndShake() {

		if (LiuLianApplication.current_user != null) {

			boolean existed=false;
			for (int i = 0; i < list_users.size(); i++) {
				Users user = list_users.get(i);

				// 将当前用户放到第一的位置
				if (user.getUid().equals(LiuLianApplication.current_user.getUid())) {
					Users temp = list_users.get(0);
					list_users.set(0, user);
					list_users.set(i, temp);

					existed=true;
					is_first = true;
					notifyDataSetChanged();

					break;
				}
				
			}
			
			// 没有找到，之前被删掉，加回去
			if(!existed && current_user!=null){
				is_first = true;
				list_users.add(0, current_user);
				notifyDataSetChanged();
			}
		}
	}

	private OnClickListener chatListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Users user = (Users) v.getTag();

			if (user.getUid().equals(LiuLianApplication.current_user.getUid())) {
				if(context instanceof TopicDetailActivity){
					interest_delete(LiuLianApplication.current_user.getUid());
				}else{
					MyToast.makeText(context, "不能和自己聊天哦", 1).show();
				}
				
			} else {
				
				if (has_data) {
					Intent intent = new Intent();
					intent.setClass(context, ChatActivity.class);
					intent.putExtra("uId", user.getUid());// 聊天对象的uid
					intent.putExtra("userId", user.getHx_username());// 聊天对象的环信ID
					intent.putExtra("nickname", user.getName());// 聊天对象的昵称
					intent.putExtra("attachmentThemeTitle", topic_name);
					intent.putExtra("attachmentId", topic_id);
					intent.putExtra("is_from_content", true);
					context.startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(context, ChatActivity.class);
					intent.putExtra("uId", user.getUid());// 聊天对象的uid
					intent.putExtra("userId", user.getHx_username());// 聊天对象的环信ID
					intent.putExtra("nickname", user.getName());// 聊天对象的昵称
					context.startActivity(intent);
				}
				StatService.onEvent(context, "count_of_chat_topic_detail", "话题页聊天次数", 1);
			}

		}
	};
	
	
	

	private void interest_delete(final String uid){
		new AsyncTask<Object, Object, Boolean>() {

			@Override
			protected Boolean doInBackground(Object... params) {
				String url = PathConst.URL_INTEREST_DELETE +"&Luid="+uid
						+"&id="+ topic_id
						+"&is_refresh=1&accesskey="+LiuLianApplication.current_user.getAccesskey();
				try {
					JSONObject json = NetworkUtil.getJsonObject(url, null, 5000);
					return "1".equals(json.getString("flag"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				return false;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					
					for(int i=0;i<list_users.size();i++){
						Users user = list_users.get(i);
						if(user.getUid().equals(uid)){
							list_users.remove(i);
							TopicDetailAdapter.this.notifyDataSetChanged();
							break;
						}
					}
					
					if(context instanceof TopicDetailActivity){
						TopicDetailActivity activity = (TopicDetailActivity) context;
						activity.interest_delete();
					}
					
				}else{
					MyToast.makeText(context, "操作失败。", Toast.LENGTH_SHORT).show();
				}
			}

		}.execute();
	}
	

}
