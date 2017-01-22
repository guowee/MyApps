package com.haomee.liulian;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haomee.adapter.ContentAdapter;
import com.haomee.consts.PathConst;
import com.haomee.entity.Content;
import com.haomee.entity.ContentPicture;
import com.haomee.entity.Movie;
import com.haomee.entity.Music;
import com.haomee.entity.Users;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;

public class PraiseActivity extends BaseActivity{
	
	
	private Context context;
	private String last_id = "";
	private boolean have_next;
	private ContentAdapter content_adapter;
	private LoadingContentTask loading_task;
	private List<Content> list_all_content;
	private PullToRefreshListView mPullRefreshListView;
	private TextView title;
	private RelativeLayout layout_blank_tip;
	private TextView tip1,tip2;
	private TextView bt_refresh;
	private LoadingDialog loadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.fragment_list_content);
		
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
		title.setText("点赞");
		init_list();
		reloadData();
	}
	
	public void reloadData(){
		last_id = "";
		if (loading_task != null) {
			loading_task.cancel(true);
		}
		loadingDialog.show();
		loading_task = new LoadingContentTask();
		loading_task.execute();
	}
	
	private void init_list() {
		list_all_content = new ArrayList<Content>();
		content_adapter = new ContentAdapter(context);
		content_adapter.is_remove_item = true; // 取消赞的时候，把记录删掉

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				// 背景重复出现的bug
				mPullRefreshListView.getChildAt(0).setVisibility(View.INVISIBLE);

				if (NetworkUtil.dataConnected(context)) {
					reloadData();
				} else {
					MyToast.makeText(PraiseActivity.this, context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Add an end-of-list listener
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if (NetworkUtil.dataConnected(context)) {
					if (!have_next) {
						MyToast.makeText(PraiseActivity.this, context.getResources().getString(R.string.is_the_last_page), Toast.LENGTH_LONG).show();
					} else {
						if (loading_task != null) {
							loading_task.cancel(true);
						}
						loading_task = new LoadingContentTask();
						loading_task.execute();
					}
				} else {
					MyToast.makeText(PraiseActivity.this, context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
				}
			}
		});

		ListView actualListView = mPullRefreshListView.getRefreshableView();

		actualListView.setAdapter(content_adapter);
	}
	
	
	
	class LoadingContentTask extends AsyncTask<String, Integer, List<Content>> {
		@Override
		protected List<Content> doInBackground(String... url) {
			ArrayList<Content> list_content = new ArrayList<Content>();
			try {
				String urlPath = PathConst.URL_LIST_PRAISE + "&uid=" + LiuLianApplication.current_user.getUid() + "&limit=" + 20 + "&last_id=" + last_id;

				String dir_offline = FileDownloadUtil.getDefaultLocalDir(PathConst.OFFLINE_DATA);
				File file_local = null;// 本地文件
				String str_json = null;// 本地json数据
				if(last_id.equals("")){		// 第一页
					list_all_content = null;
				}

				if (dir_offline != null) {
					file_local = new File(dir_offline + PathConst.OFFLINE_TOPIC_PRAISE);
				}
				try {
					// 有网取网络
					if (NetworkUtil.dataConnected(context)) {
						str_json = NetworkUtil.getHttpString(urlPath, null, 5000);// 5s延迟
					}
					if (last_id.equals("")) {// 只缓存第一页
						if (str_json != null && file_local != null) {
							FileDownloadUtil.saveStringToLocal(str_json, file_local); // 保存到本地缓存中
						}
					}
					if (str_json == null && file_local.exists() && file_local != null) {
						str_json = FileDownloadUtil.getLocalString(file_local);
					}
					JSONObject json = null;
					if (str_json != null) {
						json = new JSONObject(str_json);
					} else {
						json = NetworkUtil.getJsonObject(urlPath, null, 5000);
					}
					if (json != null) {

						have_next = json.getBoolean("have_next");
						last_id = json.getString("last_id");
						JSONArray array = json.getJSONArray("list");
						int array_length = array.length();
						Content content = null;
						for (int i = 0; i < array_length; i++) {
							JSONObject item_content = array.getJSONObject(i);
							content = new Content();
							content.setId(item_content.getString("id"));
							content.setCreate_time(item_content.getString("create_time"));
							content.setContent(item_content.getString("text_content"));
							content.setPraise_num(item_content.getString("praise_num"));
							content.setCreate_time(item_content.getString("create_time"));
							content.setView_num(item_content.getString("view_num"));
							content.setIs_praised(item_content.getBoolean("is_praise"));
							JSONObject json_user = item_content.getJSONObject("user");
							Users user = new Users();
							user.setUid(json_user.getString("uid"));
							user.setName(json_user.getString("username"));
							user.setImage(json_user.getString("head_pic"));
							user.setSex(json_user.getInt("sex"));
							user.setHx_username(json_user.getString("hx_username"));
							content.setUser(user);
							int type = item_content.getInt("type");
							content.setType(type);
							switch (type) {
							case 2://图片文字
								JSONObject json_pic = item_content.getJSONObject("pic");
								ContentPicture picture = new ContentPicture();
								picture.setLarge(json_pic.getString("large"));
								picture.setSmall(json_pic.getString("small"));
								picture.setHeight(json_pic.optInt("height"));
								picture.setWidth(json_pic.optInt("width"));
								content.setPicture(picture);
								break;
							case 3://音乐
								JSONObject json_music = item_content.getJSONObject("music");
								Music music = new Music();
								music.setId(json_music.getString("id"));
								music.setAuthor(json_music.getString("author"));
								music.setCover(json_music.getString("cover"));
								music.setTitle(json_music.getString("title"));
								music.setAlbum(json_music.getString("album"));
								music.setUrl(json_music.getString("url"));
								content.setMusic(music);
								break;
							case 4:
								JSONObject json_movie = item_content.getJSONObject("movie");
								Movie movie = new Movie();
								movie.setId(json_movie.getString("id"));
								movie.setEnam(json_movie.getString("ename"));
								movie.setCover(json_movie.getString("cover"));
								movie.setCname(json_movie.getString("cname"));
								movie.setUrl(json_movie.getString("url"));
								movie.setRtime(json_movie.getString("rTime"));
								movie.setLocation(json_movie.getString("location"));
								movie.setDesc(json_movie.getString("desc"));
								movie.setDirect(json_movie.getString("direct"));
								movie.setActor(json_movie.getString("actor"));
								content.setMovie(movie);
								break;
							}
							list_content.add(content);
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
			return list_content;
		}

		@Override
		protected void onPostExecute(List<Content> list_content) {
			super.onPostExecute(list_content);
			
			loadingDialog.dismiss();

			LiuLianApplication.is_update_praise = false;
			
			if (list_all_content == null || list_all_content.size() == 0) {// 第一次加载
				list_all_content = list_content;
			} else {
				list_all_content.addAll(list_content);
			}

			if(list_all_content.size()==0){
				mPullRefreshListView.setVisibility(View.GONE);
				bt_refresh.setVisibility(View.GONE);
				layout_blank_tip.setVisibility(View.VISIBLE);
				tip1.setText("你还没有赞过的内容哟");
				tip2.setText("去话题里发现喜欢的内容");
			}else{
				mPullRefreshListView.setVisibility(View.VISIBLE);
				content_adapter.setData(list_all_content);
			}
			mPullRefreshListView.onRefreshComplete();

		}
	}
	
	

}
