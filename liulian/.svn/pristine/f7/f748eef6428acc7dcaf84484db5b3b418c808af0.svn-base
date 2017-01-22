package com.haomee.liulian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haomee.adapter.SystemMessageAdapter;
import com.haomee.consts.PathConst;
import com.haomee.entity.SystemMessage;
import com.haomee.entity.Topic;
import com.haomee.entity.Users;
import com.haomee.util.NetworkUtil;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SystemMessageActivity extends BaseActivity {

	private PullToRefreshListView pull_refresh_list;
	private SystemMessageAdapter adapter;
	private SystemMessageActivity context;
	private boolean have_next_new;
	private String last_id_new = "";
	private List<SystemMessage> list_system_all;
	private LoadingTask loadTask;
	private TextView tv_clean;
	private String del_id;
	private View layout_blank_tip;
	private TextView tip1, tip2;
	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_system_message);
		loadingDialog = new LoadingDialog(SystemMessageActivity.this);
		tv_clean = (TextView) findViewById(R.id.tv_clean);
		initBlankTip();
		findViewById(R.id.tv_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				context.finish();
			}
		});

		tv_clean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (del_id.equals("")) {

					Toast.makeText(SystemMessageActivity.this, "没有系统消息", 1).show();

				} else {

					delMsg(del_id);
				}
			}
		});

		pull_refresh_list = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

		pull_refresh_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

				Intent intent = new Intent();
				if (list_system_all.get(position - 1).getS_meg_type().equals("6")) {
					intent.putExtra("uid", LiuLianApplication.current_user.getUid());
					intent.setClass(context, ChestActivity.class);
					context.startActivity(intent);
				} else if (list_system_all.get(position - 1).getS_meg_type().equals("5")) {
					intent.setClass(context, FeedbackActivity.class);
					context.startActivity(intent);
				}
			}
		});

		pull_refresh_list.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				if (NetworkUtil.dataConnected(context)) {
					if (!have_next_new) {
						MyToast.makeText(context, context.getResources().getString(R.string.is_the_last_page), Toast.LENGTH_LONG).show();
					} else {
						if (loadTask != null) {
							loadTask.cancel(true);
						}
						loadTask = new LoadingTask();
						loadTask.execute();
					}
				} else {

				}
			}
		});

		pull_refresh_list.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// 背景重复出现的bug
				pull_refresh_list.getChildAt(0).setVisibility(View.INVISIBLE);

				if (NetworkUtil.dataConnected(context)) {
					list_system_all = null;
					if (loadTask != null) {
						loadTask.cancel(true);
					}
					last_id_new = "";
					loadTask = new LoadingTask();
					loadTask.execute();

				} else {
					MyToast.makeText(context, context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
				}
			}
		});

		adapter = new SystemMessageAdapter(context);
		pull_refresh_list.setAdapter(adapter);

		if (NetworkUtil.dataConnected(this)) {
			loadingDialog.show();
			loadTask = new LoadingTask();
			loadTask.execute();
		} else {
			MyToast.makeText(this, getResources().getString(R.string.no_network), 1).show();
		}

	}

	private void initBlankTip() {
		layout_blank_tip = findViewById(R.id.layout_blank_tip);
		tip1 = (TextView) layout_blank_tip.findViewById(R.id.tip1);
		tip2 = (TextView) layout_blank_tip.findViewById(R.id.tip2);
		hideBlankTip();
	}

	// 空白页提示
	public void showBlankTip(String t1, String t2) {
		layout_blank_tip.setVisibility(View.VISIBLE);

		if (LiuLianApplication.height_fragment_liulian > 0) {
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layout_blank_tip.getLayoutParams();
			params.height = LiuLianApplication.height_fragment_liulian;
			layout_blank_tip.setLayoutParams(params);
		}

		tip1.setText(t1);
		tip2.setText(t2);
	}

	public void hideBlankTip() {
		layout_blank_tip.setVisibility(View.GONE);
	}

	public void delMsg(String del_id) {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("uid", LiuLianApplication.current_user.getUid());
		re.put("Luid", LiuLianApplication.current_user.getUid());
		re.put("del_id", del_id);
		asyncHttp.get(PathConst.URL_USER_DEL_MSG, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					JSONObject json = new JSONObject(arg0);
					Log.e("返回数据：", json.toString());
					if (1 == json.optInt("flag")) {
						list_system_all.clear();
						adapter.setData(list_system_all);
						pull_refresh_list.onRefreshComplete();
						Toast.makeText(SystemMessageActivity.this, "删除成功", 1).show();
					} else {
						Toast.makeText(SystemMessageActivity.this, json.optString("msg"), 1).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	class LoadingTask extends AsyncTask<String, Integer, List<SystemMessage>> {
		@Override
		protected List<SystemMessage> doInBackground(String... url) {

			ArrayList<SystemMessage> topic_list = new ArrayList<SystemMessage>();
			try {

				String urlPath = PathConst.URL_LIST_SYSTEM_MESSAGE_NEW_V3 + "&uid=" + LiuLianApplication.current_user.getUid() + "&last_id=" + last_id_new + "&limit=" + "10";

				Log.e("地址", urlPath + "");
				String str_json = null;
				try {
					// 有网取网络
					if (NetworkUtil.dataConnected(context)) {
						str_json = NetworkUtil.getHttpString(urlPath, null, 5000);// 5s延迟
					}
					JSONObject json = null;
					if (str_json != null) {
						json = new JSONObject(str_json);
					}
					if (json != null) {
						have_next_new = json.optBoolean("have_next");
						last_id_new = json.optString("last_id");
						del_id = json.optString("del_id");
						JSONArray array = json.getJSONArray("list");
						int array_length = array.length();
						SystemMessage systemMessage = null;
						for (int i = 0; i < array_length; i++) {
							JSONObject item_system = array.getJSONObject(i);
							systemMessage = new SystemMessage();
							systemMessage.setS_id(item_system.optString("uid"));
							systemMessage.setS_cont(item_system.optString("cont"));
							systemMessage.setCreate_time(item_system.optString("create_time"));
							systemMessage.setIcon(item_system.optString("icon"));
							systemMessage.setS_meg_type(item_system.optString("msg_type"));

							if (item_system.getString("msg_type").equals("2")) {

								JSONObject item_user = item_system.getJSONObject("from_user");
								Users users = new Users();
								users.setUid(item_user.optString("uid"));
								users.setName(item_user.optString("username"));
								systemMessage.setUsers(users);

							} else if (item_system.getString("msg_type").equals("3")) {

								JSONObject item_topic = item_system.getJSONObject("interest");
								Topic topic = new Topic();
								topic.setId(item_topic.optString("id"));
								topic.setTitle(item_topic.optString("title"));
								systemMessage.setTopic(topic);

							} else if (item_system.getString("msg_type").equals("4")) {

								systemMessage.setFeed_str(item_system.optString("feed_str"));

							} else if (item_system.getString("msg_type").equals("5")) {
								systemMessage.setH5_url(item_system.getString("url"));
							}
							topic_list.add(systemMessage);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return topic_list;
		}

		@Override
		protected void onPostExecute(List<SystemMessage> list_system) {
			super.onPostExecute(list_system);
			if (list_system_all == null || list_system_all.size() == 0) {// 第一次加载
				list_system_all = list_system;
			} else {
				list_system_all.addAll(list_system);
			}
			loadingDialog.dismiss();
			pull_refresh_list.onRefreshComplete();

			if (list_system_all == null || list_system_all.size() == 0) {
				pull_refresh_list.setVisibility(View.GONE);
				showBlankTip("你还没有收到系统消息哦", "努力发现吧！");
			} else {
				pull_refresh_list.setVisibility(View.VISIBLE);
				adapter.setData(list_system_all);
			}

		}
	}

}
