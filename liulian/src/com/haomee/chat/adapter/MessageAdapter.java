/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.haomee.chat.adapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.easemob.util.FileUtils;
import com.easemob.util.LatLng;
import com.easemob.util.TextFormater;
import com.haomee.chat.Utils.ImageCache;
import com.haomee.chat.Utils.ImageUtils;
import com.haomee.chat.Utils.SmileUtils;
import com.haomee.chat.activity.AlertDialog;
import com.haomee.chat.activity.ChatActivity;
import com.haomee.chat.activity.ChatGame1;
import com.haomee.chat.activity.ChatGame2;
import com.haomee.chat.activity.ContextMenu;
import com.haomee.chat.activity.ShowBigImage;
import com.haomee.chat.activity.ShowNormalFileActivity;
import com.haomee.chat.activity.ShowVideoActivity;
import com.haomee.chat.task.LoadImageTask;
import com.haomee.chat.task.LoadVideoImageTask;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.Users;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.R;
import com.haomee.liulian.TopicsDetailActivity;
import com.haomee.liulian.UserInfoDetail;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.util.StringUtil;
import com.haomee.util.ViewUtil;
import com.haomee.util.imageloader.ImageLoaderCharles;
import com.haomee.view.MyGifView;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MessageAdapter extends BaseAdapter {

	private final static String TAG = "msg";
	private SharedPreferences.Editor editor;
	private static final int MESSAGE_TYPE_RECV_TXT = 0;
	private static final int MESSAGE_TYPE_SENT_TXT = 1;
	private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
	private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
	private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
	private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
	private static final int MESSAGE_TYPE_SENT_VOICE = 6;
	private static final int MESSAGE_TYPE_RECV_VOICE = 7;
	private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
	private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
	private static final int MESSAGE_TYPE_SENT_FILE = 10;
	private static final int MESSAGE_TYPE_RECV_FILE = 11;

	public static final String IMAGE_DIR = "chat/image/";
	public static final String VOICE_DIR = "chat/audio/";
	public static final String VIDEO_DIR = "chat/video";

	private String username, uId;
	private LayoutInflater inflater;
	private Activity activity;

	// reference to conversation object in chatsdk
	private EMConversation conversation;
	private SharedPreferences preferences_chat_user;
	private ChatActivity context;

	public MessageAdapter(ChatActivity context, String username, int chatType, String uId) {
		this.uId = uId;
		this.username = username;
		preferences_chat_user = context.getSharedPreferences(CommonConst.PREFERENCES_SESSION_USERS, Context.MODE_PRIVATE);
		this.context = context;
		inflater = LayoutInflater.from(context);
		activity = (Activity) context;
		this.conversation = EMChatManager.getInstance().getConversation(username);
	}

	// public void setUser(String user) {
	// this.user = user;
	// }

	/**
	 * 获取item数
	 */
	public int getCount() {
		return conversation.getMsgCount();
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		notifyDataSetChanged();
	}

	public EMMessage getItem(int position) {
		return conversation.getMessage(position);
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * 获取item类型
	 */
	public int getItemViewType(int position) {
		EMMessage message = conversation.getMessage(position);

		if (message.getType() == EMMessage.Type.TXT) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
		}
		if (message.getType() == EMMessage.Type.IMAGE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;

		}
		if (message.getType() == EMMessage.Type.LOCATION) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
		}
		if (message.getType() == EMMessage.Type.VOICE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
		}
		if (message.getType() == EMMessage.Type.VIDEO) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
		}
		if (message.getType() == EMMessage.Type.FILE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
		}

		return -1;// invalid
	}

	public int getViewTypeCount() {
		return 12;
	}

	private View createViewByMessage(EMMessage message, int position) {
		switch (message.getType()) {

		case LOCATION:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.chat_row_received_location, null) : inflater.inflate(R.layout.chat_row_sent_location, null);

		case IMAGE:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.chat_row_received_picture, null) : inflater.inflate(R.layout.chat_row_sent_picture, null);

		case VOICE:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.chat_row_received_voice, null) : inflater.inflate(R.layout.chat_row_sent_voice, null);
		case VIDEO:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.chat_row_received_video, null) : inflater.inflate(R.layout.chat_row_sent_video, null);
		case FILE:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.chat_row_received_file, null) : inflater.inflate(R.layout.chat_row_sent_file, null);
		default:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.chat_row_received_message, null) : inflater.inflate(R.layout.chat_row_sent_message, null);
		}
	}

	@SuppressLint({ "NewApi", "WrongViewCast" })
	public View getView(final int position, View convertView, ViewGroup parent) {
		final EMMessage message = getItem(position);
		ChatType chatType = message.getChatType();
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = createViewByMessage(message, position);
			if (message.getType() == EMMessage.Type.IMAGE) {
				try {
					holder.iv = ((ImageView) convertView.findViewById(R.id.iv_sendPicture));
					holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.percentage);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.TXT) {
				try {
					// 游戏结果
					holder.layout_result = (LinearLayout) convertView.findViewById(R.id.layout_result);
					holder.layout_receive = (RelativeLayout) convertView.findViewById(R.id.receive_layout);
					holder.layout_send = (RelativeLayout) convertView.findViewById(R.id.send_layout);
					holder.send_text = (TextView) convertView.findViewById(R.id.send_text);
					holder.receive_text = (TextView) convertView.findViewById(R.id.receive_text);
					holder.send_score = (TextView) convertView.findViewById(R.id.send_score);
					holder.receive_score = (TextView) convertView.findViewById(R.id.receive_score);

					// 游戏
					holder.layout_game = (RelativeLayout) convertView.findViewById(R.id.layout_game);
					holder.iv_game_type = (ImageView) convertView.findViewById(R.id.iv_game_type);
					holder.timer_click = (TextView) convertView.findViewById(R.id.timer_click);
					holder.security_tip = (TextView) convertView.findViewById(R.id.security_tip);
					holder.image_expression = (ImageView) convertView.findViewById(R.id.image_expression);
					holder.gif_expression = (MyGifView) convertView.findViewById(R.id.gif_expression);
					holder.attach_title = (TextView) convertView.findViewById(R.id.attach_title);
					holder.layout_with_content = (RelativeLayout) convertView.findViewById(R.id.layout_with_content);
					holder.tv_without_content = (TextView) convertView.findViewById(R.id.tv_withoutcontent);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);

					// 这里是文字内容
					holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
					holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);

					holder.tv_without_content.setAutoLinkMask(Linkify.ALL);
					holder.tv_without_content.setMovementMethod(LinkMovementMethod.getInstance());

				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.VOICE) {
				try {
					holder.iv = ((ImageView) convertView.findViewById(R.id.iv_voice));
					holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.tv_length);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
					holder.iv_read_status = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.LOCATION) {
				try {
					holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.tv_location);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.VIDEO) {
				try {
					holder.iv = ((ImageView) convertView.findViewById(R.id.chatting_content_iv));
					holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.percentage);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.size = (TextView) convertView.findViewById(R.id.chatting_size_iv);
					holder.timeLength = (TextView) convertView.findViewById(R.id.chatting_length_iv);
					holder.playBtn = (ImageView) convertView.findViewById(R.id.chatting_status_btn);
					holder.container_status_btn = (LinearLayout) convertView.findViewById(R.id.container_status_btn);
					holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);

				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.FILE) {
				try {
					holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv_file_name = (TextView) convertView.findViewById(R.id.tv_file_name);
					holder.tv_file_size = (TextView) convertView.findViewById(R.id.tv_file_size);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_file_download_state = (TextView) convertView.findViewById(R.id.tv_file_state);
					holder.ll_container = (LinearLayout) convertView.findViewById(R.id.ll_file_container);
					// 这里是进度值
					holder.tv = (TextView) convertView.findViewById(R.id.pb_sending);
				} catch (Exception e) {
				}
				try {
					holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 点击头像到个人信息页

		holder.head_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				if (message.direct == EMMessage.Direct.RECEIVE) {
					if (uId != null && !uId.equals("")) {
						intent.putExtra("uid", username);
					}
				} else if (message.direct == EMMessage.Direct.SEND) {
					Users current_user = LiuLianApplication.current_user;
					if (current_user == null) {
						return;
					}
					String user_id = current_user.getUid();
					if (user_id != null && !user_id.equals("")) {
						intent.putExtra("uid", user_id);
					}
				}
				intent.setClass(context, UserInfoDetail.class);
				context.startActivity(intent);
			}
		});

		String temp = preferences_chat_user.getString(username, "");

		if (message.direct == EMMessage.Direct.RECEIVE) {
			if (!temp.equals("")) {
				holder.head_iv.setBackgroundResource(CommonConst.user_sex[Integer.valueOf(temp.split("######")[3])]);

				ImageLoaderCharles.getInstance(context).addTask(temp.split("######")[1], holder.head_iv);
			} else {
				PostUserInfo(username, holder.head_iv);
			}
		} else {
			holder.head_iv.setBackgroundResource(CommonConst.user_sex[LiuLianApplication.current_user.getSex()]);
			ImageLoaderCharles.getInstance(context).addTask(LiuLianApplication.current_user.getImage(), holder.head_iv);
		}
		// 如果是发送的消息并且不是群聊消息，显示已读textview
		if (message.direct == EMMessage.Direct.SEND && chatType != ChatType.GroupChat) {
			holder.tv_ack = (TextView) convertView.findViewById(R.id.tv_ack);
			if (holder.tv_ack != null) {
				if (message.isAcked) {
					holder.tv_ack.setVisibility(View.VISIBLE);
				} else {
					holder.tv_ack.setVisibility(View.INVISIBLE);
				}
			}
		} else {
			// 如果是文本或者地图消息并且不是group messgae，显示的时候给对方发送已读回执
			if ((message.getType() == Type.TXT || message.getType() == Type.LOCATION) && !message.isAcked && chatType != ChatType.GroupChat) {
				try {
					// 发送已读回执
					message.isAcked = true;
					EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		switch (message.getType()) {
		// 根据消息type显示item
		case IMAGE: // 图片
			handleImageMessage(message, holder, position, convertView);
			break;
		case TXT: // 文本
			handleTextMessage(message, holder, position);
			break;
		case LOCATION: // 位置
			handleLocationMessage(message, holder, position, convertView);
			break;
		case VOICE: // 语音
			handleVoiceMessage(message, holder, position, convertView);
			break;
		case VIDEO: // 视频
			handleVideoMessage(message, holder, position, convertView);
			break;
		case FILE: // 一般文件
			handleFileMessage(message, holder, position, convertView);
			break;
		default:
			// not supported
		}

		if (message.direct == EMMessage.Direct.SEND) {
			View statusView = convertView.findViewById(R.id.msg_status);
			// 重发按钮点击事件
			statusView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					// 显示重发消息的自定义alertdialog
					Intent intent = new Intent(activity, AlertDialog.class);
					intent.putExtra("msg", activity.getString(R.string.confirm_resend));
					intent.putExtra("title", activity.getString(R.string.resend));
					intent.putExtra("cancel", true);
					intent.putExtra("position", position);
					if (message.getType() == EMMessage.Type.TXT)
						activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_TEXT);
					else if (message.getType() == EMMessage.Type.VOICE)
						activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_VOICE);
					else if (message.getType() == EMMessage.Type.IMAGE)
						activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_PICTURE);
					else if (message.getType() == EMMessage.Type.LOCATION)
						activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_LOCATION);
					else if (message.getType() == EMMessage.Type.FILE)
						activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_FILE);
					else if (message.getType() == EMMessage.Type.VIDEO)
						activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_VIDEO);

				}
			});

		} else {
			/*
			 * // 长按头像，移入黑名单 holder.head_iv.setOnLongClickListener(new
			 * OnLongClickListener() {
			 * 
			 * @Override public boolean onLongClick(View v) { Intent intent =
			 * new Intent(activity, AlertDialog.class); intent.putExtra("msg",
			 * "移入到黑名单？"); intent.putExtra("cancel", true);
			 * intent.putExtra("position", position);
			 * activity.startActivityForResult(intent,
			 * ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST); return true; } });
			 */
		}

		TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);

		if (position == 0) {
			timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
			timestamp.setVisibility(View.VISIBLE);
		} else {
			// 两条消息时间离得如果稍长，显示时间
			if (DateUtils.isCloseEnough(message.getMsgTime(), conversation.getMessage(position - 1).getMsgTime())) {
				timestamp.setVisibility(View.GONE);
			} else {
				timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
				timestamp.setVisibility(View.VISIBLE);
			}
		}

		String messageContent = message.getBody().toString();
		boolean has_forbidden = hasForbidden(messageContent);
		if (message.direct == EMMessage.Direct.RECEIVE) {
			if (has_forbidden) {
				// 显示
				holder.security_tip.setVisibility(View.VISIBLE);
			}
		}

		return convertView;
	}

	public void PostUserInfo(final String temp, final ImageView image) {
		if (NetworkUtil.dataConnected(context)) {
			// 获取会话好友头像和昵称
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams re = new RequestParams();
			re.put("hx_username", temp);
			client.post(PathConst.URL_GET_USER_INFO_FROM_HX_NAME, re, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg0) {
					try {
						JSONArray json_arr = new JSONArray(arg0);
						for (int i = 0; i < json_arr.length(); i++) {
							JSONObject json = json_arr.getJSONObject(i);
							uId = json.getString("id");
							image.setImageResource(R.drawable.item_icon);
							ImageLoaderCharles.getInstance(context).addTask(json.getString("head_pic"), image);
							image.setBackgroundResource(CommonConst.user_sex[json.getInt("sex")]);
							editor = preferences_chat_user.edit();
							editor.putString(temp, json.getString("username") + "######" + json.getString("head_pic") + "######" + json.getString("id") + "######" + json.getInt("sex"));
							editor.commit();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * 文本消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 */
	private void handleTextMessage(final EMMessage message, final ViewHolder holder, final int position) {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		Spannable span = SmileUtils.getSmiledText(context, txtBody.getMessage());

		/**
		 * 显示游戏
		 */
		try {
			if (!message.getStringAttribute("chatGameId").equals("")) {
				holder.layout_with_content.setVisibility(View.GONE);
				holder.tv_without_content.setVisibility(View.GONE);
				holder.image_expression.setVisibility(View.GONE);
				holder.gif_expression.setVisibility(View.GONE);
				holder.layout_game.setVisibility(View.VISIBLE);
				if (message.getStringAttribute("chatGameLevel").equals("0")) {
					holder.iv_game_type.setImageResource(R.drawable.game_icon_0);
				} else if (message.getStringAttribute("chatGameLevel").equals("1")) {
					holder.iv_game_type.setImageResource(R.drawable.game_icon_1);
				}else{
					holder.tv.setText("请升级游戏");
				}
				if (message.getStringAttribute("chatGameState").equals("chatGameStateAgree")) {
					holder.layout_game.setVisibility(View.GONE);
					holder.layout_result.setVisibility(View.GONE);
					holder.head_iv.setVisibility(View.GONE);
				} else if (message.getStringAttribute("chatGameState").equals("chatGameStateResult")) {
					holder.layout_result.setVisibility(View.VISIBLE);
					holder.layout_game.setVisibility(View.GONE);
					holder.head_iv.setVisibility(View.GONE);
					holder.receive_score.setText(message.getStringAttribute("chatOtherScore"));
					holder.send_score.setText(message.getStringAttribute("chatMyselfScore"));
					if (Integer.parseInt(message.getStringAttribute("chatOtherScore")) > Integer.parseInt(message.getStringAttribute("chatMyselfScore"))) {
						holder.layout_receive.setBackgroundColor(context.getResources().getColor(R.color.game_win));
						holder.layout_send.setBackgroundColor(context.getResources().getColor(R.color.game_lose));
						holder.receive_text.setText("Win");
						holder.send_text.setText("Lose");
					} else if (Integer.parseInt(message.getStringAttribute("chatOtherScore")) == Integer.parseInt(message.getStringAttribute("chatMyselfScore"))) {
						holder.layout_receive.setBackgroundColor(context.getResources().getColor(R.color.game_win));
						holder.layout_send.setBackgroundColor(context.getResources().getColor(R.color.game_win));
						holder.receive_text.setText("Draw");
						holder.send_text.setText("Draw");
					} else {
						holder.layout_receive.setBackgroundColor(context.getResources().getColor(R.color.game_lose));
						holder.layout_send.setBackgroundColor(context.getResources().getColor(R.color.game_win));
						holder.receive_text.setText("Lose");
						holder.send_text.setText("Win");
					}

					holder.layout_result.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent();
							try {
								intent.putExtra("game_id", message.getStringAttribute("chatGameId"));
								intent.putExtra("chatMyselfScore", message.getStringAttribute("chatMyselfScore"));
								intent.putExtra("chatOtherScore", message.getStringAttribute("chatOtherScore"));
								intent.putExtra("other_id", username);
								intent.putExtra("is_result", true);
								if (message.getStringAttribute("chatGameLevel").equals("0")) {
									intent.setClass(context, ChatGame1.class);
								} else if (message.getStringAttribute("chatGameLevel").equals("1")) {
									intent.setClass(context, ChatGame2.class);
								}
								context.startActivity(intent);
							} catch (EaseMobException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});
				} else {
					holder.layout_result.setVisibility(View.GONE);
					holder.layout_game.setVisibility(View.VISIBLE);
					holder.head_iv.setVisibility(View.VISIBLE);
				}
				if (message.getStringAttribute("chatGameId").equals(LiuLianApplication.PUBLIC_GAME_ID)) {
					context.startRefresh(holder.timer_click);
					holder.timer_click.setText(LiuLianApplication.GAME_TIME_SEND_RECEIVE-- + "");
				} else {
					holder.timer_click.setText("已过期");
				}
				holder.layout_game.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						try {
							String chatGameLevel = message.getStringAttribute("chatGameLevel");
							intent.putExtra("game_id", message.getStringAttribute("chatGameId"));
							intent.putExtra("other_id", username);
							if (message.getStringAttribute("chatGameId").equals(LiuLianApplication.PUBLIC_GAME_ID)) {
								// 接收方
								if (message.direct == EMMessage.Direct.RECEIVE) {
									// 表示同意
									context.sendGame(message.getStringAttribute("chatGameLevel"), message.getStringAttribute("chatGameId"), 1);
									if (chatGameLevel.equals("0")) {
										intent.setClass(context, ChatGame1.class);
									} else if (chatGameLevel.equals("1")) {
										intent.setClass(context, ChatGame2.class);
									}
									context.startActivityForResult(intent, context.REQUEST_CODE_FROM_GAME);
								} else {
									MyToast.makeText(context, "请等待对方同意", 1).show();
								}
							} else {
								MyToast.makeText(context, "游戏已经过期哦", 1).show();
							}
						} catch (EaseMobException e) {
							e.printStackTrace();
						}
					}
				});
			}
			
		} catch (EaseMobException e_game) {
			holder.layout_game.setVisibility(View.GONE);
			holder.layout_result.setVisibility(View.GONE);
			holder.head_iv.setVisibility(View.VISIBLE);
			/**
			 * 显示表情
			 */
			try {

				if (!message.getStringAttribute("chatCustomFaceImage").equals("")) {
					holder.layout_with_content.setVisibility(View.GONE);
					holder.tv_without_content.setVisibility(View.GONE);

					int packageId = Integer.valueOf(message.getStringAttribute("chatCustomFacePackageId"));
					int face_id = Integer.valueOf(message.getStringAttribute("chatCustomFaceId"));

					try {
						int type = Integer.valueOf(message.getStringAttribute("chatCustomFaceIsGif"));
						if (type == 1) {
							// gif

							holder.gif_expression.setVisibility(View.VISIBLE);
							holder.image_expression.setVisibility(View.GONE);
							ViewGroup.LayoutParams params = holder.gif_expression.getLayoutParams();

							params.width = (int) Float.parseFloat(message.getStringAttribute("chatCustomFaceWidth"));
							params.height = (int) Float.parseFloat(message.getStringAttribute("chatCustomFaceHeight"));

							params.width = (ViewUtil.getScreenWidth(context) - ViewUtil.dip2px(context, 20)) * params.width / 360;
							params.height = params.width;
							holder.gif_expression.setLayoutParams(params);

							String fileName = is_have("" + packageId, "" + face_id);
							if (fileName != null) {
								holder.gif_expression.setMovieByteArray(getByteFromFile(LiuLianApplication.download_selected_sdcard + PathConst.DOWNLOAD_EMOTIONS + packageId + "/" + fileName));
							} else {
								String temp = message.getStringAttribute("chatCustomFaceImage");
								LoadGifView loadGif = new LoadGifView(holder.gif_expression);
								loadGif.execute(temp);
							}
						} else {
							// 图片
							holder.gif_expression.setVisibility(View.GONE);
							holder.image_expression.setVisibility(View.VISIBLE);
							ViewGroup.LayoutParams params = holder.image_expression.getLayoutParams();
							params.width = (int) Float.parseFloat(message.getStringAttribute("chatCustomFaceWidth"));
							params.height = (int) Float.parseFloat(message.getStringAttribute("chatCustomFaceHeight"));
							params.width = (ViewUtil.getScreenWidth(context) - ViewUtil.dip2px(context, 20)) * params.width / 360;
							params.height = params.width;
							holder.image_expression.setLayoutParams(params);
							String fileName = is_have("" + packageId, "" + face_id);
							if (fileName != null) {
								holder.image_expression.setImageBitmap(getLoacalBitmap(LiuLianApplication.download_selected_sdcard + PathConst.DOWNLOAD_EMOTIONS + packageId + "/" + fileName));
							} else {
								String temp = message.getStringAttribute("chatCustomFaceImage");
								ImageLoaderCharles.getInstance(context).addTask(temp, holder.image_expression);
							}
						}
					} catch (EaseMobException e2) {
						holder.gif_expression.setVisibility(View.GONE);
						holder.image_expression.setVisibility(View.VISIBLE);
						ViewGroup.LayoutParams params = holder.image_expression.getLayoutParams();
						params.width = (int) Float.parseFloat(message.getStringAttribute("chatCustomFaceWidth"));
						params.height = (int) Float.parseFloat(message.getStringAttribute("chatCustomFaceHeight"));
						params.width = (ViewUtil.getScreenWidth(context) - ViewUtil.dip2px(context, 20)) * params.width / 360;
						params.height = params.width;

						holder.image_expression.setLayoutParams(params);

						String fileName = is_have("" + packageId, "" + face_id);
						if (fileName != null) {
							holder.image_expression.setImageBitmap(getLoacalBitmap(LiuLianApplication.download_selected_sdcard + PathConst.DOWNLOAD_EMOTIONS + packageId + "/" + fileName));
						} else {
							String temp = message.getStringAttribute("chatCustomFaceImage");
							ImageLoaderCharles.getInstance(context).addTask(temp, holder.image_expression);
						}
					}
				}
				holder.tv.setText(span, BufferType.SPANNABLE);
			} catch (EaseMobException e1) {
				holder.layout_with_content.setVisibility(View.VISIBLE);
				holder.tv_without_content.setVisibility(View.GONE);
				holder.image_expression.setVisibility(View.GONE);
				holder.gif_expression.setVisibility(View.GONE);
				e1.printStackTrace();
				try {
					if (!message.getStringAttribute("attachmentThemeTitle").equals("")) {
						holder.layout_with_content.setVisibility(View.VISIBLE);
						holder.tv_without_content.setVisibility(View.GONE);
						holder.image_expression.setVisibility(View.GONE);
						holder.attach_title.setVisibility(View.VISIBLE);
						holder.attach_title.setText(message.getStringAttribute("attachmentThemeTitle"));
						holder.layout_with_content.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent();
								try {
									intent.putExtra("topic_id", message.getStringAttribute("attachmentId"));
									intent.setClass(context, TopicsDetailActivity.class);
									context.startActivity(intent);
								} catch (EaseMobException e) {
									e.printStackTrace();
								}
							}
						});
					} else {
						holder.tv_without_content.setVisibility(View.VISIBLE);
						holder.tv_without_content.setText(span, BufferType.SPANNABLE);
						holder.layout_with_content.setVisibility(View.GONE);
						holder.image_expression.setVisibility(View.GONE);
					}
					holder.tv.setText(span, BufferType.SPANNABLE);
				} catch (EaseMobException e2) {
					/**
					 * 显示纯文字
					 */
					holder.tv_without_content.setVisibility(View.VISIBLE);
					holder.tv_without_content.setText(span, BufferType.SPANNABLE);
					holder.layout_with_content.setVisibility(View.GONE);
					holder.image_expression.setVisibility(View.GONE);
					holder.gif_expression.setVisibility(View.GONE);
					// 设置内容
					holder.tv.setText(span, BufferType.SPANNABLE);
					e2.printStackTrace();
				}

			}
		}

		

		// 设置长按事件监听
		holder.tv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult((new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type", EMMessage.Type.TXT.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});
		if (message.direct == EMMessage.Direct.SEND) {
			switch (message.status) {
			case SUCCESS: // 发送成功
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			case FAIL: // 发送失败
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS: // 发送中
				holder.pb.setVisibility(View.VISIBLE);
				break;
			default:
				// 发送消息
				sendMsgInBackground(message, holder);
			}
		}
	}

	class LoadGifView extends AsyncTask<String, String, String> {

		private MyGifView gifview;

		private LoadGifView(MyGifView gifview) {
			this.gifview = gifview;
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			File cache = null;
			String path_cache = "";
			String file_name_md5 = "";
			if (NetworkUtil.dataConnected(context)) {
				// 获取会话好友头像和昵称
				path_cache = FileDownloadUtil.getDefaultLocalDir(PathConst.DIR_TEMP);
				try {
					String gif_url = params[0];
					if (gif_url != null && !gif_url.trim().equals("")) {
						file_name_md5 = StringUtil.getMD5Str(gif_url);
						cache = new File(path_cache + file_name_md5);
						// 检查本地是否存在
						if (!cache.exists()) {
							FileDownloadUtil.saveImageToLocal(gif_url, cache);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return path_cache + file_name_md5;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				gifview.setMovieByteArray(toByteArray3(result));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static byte[] toByteArray3(String filename) throws IOException {

		FileChannel fc = null;
		try {
			fc = new RandomAccessFile(filename, "r").getChannel();
			MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size()).load();
			System.out.println(byteBuffer.isLoaded());
			byte[] result = new byte[(int) fc.size()];
			if (byteBuffer.remaining() > 0) {
				// System.out.println("remain");
				byteBuffer.get(result, 0, byteBuffer.remaining());
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (fc != null) {
					fc.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] getByteFromFile(String url) {
		byte[] buffer = new byte[1024];
		int len = -1;
		FileInputStream inStream;
		byte[] data = null;
		try {
			inStream = new FileInputStream(url);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			data = outStream.toByteArray();
			outStream.close();
			inStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;

	}

	public void onGifClick(View v) {
		MyGifView gif = (MyGifView) v;
		gif.setPaused(!gif.isPaused());
	}

	/**
	 * @param message
	 * @param holder
	 */
	public String is_have(String packageId, String face_id) {
		File file = new File(FileDownloadUtil.getDefaultLocalDir(PathConst.DOWNLOAD_EMOTIONS) + packageId);
		if (!file.exists() || !file.isDirectory()) {
			return null;
		}
		File[] files = file.listFiles();// 获取所有表情目录文件
		for (File f : files) {
			String[] name = f.getName().split("#");
			String[] temp_name = name[0].split("\\.");
			if (face_id == null) {
				return null;
			}
			if (face_id.equals(temp_name[0])) {
				return f.getName();
			}
		}
		return null;
	}

	/**
	 * 图片消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleImageMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
		holder.pb.setTag(position);
		holder.iv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult((new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type", EMMessage.Type.IMAGE.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});

		if (message.direct == EMMessage.Direct.RECEIVE) {
			// "it is receive msg";
			if (message.status == EMMessage.Status.INPROGRESS) {
				// "!!!! back receive";
				holder.iv.setImageResource(R.drawable.item_icon);
				showDownloadImageProgress(message, holder);
				// downloadImage(message, holder);
			} else {
				// "!!!! not back receive, show image directly");
				holder.pb.setVisibility(View.GONE);
				holder.tv.setVisibility(View.GONE);
				holder.iv.setImageResource(R.drawable.item_icon);
				ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
				if (imgBody.getLocalUrl() != null) {
					// String filePath = imgBody.getLocalUrl();
					String remotePath = imgBody.getRemoteUrl();
					String filePath = ImageUtils.getImagePath(remotePath);
					String thumbRemoteUrl = imgBody.getThumbnailUrl();
					String thumbnailPath = ImageUtils.getThumbnailImagePath(thumbRemoteUrl);
					showImageView(thumbnailPath, holder.iv, filePath, imgBody.getRemoteUrl(), message);
				}
			}
			return;
		}

		// process send message
		// send pic, show the pic directly
		ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
		String filePath = imgBody.getLocalUrl();
		if (filePath != null && new File(filePath).exists())
			showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv, filePath, null, message);
		// else
		// {
		// showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv,
		// filePath, IMAGE_DIR, message);
		// }

		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			// set a timer
			final Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.pb.setVisibility(View.VISIBLE);
							holder.tv.setVisibility(View.VISIBLE);
							holder.tv.setText(message.progress + "%");
							if (message.status == EMMessage.Status.SUCCESS) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
								timer.cancel();
							} else if (message.status == EMMessage.Status.FAIL) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
								// message.setProgress(0);
								holder.staus_iv.setVisibility(View.VISIBLE);
								Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0).show();
								timer.cancel();
							}

						}
					});

				}
			}, 0, 500);
			break;
		default:
			sendPictureMessage(message, holder);
		}
	}

	/**
	 * 视频消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleVideoMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {

		VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
		// final File image=new File(PathUtil.getInstance().getVideoPath(),
		// videoBody.getFileName());
		String localThumb = videoBody.getLocalThumb();

		holder.iv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult(new Intent(activity, ContextMenu.class).putExtra("position", position).putExtra("type", EMMessage.Type.VIDEO.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});

		if (localThumb != null) {

			showVideoThumbView(localThumb, holder.iv, videoBody.getThumbnailUrl(), message);
		}
		if (videoBody.getLength() > 0) {
			String time = DateUtils.toTimeBySecond(videoBody.getLength());
			holder.timeLength.setText(time);
		}
		holder.playBtn.setImageResource(R.drawable.video_download_btn_nor);

		if (message.direct == EMMessage.Direct.RECEIVE) {
			if (videoBody.getVideoFileLength() > 0) {
				String size = TextFormater.getDataSize(videoBody.getVideoFileLength());
				holder.size.setText(size);
			}
		} else {
			if (videoBody.getLocalUrl() != null && new File(videoBody.getLocalUrl()).exists()) {
				String size = TextFormater.getDataSize(new File(videoBody.getLocalUrl()).length());
				holder.size.setText(size);
			}
		}

		if (message.direct == EMMessage.Direct.RECEIVE) {

			// System.err.println("it is receive msg");
			if (message.status == EMMessage.Status.INPROGRESS) {
				// System.err.println("!!!! back receive");
				holder.iv.setImageResource(R.drawable.item_icon);
				showDownloadImageProgress(message, holder);

			} else {
				// System.err.println("!!!! not back receive, show image directly");
				holder.iv.setImageResource(R.drawable.item_icon);
				if (localThumb != null) {
					showVideoThumbView(localThumb, holder.iv, videoBody.getThumbnailUrl(), message);
				}

			}

			return;
		}
		holder.pb.setTag(position);

		// until here ,deal with send video msg
		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			// set a timer
			final Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							holder.pb.setVisibility(View.VISIBLE);
							holder.tv.setVisibility(View.VISIBLE);
							holder.tv.setText(message.progress + "%");
							if (message.status == EMMessage.Status.SUCCESS) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
								timer.cancel();
							} else if (message.status == EMMessage.Status.FAIL) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
								// message.setProgress(0);
								holder.staus_iv.setVisibility(View.VISIBLE);
								Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0).show();
								timer.cancel();
							}

						}
					});

				}
			}, 0, 500);
			break;
		default:
			// sendMsgInBackground(message, holder);
			sendPictureMessage(message, holder);

		}

	}

	/**
	 * 语音消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleVoiceMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
		VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
		holder.tv.setText(voiceBody.getLength() + "\"");
		holder.iv.setOnClickListener(new VoicePlayClickListener(message, holder.iv, holder.iv_read_status, this, activity, username));
		holder.iv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult((new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type", EMMessage.Type.VOICE.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});

		if (message.direct == EMMessage.Direct.RECEIVE) {
			if (message.isAcked) {
				// 隐藏语音未读标志
				holder.iv_read_status.setVisibility(View.INVISIBLE);
			} else {
				holder.iv_read_status.setVisibility(View.VISIBLE);
			}
			System.err.println("it is receive msg");
			if (message.status == EMMessage.Status.INPROGRESS) {
				holder.pb.setVisibility(View.VISIBLE);
				System.err.println("!!!! back receive");
				((FileMessageBody) message.getBody()).setDownloadCallback(new EMCallBack() {

					@Override
					public void onSuccess() {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								holder.pb.setVisibility(View.INVISIBLE);
								notifyDataSetChanged();
							}
						});

					}

					@Override
					public void onProgress(int progress, String status) {
					}

					@Override
					public void onError(int code, String message) {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								holder.pb.setVisibility(View.INVISIBLE);
							}
						});

					}
				});

			} else {
				holder.pb.setVisibility(View.INVISIBLE);

			}
			return;
		}

		// until here, deal with send voice msg
		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:

			break;
		default:
			sendMsgInBackground(message, holder);
		}
	}

	/**
	 * 文件消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleFileMessage(final EMMessage message, final ViewHolder holder, int position, View convertView) {
		final NormalFileMessageBody fileMessageBody = (NormalFileMessageBody) message.getBody();
		final String filePath = fileMessageBody.getLocalUrl();
		holder.tv_file_name.setText(fileMessageBody.getFileName());
		holder.tv_file_size.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));
		holder.ll_container.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				File file = new File(filePath);
				if (file != null && file.exists()) {
					// 文件存在，直接打开
					FileUtils.openFile(file, (Activity) context);
				} else {
					// 下载
					context.startActivity(new Intent(context, ShowNormalFileActivity.class).putExtra("msgbody", fileMessageBody));
				}
				if (message.direct == EMMessage.Direct.RECEIVE && !message.isAcked) {
					try {
						EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
						message.isAcked = true;
					} catch (EaseMobException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		if (message.direct == EMMessage.Direct.RECEIVE) { // 接收的消息
			System.err.println("it is receive msg");
			File file = new File(filePath);
			if (file != null && file.exists()) {
				holder.tv_file_download_state.setText("已下载");
			} else {
				holder.tv_file_download_state.setText("未下载");
			}
			return;
		}

		// until here, deal with send voice msg
		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.INVISIBLE);
			holder.staus_iv.setVisibility(View.INVISIBLE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.INVISIBLE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			// set a timer
			final Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							holder.pb.setVisibility(View.VISIBLE);
							// holder.tv.setVisibility(View.VISIBLE);
							// holder.tv.setText(message.progress + "%");
							if (message.status == EMMessage.Status.SUCCESS) {
								holder.pb.setVisibility(View.INVISIBLE);
								// holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
								timer.cancel();
							} else if (message.status == EMMessage.Status.FAIL) {
								holder.pb.setVisibility(View.INVISIBLE);
								// holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
								// message.setProgress(0);
								holder.staus_iv.setVisibility(View.VISIBLE);
								Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0).show();
								timer.cancel();
							}

						}
					});

				}
			}, 0, 500);
			break;
		default:
			// 发送消息
			sendMsgInBackground(message, holder);
		}

	}

	/**
	 * 处理位置消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleLocationMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
		TextView locationView = ((TextView) convertView.findViewById(R.id.tv_location));
		LocationMessageBody locBody = (LocationMessageBody) message.getBody();
		locationView.setText(locBody.getAddress());
		LatLng loc = new LatLng(locBody.getLatitude(), locBody.getLongitude());
		locationView.setOnClickListener(new MapClickListener(loc, locBody.getAddress()));
		locationView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult((new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type", EMMessage.Type.LOCATION.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return false;
			}
		});

		if (message.direct == EMMessage.Direct.RECEIVE) {
			return;
		}
		// deal with send message
		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			holder.pb.setVisibility(View.VISIBLE);
			break;
		default:
			sendMsgInBackground(message, holder);
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param message
	 * @param holder
	 */
	public void sendMsgInBackground(final EMMessage message, final ViewHolder holder) {
		holder.staus_iv.setVisibility(View.GONE);
		holder.pb.setVisibility(View.VISIBLE);
		// 调用sdk发送异步发送方法
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onSuccess() {
				updateSendedView(message, holder);
			}

			@Override
			public void onError(int code, String error) {
				updateSendedView(message, holder);
			}

			@Override
			public void onProgress(int progress, String status) {
			}

		});

	}

	/*
	 * chat sdk will automatic download thumbnail image for the image message we
	 * need to register callback show the download progress
	 */
	private void showDownloadImageProgress(final EMMessage message, final ViewHolder holder) {
		System.err.println("!!! show download image progress");
		// final ImageMessageBody msgbody = (ImageMessageBody)
		// message.getBody();
		final FileMessageBody msgbody = (FileMessageBody) message.getBody();

		msgbody.setDownloadCallback(new EMCallBack() {

			@Override
			public void onSuccess() {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// message.setBackReceive(false);
						if (message.getType() == EMMessage.Type.IMAGE) {
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
						}
						notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onError(int code, String message) {

			}

			@Override
			public void onProgress(final int progress, String status) {
				if (message.getType() == EMMessage.Type.IMAGE) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							holder.tv.setText(progress + "%");

						}
					});
				}

			}

		});
	}

	/*
	 * send message with new sdk
	 */
	private void sendPictureMessage(final EMMessage message, final ViewHolder holder) {
		try {
			String to = message.getTo();

			// before send, update ui
			holder.staus_iv.setVisibility(View.GONE);
			holder.pb.setVisibility(View.VISIBLE);
			holder.tv.setVisibility(View.VISIBLE);
			holder.tv.setText("0%");
			// if (chatType == ChatActivity.CHATTYPE_SINGLE) {
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

				@Override
				public void onSuccess() {
					Log.d(TAG, "send image message successfully");
					activity.runOnUiThread(new Runnable() {
						public void run() {
							// send success
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
						}
					});
				}

				@Override
				public void onError(int code, String error) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
							// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
							holder.staus_iv.setVisibility(View.VISIBLE);
							Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0).show();
						}
					});
				}

				@Override
				public void onProgress(final int progress, String status) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.tv.setText(progress + "%");
						}
					});
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新ui上消息发送状态
	 * 
	 * @param message
	 * @param holder
	 */
	private void updateSendedView(final EMMessage message, final ViewHolder holder) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// send success
				if (message.getType() == EMMessage.Type.VIDEO) {
					holder.tv.setVisibility(View.GONE);
				}
				if (message.status == EMMessage.Status.SUCCESS) {
					if (message.getType() == EMMessage.Type.FILE) {
						holder.pb.setVisibility(View.INVISIBLE);
						holder.staus_iv.setVisibility(View.INVISIBLE);
					} else {
						holder.pb.setVisibility(View.GONE);
						holder.staus_iv.setVisibility(View.GONE);
					}

				} else if (message.status == EMMessage.Status.FAIL) {
					if (message.getType() == EMMessage.Type.FILE) {
						holder.pb.setVisibility(View.INVISIBLE);
					} else {
						holder.pb.setVisibility(View.GONE);
					}
					holder.staus_iv.setVisibility(View.VISIBLE);
					Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0).show();
				}
			}
		});
	}

	/**
	 * load image into image view
	 * 
	 * @param thumbernailPath
	 * @param iv
	 * @param position
	 * @return the image exists or not
	 */
	private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath, String remoteDir, final EMMessage message) {
		// String imagename =
		// localFullSizePath.substring(localFullSizePath.lastIndexOf("/") + 1,
		// localFullSizePath.length());
		// final String remote = remoteDir != null ? remoteDir+imagename :
		// imagename;
		final String remote = remoteDir;
		EMLog.d("###", "local = " + localFullSizePath + " remote: " + remote);
		// first check if the thumbnail image already loaded into cache
		Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
		if (bitmap != null) {
			// thumbnail image is already loaded, reuse the drawable
			iv.setImageBitmap(bitmap);
			iv.setClickable(true);
			iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					System.err.println("image view on click");
					Intent intent = new Intent(activity, ShowBigImage.class);
					File file = new File(localFullSizePath);
					if (file.exists()) {
						Uri uri = Uri.fromFile(file);
						intent.putExtra("uri", uri);
						System.err.println("here need to check why download everytime");
					} else {
						// The local full size pic does not exist yet.
						// ShowBigImage needs to download it from the server
						// first
						// intent.putExtra("", message.get);
						ImageMessageBody body = (ImageMessageBody) message.getBody();
						intent.putExtra("secret", body.getSecret());
						intent.putExtra("remotepath", remote);
					}
					if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked && message.getChatType() != ChatType.GroupChat) {
						try {
							EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
							message.isAcked = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					activity.startActivity(intent);
				}
			});
			return true;
		} else {

			new LoadImageTask().execute(thumbernailPath, localFullSizePath, remote, message.getChatType(), iv, activity, message);
			return true;
		}

	}

	/**
	 * 展示视频缩略图
	 * 
	 * @param localThumb
	 *            本地缩略图路径
	 * @param iv
	 * @param thumbnailUrl
	 *            远程缩略图路径
	 * @param message
	 */
	private void showVideoThumbView(String localThumb, ImageView iv, String thumbnailUrl, final EMMessage message) {
		// first check if the thumbnail image already loaded into cache
		Bitmap bitmap = ImageCache.getInstance().get(localThumb);
		if (bitmap != null) {
			// thumbnail image is already loaded, reuse the drawable
			iv.setImageBitmap(bitmap);
			iv.setClickable(true);
			iv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
					System.err.println("video view is on click");
					Intent intent = new Intent(activity, ShowVideoActivity.class);
					intent.putExtra("localpath", videoBody.getLocalUrl());
					intent.putExtra("secret", videoBody.getSecret());
					intent.putExtra("remotepath", videoBody.getRemoteUrl());
					if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked && message.getChatType() != ChatType.GroupChat) {
						message.isAcked = true;
						try {
							EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					activity.startActivity(intent);

				}
			});

		} else {
			new LoadVideoImageTask().execute(localThumb, thumbnailUrl, iv, activity, message, this);
		}

	}

	public static class ViewHolder {
		ImageView iv;
		TextView tv;
		ProgressBar pb;
		ImageView staus_iv;
		ImageView head_iv;
		TextView tv_userId;
		ImageView playBtn;
		TextView timeLength;
		TextView size;
		LinearLayout container_status_btn;
		LinearLayout ll_container;
		ImageView iv_read_status;
		TextView tv_ack;
		TextView tv_file_name;
		TextView tv_file_size;
		TextView tv_file_download_state;

		RelativeLayout layout_with_content;
		TextView tv_without_content, attach_title;

		ImageView image_expression;// 表情图片
		MyGifView gif_expression; // 表情Gif
		TextView security_tip;

		// 游戏
		RelativeLayout layout_game;
		ImageView iv_game_type;
		TextView timer_click;

		// 游戏结果
		LinearLayout layout_result;
		RelativeLayout layout_receive, layout_send;
		TextView receive_score, send_score, send_text, receive_text;

	}

	/*
	 * 点击地图消息listener
	 */

	class MapClickListener implements View.OnClickListener {

		LatLng location;
		String address;

		public MapClickListener(LatLng loc, String address) {
			location = loc;
			this.address = address;

		}

		@Override
		public void onClick(View v) {
			/*
			 * Intent intent; intent = new Intent(context,
			 * BaiduMapActivity.class); intent.putExtra("latitude",
			 * location.latitude); intent.putExtra("longitude",
			 * location.longitude); intent.putExtra("address", address);
			 * activity.startActivity(intent);
			 */
		}

	}

	public boolean hasForbidden(String content) {

		boolean if_have = false;
		for (int i = 0; i < CommonConst.FORBIDDEN.length; i++) {
			if (content.contains(CommonConst.FORBIDDEN[i])) {
				if_have = true;
				break;
			}
		}
		return if_have;
	}

}