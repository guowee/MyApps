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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.haomee.adapter.TopicDetailAdapter;
import com.haomee.consts.PathConst;
import com.haomee.entity.Users;
import com.haomee.util.NetworkUtil;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.sina.weibo.sdk.utils.LogUtil;

//  标签列表
public class LatestLoginActivity extends BaseActivity {

	private com.handmark.pulltorefresh.library.PullToRefreshGridView gridView_latest;
	private TopicDetailAdapter latestlogin;
	private LoadingTask loading_task;
	private ArrayList<Users> list_users_all;
	private LoadingDialog loadingDialog;
	private String category = "";
	private boolean have_next = true;
	private int last_id = 0;
	private TextView tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.acitivity_latest_login);

		category = getIntent().getStringExtra("category");

		loadingDialog = new LoadingDialog(this);

		gridView_latest = (PullToRefreshGridView) findViewById(R.id.gridView_latest);

		latestlogin = new TopicDetailAdapter(this);
		gridView_latest.setAdapter(latestlogin);

		loadingDialog.show();
		loading_task = new LoadingTask();
		loading_task.execute();

		gridView_latest.setOnRefreshListener(new OnRefreshListener<GridView>() {

			@Override
			public void onRefresh(PullToRefreshBase<GridView> refreshView) {

				if (loading_task != null) {
					loading_task.cancel(true);
				}
				last_id = 0;
				list_users_all = null;
				loading_task = new LoadingTask();
				loading_task.execute();
			}
		});

		gridView_latest.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if (NetworkUtil.dataConnected(LatestLoginActivity.this)) {
					if (!have_next) {
						gridView_latest.onRefreshComplete();
						MyToast.makeText(LatestLoginActivity.this, LatestLoginActivity.this.getResources().getString(R.string.is_the_last_page), 1).show();
					} else {
						if (loading_task != null) {
							loading_task.cancel(true);
						}
						loading_task = new LoadingTask();
						loading_task.execute();
					}
				}

			}
		});

		gridView_latest.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (list_users_all.get(position).getUid().equals(LiuLianApplication.current_user.getUid())) {

					Toast.makeText(LatestLoginActivity.this, "这是你自己哦", 1).show();

				} else {

					Intent intent = new Intent();
					intent.setClass(LatestLoginActivity.this, ImgsBrowseActivity_users.class);
					intent.putExtra("users", list_users_all);
					intent.putExtra("index", position);
					startActivity(intent);
				}
			}
		});

		this.findViewById(R.id.bt_radar).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StatService.onEvent(LatestLoginActivity.this, "latest_login_detect", "最近登录页深度扫描点击次数", 1);
				finish();
			}
		});

		this.findViewById(R.id.bt_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getIntent().getStringExtra("category_name"));
	}

	class LoadingTask extends AsyncTask<String, Integer, ArrayList<Users>> {
		@SuppressWarnings("unused")
		@Override
		protected ArrayList<Users> doInBackground(String... url) {

			ArrayList<Users> list_users = new ArrayList<Users>();
			try {
				String urlPath = PathConst.URL_LABEL_USERS_LIST + "&category=" + category + "&last_id=" + last_id + "&limit=10" + "&location_x=" + LiuLianApplication.latitude + "&location_y=" + LiuLianApplication.longtitude;
				LogUtil.e("请求地址", urlPath + "");
				String str_json = null;// 本地json数据
				try {
					JSONObject json = null;
					// 有网取网络
					if (NetworkUtil.dataConnected(LatestLoginActivity.this)) {
						json = NetworkUtil.getJsonObject(urlPath, null, 5000);
					}
					last_id = json.getInt("last_id");
					have_next = json.getBoolean("have_next");
					JSONArray json_array = null;
					json_array = json.getJSONArray("list");
					if (json_array != null && json_array.length() > 0) {
						for (int i = 0; i < json_array.length(); i++) {
							Users users = new Users();
							JSONObject json_object = json_array.getJSONObject(i);
							users.setUid(json_object.optString("uid"));
							users.setName(json_object.optString("username"));
							users.setImage(json_object.optString("head_pic"));
							users.setBack_pic(json_object.optString("back_pic"));
							users.setStar(json_object.optString("star"));
							users.setBirthday(json_object.optString("birthday"));
							users.setHx_username(json_object.optString("hx_username"));
							users.setDistance_str(json_object.optString("distance"));
							users.setIs_online(json_object.optBoolean("is_online"));
							users.setTime(json_object.optString("time"));
							users.setCity(json_object.optString("city"));
							users.setSex(json_object.optInt("sex"));
							users.setAge(json_object.optString("age"));
							users.setDistance_str(json_object.optString("distance"));
							users.setIs_sayhi(json_object.optBoolean("is_sayHi"));
							list_users.add(users);
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
			return list_users;
		}

		@Override
		protected void onPostExecute(ArrayList<Users> list_users) {
			super.onPostExecute(list_users);
			loadingDialog.dismiss();
			if (list_users_all == null || list_users_all.size() == 0) {// 第一次加载
				list_users_all = list_users;
			} else {
				list_users_all.addAll(list_users);
			}
			gridView_latest.onRefreshComplete();

			if (list_users_all == null || list_users_all.size() == 0) {
				gridView_latest.setVisibility(View.GONE);
			} else {
				gridView_latest.setVisibility(View.VISIBLE);
				latestlogin.setData(list_users_all);
			}
			gridView_latest.onRefreshComplete();

		}
	}

}
