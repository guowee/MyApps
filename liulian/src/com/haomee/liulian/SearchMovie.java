package com.haomee.liulian;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.haomee.adapter.SearchMovieAdapter;
import com.haomee.consts.PathConst;
import com.haomee.entity.Movie;
import com.haomee.view.HintEditText;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SearchMovie extends BaseActivity {

	private EditText search_content;
	private ListView search_result;
	private SearchMovieAdapter searchMovieAdapter;
	private List<Movie> list_movie;
	private Button bt_search;
	private TextView tv_hot;
	private String topic_id;
	private String title, content;
	private boolean is_loading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_movie);

		search_content = (EditText) findViewById(R.id.content_title);
		search_content.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);

		search_result = (ListView) findViewById(R.id.list_result);
		tv_hot = (TextView) findViewById(R.id.tv_hot);

		topic_id = getIntent().getStringExtra("topic_id");
		title = getIntent().getStringExtra("title");
		content = getIntent().getStringExtra("content");
		
		list_movie = new ArrayList<Movie>();
		searchMovieAdapter = new SearchMovieAdapter(this);
		search_result.setAdapter(searchMovieAdapter);
		bt_search = (Button) findViewById(R.id.bt_search);
		findViewById(R.id.bt_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		bt_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!is_loading) {
					is_loading = true;
					list_movie.clear();
					String text = search_content.getText().toString().trim();
					if (!"".equals(text)) {
						tv_hot.setVisibility(View.GONE);
						search(text);
						((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(SearchMovie.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					} else {
						MyToast.makeText(SearchMovie.this, "请输入行搜索的电影", 1).show();
					}
				} else {
					MyToast.makeText(SearchMovie.this, "正在加载数据", 1).show();
				}
			}
		});

		search_result.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String pic = list_movie.get(position).getCover();
				String music_id = list_movie.get(position).getId();
				Intent intent = new Intent();
				intent.putExtra("topic_id", topic_id);
				intent.putExtra("title", title);
				intent.putExtra("content", content);
				intent.putExtra("head_pic", pic);
				intent.putExtra("movie_id", music_id);
				intent.setClass(SearchMovie.this, SendMovieContent.class);
				SearchMovie.this.startActivity(intent);
			}
		});

		initHotMovice();
	}

	//热门电影关键词

	public void initHotMovice() {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		String url = PathConst.URL_SEARCH_HOT_MOVIE;
		Log.e("请求地址", url + "");
		asyncHttp.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					Log.e("返回数据", arg0 + "");
					JSONArray json_arr = new JSONArray(arg0);
					if (json_arr.length() != 0) {
						for (int i = 0; i < json_arr.length(); i++) {
							JSONObject json_m = json_arr.getJSONObject(i);
							Movie m = new Movie();
							m.setId(json_m.optString("id"));
							m.setEnam(json_m.optString("ename"));
							m.setCname(json_m.optString("cname"));
							m.setActor(json_m.optString("actor"));
							m.setUrl(json_m.optString("url"));
							m.setCover(json_m.optString("cover"));
							list_movie.add(m);
						}
						searchMovieAdapter.setData(list_movie);
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
		String url = PathConst.URL_SEARCH_MOVIE + "&title=" + URLEncoder.encode(text_content);
		Log.e("请求地址", url + "");
		asyncHttp.get(url, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					Log.e("返回数据", arg0 + "");
					JSONObject json_object = new JSONObject(arg0);
					JSONArray json_arr = json_object.getJSONArray("list");
					list_movie.clear();
					for (int i = 0; i < json_arr.length(); i++) {
						JSONObject json_m = json_arr.getJSONObject(i);
						Movie m = new Movie();
						m.setId(json_m.optString("id"));
						m.setEnam(json_m.optString("ename"));
						m.setCname(json_m.optString("cname"));
						m.setActor(json_m.optString("actor"));
						m.setUrl(json_m.optString("url"));
						m.setCover(json_m.optString("cover"));
						list_movie.add(m);
					}
					searchMovieAdapter.setData(list_movie);
					is_loading = false;
					if (json_arr.length() == 0) {
						MyToast.makeText(SearchMovie.this, "没有搜到相应的电影", 1).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
