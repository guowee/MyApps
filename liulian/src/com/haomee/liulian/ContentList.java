package com.haomee.liulian;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.haomee.util.imageloader.ImageLoaderCharles;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haomee.adapter.ContentAdapter;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.Content;
import com.haomee.entity.ContentPicture;
import com.haomee.entity.Movie;
import com.haomee.entity.Music;
import com.haomee.entity.ShareContent;
import com.haomee.entity.Topic;
import com.haomee.entity.Users;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.util.ViewUtil;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.sina.weibo.sdk.utils.LogUtil;

public class ContentList extends BaseActivity {

	public static final int REQUEST_SEND_CONTENT = 1;
	public static final int RESUlT_SEND_SUCCESS = 2;
	private static final int REQUEST_DETAIL = 1000;

	private Activity activity_context;
	private PullToRefreshListView listview_content;
	private ContentAdapter content_adapter;
	LoadingContentTask loading_content_task;
	private boolean have_next = false;
	private String last_id = "";
	private List<Content> list_all_content;
	private String topic_id = "";
	private ArrayList<Content> list_content;
	private ArrayList<Users> list_view_user;
	public String pic_large = "";
	public String pic_small = "";
	Topic topic;
	private ImageView rotate_choice, rotate_background_choice, bt_back;
	private LinearLayout show_choice, shadow;
	private LinearLayout text, movie, music, picture;
	private Animation animation_in_from_right, animation_out_to_right, animation_rotate_open, animation_rotate_close, animation_show_alpha, animation_disappear_alpha = null;
	private boolean is_open = false;
	private TextView content_title, hint_topic_content;
	private String title;
	private ImageView have_more;
	private int location = 0;
	private LoadingDialog loadingDialog;
	private SharedPreferences preferences_is_first;
	private View view_head;
	private ImageView topic_image;
	private LinearLayout layout_user;
	private Users create_user;
	private TextView tv_create_name;
	private TextView tv_content_count;
	private TextView view_user_num;
	private ImageView iv_content_image;
	private ImageView iv_bg;
	private RelativeLayout rl_head;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content_list);
		activity_context = ContentList.this;
		title = getIntent().getStringExtra("title");

		topic_id = getIntent().getStringExtra("topic_id");
		initview();
		LiuLianApplication.initLocation();

		// 弹出提示
		preferences_is_first = this.getSharedPreferences(CommonConst.PREFERENCES_FIRST, Context.MODE_PRIVATE);
		boolean is_first_open = preferences_is_first.getBoolean("is_first_tip_add_topic", true);
		if (is_first_open) {
			showHelpTip();
		}

	}

	public void initview() {
		hint_topic_content = (TextView) findViewById(R.id.hint_topic_content);

		animation_show_alpha = AnimationUtils.loadAnimation(activity_context, R.anim.alpha_show);
		animation_disappear_alpha = AnimationUtils.loadAnimation(activity_context, R.anim.alpha_disappear);
		animation_in_from_right = AnimationUtils.loadAnimation(activity_context, R.anim.slide_in_from_right);
		animation_out_to_right = AnimationUtils.loadAnimation(activity_context, R.anim.slide_out_to_right);
		animation_rotate_open = AnimationUtils.loadAnimation(activity_context, R.anim.choice_roate_open);
		animation_rotate_close = AnimationUtils.loadAnimation(activity_context, R.anim.choice_roate_close);

		rotate_background_choice = (ImageView) findViewById(R.id.rotate_background_choice);

		text = (LinearLayout) findViewById(R.id.text);
		movie = (LinearLayout) findViewById(R.id.movie);
		music = (LinearLayout) findViewById(R.id.music);
		picture = (LinearLayout) findViewById(R.id.picture);
		text.setOnClickListener(btItemClick);
		movie.setOnClickListener(btItemClick);
		music.setOnClickListener(btItemClick);
		picture.setOnClickListener(btItemClick);
		rotate_choice = (ImageView) findViewById(R.id.rotate_choice);
		rotate_choice.setOnClickListener(btItemClick);
		show_choice = (LinearLayout) findViewById(R.id.show_choice);
		shadow = (LinearLayout) findViewById(R.id.shadow);
		shadow.setOnClickListener(btItemClick);

		listview_content = (PullToRefreshListView) findViewById(R.id.list_content);

		view_head = LayoutInflater.from(activity_context).inflate(R.layout.activity_content_list_head, null);
		content_title = (TextView) view_head.findViewById(R.id.content_title);
		bt_back = (ImageView) view_head.findViewById(R.id.bt_back);
		have_more = (ImageView) view_head.findViewById(R.id.have_more);
		topic_image = (ImageView) view_head.findViewById(R.id.topic_image);
		layout_user = (LinearLayout) view_head.findViewById(R.id.layout_user);
		tv_create_name = (TextView) view_head.findViewById(R.id.tv_create_name);
		tv_content_count = (TextView) view_head.findViewById(R.id.tv_content_count);
		view_user_num = (TextView) view_head.findViewById(R.id.view_user_num);
		iv_content_image = (ImageView) view_head.findViewById(R.id.iv_content_image);
		iv_bg = (ImageView) view_head.findViewById(R.id.iv_bg);
		rl_head = (RelativeLayout) view_head.findViewById(R.id.rl_head);

		listview_content.getRefreshableView().addHeaderView(view_head);
		have_more.setOnClickListener(btItemClick);
		bt_back.setOnClickListener(btItemClick);
		content_adapter = new ContentAdapter(ContentList.this);
		listview_content.getRefreshableView().setAdapter(content_adapter);

		topic_image.setOnClickListener(btItemClick);

		// 添加空白底部，以免被添加按钮挡住
		ListView listView = listview_content.getRefreshableView();
		View footer = new View(this);
		footer.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, ViewUtil.dip2px(this, 90)));
		listView.addFooterView(footer, null, false); // 不可点击
		listview_content.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if (NetworkUtil.dataConnected(activity_context)) {
					if (!have_next) {
						// MyToast.makeText(activity_context,
						// activity_context.getResources().getString(R.string.is_the_last_page),
						// Toast.LENGTH_LONG).show();

					} else {
						if (loading_content_task != null) {
							loading_content_task.cancel(true);
						}
						loading_content_task = new LoadingContentTask();
						loading_content_task.execute();
					}
				} else {
					Toast.makeText(activity_context, "网络不给力", Toast.LENGTH_SHORT).show();
				}
			}
		});

		listview_content.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_FLING:
					/*** 滑动过程中禁止图片加载 ***/
					// mImageLoader.lock();
					/*
					 * rotate_choice.startAnimation(animation_disappear_alpha);
					 * rotate_background_choice
					 * .startAnimation(animation_disappear_alpha);
					 * animation_disappear_alpha.setFillAfter(true);
					 */
					break;
				case OnScrollListener.SCROLL_STATE_IDLE:
					/*** 停止滑动且开始图片加载 ***/
					// mImageLoader.unlock();
					/*
					 * if (!(view.getLastVisiblePosition() ==
					 * (view.getCount()))) {//没有滑到底部
					 * rotate_choice.startAnimation(animation_show_alpha);
					 * rotate_background_choice
					 * .startAnimation(animation_show_alpha);
					 * animation_show_alpha.setFillAfter(true); }
					 */
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					// mImageLoader.lock();
					/*
					 * rotate_choice.startAnimation(animation_disappear_alpha);
					 * rotate_background_choice
					 * .startAnimation(animation_disappear_alpha);
					 * animation_disappear_alpha.setFillAfter(true);
					 */
					break;
				default:
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

				/*
				 * int lastItem = firstVisibleItem + visibleItemCount;
				 * if(lastItem == totalItemCount) {
				 * System.out.println("Scroll to the listview last item"); View
				 * lastItemView=(View)
				 * listview_content.getChildAt(listview_content
				 * .getChildCount()-1); if
				 * ((listview_content.getBottom())==lastItemView.getBottom()) {
				 * rotate_choice.setVisibility(View.GONE);
				 * rotate_background_choice.setVisibility(View.GONE); }else{
				 * rotate_choice.setVisibility(View.VISIBLE);
				 * rotate_background_choice.setVisibility(View.VISIBLE); } }
				 */
			}
		});
		listview_content.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				// 背景重复出现的bug
				listview_content.getChildAt(0).setVisibility(View.INVISIBLE);
				// Do work to refresh the list here.
				if (NetworkUtil.dataConnected(activity_context)) {
					list_all_content = null;
					if (loading_content_task != null) {
						loading_content_task.cancel(true);
					}
					last_id = "";
					if (list_view_user != null) {
						list_view_user = null;
					}
					loading_content_task = new LoadingContentTask();
					loading_content_task.execute();

				} else {
					MyToast.makeText(activity_context, activity_context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
				}
			}
		});

		listview_content.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(list_all_content!=null){
					location = position - 2;
					Intent intent = new Intent();
					intent.putExtra("topic_id", list_all_content.get(position - 2).getId());
					intent.setClass(activity_context, ContentDetailActivity.class);
					activity_context.startActivityForResult(intent, REQUEST_DETAIL);
				}
				
			}
		});

		loadingDialog = new LoadingDialog(this);

		loadingDialog.show();
		loading_content_task = new LoadingContentTask();
		loading_content_task.execute();

		initBlankTip();
	}

	private void showHelpTip() {
		Intent intent = new Intent();
		intent.putExtra("from", "tip_add_topic");
		intent.setClass(this, HelpTipActivity.class);
		this.startActivity(intent);

		Editor editor = preferences_is_first.edit();
		editor.putBoolean("is_first_tip_add_topic", false);
		editor.commit();
	}

	public OnClickListener btItemClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent_send = new Intent();
			switch (v.getId()) {
			case R.id.have_more:
				if (!NetworkUtil.dataConnected(activity_context)) {
					MyToast.makeText(activity_context, activity_context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
				} else {

					if (list_all_content == null) {
						MyToast.makeText(activity_context, "请刷新页面", Toast.LENGTH_SHORT).show();
					} else {
						intent_send.setClass(activity_context, ShareActivity.class);
						ShareContent share = new ShareContent();
						share.setId(topic.getId());
						share.setTitle(topic.getTitle());
						share.setDesc(topic.getDesc());

						String summary = topic.getContent() == null ? "" : topic.getContent();
						share.setSummary(summary);
						share.setImg_url(topic.getPic());
						share.setRedirect_url(CommonConst.URL_SHARE_TOPIC + topic.getId() + ".html");
						intent_send.putExtra("share", share);
						intent_send.putExtra("isFav", topic.isFav());
						intent_send.putExtra("have_report", true);
						activity_context.startActivity(intent_send);
						StatService.onEvent(activity_context, "content_list_top_more", "话题详情页顶部菜单点击次数", 1);
					}
				}
				break;
			case R.id.bt_back:
				activity_context.finish();
				break;
			case R.id.rotate_choice:
				if (!is_open) {
					rotate_choice.startAnimation(animation_rotate_open);
					animation_rotate_open.setFillAfter(true);
					show_choice.startAnimation(animation_in_from_right);
					animation_in_from_right.setFillAfter(true);
					show_choice.setVisibility(View.VISIBLE);
					shadow.setVisibility(View.VISIBLE);
					is_open = true;
					StatService.onEvent(activity_context, "content_list_bottom_open", "话题详情页底部按钮展开点击次数", 1);
				} else {
					rotate_choice.startAnimation(animation_rotate_close);
					rotate_choice.setImageResource(R.drawable.content_button_menu_pink);
					show_choice.startAnimation(animation_out_to_right);
					show_choice.setVisibility(View.INVISIBLE);
					shadow.setVisibility(View.GONE);
					is_open = false;
				}
				break;
			case R.id.shadow:
				rotate_choice.startAnimation(animation_rotate_close);
				rotate_choice.setImageResource(R.drawable.content_button_menu_pink);
				show_choice.startAnimation(animation_out_to_right);
				show_choice.setVisibility(View.INVISIBLE);
				shadow.setVisibility(View.GONE);
				is_open = false;
				break;
			case R.id.text:
				setCloseAnim();
				Intent intent_text = new Intent();
				intent_text.setClass(ContentList.this, SendTextContent.class);
				intent_text.putExtra("topic_id", topic_id);
				intent_text.putExtra("title", title);
				ContentList.this.startActivityForResult(intent_text, REQUEST_SEND_CONTENT);
				StatService.onEvent(activity_context, "content_list_bottom_text", "话题详情页底部文本点击次数", 1);
				break;
			case R.id.movie:
				setCloseAnim();
				intent_send.putExtra("topic_id", topic_id);
				intent_send.putExtra("title", title);
				intent_send.setClass(activity_context, SearchMovie.class);
				startActivity(intent_send);
				StatService.onEvent(activity_context, "content_list_bottom_movie", "话题详情页底部电影点击次数", 1);

				break;
			case R.id.music:
				setCloseAnim();
				intent_send.putExtra("topic_id", topic_id);
				intent_send.putExtra("title", title);
				intent_send.setClass(activity_context, SearchMusic.class);
				startActivity(intent_send);
				StatService.onEvent(activity_context, "content_list_bottom_music", "话题详情页底部图片点击次数", 1);

				break;
			case R.id.picture:
				setCloseAnim();
				intent_send.putExtra("topic_id", topic_id);
				intent_send.putExtra("title", title);
				intent_send.setClass(activity_context, SearchImage.class);
				startActivity(intent_send);
				StatService.onEvent(activity_context, "content_list_bottom_picture", "话题详情页底部图片点击次数", 1);
				break;
			// 话题创建
			case R.id.topic_image:
				StatService.onEvent(ContentList.this, "content_list_icon", "内容列表页访问人头像点击次数", 1);
				Intent intent = new Intent();
				intent.setClass(ContentList.this, ImgsBrowseActivity_users.class);
				intent.putExtra("users", list_view_user);
				intent.putExtra("index", 0);
				startActivity(intent);
				break;
			}
		}
	};

	private View layout_blank_tip;
	private TextView tip1, tip2, bt_refresh;

	private void initBlankTip() {
		layout_blank_tip = this.findViewById(R.id.layout_blank_tip);
		tip1 = (TextView) layout_blank_tip.findViewById(R.id.tip1);
		tip2 = (TextView) layout_blank_tip.findViewById(R.id.tip2);
		bt_refresh = (TextView) layout_blank_tip.findViewById(R.id.bt_refresh);
		hideBlankTip();
	}

	// 空白页提示
	public void showBlankTip(String t1, String t2, boolean bt_refresh_visible) {
		layout_blank_tip.setVisibility(View.VISIBLE);

		if (LiuLianApplication.height_fragment_liulian > 0) {
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layout_blank_tip.getLayoutParams();
			params.height = LiuLianApplication.height_fragment_liulian;
			layout_blank_tip.setLayoutParams(params);
		}

		tip1.setText(t1);
		tip2.setText(t2);

		bt_refresh.setVisibility(bt_refresh_visible ? View.VISIBLE : View.GONE);
	}

	public void hideBlankTip() {
		layout_blank_tip.setVisibility(View.GONE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SEND_CONTENT || resultCode == REQUEST_SEND_CONTENT) { // 发送成功之后，刷新界面
			list_all_content = null;
			if (loading_content_task != null) {
				loading_content_task.cancel(true);
			}
			last_id = "";
			loading_content_task = new LoadingContentTask();
			loading_content_task.execute();

		} else if (requestCode == REQUEST_DETAIL) {
			if (data != null) {
				Content content = (Content) data.getSerializableExtra("content");
				if (content != null) {
					list_all_content.get(location).setPraise_num(content.getPraise_num());
					list_all_content.get(location).setIs_praised(content.isIs_praised());
					content_adapter.setData(list_all_content);
				}
			}
		}
	}

	/** 加载最新话题列表 */
	class LoadingContentTask extends AsyncTask<String, Integer, List<Content>> {
		@Override
		protected List<Content> doInBackground(String... url) {
			list_content = new ArrayList<Content>();
			if (list_view_user == null) {
				list_view_user = new ArrayList<Users>();
			}
			try {

				// Thread.sleep(3000);

				String uid = LiuLianApplication.current_user == null ? "" : LiuLianApplication.current_user.getUid();

				String urlPath = PathConst.URL_CONTENT_LIST + "&id=" + topic_id + "&uid=" + uid + "&last_id=" + last_id + "&limit=10";

				LogUtil.e("地址", urlPath + "");

				String dir_offline = FileDownloadUtil.getDefaultLocalDir(PathConst.OFFLINE_DATA);
				File file_local = null;// 本地文件
				String str_json = null;// 本地json数据
				if (dir_offline != null) {
					file_local = new File(dir_offline + topic_id + PathConst.OFFLINE_CONTENT);// 保存最新话题的本地json的文件
				}
				try {
					// 有网取网络
					if (NetworkUtil.dataConnected(activity_context)) {
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
						if (last_id == "") {
							topic = new Topic();
							JSONObject info = json.getJSONObject("info");
							topic.setId(info.getString("id"));
							topic.setTitle(info.getString("title"));
							topic.setDesc(info.getString("desc"));
							topic.setCreate_time(info.getString("create_time"));
							topic.setInput_type(info.getString("input_type"));
							topic.setContent_table_id(info.getString("content_table_id"));
							topic.setContent_num(info.getString("content_num"));
							topic.setUser_num(info.optInt("user_num"));
							topic.setView_range(info.getString("view_range"));
							topic.setLocation_x(info.getString("location_x"));
							topic.setLocation_y(info.getString("location_y"));
							topic.setIcon(info.getString("icon"));
							topic.setUpdate_time(info.getString("update_time"));
							topic.setFav(info.getBoolean("is_focus"));
							topic.setBack_img(info.getString("back_img"));
							topic.setView_user_num(info.getInt("view_user_num"));

							JSONObject json_create_user = info.getJSONObject("create_user");
							create_user = new Users();
							create_user.setUid(json_create_user.optString("uid"));
							create_user.setName(json_create_user.optString("username"));
							create_user.setImage(json_create_user.optString("head_pic"));
							create_user.setSex(Integer.parseInt(json_create_user.optString("sex")));
							create_user.setCity(json_create_user.optString("city"));
							create_user.setBack_pic(json_create_user.optString("back_pic"));
							create_user.setIs_sayhi(json_create_user.optBoolean("is_sayHi"));
							create_user.setHx_username(json_create_user.optString("hx_username"));

							JSONArray view_users_array = info.getJSONArray("view_users");
							int view_users_length = view_users_array.length();
							list_view_user.clear();
							for (int i = 0; i < view_users_length; i++) {
								Users view_users = new Users();
								JSONObject item_user = view_users_array.getJSONObject(i);
								view_users.setUid(item_user.optString("uid"));
								view_users.setName(item_user.optString("username"));
								view_users.setHx_username(item_user.optString("hx_username"));
								view_users.setImage(item_user.optString("head_pic"));
								view_users.setSex(item_user.optInt("sex"));
								view_users.setAge(item_user.optString("age"));
								view_users.setCity(item_user.optString("city"));
								view_users.setBack_pic(item_user.optString("back_pic"));
								view_users.setIs_sayhi(item_user.optBoolean("is_sayHi"));
								list_view_user.add(view_users);
							}
							list_view_user.add(0, create_user);

						}
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
							case 2:// 图片文字
								JSONObject json_pic = item_content.getJSONObject("pic");
								ContentPicture picture = new ContentPicture();
								picture.setLarge(json_pic.getString("large"));
								picture.setSmall(json_pic.getString("small"));
								picture.setHeight(json_pic.optInt("height"));
								picture.setWidth(json_pic.optInt("width"));
								content.setPicture(picture);
								break;
							case 3:// 音乐
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
							case 4:// 电影
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
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list_content;
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(List<Content> list_content) {
			loadingDialog.dismiss();
			if (list_content.size() == 0) {// 网络加载延迟
				listview_content.setVisibility(View.GONE);
			} else {
				if (list_all_content == null || list_all_content.size() == 0) {// 第一次加载
					hint_topic_content.setText(topic.getDesc());
					list_all_content = list_content;
				} else {
					list_all_content.addAll(list_content);
				}
				content_title.setText(title);
				content_adapter.setData(list_all_content);
				listview_content.setVisibility(View.VISIBLE);
				listview_content.onRefreshComplete();
			}
			if (list_all_content == null || list_all_content.size() == 0) {
				showBlankTip("一个新的话题被发现了", "赶快发言吧！", false);
			} else {
				hideBlankTip();
			}
			initHead();
		}
	}

	public void initHead() {

		if (create_user != null) {

			tv_create_name.setText("发起人：" + create_user.getName());

			tv_content_count.setText("内容数：" + topic.getContent_num());

			view_user_num.setText("访问人数：" + topic.getView_user_num());


            ImageLoaderCharles.getInstance(activity_context).addTask(topic.getIcon(), iv_content_image);

			int width = ViewUtil.getScreenWidth(activity_context);
			int height = rl_head.getHeight();
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
			iv_bg.setLayoutParams(params);

            ImageLoaderCharles.getInstance(activity_context).addTask(topic.getBack_img(), iv_bg);

            ImageLoaderCharles.getInstance(activity_context).addTask(create_user.getImage(), topic_image);

			if (list_view_user != null) {
				LayoutInflater inflater = activity_context.getLayoutInflater();
				layout_user.removeAllViews();
				for (int i = 1; i < list_view_user.size(); i++) {
					Users user = list_view_user.get(i);
					View item = inflater.inflate(R.layout.user_item, null);
					ImageView img = (ImageView) item.findViewById(R.id.iv_icon);
                    ImageLoaderCharles.getInstance(activity_context).addTask(user.getImage(), img);
					item.setTag(R.id.content_list_item, i);
					item.setOnClickListener(itemListener);
					layout_user.addView(item);

				}
			}
		}
	}

	OnClickListener itemListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (NetworkUtil.dataConnected(ContentList.this)) {

				int index = (Integer) v.getTag(R.id.content_list_item);
				if (list_view_user.get(index).getUid().equals(LiuLianApplication.current_user.getUid())) {

					Toast.makeText(ContentList.this, "这是你自己哦", Toast.LENGTH_SHORT).show();

				} else {
					StatService.onEvent(ContentList.this, "content_list_icon", "内容列表页访问人头像点击次数", 1);
					Intent intent = new Intent();
					intent.setClass(ContentList.this, ImgsBrowseActivity_users.class);
					intent.putExtra("users", list_view_user);
					intent.putExtra("index", index);
					startActivity(intent);

				}

			} else {
				MyToast.makeText(ContentList.this, ContentList.this.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			}
		}
	};

	public void setCloseAnim() {
		rotate_choice.startAnimation(animation_rotate_close);
		rotate_choice.setImageResource(R.drawable.content_button_menu_pink);

		show_choice.startAnimation(animation_out_to_right);
		show_choice.setVisibility(View.INVISIBLE);

		shadow.setVisibility(View.GONE);
		is_open = false;
	}

	/*
	 * private Handler handler_timer = new Handler() { public void
	 * handleMessage(Message msg) {
	 * 
	 * RoundProgressBar progressBar = content_adapter.getCurrentProgressBar();
	 * if (progressBar != null) {
	 * 
	 * int position = MyMusicPlayer.getInstance().getCurrentPosition(); int
	 * duration = MyMusicPlayer.getInstance().getDuration();
	 * 
	 * if (duration > 0) { int progress = position * 100 / duration;
	 * progressBar.setProgress(progress); }
	 * 
	 * }
	 * 
	 * handler_timer.sendEmptyMessageDelayed(0, 1000);
	 * 
	 * }; };
	 * 
	 * @Override protected void onResume() { super.onResume();
	 * 
	 * handler_timer.sendEmptyMessage(0); };
	 * 
	 * @Override protected void onDestroy() { super.onDestroy();
	 * 
	 * handler_timer.removeMessages(0); };
	 */

}
