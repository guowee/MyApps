package com.haomee.liulian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haomee.adapter.TopicAdapter;
import com.haomee.consts.PathConst;
import com.haomee.entity.Topic;
import com.haomee.util.NetworkUtil;
import com.haomee.view.HintEditText;
import com.haomee.view.MyToast;

//搜索
public class SearchTopicActivity extends BaseActivity {

	private EditText et_search_input;
	private TextView tv_search;
	private PullToRefreshListView list_search_topic;
	private Context context;
	private TopicAdapter topic_adapter;
	private String last_id = "";
	private String limit = "15";
	private boolean have_next_new = false;
	private LoadingNewestTask loading_newest_task;
	private LoadingRangeTopic loading_range_topic;
	private LoadingTrySearch loading_try_search;
	private String input_name;
	private List<Topic> topic_list;
	private String word;
	private LinearLayout range_topic;
	LinearLayout try_search;
	private RelativeLayout rl_search;
	private LinearLayout ll_listview;
	private RelativeLayout rl_search_null;
	private ImageView iv_add_topic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_topic);
		context = SearchTopicActivity.this;

		et_search_input = (EditText) findViewById(R.id.et_search_input);
		et_search_input.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);
		tv_search = (TextView) findViewById(R.id.bt_search);
		list_search_topic = (PullToRefreshListView) findViewById(R.id.list_search_topic);
		range_topic = (LinearLayout) findViewById(R.id.range_topic);
		try_search = (LinearLayout) findViewById(R.id.try_search);
		rl_search = (RelativeLayout) findViewById(R.id.rl_search);
		ll_listview = (LinearLayout) findViewById(R.id.ll_listview);
		rl_search_null = (RelativeLayout) findViewById(R.id.rl_search_null);
		iv_add_topic = (ImageView) findViewById(R.id.iv_add_topic);

		initView();
		tv_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				last_id = "";
				input_name = et_search_input.getText().toString().trim();
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(SearchTopicActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				if (loading_newest_task != null) {
					loading_newest_task.cancel(true);
				}
				if (NetworkUtil.dataConnected(SearchTopicActivity.this)) {
					loading_newest_task = new LoadingNewestTask();
					loading_newest_task.execute();
					StatService.onEvent(context, "search_topic_search", "搜索页区搜索点击次数", 1);
				} else {
					MyToast.makeText(SearchTopicActivity.this, getResources().getString(R.string.no_network), 1).show();
				}

			}
		});

		iv_add_topic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, TopicAdd.class);
				startActivity(intent);
			}
		});

		// 区域话题
		range_topic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				topic_list = null;
				if (loading_range_topic != null) {
					loading_range_topic.cancel(true);
				}
				loading_range_topic = new LoadingRangeTopic();
				loading_range_topic.execute();

				StatService.onEvent(context, "search_topic_district", "搜索页区域话题点击次数", 1);

			}
		});

		// 随机话题
		try_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				topic_list = null;
				if (loading_try_search != null) {
					loading_try_search.cancel(true);
				}
				loading_try_search = new LoadingTrySearch();
				loading_try_search.execute();

				StatService.onEvent(context, "search_topic_random", "搜索页随机话题点击次数", 1);
			}
		});
	}

	private void initView() {
		topic_adapter = new TopicAdapter(context, false);
		/*
		 * list_search_topic.setOnLastItemVisibleListener(new
		 * OnLastItemVisibleListener() {
		 * 
		 * @Override public void onLastItemVisible() { if
		 * (NetworkUtil.dataConnected(context)) { if (!have_next_new) {
		 * MyToast.makeText(context,
		 * context.getResources().getString(R.string.is_the_last_page),
		 * Toast.LENGTH_LONG).show(); } else { if (loading_newest_task != null)
		 * { loading_newest_task.cancel(true); } loading_newest_task = new
		 * LoadingNewestTask(); loading_newest_task.execute(); } } else {
		 * 
		 * Toast.makeText(SearchTopicActivity.this, "网络不给力", 1).show();
		 * 
		 * } } });
		 */
		list_search_topic.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int item_position, long id) {

				Intent intent = new Intent();
				intent.setClass(context, ContentList.class);
				intent.putExtra("topic_id", topic_list.get(item_position - 1).getId());
				intent.putExtra("title", topic_list.get(item_position - 1).getTitle());
				intent.putExtra("is_area", topic_list.get(item_position - 1).getView_range());
				startActivity(intent);

			}
		});
	}

	class LoadingNewestTask extends AsyncTask<String, Integer, List<Topic>> {
		@Override
		protected List<Topic> doInBackground(String... url) {
			try {
				String urlPath = PathConst.URL_SEARCH_TOPIC + "&title=" + input_name + "&location_x=" + LiuLianApplication.latitude + "&location_y=" + LiuLianApplication.longtitude + "&limit=" + limit + "&last_id=" + last_id;
				Log.e("请求地址:", urlPath + "");
				String str_json;
				try {
					str_json = NetworkUtil.getHttpString(urlPath, null, 5000);// 5s延迟
					JSONObject json = null;
					topic_list = new ArrayList<Topic>();
					if (str_json != null) {
						json = new JSONObject(str_json);
					} else {
						json = NetworkUtil.getJsonObject(urlPath, null, 5000);
					}
					if (json != null) {
						Log.e("返回结果：", json + "");
						have_next_new = json.getBoolean("have_next");
						last_id = json.getString("last_id");
						JSONArray array = json.getJSONArray("list");
						int array_length = array.length();
						Topic topic = null;
						for (int i = 0; i < array_length; i++) {
							JSONObject item_topic = array.getJSONObject(i);
							topic = new Topic();
							topic.setId(item_topic.getString("id"));
							topic.setTitle(item_topic.getString("title"));
							topic.setDesc(item_topic.getString("desc"));
							topic.setContent_num(item_topic.getString("content_num"));
							topic.setUser_num(item_topic.optInt("user_num"));
							topic.setCreate_time(item_topic.getString("create_time"));
							topic.setLocation_x(item_topic.getString("location_x"));
							topic.setLocation_y(item_topic.getString("location_y"));
							topic.setIcon(item_topic.getString("icon"));
							topic_list.add(topic);
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
		protected void onPostExecute(List<Topic> topic_list) {
			super.onPostExecute(topic_list);
			if (topic_list != null) {
				if (topic_list.size() == 0) {
					rl_search.setVisibility(View.GONE);
					ll_listview.setVisibility(View.GONE);
					rl_search_null.setVisibility(View.VISIBLE);
				} else {
					rl_search_null.setVisibility(View.GONE);
					rl_search.setVisibility(View.GONE);
					ll_listview.setVisibility(View.VISIBLE);
					list_search_topic.setAdapter(topic_adapter);
					topic_adapter.setData(topic_list);
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	// 区域话题
	class LoadingRangeTopic extends AsyncTask<String, Integer, List<Topic>> {
		@Override
		protected List<Topic> doInBackground(String... url) {
			try {
				String urlPath = PathConst.URL_SEARCH_RANGE_TOPIC + "&location_x=" + LiuLianApplication.latitude + "&location_y=" + LiuLianApplication.longtitude;
				Log.e("请求地址:", urlPath + "");
				String str_json;
				try {
					str_json = NetworkUtil.getHttpString(urlPath, null, 5000);// 5s延迟
					JSONArray json = null;
					topic_list = new ArrayList<Topic>();
					if (str_json != null) {
						json = new JSONArray(str_json);
					} else {
						json = NetworkUtil.getJsonArray(urlPath, null, 5000);
					}
					if (json != null) {
						Log.e("返回结果：", json + "");
						int array_length = json.length();
						Topic topic = null;
						for (int i = 0; i < array_length; i++) {
							JSONObject item_topic = json.getJSONObject(i);
							topic = new Topic();
							topic.setId(item_topic.getString("id"));
							topic.setTitle(item_topic.getString("title"));
							topic.setDesc(item_topic.getString("desc"));
							topic.setContent_num(item_topic.getString("content_num"));
							topic.setUser_num(item_topic.optInt("user_num"));
							topic.setCreate_time(item_topic.getString("create_time"));
							topic.setLocation_x(item_topic.getString("location_x"));
							topic.setLocation_y(item_topic.getString("location_y"));
							topic.setIcon(item_topic.getString("icon"));
							topic_list.add(topic);
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
		protected void onPostExecute(List<Topic> topic_list) {
			super.onPostExecute(topic_list);
			if (topic_list != null) {
				if (topic_list.size() == 0) {
					rl_search.setVisibility(View.GONE);
					ll_listview.setVisibility(View.GONE);
					rl_search_null.setVisibility(View.VISIBLE);
				} else {
					rl_search_null.setVisibility(View.GONE);
					rl_search.setVisibility(View.GONE);
					ll_listview.setVisibility(View.VISIBLE);
					et_search_input.setText(word);
					list_search_topic.setAdapter(topic_adapter);
					topic_adapter.setData(topic_list);
				}
			}
		}
	}

	// 随机话题
	class LoadingTrySearch extends AsyncTask<String, Integer, List<Topic>> {
		@Override
		protected List<Topic> doInBackground(String... url) {
			try {
				String urlPath = PathConst.URL_SEARCH_TRY_SEARCH;
				Log.e("请求地址:", urlPath + "");
				String str_json;
				try {
					str_json = NetworkUtil.getHttpString(urlPath, null, 5000);// 5s延迟
					JSONObject json = null;
					topic_list = new ArrayList<Topic>();
					if (str_json != null) {
						json = new JSONObject(str_json);
					} else {
						json = NetworkUtil.getJsonObject(urlPath, null, 5000);
					}
					if (json != null) {
						Log.e("返回结果：", json + "");
						word = json.optString("word");
						JSONArray array = json.getJSONArray("list");
						int array_length = array.length();
						Topic topic = null;
						for (int i = 0; i < array_length; i++) {
							JSONObject item_topic = array.getJSONObject(i);
							topic = new Topic();
							topic.setId(item_topic.getString("id"));
							topic.setTitle(item_topic.getString("title"));
							topic.setContent_num(item_topic.getString("content_num"));
							topic.setUser_num(item_topic.optInt("user_num"));
							topic.setDesc(item_topic.getString("desc"));
							topic.setCreate_time(item_topic.getString("create_time"));
							topic.setLocation_x(item_topic.getString("location_x"));
							topic.setLocation_y(item_topic.getString("location_y"));
							topic.setIcon(item_topic.getString("icon"));
							topic_list.add(topic);
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
		protected void onPostExecute(List<Topic> topic_list) {
			super.onPostExecute(topic_list);
			if (topic_list != null) {
				if (topic_list.size() == 0) {
					rl_search.setVisibility(View.GONE);
					ll_listview.setVisibility(View.GONE);
					rl_search_null.setVisibility(View.VISIBLE);
				} else {
					rl_search_null.setVisibility(View.GONE);
					rl_search.setVisibility(View.GONE);
					ll_listview.setVisibility(View.VISIBLE);
					et_search_input.setText(word);
					list_search_topic.setAdapter(topic_adapter);
					topic_adapter.setData(topic_list);
				}
			}
		}
	}
}
