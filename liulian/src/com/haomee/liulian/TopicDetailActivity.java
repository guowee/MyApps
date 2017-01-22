package com.haomee.liulian;

import java.util.ArrayList;

import com.haomee.util.imageloader.ImageLoaderCharles;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.haomee.adapter.TopicDetailAdapter;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.ShareContent;
import com.haomee.entity.Topic;
import com.haomee.entity.Users;
import com.haomee.player.MySoundPlayer;
import com.haomee.util.NetworkUtil;
import com.haomee.view.LoadingDialog;
import com.haomee.view.PullToRefreshScrollView;
import com.haomee.view.PullToRefreshScrollView.OnHeaderRefreshListener;
import com.haomee.view.PullToRefreshScrollView.ScrollingStateListener;

public class TopicDetailActivity extends BaseActivity {

	private GridView gridView_users;
	private TopicDetailAdapter topicAdapter;
	private LoadingTask loading_task;
	private LoadingDialog loadingDialog;

	private String topic_id, topic_name;
	// private Topic topic;
	private Topic topic;

	private ImageView img_bg, img_icon;
	private TextView topic_title, user_count, text_user_name, text_time;
	private int last_id;
	private boolean have_next = true;
	private ArrayList<Users> list_user_all;
	private boolean is_loading = false;

	private PullToRefreshScrollView pullRefreshScrollView;
	private ScrollView scrollView;
	private View layout_footer_loading;
	private boolean is_first = true;

	private String shareURL, shareIcon;

	private boolean is_first_toast = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.topic_detail);


		topic_id = this.getIntent().getStringExtra("topic_id");

		// topic = (Topic) this.getIntent().getSerializableExtra("topic");

		loadingDialog = new LoadingDialog(this);

		pullRefreshScrollView = (PullToRefreshScrollView) this.findViewById(R.id.pullRefreshScrollView);
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		gridView_users = (GridView) findViewById(R.id.gridView_users);
		layout_footer_loading = this.findViewById(R.id.layout_footer_loading);
		layout_footer_loading.setVisibility(View.INVISIBLE);

		img_bg = (ImageView) this.findViewById(R.id.img_bg);
		img_icon = (ImageView) this.findViewById(R.id.img_icon);
		topic_title = (TextView) this.findViewById(R.id.topic_title);
		user_count = (TextView) this.findViewById(R.id.user_count);
		text_user_name = (TextView) this.findViewById(R.id.text_user_name);
		text_time = (TextView) this.findViewById(R.id.text_time);

		if (topic_id != null) {
			topicAdapter = new TopicDetailAdapter(this);
			gridView_users.setAdapter(topicAdapter);

			loadingDialog.show();
			loading_task = new LoadingTask();
			loading_task.execute();

		}

		this.findViewById(R.id.bt_share).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (topic != null && shareURL != null) {
					Intent intent_send = new Intent();
					intent_send.setClass(TopicDetailActivity.this, ShareActivity.class);
					ShareContent share = new ShareContent();
					share.setId(topic.getId());
					share.setTitle(topic.getUser_num() + "人 喜欢“" + topic.getTitle() + "”");
					share.setSummary("我在榴莲找到相同兴趣的人，你也来吧");
					share.setImg_url(shareIcon);
					share.setRedirect_url(shareURL);
					intent_send.putExtra("share", share);
					TopicDetailActivity.this.startActivity(intent_send);
					StatService.onEvent(TopicDetailActivity.this, "count_of_share_topic_detail", "话题页分享点击次数", 1);
				}

			}
		});

		this.findViewById(R.id.bt_add).setOnClickListener(new OnClickListener() {

			boolean is_start = false;

			@Override
			public void onClick(View v) {
				if (topic != null && topicAdapter != null && topicAdapter.getCount() > 0 && !is_start) {

					MySoundPlayer.getInstance(TopicDetailActivity.this).play_background(R.raw.sound_click, false);

					scrollView.scrollTo(0, 0);
					topicAdapter.refreshAndShake();

					is_start = true;
					// 2秒之后才能点击
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							is_start = false;
						}
					}, 2000);

					interest_add();
					StatService.onEvent(TopicDetailActivity.this, "count_of_add_topic_detail", "话题页加1次数", 1);
				}

			}
		});

		pullRefreshScrollView.setScrollingStateListener(new ScrollingStateListener() {

			@Override
			public void onScrollStop() {
			}

			@Override
			public void onBottom() {

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
						Toast.makeText(TopicDetailActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
					}
				}

			}

			@Override
			public void onScrollStopAndUp() {

			}
		});

		pullRefreshScrollView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
			@Override
			public void onHeaderRefresh(PullToRefreshScrollView view) {

				if (loading_task != null) {
					loading_task.cancel(true);
				}

				last_id = 0;
				list_user_all = null;
				loading_task = new LoadingTask();
				loading_task.execute();

			}
		});

		gridView_users.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (list_user_all.get(position).getUid().equals(LiuLianApplication.current_user.getUid())) {

					Toast.makeText(TopicDetailActivity.this, "这是你自己哦", 1).show();

				} else {

					Intent intent = new Intent();
					intent.setClass(TopicDetailActivity.this, UserInfoDetail.class);
					intent.putExtra("uid", list_user_all.get(position).getUid());
					startActivity(intent);

					StatService.onEvent(TopicDetailActivity.this, "count_of_other_topic_detail", "话题页他人点击次数", 1);
				}
			}
		});

		this.findViewById(R.id.bt_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void interest_add() {
		new AsyncTask<Object, Object, Boolean>() {

			@Override
			protected Boolean doInBackground(Object... params) {
				String url = PathConst.URL_INTEREST_ADD + topic.getId();
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
					topic.setView_user_num(topic.getView_user_num() + 1);
					user_count.setText(topic.getUser_num() + "人  |  " + topic.getView_user_num() + "次  ");
				}
			}

		}.execute();
	}

	// 在Adapter里面调用的
	public void interest_delete() {

		// topic.setUser_num(topic.getUser_num() - 1);
		user_count.setText((topic.getUser_num() - 1) + "人  |  " + topic.getView_user_num() + "次  ");
		StatService.onEvent(TopicDetailActivity.this, "count_of_delete_topic_detail", "话题页删除自己次数", 1);
	}

	class LoadingTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... url) {

			if (topic_id == null) {
				return false;
			}

			is_loading = true;

			try {

				String is_refresh = is_first ? "0" : "1";
				String urlPath = PathConst.URL_INTEREST_USERS + "&Luid=" + LiuLianApplication.current_user.getUid() + "&uid=" + LiuLianApplication.current_user.getUid() + "&location_x=" + LiuLianApplication.latitude + "&location_y="
						+ LiuLianApplication.longtitude + "&id=" + topic_id + "&last_id=" + last_id + "&is_refresh=" + is_refresh;

				is_first = false;

				if (NetworkUtil.dataConnected(TopicDetailActivity.this)) {
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
							list_users.add(users);
						}
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean re) {

			is_loading = false;
			loadingDialog.dismiss();

			if (topic != null && topic.getList_user() != null) {
				if (list_user_all == null) { // 第一次
					list_user_all = topic.getList_user();

					topic_title.setText(topic.getTitle());
					user_count.setText(topic.getUser_num() + "人  |  " + topic.getView_user_num() + "次  ");
					text_time.setText("发起时间：" + topic.getCreate_time());

                    ImageLoaderCharles.getInstance(TopicDetailActivity.this).addTask(topic.getBack_img(), img_bg);
					final Users create_user = topic.getCreate_user();
					img_icon.setBackgroundResource(CommonConst.user_sex[create_user.getSex()]);

                    ImageLoaderCharles.getInstance(TopicDetailActivity.this).addTask(create_user.getHead_pic_small(),img_icon);
					text_user_name.setText(create_user.getName());
					img_icon.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent();
							intent.setClass(TopicDetailActivity.this, UserInfoDetail.class);
							intent.putExtra("uid", create_user.getUid());
							startActivity(intent);
							StatService.onEvent(TopicDetailActivity.this, "count_of_icon_topic_detail", "话题页发起人点击次数", 1);
						}
					});
				} else {
					list_user_all.addAll(topic.getList_user());
				}
				topicAdapter.setData(list_user_all, topic_id, topic_name);
			}

			pullRefreshScrollView.onHeaderRefreshComplete();
			layout_footer_loading.setVisibility(View.INVISIBLE);

			// gridView_users.onRefreshComplete();

		}
	}

}
