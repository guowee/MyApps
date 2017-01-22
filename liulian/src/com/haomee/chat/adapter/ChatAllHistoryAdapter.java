package com.haomee.chat.adapter;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.DateUtils;
import com.haomee.chat.Utils.SmileUtils;
import com.haomee.chat.activity.ChatActivity;
import com.haomee.chat.db.InviteMessgeDao;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.fragment.MessageFragment;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.LoginPageActivity;
import com.haomee.liulian.MainActivity;
import com.haomee.liulian.R;
import com.haomee.liulian.TopicsDetailActivity;
import com.haomee.liulian.UserInfoDetail;
import com.haomee.util.NetworkUtil;
import com.haomee.util.StringUtil;
import com.haomee.util.imageloader.ImageLoaderCharles;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ChatAllHistoryAdapter extends BaseSwipeAdapter {

	private Context mContext;
	private Animation animation;
	List<EMConversation> conversation_list;
	private SharedPreferences.Editor editor;
	private SharedPreferences preferences_chat_user;
	private TextView textViewResourceId;
	private SharedPreferences preferences_new_day;

	private MessageFragment messageFragment;

	public ChatAllHistoryAdapter(Context mContext, int textViewResourceId, List<EMConversation> conversation_list, MessageFragment messageFragment) {
		this.textViewResourceId = this.textViewResourceId;
		this.mContext = mContext;
		this.conversation_list = conversation_list;
		animation = AnimationUtils.loadAnimation(mContext, R.anim.push_out);
		preferences_chat_user = mContext.getSharedPreferences(CommonConst.PREFERENCES_SESSION_USERS, Context.MODE_PRIVATE);
		preferences_new_day = mContext.getSharedPreferences(CommonConst.PREFERENCES_NEW_DAY_CHAT, Context.MODE_PRIVATE);
		this.messageFragment = messageFragment;
	}

	@Override
	public View generateView(final int position, ViewGroup parent) {
		View convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_row_chat_history, null);

		return convertView;
	}

	@Override
	public void fillValues(final int position, View convertView) {
		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
		TextView message_from = (TextView) convertView.findViewById(R.id.message_from);
		TextView message = (TextView) convertView.findViewById(R.id.message);
		TextView time = (TextView) convertView.findViewById(R.id.time);
		ImageView avatar = (ImageView) convertView.findViewById(R.id.item_image);
		ImageView msgState = (ImageView) convertView.findViewById(R.id.msg_state);

		final SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));
		final EMConversation conversation = (EMConversation) getItem(position);
		convertView.findViewById(R.id.delete).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
				EMChatManager.getInstance().deleteConversation(conversation.getUserName(), conversation.isGroup());
				InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(mContext);
				inviteMessgeDao.deleteMessage(conversation.getUserName());
				deleteItem(swipeLayout, position);
				swipeLayout.close();
			}
		});

		convertView.findViewById(R.id.top).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "屏蔽成功", Toast.LENGTH_SHORT).show();
				try {
					EMContactManager.getInstance().addUserToBlackList(conversation.getUserName(), false);

					EMChatManager.getInstance().deleteConversation(conversation.getUserName(), conversation.isGroup());
					InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(mContext);
					inviteMessgeDao.deleteMessage(conversation.getUserName());

				} catch (EaseMobException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				deleteItem(swipeLayout, position);
				swipeLayout.close();
			}
		});

		// 获取与此用户/群组的会话
		// 获取用户username或者群组groupid
		final String username = conversation.getUserName();
		avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(mContext, UserInfoDetail.class);
				intent.putExtra("uid", username);
				mContext.startActivity(intent);
			}
		});

		String[] temp = preferences_chat_user.getString(username, "").split("######");
		String nickname = "";
		String icon = "";
		String uid = "";
		int sex = 0;
		if (temp.length == 4) {
			nickname = temp[0];
			icon = temp[1];
			uid = temp[2];
			sex = Integer.parseInt(temp[3]);
		} else {
			PostUserInfo(username, avatar, name);
		}

		// 本地或者服务器获取用户详情，以用来显示头像和nick
		avatar.setImageResource(R.drawable.item_icon);
		avatar.setBackgroundResource(CommonConst.user_sex[sex]);
		ImageLoaderCharles.getInstance(mContext).addTask(icon, avatar);
		name.setText(nickname);
		final String to_uid = uid;
		final String to_nickname = nickname;
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = conversation.getUserName();

				if (LiuLianApplication.current_user == null) {
					Intent intent = new Intent();
					intent.setClass(mContext, LoginPageActivity.class);
					mContext.startActivity(intent);
					MyToast.makeText(mContext, "请先登录", Toast.LENGTH_SHORT).show();
					return;
				}
				if (username.equals(LiuLianApplication.current_user.getHx_username())) {
					Toast.makeText(mContext, "不能和自己聊天", 0).show();
				} else if ("".equals(LiuLianApplication.current_user.getHx_username())) {
					MyToast.makeText(mContext, "登陆聊天服务器失败，请尝试重新登陆", Toast.LENGTH_SHORT).show();
				} else {
					// 进入聊天页面
					Intent intent = new Intent(mContext, ChatActivity.class);
					intent.putExtra("userId", username);
					intent.putExtra("uId", to_uid);
					intent.putExtra("nickname", to_nickname);

					mContext.startActivity(intent);
				}
			}
		});

		// name.setTag(uid);
		if (conversation.getUnreadMsgCount() > 0) {
			// 显示与此用户的消息未读数
			unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}

		if (conversation.getMsgCount() != 0) {
			// 把最后一条消息的内容作为item的message内容
			EMMessage lastMessage = conversation.getLastMessage();
			try {
				String message_state = lastMessage.getStringAttribute("chatGameState");
				if (lastMessage.getFrom().equals(LiuLianApplication.current_user.getHx_username())) {
					if (message_state.equals("chatGameStateStart")) {
						message_from.setText("你发送了一个游戏邀请 ");
					} else if (message_state.equals("chatGameStateAgree")) {
						message_from.setText("你同意了对方的游戏邀请 ");
					} else if (message_state.equals("chatGameStateResult")) {
						message_from.setText("你收到了一份游戏结果 ");
					}
				} else {
					if (message_state.equals("chatGameStateStart")) {
						message_from.setText("你收到了一个游戏邀请 ");
					} else if (message_state.equals("chatGameStateAgree")) {
						message_from.setText("对方同意了你的游戏邀请 ");
					} else if (message_state.equals("chatGameStateResult")) {
						message_from.setText("你收到了一份游戏结果 ");
					}

				}
				message.setVisibility(View.GONE);
			} catch (EaseMobException e1) {
				message.setVisibility(View.VISIBLE);
				e1.printStackTrace();
				if (lastMessage.direct == EMMessage.Direct.SEND) {
					message_from.setText("发送: ");
				} else {
					message_from.setText("收到: ");
				}
				try {
					message.setText("[" + lastMessage.getStringAttribute("chatCustomFaceName") + "]");
				} catch (Exception e) {
					try {
						message.setText(lastMessage.getStringAttribute("attachment_content"));
					} catch (Exception e2) {
						message.setText(SmileUtils.getSmiledText(mContext, getMessageDigest(lastMessage, (mContext))), BufferType.SPANNABLE);
					}

				}
			}

			time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
			if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
				msgState.setVisibility(View.VISIBLE);
			} else {
				msgState.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public int getCount() {

		return conversation_list.size();
	}

	@Override
	public Object getItem(int position) {
		return conversation_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void deleteItem(View view, final int position) {
		view.startAnimation(animation);

		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// ((MainActivity) mContext).updateUnreadLabel();
				conversation_list.remove(position);
				notifyDataSetChanged();
				/*
				 * if (conversation_list.size() == 0) {
				 * messageFragment.showBlankTip("你还没有LIer过一个人哦！",
				 * "快去话题页面找ta一起LIer吧！"); }
				 */
			}
		});

	}

	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe;
	}

	/**
	 * 根据消息内容和消息类型获取消息内容提示
	 */
	private String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case IMAGE: // 图片消息
			ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
			digest = getStrng(context, R.string.picture) + imageBody.getFileName();
			break;
		case VOICE:// 语音消息
			digest = getStrng(context, R.string.voice);
			break;
		case VIDEO: // 视频消息
			digest = getStrng(context, R.string.video);
			break;
		case TXT: // 文本消息
			TextMessageBody txtBody = (TextMessageBody) message.getBody();
			digest = txtBody.getMessage();
			break;
		case FILE: // 普通文件消息
			digest = getStrng(context, R.string.file);
			break;
		default:
			System.err.println("error, unknow type");
			return "";
		}

		return digest;
	}

	String getStrng(Context context, int resId) {
		return context.getResources().getString(resId);
	}

	public void PostUserInfo(final String temp, final ImageView image, final TextView text) {
		if (NetworkUtil.dataConnected(mContext)) {
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
							image.setImageResource(R.drawable.item_icon);
							ImageLoaderCharles.getInstance(mContext).addTask(json.getString("head_pic"), image);
							image.setBackgroundResource(CommonConst.user_sex[json.getInt("sex")]);
							text.setText(json.getString("username"));
							text.setTag(json.getString("id"));
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
}
