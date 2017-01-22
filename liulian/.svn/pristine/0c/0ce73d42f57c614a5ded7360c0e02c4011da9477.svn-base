package com.haomee.liulian;

import java.util.ArrayList;
import java.util.List;

import com.haomee.util.imageloader.ImageLoaderCharles;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.haomee.adapter.LookUserAdapter;
import com.haomee.adapter.ZanUserAdapter;
import com.haomee.chat.activity.ChatActivity;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.Content;
import com.haomee.entity.ContentPicture;
import com.haomee.entity.Movie;
import com.haomee.entity.Music;
import com.haomee.entity.ShareContent;
import com.haomee.entity.Users;
import com.haomee.player.MediaPlayerActivity;
import com.haomee.player.MyMusicPlayer;
import com.haomee.util.NetworkUtil;
import com.haomee.util.ViewUtil;
import com.haomee.view.CircleImageView;
import com.haomee.view.MyToast;
import com.haomee.view.RoundProgressBar;
import com.haomee.view.UnScrollableGridView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sina.weibo.sdk.utils.LogUtil;

public class ContentDetailActivity extends BaseActivity implements OnClickListener {

	private ImageView item_image;
	private TextView m_name, m_director, m_actor;
	private com.haomee.view.CircleImageView user_img;
	private TextView user_name;
	private TextView start_session;
	private TextView item_create_time;
	private TextView view_num;
	private TextView item_praise;
	private ImageView bt_love;
	private ImageView bt_share, bt_delete;
	private com.haomee.view.RoundProgressBar bt_play;
	private ImageView bt_chat;
	private Context activity_context;
	private TextView item_content;
	private ImageView bt_back;
	private LinearLayout linear_mm;
	private int margin_image;
	private DisplayMetrics dm;
	private Content content;
	private UnScrollableGridView zan_gridview, look_gridview;
	private ZanUserAdapter zanUserAdapter;
	private LookUserAdapter lookUserAdapter;
	// 获取当前进度条

	private RoundProgressBar progressBar;
	private TextView content_title;
	private ArrayList<Users> zan_list,lookList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_detail);
		activity_context = this;
		zan_list = new ArrayList<Users>();
		lookList = new ArrayList<Users>();
		
		margin_image = ViewUtil.dip2px(activity_context, 10);
		zanUserAdapter = new ZanUserAdapter(activity_context);
		lookUserAdapter = new LookUserAdapter(activity_context);
		initView();
		bt_back = (ImageView) findViewById(R.id.bt_back);
		bt_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		getData(getIntent().getStringExtra("topic_id"));

	}

	public void initView() {

		content_title = (TextView) findViewById(R.id.content_title);
		item_image = (ImageView) findViewById(R.id.item_image);
		bt_play = (RoundProgressBar) findViewById(R.id.bt_play);
		m_name = (TextView) findViewById(R.id.m_name);
		m_director = (TextView) findViewById(R.id.m_director);
		m_actor = (TextView) findViewById(R.id.m_actor);
		user_img = (CircleImageView) findViewById(R.id.user_img);
		user_name = (TextView) findViewById(R.id.user_name);
		start_session = (TextView) findViewById(R.id.start_session);
		item_create_time = (TextView) findViewById(R.id.item_create_time);
		view_num = (TextView) findViewById(R.id.view_num);
		item_praise = (TextView) findViewById(R.id.item_praise);
		bt_love = (ImageView) findViewById(R.id.bt_love);
		bt_share = (ImageView) findViewById(R.id.bt_share);
		bt_delete = (ImageView) findViewById(R.id.bt_delete);
		bt_chat = (ImageView) findViewById(R.id.bt_chat);
		item_content = (TextView) findViewById(R.id.item_content);
		linear_mm = (LinearLayout) findViewById(R.id.linear_mm);
		zan_gridview = (UnScrollableGridView) findViewById(R.id.zan_gridview);

		look_gridview = (UnScrollableGridView) findViewById(R.id.look_gridview);
		look_gridview.setFocusable(false);
		look_gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		zan_gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		bt_play.setOnClickListener(this);
		user_img.setOnClickListener(this);
		start_session.setOnClickListener(this);
		bt_love.setOnClickListener(this);
		bt_share.setOnClickListener(this);
		bt_delete.setOnClickListener(this);
		bt_chat.setOnClickListener(this);
		
	}

	public void initData(final Content content) {

		if (content != null) {
			int type = content.getType();
			if (type == 1) {
				linear_mm.setVisibility(View.GONE);
				item_image.setVisibility(View.GONE);
				bt_play.setVisibility(View.GONE);

			} else if (type == 2) {
				dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				linear_mm.setVisibility(View.GONE);
				item_image.setVisibility(View.VISIBLE);
				bt_play.setVisibility(View.GONE);
				item_content.setText(content.getText_content());
				ContentPicture pic = content.getPicture();
				// 控制图片宽高
				int width = dm.widthPixels;
				if (width > 0 && pic.getWidth() > 0) {
					int height = pic.getHeight() * (width - margin_image * 2) / pic.getWidth();
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
					params.setMargins(margin_image, margin_image, margin_image, 0);
					item_image.setLayoutParams(params);
				}

                ImageLoaderCharles.getInstance(ContentDetailActivity.this).addTask(content.getPicture().getLarge(), item_image);
				item_image.setOnClickListener(this);

			} else if (type == 3) {
				linear_mm.setVisibility(View.VISIBLE);
				item_image.setVisibility(View.VISIBLE);
				bt_play.setVisibility(View.VISIBLE);
				dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				Music music = content.getMusic();

				if (MyMusicPlayer.getInstance().isCurrentMusic(music)) {
					progressBar = bt_play;
					bt_play.setProgress(MyMusicPlayer.getInstance().getProgress());

					MyMusicPlayer.getInstance().setProgressBar(progressBar);

					if (MyMusicPlayer.getInstance().getStatus() == MyMusicPlayer.STATUS_PLAYING) {
						bt_play.setImageResource(R.drawable.content_button_music_pause);
					} else {
						bt_play.setImageResource(R.drawable.content_button_music_start);
					}

					MyMusicPlayer.getInstance().setProgressBar(progressBar);

				} else {
					bt_play.setProgress(0);
					bt_play.setImageResource(R.drawable.content_button_music_start);
				}

				int width = dm.widthPixels - 10;
				if (width > 0) {
					int height = width - margin_image * 2; // 1:1
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
					params.setMargins(margin_image, margin_image, margin_image, 0);
					item_image.setLayoutParams(params);
				}
                ImageLoaderCharles.getInstance(ContentDetailActivity.this).addTask(music.getCover(), item_image);
				m_name.setText("歌曲:" + music.getTitle());
				m_director.setText("歌手:" + music.getAuthor());
				m_actor.setText("专辑名称:" + music.getAlbum());

			} else if (type == 4) {
				linear_mm.setVisibility(View.VISIBLE);
				item_image.setVisibility(View.VISIBLE);
				dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				Movie movie = content.getMovie();
				int width = dm.widthPixels - 10;
				if (width > 0) {
					int height = (width - margin_image * 2) / 2 * 3; // 1:1
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
					params.setMargins(margin_image, margin_image, margin_image, 0);
					item_image.setLayoutParams(params);
				}

                ImageLoaderCharles.getInstance(ContentDetailActivity.this).addTask(movie.getCover(), item_image);
				m_name.setText("电影:" + movie.getCname() + " (" + movie.getEnam() + ") " + movie.getRtime() + " " + movie.getLocation());
				m_director.setText("导演:" + movie.getDirect());
				m_actor.setText("演员:" + movie.getActor());
				
				if(movie.getUrl().equals("")){
					
					bt_play.setVisibility(View.GONE);
				
				}else{
					
					bt_play.setVisibility(View.VISIBLE);
				}

			}
			if (content.getUser().getUid().equals(LiuLianApplication.current_user.getUid())) {
				bt_delete.setVisibility(View.VISIBLE);
				bt_chat.setVisibility(View.GONE);
			} else {
				bt_delete.setVisibility(View.GONE);
				bt_chat.setVisibility(View.VISIBLE);
			}

			if (!content.getText_content().equals("")) {
				item_content.setVisibility(View.VISIBLE);
				item_content.setText(content.getText_content());
			} else {
				item_content.setVisibility(View.GONE);
			}

			item_create_time.setText(content.getCreate_time());
			view_num.setText(content.getView_num());
			item_praise.setText(content.getPraise_num());
			if (content.isIs_praised()) {
				bt_love.setImageResource(R.drawable.content_button_like_pressed);
			} else {
				bt_love.setImageResource(R.drawable.content_button_like_default);
			}
			Users user = content.getUser();
			user_name.setText(user.getName());
            ImageLoaderCharles.getInstance(ContentDetailActivity.this).addTask(user.getImage(),user_img);

			if (content.getList_view() != null && content.getList_view().size() > 0) {
				lookList = content.getList_view() ;
				lookUserAdapter.setData(lookList);
				look_gridview.setAdapter(lookUserAdapter);
			} else {
				look_gridview.setVisibility(View.GONE);
				findViewById(R.id.text_view).setVisibility(View.GONE);
			}
			if (content.getList_praise() != null && content.getList_praise().size() > 0) {
				zan_list = content.getList_praise();
				zanUserAdapter.setData(zan_list);
				zan_gridview.setAdapter(zanUserAdapter);
			} else {
				zan_gridview.setVisibility(View.GONE);
				findViewById(R.id.text_praise).setVisibility(View.GONE);
			}
		}

		zan_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				
				if(zan_list.get(position).getUid().equals(LiuLianApplication.current_user.getUid())){
					
					Toast.makeText(ContentDetailActivity.this, "这是你自己哦", Toast.LENGTH_SHORT).show();
					
				}else{
				
					Intent intent = new Intent();
					intent.setClass(ContentDetailActivity.this, ImgsBrowseActivity_users.class);
					intent.putExtra("users", zan_list);
					intent.putExtra("index", position);
					startActivity(intent);
				}

				StatService.onEvent(activity_context, "content_detail_praise_icon", "内容详情页赞过人的头像点击次数", 1);

			}
		});
		look_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				if(lookList.get(position).getUid().equals(LiuLianApplication.current_user.getUid())){
					
					Toast.makeText(ContentDetailActivity.this, "这是你自己哦", Toast.LENGTH_SHORT).show();
					
				}else{
				
					Intent intent = new Intent();
					intent.setClass(ContentDetailActivity.this, ImgsBrowseActivity_users.class);
					intent.putExtra("users", lookList);
					intent.putExtra("index", position);
					startActivity(intent);
				}
				
			

				StatService.onEvent(activity_context, "content_detail_view_icon", "内容详情页赞过人的头像点击次数", 1);
			}
		});

	}

	private Button bt_commit, bt_cancel;
	private AlertDialog alertDialog;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.bt_delete:
			alertDialog = new AlertDialog.Builder(activity_context).create();
			alertDialog.show();
			Window window = alertDialog.getWindow();
			window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			window.setContentView(R.layout.dialog_del);
			bt_commit = (Button) window.findViewById(R.id.bt_commit);
			bt_cancel = (Button) window.findViewById(R.id.bt_cancel);
			bt_commit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					delete_content(content);//删除内容
					alertDialog.dismiss();
					Intent intent = new Intent();
					setResult(ContentList.REQUEST_SEND_CONTENT, intent);
					ContentDetailActivity.this.finish();
				}
			});
			bt_cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.dismiss();
				}
			});
			break;
		case R.id.bt_chat:
			//发起会话
			intent.setClass(activity_context, ChatActivity.class);
			intent.putExtra("uId", content.getUser().getUid());//聊天对象的uid
			intent.putExtra("userId", content.getUser().getHx_username());//聊天对象的环信ID
			intent.putExtra("nickname", content.getUser().getName());//聊天对象的昵称
			intent.putExtra("attachmentContent", content.getText_content());
			intent.putExtra("attachmentId", content.getId());
			intent.putExtra("is_from_content", true);
			//文字
			//图文
			switch (content.getType()) {
			case 1:
				intent.putExtra("attachmentImage", "");
				intent.putExtra("attachmentTitle", "");
				break;
			case 2:
				intent.putExtra("attachmentTitle", "");
				intent.putExtra("attachmentImage", content.getPicture().getSmall() + "!300");
				break;
			case 3://音乐
				intent.putExtra("attachmentTitle", content.getMusic().getTitle());
				intent.putExtra("attachmentImage", content.getMusic().getCover());
				break;
			case 4://电影
				intent.putExtra("attachmentTitle", content.getMovie().getCname());
				intent.putExtra("attachmentImage", content.getMovie().getCover());
				break;
			}
			activity_context.startActivity(intent);

			break;

		case R.id.bt_play:

			if (content.getType() == 3) {
				// 恢复以前的
				if (progressBar != null && progressBar != v) {
					progressBar.setProgress(0);
					progressBar.setImageResource(R.drawable.content_button_music_start);
				}
				progressBar = (RoundProgressBar) v;
				MyMusicPlayer.getInstance().setProgressBar(progressBar);
				Music music = content.getMusic();
				if (MyMusicPlayer.getInstance().isCurrentMusic(music)) {
					if (MyMusicPlayer.getInstance().getStatus() == MyMusicPlayer.STATUS_PLAYING) {
						bt_play.setImageResource(R.drawable.content_button_music_start);
						MyMusicPlayer.getInstance().pause();
					} else if (MyMusicPlayer.getInstance().getStatus() == MyMusicPlayer.STATUS_PAUSED) {
						bt_play.setImageResource(R.drawable.content_button_music_pause);
						MyMusicPlayer.getInstance().play();
					}
				} else {

					final RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					rotateAnimation.setDuration(1000);
					rotateAnimation.setRepeatCount(-1);
					rotateAnimation.setInterpolator(new LinearInterpolator());

					v.startAnimation(rotateAnimation);

					MyMusicPlayer.getInstance().setOnPreparedListener(new OnPreparedListener() {
						@Override
						public void onPrepared(MediaPlayer mp) {
							rotateAnimation.cancel();

							bt_play.setImageResource(R.drawable.content_button_music_pause);
						}
					});

					boolean success = MyMusicPlayer.getInstance().start(music);
					if (!success) {
						Toast.makeText(activity_context, "播放失败", Toast.LENGTH_SHORT).show();
						rotateAnimation.cancel();
					}
				}
			}else if(content.getType() ==4){
				
				MyMusicPlayer.getInstance().pause(); // 如果有音乐先暂停
				intent.setClass(activity_context, MediaPlayerActivity.class);
				intent.putExtra("movie", content.getMovie());
				activity_context.startActivity(intent);
				
			}
			break;
		case R.id.user_img:
			if (content.getUser().getUid().equals(LiuLianApplication.current_user.getUid())) {
				Toast.makeText(activity_context, "这是你自己哦", Toast.LENGTH_SHORT).show();
			} else {
				intent.setClass(activity_context, UserInfoDetail.class);
				intent.putExtra("uid", content.getUser().getUid());
				activity_context.startActivity(intent);
			}
			break;
		case R.id.start_session:
			intent.putExtra("report_type", CommonConst.REPOST_TYPE_CONTENT);
			intent.putExtra("title", content.getUser().getName());
			intent.putExtra("content", content.getText_content());
			intent.putExtra("content_id", content.getId());
			intent.setClass(activity_context, ReportActivity.class);
			activity_context.startActivity(intent);
			break;
		case R.id.bt_love:
			if (NetworkUtil.dataConnected(activity_context)) {
				if (content.isIs_praised()) {
					content.setIs_praised(false);
					bt_love.setImageResource(R.drawable.content_button_like_default);
					praise_content(content, 2, item_praise);//取消
					Users user = new Users();
					zan_list.add(user);
					zanUserAdapter.setData(zan_list);
				} else {
					content.setIs_praised(true);
					bt_love.setImageResource(R.drawable.content_button_like_pressed);
					praise_content(content, 1, item_praise);//赞
					StatService.onEvent(activity_context, "content_detail_praise", "内容详情页赞点击次数", 1);
				}
			} else {
				MyToast.makeText(activity_context, "请检查网络", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.bt_share:
			if (!NetworkUtil.dataConnected(activity_context)) {
				MyToast.makeText(activity_context, activity_context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			} else {
				Intent intent_send = new Intent();
				intent_send.setClass(activity_context, ShareActivity.class);
				ShareContent share = new ShareContent();
				share.setId(content.getId());
				share.setTitle("榴莲");
				String summary = content.getText_content();
				share.setSummary(summary);

				switch (content.getType()) {
				case 1:
					share.setImg_url(com.haomee.consts.CommonConst.ICON_DEFAULT_URL);//logo图片
					break;
				case 2:
					share.setImg_url(content.getPicture().getSmall());
					break;
				case 3:
					share.setImg_url(content.getMusic().getCover());
					break;
				case 4:
					share.setImg_url(content.getMovie().getCover());
					break;
				}
				
				LogUtil.e("分享", CommonConst.URL_SHARE_CONTENT + content.getId() +".html");
				share.setRedirect_url(CommonConst.URL_SHARE_CONTENT + content.getId() +".html");
				intent_send.putExtra("share", share);
				intent_send.putExtra("have_report", false);
				activity_context.startActivity(intent_send);
				StatService.onEvent(activity_context, "content_detail_share", "内容详情页分享点击次数", 1);

			}
			break;
		case R.id.item_image:
			intent.setClass(ContentDetailActivity.this, ImgsBrowseActivity_Single.class);
			intent.putExtra("url", content.getPicture().getLarge());
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	public void delete_content(final Content content) {
		String url = PathConst.URL_DEL_CONTENT + "&id=" + content.getId() + "&uid=" + LiuLianApplication.current_user.getUid() + "&accesskey=" + LiuLianApplication.current_user.getAccesskey();
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					JSONObject json = new JSONObject(arg0);
					if (1 == json.optInt("flag")) {
						MyToast.makeText(activity_context, "删除成功", Toast.LENGTH_SHORT).show();
					} else {
						MyToast.makeText(activity_context, "删除失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

	private void praise_content(final Content content, final int is_praise, final TextView textview) {

		if (LiuLianApplication.current_user == null) {
			MyToast.makeText(activity_context, "还没有登录", Toast.LENGTH_SHORT).show();
		} else {
			String url = PathConst.URL_PRAISE_CONTENT + "&id=" + content.getId() + "&flag=" + is_praise + "&uid=" + LiuLianApplication.current_user.getUid() + "&accesskey=" + LiuLianApplication.current_user.getAccesskey();
			AsyncHttpClient asyncHttp = new AsyncHttpClient();
			asyncHttp.get(url, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					try {
						JSONObject json = new JSONObject(arg0);
						if (1 == json.optInt("flag")) {
							if (is_praise == 1) {
								int num = Integer.parseInt(content.getPraise_num()) + 1;
								content.setPraise_num(num + "");
								content.setIs_praised(true);
								textview.setText(num + "");
								MyToast.makeText(activity_context, "恭喜赞成功", Toast.LENGTH_SHORT).show();
							} else {
								int num = Integer.parseInt(content.getPraise_num()) - 1;
								content.setPraise_num(num + "");
								textview.setText(num + "");
								content.setIs_praised(false);
								MyToast.makeText(activity_context, "恭喜取消赞成功", Toast.LENGTH_SHORT).show();
							}

							LiuLianApplication.is_update_praise = true;

						} else {
							MyToast.makeText(activity_context, "操作失败", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}

	}

	public void getData(String id) {

		content = new Content();
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("id", id);
		re.put("uid", LiuLianApplication.current_user.getUid());
		Log.e("请求地址：", PathConst.URL_GET_CONTENT + "&id=" + id + "&uid=" + LiuLianApplication.current_user.getUid());
		asyncHttp.get(PathConst.URL_GET_CONTENT, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					JSONObject json_topic = new JSONObject(arg0);

					if (json_topic.getString("error").equals("1")) {
						MyToast.makeText(activity_context, "该内容已删除", Toast.LENGTH_SHORT).show();
						ContentDetailActivity.this.finish();
						return;
					}

					Log.e("返回数据：", json_topic.toString());
					content.setId(json_topic.getString("id"));
					content.setCreate_time(json_topic.getString("create_time"));
					content.setIs_praised(json_topic.getBoolean("is_praise"));
					content.setView_num(json_topic.getString("view_num"));
					content.setPraise_num(json_topic.getString("praise_num"));
					content.setText_content(json_topic.getString("text_content"));
					content.setTopic(json_topic.getString("topic_title"));
					content_title.setText(json_topic.getString("topic_title"));
					int type = Integer.parseInt(json_topic.getString("type"));
					content.setType(type);
					if (type == 1) {

					} else if (type == 2) {
						JSONObject json_pic = json_topic.getJSONObject("pic");
						ContentPicture picture = new ContentPicture();
						picture.setLarge(json_pic.getString("large"));
						picture.setSmall(json_pic.getString("small"));
						picture.setHeight(json_pic.optInt("height"));
						picture.setWidth(json_pic.optInt("width"));
						content.setPicture(picture);
					} else if (type == 3) {
						JSONObject json_music = json_topic.getJSONObject("music");
						Music music = new Music();
						music.setId(json_music.getString("id"));
						music.setAuthor(json_music.getString("author"));
						music.setCover(json_music.getString("cover"));
						music.setTitle(json_music.getString("title"));
						music.setAlbum(json_music.getString("album"));
						music.setUrl(json_music.getString("url"));
						content.setMusic(music);
					} else if (type == 4) {
						JSONObject json_movie = json_topic.getJSONObject("movie");
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
					}
					JSONObject json_user = json_topic.getJSONObject("user");
					Users user = new Users();
					user.setUid(json_user.getString("uid"));
					user.setName(json_user.getString("username"));
					user.setImage(json_user.getString("head_pic"));
					user.setSex(json_user.getInt("sex"));
					user.setHx_username(json_user.getString("hx_username"));
					content.setUser(user);

					JSONArray json_users_view = json_topic.getJSONArray("view_user");
					ArrayList<Users> list_users = new ArrayList<Users>();
					int length_view = json_users_view.length();
					for (int i = 0; i < length_view; i++) {
						JSONObject json_u = json_users_view.getJSONObject(i);
						Users user_ = new Users();
						user_.setUid(json_u.getString("uid"));
						user_.setName(json_u.getString("username"));
						user_.setImage(json_u.getString("head_pic"));
						user_.setSex(json_u.getInt("sex"));
						user_.setHx_username(json_u.getString("hx_username"));
						user_.setSex(json_u.optInt("sex"));
						user_.setAge(json_u.optString("age"));
						user_.setCity(json_u.optString("city"));
						user_.setBack_pic(json_u.optString("back_pic"));
						user_.setIs_sayhi(json_u.optBoolean("is_sayHi"));
						user_.setHx_username(json_u.optString("hx_username"));
						list_users.add(user_);
					}
					content.setList_view(list_users);

					JSONArray json_users = json_topic.getJSONArray("praise_user");
					ArrayList<Users> list_users_praise_num = new ArrayList<Users>();
					int length_praise = json_users.length();
					for (int i = 0; i < length_praise; i++) {
						JSONObject json_u = json_users.getJSONObject(i);
						Users user_ = new Users();
						user_.setUid(json_u.getString("uid"));
						user_.setName(json_u.getString("username"));
						user_.setImage(json_u.getString("head_pic"));
						user_.setSex(json_u.getInt("sex"));
						user_.setHx_username(json_u.getString("hx_username"));
						user_.setSex(json_u.optInt("sex"));
						user_.setAge(json_u.optString("age"));
						user_.setCity(json_u.optString("city"));
						user_.setBack_pic(json_u.optString("back_pic"));
						user_.setIs_sayhi(json_u.optBoolean("is_sayHi"));
						user_.setHx_username(json_u.optString("hx_username"));
						list_users_praise_num.add(user_);
					}
					content.setList_praise(list_users_praise_num);

				} catch (JSONException e) {
					e.printStackTrace();
				}
				initData(content);
			}
		});
	}

	private void setResultValue() {
		if (content != null) {
			Intent intents = new Intent();
			intents.putExtra("content", content);
			setResult(0, intents);
		}
	}

	@Override
	public void onBackPressed() {
		setResultValue();
		super.onBackPressed();
	}

}
