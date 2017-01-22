package com.haomee.liulian;

import java.io.File;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haomee.adapter.ListViewAdapter;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.ShareContent;
import com.haomee.entity.Topic;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;

public class FavActivity extends BaseActivity{
	
	
	private Context context;
	private String last_id = "";
	private boolean have_next_new;
	private ListViewAdapter topic_adapter;

	private LoadingTask loading_task;

	private List<Topic> list_topic_all;
	private PullToRefreshListView mPullRefreshListView;
	private TextView title;
	
	private RelativeLayout layout_blank_tip;
	private TextView tip1,tip2;
	
	private TextView bt_refresh;
	private LoadingDialog loadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.fragment_list_topic);
		
		loadingDialog = new LoadingDialog(this);
		
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		layout_blank_tip = (RelativeLayout) findViewById(R.id.layout_blank_tip);
		bt_refresh = (TextView) findViewById(R.id.bt_refresh);
		
		findViewById(R.id.bt_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				onBackPressed();
			}
		});
		tip1 = (TextView) findViewById(R.id.tip1);
		tip2 = (TextView) findViewById(R.id.tip2);
		
		title = (TextView) findViewById(R.id.title);
		title.setText("收藏");
		init_topic();
		reloadData();
		
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(context, ContentList.class);
				Topic topic = list_topic_all.get(position - 1);
				intent.putExtra("topic_id", topic.getId());
				intent.putExtra("title", topic.getTitle());
				intent.putExtra("is_area", topic.getView_range());
				startActivity(intent);
			}
		});
		
	}
	public void reloadData(){
		last_id = "";
		
		if (loading_task != null) {
			loading_task.cancel(true);
		}
		loadingDialog.show();
		loading_task = new LoadingTask();
		loading_task.execute();
	}
	
	
	private void init_topic() {
		list_topic_all = new ArrayList<Topic>();
		topic_adapter = new ListViewAdapter(context, "fav");
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				// 背景重复出现的bug
				mPullRefreshListView.getChildAt(0).setVisibility(View.INVISIBLE);

				// Do work to refresh the list here.
				if (NetworkUtil.dataConnected(context)) {
					reloadData();
				} else {
					MyToast.makeText(FavActivity.this, context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Add an end-of-list listener
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if (NetworkUtil.dataConnected(context)) {
					if (!have_next_new) {
						MyToast.makeText(FavActivity.this, context.getResources().getString(R.string.is_the_last_page), 1).show();
					} else {
						if (loading_task != null) {
							loading_task.cancel(true);
						}
						loading_task = new LoadingTask();
						loading_task.execute();
					}
				} else {
					MyToast.makeText(FavActivity.this, context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
				}
			}
		});

		ListView actualListView = mPullRefreshListView.getRefreshableView();

		actualListView.setAdapter(topic_adapter);

	}
	
	/**加载话题列表*/
	class LoadingTask extends AsyncTask<String, Integer, List<Topic>> {
		@Override
		protected List<Topic> doInBackground(String... url) {
			ArrayList<Topic> topic_list = new ArrayList<Topic>();
			try {
				String urlPath = PathConst.URL_LIST_FAV 
						+ "&uid=" + LiuLianApplication.current_user.getUid() 
						+ "&accesskey=" + LiuLianApplication.current_user.getAccesskey() 
						+ "&limit=20" + "&last_id=" + last_id 
						+ "&location_x=" + LiuLianApplication.longtitude + "&location_y=" + LiuLianApplication.latitude;
				
				Log.e("url", urlPath+"");

				String dir_offline = FileDownloadUtil.getDefaultLocalDir(PathConst.OFFLINE_DATA);
				File file_local = null;// 本地文件
				String str_json = null;// 本地json数据
				if(last_id.equals("")){		// 第一页
					list_topic_all = null;
				}

				if (dir_offline != null) {
					file_local = new File(dir_offline + PathConst.OFFLINE_TOPIC_FAV);// 保存最新话题的本地json的文件
				}

				try {

					// 有网取网络
					if (NetworkUtil.dataConnected(context)) {
						str_json = NetworkUtil.getHttpString(urlPath, null, 5000);// 5s延迟
					}

					// 只缓存第一页
					if (last_id.equals("") && str_json != null && file_local != null) {
						FileDownloadUtil.saveStringToLocal(str_json, file_local); // 保存到本地缓存中
					}

					// 第一页网络获取失败则取本地
					if (last_id.equals("") && str_json == null && file_local != null && file_local.exists()) {
						str_json = FileDownloadUtil.getLocalString(file_local);
					}

					JSONObject json = null;
					if (str_json != null) {
						json = new JSONObject(str_json);
					}

					if (json != null) {
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
							topic.setCreate_time(item_topic.getString("create_time"));
							topic.setLocation_x(item_topic.getString("location_x"));
							topic.setLocation_y(item_topic.getString("location_y"));
							topic.setIcon(item_topic.getString("icon"));
							topic.setUser_num(item_topic.optInt("user_num"));
							topic.setContent_num(item_topic.optString("content_num"));
							topic_list.add(topic);
						}
						
						if (json.has("egg")) {
							JSONArray json_arr = json.getJSONArray("egg");
							JSONObject egg_obj = json_arr.getJSONObject(0);
							Intent intent_send = new Intent();
							intent_send.setClass( context, ShareMedalActivity.class);
							ShareContent share = new ShareContent();
							share.setId(egg_obj.getString("id"));
							share.setTitle(egg_obj.getString("name"));
							share.setSummary(egg_obj.getString("desc"));
							share.setImg_url(egg_obj.getString("icon"));
							share.setRedirect_url(CommonConst.GOV_URL);
							intent_send.putExtra("share", share);
							context.startActivity(intent_send);
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
			
			loadingDialog.dismiss();
			LiuLianApplication.is_update_fav = false;
			
			if (list_topic_all == null || list_topic_all.size() == 0) {// 第一次加载
				list_topic_all = topic_list;
			} else {
				list_topic_all.addAll(topic_list);
			}
			
			if(list_topic_all.size()==0){
				mPullRefreshListView.setVisibility(View.GONE);
				bt_refresh.setVisibility(View.GONE);
				layout_blank_tip.setVisibility(View.VISIBLE);
				tip1.setText("你还没有收藏过话题哟");
				tip2.setText("去发现页找更多有趣话题");
			}else{
				mPullRefreshListView.setVisibility(View.VISIBLE);
				topic_adapter.setData(list_topic_all);
			}
			mPullRefreshListView.onRefreshComplete();

		}
	}

	
	

}
