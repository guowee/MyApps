package com.haomee.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.haomee.chat.adapter.ChatAllHistoryAdapter;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.liulian.BaseFragment;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.R;
import com.haomee.liulian.SystemMessageActivity;
import com.haomee.util.NetworkUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MessageFragment extends BaseFragment {

	private View view;
	private ChatAllHistoryAdapter adapter;

	private boolean hidden;
	private Context context;
	public ImageView bt_system;
	private SharedPreferences preferences;
	private SharedPreferences preference;
	SharedPreferences.Editor editor_medal = null;
	// private TextView tip1, tip2;
	private String pref_last_id = "";
	private RelativeLayout layout_sys_msg;
	// private View layout_blank_tip;
	private ListView listView_session;
	private TextView unread_msg_number;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			context = MessageFragment.this.getActivity();
			preference = context.getSharedPreferences(CommonConst.PREFERENCES_MEDAL, Activity.MODE_PRIVATE);
			editor_medal = preference.edit();
			view = inflater.inflate(R.layout.fragment_hi, null);
			initview();
			
			showSessionList();
		} else {
			((ViewGroup) view.getParent()).removeView(view);
		}
		return view;
	}

	public void initview() {

		unread_msg_number = (TextView) view.findViewById(R.id.unread_msg_number);
		bt_system = (ImageView) view.findViewById(R.id.bt_system);
		layout_sys_msg = (RelativeLayout) view.findViewById(R.id.layout_sys_msg);
		layout_sys_msg.setOnClickListener(itemClick);
		listView_session = (ListView) view.findViewById(R.id.list_session);

		// layout_blank_tip = view.findViewById(R.id.layout_blank_tip);
		// tip1 = (TextView) layout_blank_tip.findViewById(R.id.tip1);
		// tip2 = (TextView) layout_blank_tip.findViewById(R.id.tip2);
	}

	private OnClickListener itemClick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.layout_sys_msg:
				unread_msg_number.setVisibility(View.GONE);
				Intent intent = new Intent();
				intent.setClass(context, SystemMessageActivity.class);
				context.startActivity(intent);
				SharedPreferences.Editor editor = null;
				if (preferences != null) {
					editor = preferences.edit();
				}
				// SharedPreferences.Editor editor = preferences.edit();
				if (editor != null) {
					editor.putString("pref_last_id", pref_last_id);
					editor.commit();
				}
				break;
			}
		}
	};

	public void showSessionList() {// 显示消息列表
		if (loadConversationsWithRecentChat().size() == 0) {
			listView_session.setVisibility(View.GONE);
			// showBlankTip("你还没有LIer过一个人哦！", "快去话题页面找taLIer吧！");
		} else {
			adapter = new ChatAllHistoryAdapter(context, 1, loadConversationsWithRecentChat(), MessageFragment.this);
			listView_session.setAdapter(adapter);
			listView_session.setVisibility(View.VISIBLE);
		}
	}

	/*
	 * // 空白页提示 public void showBlankTip(String t1, String t2) {
	 * layout_blank_tip.setVisibility(View.VISIBLE); if
	 * (LiuLianApplication.height_fragment_liulian > 0) {
	 * FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
	 * layout_blank_tip.getLayoutParams(); params.height =
	 * LiuLianApplication.height_fragment_liulian;
	 * layout_blank_tip.setLayoutParams(params); } tip1.setText(t1);
	 * tip2.setText(t2); }
	 * 
	 * public void hideBlankTip() { layout_blank_tip.setVisibility(View.GONE); }
	 */
	/*
	 * 刷新页面
	 */
	public void refresh() {
		showSessionList();
		adapter = new ChatAllHistoryAdapter(context, R.layout.chat_row_chat_history, loadConversationsWithRecentChat(), MessageFragment.this);
		listView_session.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		/*
		 * if (loadConversationsWithRecentChat().size() > 0) { hideBlankTip(); }
		 */
	}

	/**
	 * 获取所有会话
	 * 
	 * @param context
	 * @return
	 */
	private List<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
		List<EMConversation> conversationList = new ArrayList<EMConversation>();
		// 过滤掉messages seize为0的conversation
		for (EMConversation conversation : conversations.values()) {
			if (conversation.getAllMessages().size() != 0)
				conversationList.add(conversation);

		}
		// 排序
		sortConversationByLastChatTime(conversationList);
		return conversationList;
	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(List<EMConversation> conversationList) {
		Collections.sort(conversationList, new Comparator<EMConversation>() {
			@Override
			public int compare(final EMConversation con1, final EMConversation con2) {

				EMMessage con2LastMessage = con2.getLastMessage();
				EMMessage con1LastMessage = con1.getLastMessage();
				if (con2LastMessage.getMsgTime() == con1LastMessage.getMsgTime()) {
					return 0;
				} else if (con2LastMessage.getMsgTime() > con1LastMessage.getMsgTime()) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}

	public void hasSysInfo() {
		String url = PathConst.URL_HAVE_SYS_NEW_V3 + "&uid=" + LiuLianApplication.current_user.getUid() + "&Luid=" + LiuLianApplication.current_user.getUid() + "&last_id=" + pref_last_id;
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					if (arg0 == null || arg0.length() == 0) {
						return;
					}
					JSONObject json = new JSONObject(arg0);
					if (json == null || "".equals(json)) {
						return;// 防止网络连接超时出现空指针异常
					}
					// JSONObject json = new JSONObject(arg0);
					if (1 == json.optInt("flag")) {
						pref_last_id = json.optString("last_id");
						if (json.optBoolean("have")) {
							// 有最新的
							// 显示小红点
							unread_msg_number.setVisibility(View.VISIBLE);
							unread_msg_number.setText(json.getString("sysmsg_count"));
						} else {
							// 隐藏小红点
							unread_msg_number.setVisibility(View.GONE);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!hidden) {
			refresh();
		}
		// 判断是否有新的系统消息
		if (NetworkUtil.dataConnected(context)) {
			preferences = context.getSharedPreferences(CommonConst.PREFERENCES_SYS_INFO, Activity.MODE_PRIVATE);
			pref_last_id = preferences.getString("pref_last_id", ""); // 上次获取系统消息的最大id
			hasSysInfo();
		}

		if (loadConversationsWithRecentChat().size() == 0) {
			// showBlankTip("你还没有LIer过一个人哦！", "快去话题页面找ta一起LIer吧！");
			listView_session.setVisibility(View.GONE);
		} else {
			// hideBlankTip();
			listView_session.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	
}
