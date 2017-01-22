package com.haomee.adapter;

import java.util.ArrayList;
import java.util.List;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.haomee.util.imageloader.ImageLoaderCharles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mobstat.StatService;
import com.haomee.chat.activity.ChatActivity;
import com.haomee.chat.domain.User;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.TextItem;
import com.haomee.entity.UserTextList;
import com.haomee.entity.Users;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.R;
import com.haomee.liulian.ReportActivity1;
import com.haomee.liulian.TestActivity;
import com.haomee.liulian.TopicDetailActivity;
import com.haomee.liulian.TopicsDetailActivity;
import com.haomee.player.MySoundPlayer;
import com.haomee.util.NetworkUtil;
import com.haomee.util.ViewUtil;
import com.haomee.view.CircleImageView;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TopicsDetailAdapter extends BaseAdapter {

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

	private AlertDialog dialog;
	private LoadingDialog loadingDialog;

	public TopicsDetailAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_width = dm.widthPixels;
		item_width = (screen_width - ViewUtil.dip2px(context, 24)) / 3;
		loadingDialog = new LoadingDialog(context);
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
			convertView = inflater.inflate(R.layout.item_topicsdetail, null);
			viewHolder.iv_item_topicsdetail_icon = (CircleImageView) convertView.findViewById(R.id.iv_item_topicsdetail_icon);
			viewHolder.tv_item_topicsdetail_name = (TextView) convertView.findViewById(R.id.tv_item_topicsdetail_name);
			viewHolder.tv_item_topicsdetail_age = (TextView) convertView.findViewById(R.id.tv_item_topicsdetail_age);
			viewHolder.tv_item_topicsdetail_time = (TextView) convertView.findViewById(R.id.tv_item_topicsdetail_time);
			viewHolder.tv_item_topicsdetail_address = (TextView) convertView.findViewById(R.id.tv_item_topicsdetail_address);
			viewHolder.iv_item_topicsdetail_chat = (ImageView) convertView.findViewById(R.id.iv_item_topicsdetail_chat);
			viewHolder.iv_item_topicsdetail_delete = (ImageView) convertView.findViewById(R.id.iv_item_topicsdetail_delete);
			viewHolder.tv_item_topicsdetail_report = (TextView) convertView.findViewById(R.id.tv_item_topicsdetail_report);
			viewHolder.tv_item_topcisdetail_description = (TextView) convertView.findViewById(R.id.tv_item_topicsdetail_description);

			viewHolder.ll_item_topicsdetail_back = (LinearLayout) convertView.findViewById(R.id.ll_item_topicsdetail_back);
			viewHolder.user_level_icon = (ImageView) convertView.findViewById(R.id.user_level_icon);

			convertView.setTag(viewHolder);
			viewHolder.iv_item_topicsdetail_chat.setOnClickListener(chatListener);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// Users user=list_users.get(position);
		// 动态设置头像的宽高

		// ViewGroup.LayoutParams params =
		// viewHolder.iv_image.getLayoutParams();
		// params.width = item_width;
		// params.height = item_width;
		// viewHolder.iv_image.setLayoutParams(params);
		// viewHolder.iv_image.setImageResource(R.drawable.item_default);

		user = list_users.get(position);
		if (user != null) {
			String image = user.getImage();
			viewHolder.iv_item_topicsdetail_icon.setImageResource(R.drawable.item_icon);
			ImageLoaderCharles.getInstance(context).addTask(image, viewHolder.iv_item_topicsdetail_icon);

			ImageLoaderCharles.getInstance(context).addTask(user.getUser_level_icon(), viewHolder.user_level_icon);
		}
		if (is_first && if_shake) {
			is_first = false;
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					shakeAnim(parent.getChildAt(0));
				}
			}, 500);
		}

		viewHolder.tv_item_topicsdetail_name.setText(user.getName());
		viewHolder.tv_item_topicsdetail_age.setText(user.getAge());
		viewHolder.tv_item_topicsdetail_time.setText(user.getTime());
		viewHolder.tv_item_topicsdetail_address.setText("(" + user.getCity() + ")");
		viewHolder.iv_item_topicsdetail_chat.setTag(user);
		viewHolder.iv_item_topicsdetail_icon.setBackgroundResource(CommonConst.user_sex[user.getSex()]);
		viewHolder.tv_item_topcisdetail_description.setText(user.getSpeekContent());

		if (user.getUid().equals(LiuLianApplication.current_user.getUid())) {
			current_user = user;
			if (context instanceof TopicsDetailActivity) {
				// 是自己
				viewHolder.ll_item_topicsdetail_back.setBackgroundColor(Color.parseColor("#fffeec"));
				viewHolder.iv_item_topicsdetail_delete.setVisibility(View.VISIBLE);
				viewHolder.tv_item_topicsdetail_report.setVisibility(View.GONE);
				viewHolder.iv_item_topicsdetail_chat.setVisibility(View.GONE);
			} else {
				viewHolder.ll_item_topicsdetail_back.setBackgroundColor(Color.parseColor("#ffffff"));
				viewHolder.iv_item_topicsdetail_delete.setVisibility(View.GONE);
				// viewHolder.tv_item_topicsdetail_report.setVisibility(View.VISIBLE);
				// 暂时全部都隐藏
				viewHolder.tv_item_topicsdetail_report.setVisibility(View.GONE);
				// viewHolder.iv_item_topicsdetail_chat.setImageResource(R.drawable.topic_detail_chat);
				viewHolder.iv_item_topicsdetail_chat.setVisibility(View.VISIBLE);

			}
		} else {

			if (user.isIs_can_talk()) {
				viewHolder.iv_item_topicsdetail_chat.setImageResource(R.drawable.topic_detail_chat);
			} else {
				// viewHolder.iv_item_topicsdetail_chat.setImageResource(R.drawable.topic_chat_lock);

				// MyToast.makeText(context, "等级不够呀(>.<)可以去TA主页解锁",
				// Toast.LENGTH_SHORT).show();
			}

			viewHolder.ll_item_topicsdetail_back.setBackgroundColor(Color.parseColor("#ffffff"));
			viewHolder.iv_item_topicsdetail_delete.setVisibility(View.GONE);

			viewHolder.tv_item_topicsdetail_report.setVisibility(View.GONE);
			// viewHolder.tv_item_topicsdetail_report.setVisibility(View.VISIBLE);
			// viewHolder.iv_item_topicsdetail_chat.setImageResource(R.drawable.topic_detail_chat);
			viewHolder.iv_item_topicsdetail_chat.setVisibility(View.VISIBLE);

		}

		viewHolder.tv_item_topicsdetail_report.setOnClickListener(reportListener);
		viewHolder.iv_item_topicsdetail_delete.setOnClickListener(deleteListener);

		return convertView;
	}

	public void setData(List<Users> list_users) {
		if_shake = false;
		has_data = false;
		this.list_users = list_users;
		notifyDataSetChanged();
	}

	public void setData(List<Users> list_users, String topic_id, String topic_name) {
		if_shake = true;
		has_data = true;
		this.topic_id = topic_id;
		this.topic_name = topic_name;
		this.list_users = list_users;
		notifyDataSetChanged();
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

			boolean existed = false;
			for (int i = 0; i < list_users.size(); i++) {
				Users user = list_users.get(i);

				// 将当前用户放到第一的位置
				if (user.getUid().equals(LiuLianApplication.current_user.getUid())) {
					Users temp = list_users.get(0);
					list_users.set(0, user);
					list_users.set(i, temp);

					existed = true;
					is_first = true;
					notifyDataSetChanged();

					break;
				}

			}

			// 没有找到，之前被删掉，加回去
			if (!existed && current_user != null) {
				is_first = true;
				list_users.add(0, current_user);
				notifyDataSetChanged();
			}
		}
	}

	private class ViewHolder {

		private CircleImageView iv_item_topicsdetail_icon;
		private TextView tv_item_topicsdetail_name;
		private TextView tv_item_topicsdetail_age;
		private TextView tv_item_topicsdetail_address;
		private TextView tv_item_topcisdetail_description;
		private TextView tv_item_topicsdetail_time;
		private ImageView iv_item_topicsdetail_chat;
		private TextView tv_item_topicsdetail_report;
		private LinearLayout ll_item_topicsdetail_back;
		private ImageView iv_item_topicsdetail_delete;
		private ImageView user_level_icon;
	}

	private OnClickListener reportListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (user == null) {
				return;
			}
			Intent intent = new Intent();
			intent.setClass(context, ReportActivity1.class);
			intent.putExtra("flag", 1);
			intent.putExtra("mid", topic_id);
			intent.putExtra("uid", LiuLianApplication.current_user.getUid());
			context.startActivity(intent);
		}
	};

	/**
	 * 匹配测试
	 */
	private void show_dialog(final Users users) {
		final Intent intent = new Intent();
		View view = inflater.inflate(R.layout.dialog_user_chat, null);

		CircleImageView imag_icon;
		TextView user_level;
		TextView percent_1, percent_2, percent_3;
		TextView message_notice;
		LinearLayout ll_start_talk;
		TextView can_not_talk;
		final TextView talk;

		imag_icon = (CircleImageView) view.findViewById(R.id.img_icon);
		user_level = (TextView) view.findViewById(R.id.user_level);
		percent_1 = (TextView) view.findViewById(R.id.percent_1);
		percent_2 = (TextView) view.findViewById(R.id.percent_2);
		percent_3 = (TextView) view.findViewById(R.id.percent_3);
		message_notice = (TextView) view.findViewById(R.id.message_notice);
		ll_start_talk = (LinearLayout) view.findViewById(R.id.ll_start_talk);
		can_not_talk = (TextView) view.findViewById(R.id.can_not_talk);
		talk = (TextView) view.findViewById(R.id.talk);

		ll_start_talk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String talk_content = talk.getText().toString().trim();
				if ("开始".equals(talk_content)) {// 测试
					get_user_question(users);
				}
			}
		});

		if (user == null) {
			return;
		}

		ImageLoaderCharles.getInstance(context).addTask(users.getImage(), imag_icon);
		user_level.setText("Level  " + users.getUser_level());

		percent_1.setText("你的等级低于该用户");
		percent_2.setVisibility(View.GONE);
		percent_3.setVisibility(View.GONE);
		message_notice.setText("如需解锁聊天请进行匹配测试");
		ll_start_talk.setVisibility(View.VISIBLE);
		talk.setText("开始");
		can_not_talk.setVisibility(View.GONE);

		dialog = new AlertDialog.Builder(context).show();
		dialog.setContentView(view);// 对比
		view.findViewById(R.id.bt_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

	}

	/**
	 * 获取测试题
	 */
	private void get_user_question(final Users users) {
		loadingDialog.show();
		if (!NetworkUtil.dataConnected(context)) {
			MyToast.makeText(context, context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			loadingDialog.dismiss();
			return;
		}
		if (user == null) {
			loadingDialog.dismiss();
			return;
		}
		String url = PathConst.URL_GET_USER_QUESTION + users.getUid();
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);
				if (arg0 == null || arg0.length() == 0) {
					loadingDialog.dismiss();
					return;
				}
				try {
					JSONObject json = new JSONObject(arg0);
					if (json == null || "".equals(json)) {
						loadingDialog.dismiss();
						return;// 防止网络连接超时出现空指针异常
					}
					JSONArray array = json.getJSONArray("list");
					if (array == null || array.length() == 0) {
						loadingDialog.dismiss();
						return;
					}
					UserTextList user_text_info = new UserTextList();
					List<TextItem> text_list = new ArrayList<TextItem>();
					for (int index = 0; index < array.length(); index++) {
						JSONObject obj = array.getJSONObject(index);
						TextItem item = new TextItem();
						item.setId(obj.optString("id"));
						item.setLeft_id(obj.optString("left_id"));
						item.setRight_id(obj.optString("right_id"));
						item.setLeft_title(obj.optString("left_title"));
						item.setRight_title(obj.optString("right_title"));
						item.setLeft_num(obj.optString("left_num"));
						item.setRight_num(obj.optString("right_num"));
						item.setAnwser(obj.optString("anwser"));
						text_list.add(item);
					}
					user_text_info.setList(text_list);
					loadingDialog.dismiss();
					Intent intent = new Intent();
					intent.setClass(context, TestActivity.class);
					intent.putExtra("user_text_info", user_text_info);
					intent.putExtra("total", json.optString("total"));
					intent.putExtra("test_flag", 1);
					intent.putExtra("user", users);
					dialog.dismiss();
					context.startActivity(intent);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					loadingDialog.dismiss();
					e.printStackTrace();
				}

			}
		});
	}

	private OnClickListener deleteListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			interest_delete(LiuLianApplication.current_user.getUid());
		}
	};

	private OnClickListener chatListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			Users users = (Users) v.getTag();

			if (users.isIs_can_talk()) {
				say_hi(users);
				MyToast.makeText(context, "打招呼成功", 1).show();
			} else {
				MyToast.makeText(context, "等级不够呀(>.<)可以去TA主页解锁", Toast.LENGTH_SHORT).show();
			}

			// Users users=(Users) v.getTag();
			//
			// if(users.getUid().equals(LiuLianApplication.current_user.getUid())){
			// if(context instanceof TopicsDetailActivity){
			// interest_delete(LiuLianApplication.current_user.getUid());
			// }else{
			// MyToast.makeText(context, "不能和自己聊天哦", 1).show();
			// }
			// }else{
			// if(users.isIs_can_talk()){
			// if(has_data){
			// Intent intent=new Intent();
			// intent.setClass(context, ChatActivity.class);
			// intent.putExtra("uId", users.getUid());//聊天对象的uid
			// intent.putExtra("userId", users.getHx_username());//聊天对象的环信ID;
			// intent.putExtra("nickname", users.getName());//聊天对象的昵称
			// intent.putExtra("attachmentThemeTitle", topic_name);
			// intent.putExtra("attachmentId", topic_id);
			// intent.putExtra("is_from_content", true);
			// context.startActivity(intent);
			// }else{
			// Intent intent=new Intent();
			// intent.setClass(context, ChatActivity.class);
			// intent.putExtra("uId", users.getUid()); //聊天对象的uid
			// intent.putExtra("userId", users.getHx_username());//聊天对象的环信id
			// intent.putExtra("nickname", users.getName());
			// intent.putExtra("other_image", user.getImage());
			// intent.putExtra("other_sex", user.getSex());
			// context.startActivity(intent);
			// }
			// }else {
			// show_dialog(users);
			// }
			//
			// StatService.onEvent(context, "count_of_chat_topic_detail",
			// "话题页聊天次数",1);
			// }

		}
	};
	private Users user;

	private void interest_delete(final String uid) {
		new AsyncTask<Object, Object, Boolean>() {

			@Override
			protected Boolean doInBackground(Object... params) {
				String url = PathConst.URL_INTEREST_DELETE + "&Luid=" + uid + "&id=" + topic_id + "&is_refresh=1&accesskey=" + LiuLianApplication.current_user.getAccesskey();
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

					for (int i = 0; i < list_users.size(); i++) {
						Users user = list_users.get(i);
						if (user.getUid().equals(uid)) {
							list_users.remove(i);
							TopicsDetailAdapter.this.notifyDataSetChanged();
							break;
						}
					}

					if (context instanceof TopicDetailActivity) {
						TopicDetailActivity activity = (TopicDetailActivity) context;
						activity.interest_delete();
					}

				} else {
					MyToast.makeText(context, "操作失败。", Toast.LENGTH_SHORT).show();
				}
			}

		}.execute();
	}

	public void say_hi(final Users user) {
		String url = PathConst.URL_SAY_HI + "&login_uid=" + LiuLianApplication.current_user.getUid() + "&Luid=" + LiuLianApplication.current_user.getUid() + "&to_hx_username=" + user.getHx_username();
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					JSONObject json_obj = new JSONObject(arg0);
					if (json_obj.getString("flag").equals("1")) {
						// //sayhi成功
						/*
						 * Intent intent = new Intent();
						 * intent.setClass(context, ChatActivity.class);
						 * intent.putExtra("uId", user.getUid());// 聊天对象的uid
						 * intent.putExtra("userId", user.getHx_username());//
						 * 聊天对象的环信ID intent.putExtra("nickname",
						 * user.getName());// 聊天对象的昵称
						 * intent.putExtra("is_from_hi", true);
						 * context.startActivity(intent);
						 */
						EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
						EMConversation conversation = EMChatManager.getInstance().getConversation(user.getHx_username());
						TextMessageBody txtBody = new TextMessageBody("hi");
						// 设置消息body
						message.addBody(txtBody);
						// 设置要发给谁,用户username或者群聊groupid
						message.setReceipt(user.getHx_username());
						// 把messgage加到conversation中

						conversation.addMessage(message);
						sendMsgInBackground(message);
					} else {
						MyToast.makeText(context, "今天已经跟ta打过招呼了", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void sendMsgInBackground(final EMMessage message) {
		// 调用sdk发送异步发送方法
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onSuccess() {
				// MyToast.makeText(context, "打招呼成功", 1).show();
			}

			@Override
			public void onError(int code, String error) {
				// MyToast.makeText(context, "打招呼失败", 1).show();
			}

			@Override
			public void onProgress(int progress, String status) {
			}

		});

	}

}
