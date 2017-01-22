package com.haomee.liulian;

import java.util.ArrayList;
import java.util.Random;

import com.haomee.util.imageloader.ImageLoaderCharles;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haomee.adapter.TopicsDetailAdapter;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.ShareContent;
import com.haomee.entity.Topic;
import com.haomee.entity.Users;
import com.haomee.util.NetworkUtil;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class TopicsDetailActivity extends BaseActivity implements OnClickListener {

	private ImageView img_bg, img_icon;
	private TextView topic_title, user_count, text_user_name, text_time;
	private int last_id;
	private boolean have_next = true;
	private ArrayList<Users> list_user_all;
	private boolean is_loading = false;
	private LoadingTask loading_task;

	private ImageView iv_topicsdetail_back;
	private TextView tv_topicsdetail_share;
	private PullToRefreshListView pullToRefreshListView;
	private String shareURL, shareIcon;

	private String topic_id, topic_name;
	private View layout_footer_loading;
	private boolean is_first = true;
	private boolean is_first_toast = true;

	private Topic topic;
	private LoadingDialog loadingDialog;

	private TopicsDetailAdapter adapter;
	private View view_header;
	private ListView listView;

	private LinearLayout ll_header_send;
	private EditText et_header_message;
	boolean is_start = false;

	private InputMethodManager manager;
	private Random rdm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivity_topicsdetail);
		findViews();
		bindListener();
		initData();
	}

	private void findViews() {

		iv_topicsdetail_back = (ImageView) findViewById(R.id.iv_topicsdetail_back);
		tv_topicsdetail_share = (TextView) findViewById(R.id.tv_topicsdetail_share);

		pullToRefreshListView = (PullToRefreshListView) this.findViewById(R.id.lv_topicsdetail_topicsdetail);
		view_header = LayoutInflater.from(this).inflate(R.layout.layout_topicsdetailhead, null);

		ll_header_send = (LinearLayout) view_header.findViewById(R.id.ll_header_send);
		et_header_message = (EditText) view_header.findViewById(R.id.et_header_message);

		listView = pullToRefreshListView.getRefreshableView();
		listView.addHeaderView(view_header);

		layout_footer_loading = LayoutInflater.from(this).inflate(R.layout.refresh_footer_loading, null);

		listView.addFooterView(layout_footer_loading);
		layout_footer_loading.setVisibility(View.GONE);

		img_bg = (ImageView) view_header.findViewById(R.id.img_bg);
		img_icon = (ImageView) view_header.findViewById(R.id.img_icon);
		topic_title = (TextView) view_header.findViewById(R.id.topic_title);
		user_count = (TextView) view_header.findViewById(R.id.user_count);
		text_user_name = (TextView) view_header.findViewById(R.id.text_user_name);
		text_time = (TextView) view_header.findViewById(R.id.text_time);

	}

	private void bindListener() {
		iv_topicsdetail_back.setOnClickListener(this);
		tv_topicsdetail_share.setOnClickListener(this);
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (loading_task != null) {
					loading_task.cancel(true);
				}

				last_id = 0;
				list_user_all = null;
				loading_task = new LoadingTask();
				loading_task.execute();
			}
		});

		pullToRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {

				if (have_next) {
					// 如果上一次的还没有加载完，就不要启动新任务了
					if (!is_loading) {
						if (loading_task != null) {
							loading_task.cancel(true);
						}

						layout_footer_loading.setVisibility(View.VISIBLE);
						loading_task = new LoadingTask();
						loading_task.execute();
					}
				} else {
					layout_footer_loading.setVisibility(View.INVISIBLE);
					if (is_first_toast) {
						is_first_toast = false;
						Toast.makeText(TopicsDetailActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
					}
				}

			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (list_user_all.get((int) id).getUid().equals(LiuLianApplication.current_user.getUid())) {

					Toast.makeText(TopicsDetailActivity.this, "这是你自己哦", 1).show();

				} else {

					Intent intent = new Intent();
					intent.setClass(TopicsDetailActivity.this, UserInfoDetail.class);
					intent.putExtra("uid", list_user_all.get((int) id).getUid());
					startActivity(intent);

					StatService.onEvent(TopicsDetailActivity.this, "count_of_other_topic_detail", "话题页他人点击次数", 1);
				}
			}
		});

		ll_header_send.setOnClickListener(this);

	}

	private void initData() {

		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		rdm = new Random();
		topic_id = this.getIntent().getStringExtra("topic_id");
		loadingDialog = new LoadingDialog(this);

		if (topic_id != null) {
			adapter = new TopicsDetailAdapter(TopicsDetailActivity.this);
			listView.setAdapter(adapter);

			if (NetworkUtil.dataConnected(TopicsDetailActivity.this)) {

				loadingDialog.show();
				loading_task = new LoadingTask();
				loading_task.execute();
			} else {
				MyToast.makeText(TopicsDetailActivity.this, "获取数据失败，请检查网络", Toast.LENGTH_SHORT).show();
			}

		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tv_topicsdetail_share:

			share();

			break;
		case R.id.iv_topicsdetail_back:
			finish();
			break;

		case R.id.ll_header_send:
			String content = this.et_header_message.getText().toString();
			if ("".equals(content)) {
				MyToast.makeText(TopicsDetailActivity.this, "内容不能为空", 0).show();
				return;
			}

			if (NetworkUtil.dataConnected(this)) {

				addInterest(content);

			} else {
				MyToast.makeText(TopicsDetailActivity.this, "网络错误，请重新连接", 0).show();
				;
			}
			break;
		}

	}

	private void addInterest(String content) {
		AsyncHttpClient client = new AsyncHttpClient();
		String url=PathConst.URL_INTEREST_TOPICS_ADD+"&Luid="+LiuLianApplication.current_user.getUid()+"&id="+this.getIntent().getStringExtra("topic_id");
		RequestParams params = new RequestParams();
			
		params.put("content", content);
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, String json) {
				super.onSuccess(arg0, json);
				try {
					JSONObject object = new JSONObject(json);

					if (object != null && !"".equals(object)) {
						if ("1".equals(object.getString("flag"))) {
							// if (loading_task != null) {
							// loading_task.cancel(true);
							// }
							// last_id = 0;
							// list_user_all = null;
							// loading_task = new LoadingTask();
							// loading_task.execute();

							JSONArray json_array = object.getJSONArray("list");
							list_user_all.clear();
							topic.setList_user(list_user_all);

							if (json_array == null || json_array.length() == 0) {
								// MyToast.makeText(TopicsDetailActivity.this,
								// "", length)
								return;
							}

							if (json_array != null && json_array.length() > 0) {
								for (int i = 0; i < json_array.length(); i++) {
									Users users = new Users();
									JSONObject json_object = json_array.getJSONObject(i);
									users.setUid(json_object.optString("uid"));
									users.setName(json_object.optString("username"));
									users.setImage(json_object.optString("head_pic"));
									users.setSex(json_object.optInt("sex"));
									users.setAge(json_object.optString("age"));
									users.setStar(json_object.optString("star"));
									users.setBirthday(json_object.optString("birthday"));
									users.setHx_username(json_object.optString("hx_username"));
									users.setSignature(json_object.optString("signature"));
									users.setCity(json_object.optString("city"));
									users.setBack_pic(json_object.optString("back_pic"));
									users.setIs_sayhi(json_object.optBoolean("is_sayHi"));
									users.setIs_online(json_object.optBoolean("is_online"));
									users.setDistance_str(json_object.optString("distance"));
									users.setTime(json_object.optString("time"));
									users.setSpeekContent(json_object.optString("content"));
									users.setIs_can_talk(json_object.optBoolean("is_can_talk"));
									users.setUser_level_icon(json_object.optString("user_level_icon"));
									users.setUser_level(json_object.optString("user_level"));
									list_user_all.add(users);
								}
							}

							adapter.notifyDataSetChanged();
							et_header_message.setText("");

							hideKeyboard();
						} else if ("0".equals(object.getString("flag"))) {
							MyToast.makeText(TopicsDetailActivity.this, object.getString("msg"), 0).show();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {

				super.onFailure(arg0, arg1);
				Toast.makeText(TopicsDetailActivity.this, "网络连接错误，请重试", Toast.LENGTH_SHORT).show();
			}

		});

	}

	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private void share() {
		if (topic != null && shareURL != null) {
			Intent intent_send = new Intent();
			intent_send.setClass(TopicsDetailActivity.this, ShareActivity.class);
			ShareContent share = new ShareContent();
			share.setId(topic.getId());
			share.setTitle(topic.getUser_num() + "人 喜欢“" + topic.getTitle() + "”");
			share.setSummary("我在榴莲找到相同兴趣的人，你也来吧");
			share.setImg_url(shareIcon);
			share.setRedirect_url(shareURL);
			intent_send.putExtra("share", share);
			TopicsDetailActivity.this.startActivity(intent_send);
			StatService.onEvent(TopicsDetailActivity.this, "count_of_share_topic_detail", "话题页分享点击次数", 1);
		}
	}

	class LoadingTask extends AsyncTask<String, Integer, Topic> {

		@Override
		protected Topic doInBackground(String... url) {

			if (topic_id == null) {
				return null;
			}

			is_loading = true;

			try {

				String is_refresh = is_first ? "0" : "1";
				String urlPath = PathConst.URL_INTEREST_USERS + "&Luid=" + LiuLianApplication.current_user.getUid() + "&uid=" + LiuLianApplication.current_user.getUid() + "&location_x=" + LiuLianApplication.latitude + "&location_y="
						+ LiuLianApplication.longtitude + "&id=" + topic_id + "&last_id=" + last_id + "&is_refresh=" + is_refresh;

				is_first = false;

				if (NetworkUtil.dataConnected(TopicsDetailActivity.this)) {
					JSONObject json = NetworkUtil.getJsonObject(urlPath, null, 5000);

					topic = new Topic();

					// 第一次
					if (last_id == 0) {

						JSONObject json_info = json.getJSONObject("info");

						topic.setId(json_info.getString("id"));
						topic.setTitle(json_info.getString("title"));
						topic.setUser_num(json_info.optInt("user_num"));
						topic.setView_user_num(json_info.optInt("view_num"));
						topic.setBack_img(json_info.getString("back_img"));
						topic.setCreate_time(json_info.getString("create_time"));

						shareURL = json_info.optString("shareURL");
						shareIcon = json_info.optString("share_icon");

						topic_name = json_info.getString("title");
						JSONObject json_create_user = json_info.getJSONObject("create_user");
						Users create_user = new Users();
						create_user.setUid(json_create_user.getString("uid"));
						create_user.setName(json_create_user.getString("username"));
						create_user.setHead_pic_small(json_create_user.getString("head_pic"));
						create_user.setSex(json_create_user.getInt("sex"));
						topic.setCreate_user(create_user);
					}

					last_id = json.getInt("last_id");
					have_next = json.getBoolean("have_next");

					JSONArray json_array = json.getJSONArray("list");
					ArrayList<Users> list_users = new ArrayList<Users>();

					topic.setList_user(list_users);

					if (json_array != null && json_array.length() > 0) {
						for (int i = 0; i < json_array.length(); i++) {
							Users users = new Users();
							JSONObject json_object = json_array.getJSONObject(i);
							users.setUid(json_object.optString("uid"));
							users.setName(json_object.optString("username"));
							users.setImage(json_object.optString("head_pic"));
							users.setSex(json_object.optInt("sex"));
							users.setAge(json_object.optString("age"));
							users.setStar(json_object.optString("star"));
							users.setBirthday(json_object.optString("birthday"));
							users.setHx_username(json_object.optString("hx_username"));
							users.setSignature(json_object.optString("signature"));
							users.setCity(json_object.optString("city"));
							users.setBack_pic(json_object.optString("back_pic"));
							users.setIs_sayhi(json_object.optBoolean("is_sayHi"));
							users.setIs_online(json_object.optBoolean("is_online"));
							users.setDistance_str(json_object.optString("distance"));
							users.setTime(json_object.optString("time"));
							users.setSpeekContent(json_object.optString("content"));
							users.setIs_can_talk(json_object.optBoolean("is_can_talk"));
							users.setUser_level_icon(json_object.optString("user_level_icon"));
							users.setUser_level(json_object.optString("user_level"));
							list_users.add(users);
						}
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return topic;
		}

		@Override
		protected void onPostExecute(Topic re) {

			is_loading = false;
			loadingDialog.dismiss();

			if (topic != null && topic.getList_user() != null) {
				if (list_user_all == null) { // 第一次
					list_user_all = topic.getList_user();

					topic_title.setText(topic.getTitle());
					user_count.setText(topic.getUser_num() + "人  |  " + topic.getView_user_num() + "次  ");
					text_time.setText("发起时间：" + topic.getCreate_time());

					// 如果背景为空,给定一个指定色值
					img_bg.setBackgroundColor(CommonConst.bg_colors[rdm.nextInt(CommonConst.bg_colors.length)]);
					img_bg.setImageDrawable(null);
					ImageLoaderCharles.getInstance(TopicsDetailActivity.this).addTask(topic.getBack_img(), img_bg);

					final Users create_user = topic.getCreate_user();
					img_icon.setBackgroundResource(CommonConst.user_sex[create_user.getSex()]);
					ImageLoaderCharles.getInstance(TopicsDetailActivity.this).addTask(create_user.getHead_pic_small(), img_icon);
					text_user_name.setText(create_user.getName());
					img_icon.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent();
							intent.setClass(TopicsDetailActivity.this, UserInfoDetail.class);
							intent.putExtra("uid", create_user.getUid());
							startActivity(intent);
							StatService.onEvent(TopicsDetailActivity.this, "count_of_icon_topic_detail", "话题页发起人点击次数", 1);
						}
					});
				} else {
					// list_user_all.clear();
					list_user_all.addAll(topic.getList_user());
				}

				adapter.setData(list_user_all, topic_id, topic_name);
				pullToRefreshListView.onRefreshComplete();
			}

			layout_footer_loading.setVisibility(View.INVISIBLE);

		}
	}

}
