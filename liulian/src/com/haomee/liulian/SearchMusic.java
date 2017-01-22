package com.haomee.liulian;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.haomee.adapter.SearchMusicAdapter;
import com.haomee.consts.PathConst;
import com.haomee.entity.Music;
import com.haomee.view.HintEditText;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SearchMusic extends BaseActivity {

	private EditText search_content;
	private ListView search_result;
	private SearchMusicAdapter searchMusicAdapter;
	private List<Music> list_music;
	private Button bt_search;
	private TextView tv_hot;

	private String topic_id;
	private String title, content;
	private boolean is_loading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_music);

		topic_id = getIntent().getStringExtra("topic_id");
		title = getIntent().getStringExtra("title");
		content = getIntent().getStringExtra("content");
		search_content = (EditText) findViewById(R.id.content_title);
		search_content.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);
		search_result = (ListView) findViewById(R.id.list_result);
		tv_hot = (TextView) findViewById(R.id.tv_hot);

		searchMusicAdapter = new SearchMusicAdapter(this);
		search_result.setAdapter(searchMusicAdapter);
		list_music = new ArrayList<Music>();
		bt_search = (Button) findViewById(R.id.bt_search);

		findViewById(R.id.bt_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		bt_search.setOnClickListener(new OnClickListener() {
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				if(!is_loading){
					is_loading = true;
					String text = search_content.getText().toString().trim();
					if (!"".equals(text)) {
						tv_hot.setVisibility(View.GONE);
						search(text);
						((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(SearchMusic.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					} else {
						MyToast.makeText(SearchMusic.this, "请输入行搜索的歌曲", 1).show();
					}
				}else{
					MyToast.makeText(SearchMusic.this, "正在加载数据", 1).show();
				}
			}
		});

		search_result.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String pic = list_music.get(position).getCover();
				String music_id = list_music.get(position).getId();
				Intent intent = new Intent();
				intent.putExtra("topic_id", topic_id);
				intent.putExtra("title", title);
				intent.putExtra("content", content);
				intent.putExtra("head_pic", pic);
				intent.putExtra("music_id", music_id);
				intent.putExtra("name", list_music.get(position).getTitle() + "  " + list_music.get(position).getAuthor());
				intent.setClass(SearchMusic.this, SendMusicContent.class);
				SearchMusic.this.startActivity(intent);
			}
		});
		initHotMusic();
	}

	//热门关键词

	public void initHotMusic() {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		String url = PathConst.URL_SEARCH_HOT_MUSIC;
		Log.e("请求地址", url + "");
		asyncHttp.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					Log.e("返回数据", arg0 + "");
					if(arg0==null||arg0.equals("")){
						return;
					}
					JSONArray json_arr = new JSONArray(arg0);
					if (json_arr.length() != 0) {
						for (int i = 0; i < json_arr.length(); i++) {
							JSONObject json_m = json_arr.getJSONObject(i);
							Music m = new Music();
							m.setId(json_m.optString("id"));
							m.setTitle(json_m.optString("title"));
							m.setAuthor(json_m.optString("author"));
							m.setAlbum(json_m.optString("album"));
							m.setCover(json_m.optString("cover"));
							m.setUrl(json_m.optString("url"));
							m.setPlaytime(json_m.optString("playtime"));
							list_music.add(m);
						}
						searchMusicAdapter.setData(list_music);
					}else{
						Toast.makeText(SearchMusic.this, "请输入要查询的音乐", 1).show();
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@SuppressWarnings("deprecation")
	public void search(String text_content) {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		String url = PathConst.URL_SEARCH_MUSIC + "&title=" + URLEncoder.encode(text_content);
		Log.e("请求地址", url + "");
		asyncHttp.get(url, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					list_music.clear();
					JSONObject json_object = new JSONObject(arg0);
					JSONArray json_arr = new JSONArray(json_object.optString("list"));
					if (json_arr.length() != 0) {
						for (int i = 0; i < json_arr.length(); i++) {
								JSONObject json_m = json_arr.getJSONObject(i);
								Music m = new Music();
								m.setId(json_m.optString("id"));
								m.setTitle(json_m.optString("title"));
								m.setAuthor(json_m.optString("author"));
								m.setAlbum(json_m.optString("album"));
								m.setCover(json_m.optString("cover"));
								m.setUrl(json_m.optString("url"));
								m.setPlaytime(json_m.optString("playtime"));
								list_music.add(m);
							}
						}else{
							MyToast.makeText(SearchMusic.this, "没有搜索到相关的音乐", 1).show();
					}
					searchMusicAdapter.setData(list_music);
					is_loading = false;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
