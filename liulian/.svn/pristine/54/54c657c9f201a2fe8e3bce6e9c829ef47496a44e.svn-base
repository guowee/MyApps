package com.haomee.liulian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.haomee.util.imageloader.ImageLoaderCharles;
import com.tencent.connect.UserInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.haomee.chat.activity.ChatActivity;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.Background_Img;
import com.haomee.entity.Label;
import com.haomee.entity.TextItem;
import com.haomee.entity.Topic;
import com.haomee.entity.UserTextList;
import com.haomee.entity.Users;
import com.haomee.util.BitmapUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.util.ViewUtil;
import com.haomee.view.CircleImageView;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.npi.blureffect.Blur;
import com.sina.weibo.sdk.utils.LogUtil;

public class UserInfoDetail extends BaseActivity {

	// private ImageView iv_bg;
	private TextView tv_uname, tv_age, tv_star, tv_distance, text_title;
	private LinearLayout container_photos, container_labels, container_topics;
	private View layout_labels, layout_topics, layout_signature, layout_talk, layout_photos;
	private ImageView tv_image;
	private Context activity_context;
	private LoadingUserDetail loadingUserDetail;
	private String uid;
	private ArrayList<Topic> topic_list;
	private TextView tv_signature;
	// private TextView tv_percent;
	private ScrollView sl_fill;
	private ImageView bt_back;
	private ImageView bt_attention;
	private TextView text_attention;
	private TextView on_line_time;
	private ImageView mBlurredImage;
	private ImageView mNormalImage;
	// private ScrollableImageView mBlurredImageHeader;
	private float alpha;
	private int TOP_HEIGHT;

	private int screenWidth, screenHeigth;

	private LoadingDialog loadingDialog;
	private TextView user_level;
	private LayoutInflater inflater;
	private int user_mine_level = 0;
	private AlertDialog dialog;
	private MyReceiver my_receiver;
	private boolean is_can_talk = false;
	private ImageView user_level_icon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.user_info_detail_test);

		uid = getIntent().getStringExtra("uid");
		inflater = LayoutInflater.from(this);
		screenWidth = ViewUtil.getScreenWidth(this);
		screenHeigth = ViewUtil.getScreenHeight(this);
		mBlurredImage = (ImageView) findViewById(R.id.blurred_image);
		mNormalImage = (ImageView) findViewById(R.id.normal_image);
		// mBlurredImageHeader = (ScrollableImageView)
		// findViewById(R.id.blurred_image_header);
		// mBlurredImageHeader.setScreenWidth(screenWidth);

		TOP_HEIGHT = ViewUtil.dip2px(this, 200);
		View header_blank = this.findViewById(R.id.header_blank);
		header_blank.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, TOP_HEIGHT));

		activity_context = this;
		// iv_bg = (ImageView) findViewById(R.id.iv_bg);
		user_level_icon = (ImageView) findViewById(R.id.user_level_icon);
		tv_image = (ImageView) findViewById(R.id.tv_image);
		tv_uname = (TextView) findViewById(R.id.tv_uname);
		user_level = (TextView) findViewById(R.id.user_level);
		text_title = (TextView) findViewById(R.id.text_title);
		tv_age = (TextView) findViewById(R.id.tv_age);
		tv_star = (TextView) findViewById(R.id.tv_star);
		tv_distance = (TextView) findViewById(R.id.tv_distance);
		container_photos = (LinearLayout) findViewById(R.id.container_photos);
		container_labels = (LinearLayout) findViewById(R.id.container_labels);
		layout_labels = findViewById(R.id.layout_labels);
		container_topics = (LinearLayout) findViewById(R.id.container_topics);
		layout_topics = findViewById(R.id.layout_topics);
		tv_signature = (TextView) findViewById(R.id.tv_signature);
		layout_signature = findViewById(R.id.layout_signature);
		bt_back = (ImageView) findViewById(R.id.bt_back);
		layout_photos = findViewById(R.id.layout_photos);
		on_line_time = (TextView) findViewById(R.id.on_line_time);
		layout_talk = findViewById(R.id.layout_talk);

		bt_attention = (ImageView) this.findViewById(R.id.bt_attention);
		text_attention = (TextView) this.findViewById(R.id.text_attention);

		sl_fill = (ScrollView) findViewById(R.id.sl_fill);
		sl_fill.smoothScrollTo(0, 0);
		loadingDialog = new LoadingDialog(this);
		if (NetworkUtil.dataConnected(activity_context)) {
			loadingUserDetail = new LoadingUserDetail();
			loadingUserDetail.execute();
			loadingDialog.show();
		} else {
			Toast.makeText(activity_context, "网络不给力", 1).show();
		}

		bt_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// is_can_talk=user.isIs_can_talk();

		layout_talk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (user == null) {
					return;
				}

				if (LiuLianApplication.current_user != null && LiuLianApplication.current_user.getUid().equals(user.getUid())) {
					Toast.makeText(UserInfoDetail.this, "这是你自己哦", Toast.LENGTH_SHORT).show();
					return;
				}

				if (is_can_talk) {// 可以聊天
					Intent intent = new Intent();
					intent.setClass(activity_context, ChatActivity.class);
					intent.putExtra("uId", user.getUid());// 聊天对象的uid
					intent.putExtra("userId", user.getHx_username());// 聊天对象的环信ID
					intent.putExtra("nickname", user.getName());// 聊天对象的昵称
					startActivity(intent);
					StatService.onEvent(activity_context, "ta_homepage_chat", "ta的主页聊天点击次数", 1);
				} else {// 不可以
					show_dialog();
				}

			}
		});

		tv_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (user != null) {
					final AlertDialog dialog = new AlertDialog.Builder(activity_context).create();
					dialog.show();
					Window window = dialog.getWindow();
					window.setContentView(R.layout.user_image);
					RelativeLayout rela_main = (RelativeLayout) window.findViewById(R.id.main);
					ImageView iv_icon = (ImageView) window.findViewById(R.id.iv_icon);
					ImageLoaderCharles.getInstance(UserInfoDetail.this).addTask(user.getHead_pic_big(), iv_icon);
					rela_main.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
				}

			}
		});

		this.findViewById(R.id.bt_attention).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (user != null) {

					loadingDialog.show();

					new AsyncTask<Object, Object, String>() {

						@Override
						protected String doInBackground(Object... params) {
							int type = user.isFocused() ? 2 : 1;
							String url = PathConst.URL_USER_FOCUS + "&uid=" + LiuLianApplication.current_user.getUid() + "&focus_uid=" + user.getUid() + "&accesskey=" + LiuLianApplication.current_user.getAccesskey() + "&type=" + type;
							try {
								JSONObject json = NetworkUtil.getJsonObject(url, null, 5000);
								int flag = json.getInt("flag");
								String msg = json.getString("msg");
								if (flag == 1) {
									user.setFocused(!user.isFocused());
								}
								return msg;
							} catch (Exception e) {
								e.printStackTrace();
								return null;
							}

						}

						@Override
						protected void onPostExecute(String msg) {
							loadingDialog.dismiss();

							if (msg == null) {
								msg = "操作失败";
							}
							Toast.makeText(UserInfoDetail.this, msg, Toast.LENGTH_SHORT).show();

							setFocusBt();
						}

					}.execute();
				}
			}
		});

		sl_fill.setOnTouchListener(new OnTouchListener() {
			private int lastY = 0;
			Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					if (lastY == sl_fill.getScrollY()) {
						// 停止了
						Log.i("test", "滚动停止");
					} else {
						handler.sendMessageDelayed(handler.obtainMessage(0), 10);
						lastY = sl_fill.getScrollY();
					}

					// updateArraws();

					onScrolling();
				}
			};

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {
					handler.sendMessageDelayed(handler.obtainMessage(0), 10);
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					// updateArraws();

					onScrolling();
				}

				return false;
			}

		});

	}

	@SuppressLint("NewApi")
	private void onScrolling() {

		if (android.os.Build.VERSION.SDK_INT >= 11) {
			int offset_y = sl_fill.getScrollY();
			alpha = Math.abs((float) offset_y / TOP_HEIGHT);
			if (alpha > 1) {
				alpha = 1;
			}

			mBlurredImage.setAlpha(alpha);

			// text_title.setAlpha(alpha);

			float alpha_title = (float) offset_y / (TOP_HEIGHT / 2) - 1.5f;
			if (alpha_title > 0.8f) {
				alpha_title = 0.8f;
			} else if (alpha_title < 0) {
				alpha_title = 0;

			}
			text_title.setAlpha(alpha_title);

			if (offset_y < 0) {
				// float scale = (float) (-offset_y - 100) / TOP_HEIGHT / 2 + 1;
				// // 缩放规则
				float scale = (float) (-offset_y) / TOP_HEIGHT / 2 + 1; // 缩放规则
				mBlurredImage.setScaleX(scale);
				mBlurredImage.setScaleY(scale);
				mNormalImage.setScaleX(scale);
				mNormalImage.setScaleY(scale);
			}

			// Parallax effect : we apply half the scroll amount to our
			// three views
			mBlurredImage.setTop(-offset_y / 2);
			mNormalImage.setTop(-offset_y / 2);
			// mBlurredImageHeader.handleScroll(-offset_y / 2);
		}

	}

	private void setBackground(final Bitmap image) {
		// final File blurredImage = new File(getFilesDir() + BLURRED_IMG_PATH);
		// if (!blurredImage.exists()) {
		setProgressBarIndeterminateVisibility(true);
		new Thread(new Runnable() {
			@Override
			public void run() {

				final Bitmap bmpBlurred = Blur.fastblur(UserInfoDetail.this, image, 12);

				// final Bitmap bmpBlurred_scaled =
				// Bitmap.createScaledBitmap(bmpBlurred, screenWidth, (int)
				// (bmpBlurred.getHeight() * ((float) screenWidth) / (float)
				// bmpBlurred.getWidth()), false);

				// ImageUtils.storeImage(newImg, blurredImage);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// updateView(screenWidth);
						mNormalImage.setImageBitmap(image);
						mBlurredImage.setImageBitmap(bmpBlurred);
						// mBlurredImageHeader.setoriginalImage(bmpBlurred_scaled);
						// And finally stop the progressbar
						setProgressBarIndeterminateVisibility(false);
					}
				});

			}
		}).start();

		my_receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter("MyReceiver_Test");
		registerReceiver(my_receiver, filter);

	}

	private void setFocusBt() {
		if (user.isFocused()) {
			bt_attention.setImageResource(R.drawable.image_button_subtract);
			text_attention.setText("取消关注");
			StatService.onEvent(activity_context, "ta_homepage_careless", "ta的主页取消关注点击次数", 1);
		} else {
			bt_attention.setImageResource(R.drawable.image_button_add);
			text_attention.setText("添加关注");
			StatService.onEvent(activity_context, "ta_homepage_care", "ta的主页关注点击次数", 1);
		}
	}

	private Users user = null;
	private SharedPreferences.Editor editor;
	private SharedPreferences preferences_chat_user;

	class LoadingUserDetail extends AsyncTask<String, Integer, Users> {
		@Override
		protected Users doInBackground(String... url) {

			try {
				preferences_chat_user = UserInfoDetail.this.getSharedPreferences(CommonConst.PREFERENCES_SESSION_USERS, Context.MODE_PRIVATE);
				String urlPath = PathConst.URL_OTHER_USER_DETAIL + "&uid=" + uid + "&login_uid=" + LiuLianApplication.current_user.getUid() + "&location_x=" + LiuLianApplication.latitude + "&location_y=" + LiuLianApplication.longtitude;
				LogUtil.e("地址", urlPath + "");
				try {
					JSONObject json = null;
					// 有网取网络
					if (NetworkUtil.dataConnected(activity_context)) {
						json = NetworkUtil.getJsonObject(urlPath, null, 5000);
					}

					if (json != null) {
						user = new Users();
						user.setUid(uid);
						user.setName(json.optString("username"));
						user.setSex(json.optInt("sex"));
						user.setAge(json.optString("age"));
						user.setStar(json.optString("star"));
						user.setSignature(json.optString("signature"));
						user.setDistance_str(json.optString("distance"));
						user.setPercent(json.optString("percent"));
						user.setIs_online(json.optBoolean("is_online"));
						user.setImage(json.optString("head_pic"));
						user.setHead_pic_big(json.optString("head_pic_big"));
						user.setHx_username(json.optString("hx_username"));
						user.setFocused(json.optBoolean("is_focus"));
						user.setIs_sayhi(json.optBoolean("is_sayHi"));
						user.setCity(json.optString("city"));
						user.setIs_can_talk(json.optBoolean("is_can_talk"));
						user.setUser_level(json.optString("user_level"));
						user.setUser_level_icon(json.optString("user_level_icon"));

						editor = preferences_chat_user.edit();
						editor.putString(user.getHx_username(), user.getName() + "######" + user.getImage() + "######" + user.getUid() + "######" + user.getSex());
						editor.commit();

						// JSONObject jsonobject =
						// json.getJSONObject("back_pic");
						Background_Img background_Img = new Background_Img();
						background_Img.setUrl(json.optString("back_pic"));
						// background_Img.setWidth(jsonobject.optString("width"));
						// background_Img.setHeight(jsonobject.optString("height"));
						user.setBackground_img(background_Img);

						JSONArray photos_json = json.getJSONArray("photos");
						JSONArray photos_json_small = json.getJSONArray("photos_small");

						ArrayList<String> photos = new ArrayList<String>();
						for (int i = 0; i < photos_json.length(); i++) {
							String url_img = photos_json.getString(i);
							photos.add(url_img);
						}
						ArrayList<String> photos_small = new ArrayList<String>();
						for (int i = 0; i < photos_json_small.length(); i++) {
							String url_img = photos_json_small.getString(i);
							photos_small.add(url_img);
						}
						user.setPhotos(photos);
						user.setPhotos_small(photos_small);

						JSONArray labels_json = json.getJSONArray("label");
						Label[] labels = new Label[labels_json.length()];
						for (int i = 0; i < labels_json.length(); i++) {
							JSONObject label_json = labels_json.getJSONObject(i);
							Label label = new Label();
							label.setId(label_json.getString("id"));
							label.setName(label_json.getString("name"));
							label.setIcon(label_json.getString("icon"));
							labels[i] = label;
						}
						user.setLabel(labels);

						JSONArray topics_json = json.getJSONArray("interests");
						topic_list = new ArrayList<Topic>();
						for (int i = 0; i < topics_json.length(); i++) {
							Topic topic = new Topic();
							JSONObject topic_jsonObject = topics_json.getJSONObject(i);
							topic.setTitle(topic_jsonObject.optString("title"));
							topic.setId(topic_jsonObject.optString("id"));
							topic.setPic(topic_jsonObject.optString("pic"));
							topic_list.add(topic);
						}
						user.setList_topic(topic_list);
						is_can_talk = user.isIs_can_talk();
					}

				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					e.printStackTrace();
				}

				Bitmap bitmap = ImageLoaderCharles.getInstance(UserInfoDetail.this).getBitmap(user.getBackground_img().getUrl());

				// 没有使用默认的
				if (bitmap == null) {
					bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_default);
				}

				bitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, (int) (bitmap.getHeight() * ((float) screenWidth) / (float) bitmap.getWidth()), false);

				if (bitmap.getHeight() >= screenHeigth) {
					bitmap_bg = bitmap;
				} else if (bitmap.getHeight() >= screenHeigth / 2) {
					bitmap_bg = BitmapUtil.createReflectedImage(bitmap); // 倒影2倍
				} else {
					bitmap_bg = BitmapUtil.createRepeatImage(bitmap); // 很短，就3倍
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return user;
		}

		Bitmap bitmap_bg;

		@Override
		protected void onPostExecute(Users user) {
			super.onPostExecute(user);

			loadingDialog.dismiss();

			if (user != null) {

				if (bitmap_bg != null) {
					setBackground(bitmap_bg);
				}
				user_level.setText("LV. " + user.getUser_level());
				text_title.setText(user.getName() + "、" + user.getAge());
				tv_uname.setText(user.getName());
				tv_age.setText(user.getAge() + "岁,");
				tv_star.setText(user.getStar());
				tv_signature.setText(user.getSignature());
				setFocusBt();

				ImageLoaderCharles.getInstance(UserInfoDetail.this).addTask(user.getImage(), tv_image);
				ImageLoaderCharles.getInstance(UserInfoDetail.this).addTask(user.getUser_level_icon(), user_level_icon);
				// new LevelTask(user.getUser_level_icon(), tv_uname,
				// activity_context).execute();
				tv_image.setBackgroundResource(CommonConst.user_sex[user.getSex()]);

				Drawable drawable = null;
				if (user.isIs_online()) {
					drawable = getResources().getDrawable(R.drawable.image_icon_online);
					on_line_time.setText("在线");
				} else {
					drawable = getResources().getDrawable(R.drawable.image_icon_offline);
					on_line_time.setText("离线");

				}
				// / 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				on_line_time.setCompoundDrawables(drawable, null, null, null);
				tv_distance.setText(user.getDistance_str() + "(" + user.getCity() + ")");
				if ("".equals(user.getSignature())) {
					layout_signature.setVisibility(View.GONE);
				}

				LayoutInflater inflater = ((Activity) activity_context).getLayoutInflater();
				container_photos.removeAllViews();
				if (user.getPhotos() != null && user.getPhotos().size() > 0) {

					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.rightMargin = ViewUtil.dip2px(UserInfoDetail.this, 6);

					for (int i = 0; i < user.getPhotos_small().size(); i++) {
						View item = inflater.inflate(R.layout.user_item, null);
						ImageView img = (ImageView) item.findViewById(R.id.iv_icon);
						ImageLoaderCharles.getInstance(UserInfoDetail.this).addTask(user.getPhotos_small().get(i), img);
						item.setTag(i);
						item.setOnClickListener(imgsListener);

						container_photos.addView(item, params);
					}
				} else {

					layout_photos.setVisibility(View.GONE);
				}

				container_labels.removeAllViews();
				if (user.getLabel() != null && user.getLabel().length > 0) {

					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.rightMargin = ViewUtil.dip2px(UserInfoDetail.this, 12);

					for (int i = 0; i < user.getLabel().length; i++) {
						View item = inflater.inflate(R.layout.label_image, null);
						ImageView img = (ImageView) item.findViewById(R.id.item_image);
						TextView name = (TextView) item.findViewById(R.id.item_name);
						// img.setPadding(10, 10, 0, 10);
						name.setText(user.getLabel()[i].getName());
						ImageLoaderCharles.getInstance(UserInfoDetail.this).addTask(user.getLabel()[i].getIcon(), img);
						item.setTag(user.getLabel()[i].getId() + "####" + user.getLabel()[i].getName());
						item.setOnClickListener(labelListener);
						container_labels.addView(item, params);
					}
				} else {
					layout_labels.setVisibility(View.GONE);
				}

				container_topics.removeAllViews();
				if (user.getList_topic() != null && user.getList_topic().size() > 0) {

					int item_rightMargin = ViewUtil.dip2px(UserInfoDetail.this, 10);
					int item_width = (screenWidth - 6 * item_rightMargin) / 3;
					int item_height = item_width * 5 / 4;
					RelativeLayout.LayoutParams params_img = new RelativeLayout.LayoutParams(item_width, item_height);

					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.rightMargin = item_rightMargin;

					for (int i = 0; i < user.getList_topic().size(); i++) {
						Topic topic = user.getList_topic().get(i);
						View item = inflater.inflate(R.layout.topic_item, null);
						ImageView img = (ImageView) item.findViewById(R.id.topic_image);
						TextView topic_name = (TextView) item.findViewById(R.id.topic_name);
						img.setLayoutParams(params_img);
						// img.setPadding(10, 10, 0, 10);
						ImageLoaderCharles.getInstance(UserInfoDetail.this).addTask(topic.getPic(), img);
						topic_name.setText(topic.getTitle());
						item.setTag(topic);
						container_topics.addView(item, params);
						item.setOnClickListener(topicListener);
					}
				} else {
					layout_topics.setVisibility(View.GONE);
				}
			}
		}
	}

	private OnClickListener imgsListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (user != null) {
				int index = (Integer) v.getTag();
				Intent intent = new Intent();
				intent.setClass(UserInfoDetail.this, ImgsBrowseActivity.class);
				intent.putExtra("title", user.getName());
				intent.putExtra("index", index);
				intent.putStringArrayListExtra("urls", user.getPhotos());
				startActivity(intent);
				StatService.onEvent(UserInfoDetail.this, "ta_homepage_pics", "ta的主页相册点击次数", 1);
			}
		}
	};

	// 标签点击
	private OnClickListener labelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String category = (String) v.getTag();
			String[] temp = category.split("####");
			Intent intent = new Intent();
			intent.setClass(UserInfoDetail.this, LatestLoginActivity.class);
			intent.putExtra("category", temp[0]);
			intent.putExtra("category_name", temp[1]);
			startActivity(intent);
		}
	};

	private OnClickListener topicListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Topic topic = (Topic) v.getTag();
			Intent intent = new Intent();
			intent.setClass(UserInfoDetail.this, TopicsDetailActivity.class);
			intent.putExtra("topic_id", topic.getId());
			startActivity(intent);
			StatService.onEvent(UserInfoDetail.this, "ta_homepage_topics", "ta的主页话题点击次数", 1);
		}
	};

	/**
	 * 匹配测试
	 */
	private void show_dialog() {
		final Intent intent = new Intent();
		View view = inflater.inflate(R.layout.dialog_user_chat, null);

		CircleImageView imag_icon;
		TextView user_level;
		TextView percent_1, percent_2, percent_3;
		TextView message_notice;
		LinearLayout ll_start_talk;
		TextView can_not_talk;
		final TextView talk;

		imag_icon = (CircleImageView) view.findViewById(R.id.img_icon);
		user_level = (TextView) view.findViewById(R.id.user_level);
		percent_1 = (TextView) view.findViewById(R.id.percent_1);
		percent_2 = (TextView) view.findViewById(R.id.percent_2);
		percent_3 = (TextView) view.findViewById(R.id.percent_3);
		message_notice = (TextView) view.findViewById(R.id.message_notice);
		ll_start_talk = (LinearLayout) view.findViewById(R.id.ll_start_talk);
		can_not_talk = (TextView) view.findViewById(R.id.can_not_talk);
		talk = (TextView) view.findViewById(R.id.talk);

		ll_start_talk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String talk_content = talk.getText().toString().trim();
				if ("开始".equals(talk_content)) {// 测试
					get_user_question();
				}
			}
		});

		if (user == null) {
			return;
		}

		ImageLoaderCharles.getInstance(UserInfoDetail.this).addTask(user.getImage(), imag_icon);
		user_level.setText("Level  " + user.getUser_level());

		percent_1.setText("你的等级低于该用户");
		percent_2.setVisibility(View.GONE);
		percent_3.setVisibility(View.GONE);
		message_notice.setText("如需解锁聊天请进行匹配测试");
		ll_start_talk.setVisibility(View.VISIBLE);
		talk.setText("开始");
		can_not_talk.setVisibility(View.GONE);

		dialog = new AlertDialog.Builder(activity_context).show();
		dialog.setContentView(view);// 对比
		view.findViewById(R.id.bt_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

	}

	/**
	 * 获取测试题
	 */
	private void get_user_question() {
		loadingDialog.show();
		if (!NetworkUtil.dataConnected(activity_context)) {
			MyToast.makeText(activity_context, activity_context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			loadingDialog.dismiss();
			return;
		}
		if (user == null) {
			loadingDialog.dismiss();
			return;
		}
		String url = PathConst.URL_GET_USER_QUESTION + user.getUid();
		// String
		// url="http://api.durian.haomee.cn/?m=Question&a=getUserQuestion&uid=225";
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);
				if (arg0 == null || arg0.length() == 0) {
					loadingDialog.dismiss();
					return;
				}
				try {
					JSONObject json = new JSONObject(arg0);
					if (json == null || "".equals(json)) {
						loadingDialog.dismiss();
						return;// 防止网络连接超时出现空指针异常
					}
					JSONArray array = json.getJSONArray("list");
					if (array == null || array.length() == 0) {
						loadingDialog.dismiss();
						return;
					}
					UserTextList user_text_info = new UserTextList();
					List<TextItem> text_list = new ArrayList<TextItem>();
					for (int index = 0; index < array.length(); index++) {
						JSONObject obj = array.getJSONObject(index);
						TextItem item = new TextItem();
						item.setId(obj.optString("id"));
						item.setLeft_id(obj.optString("left_id"));
						item.setRight_id(obj.optString("right_id"));
						item.setLeft_title(obj.optString("left_title"));
						item.setRight_title(obj.optString("right_title"));
						item.setLeft_num(obj.optString("left_num"));
						item.setRight_num(obj.optString("right_num"));
						item.setAnwser(obj.optString("anwser"));
						text_list.add(item);
					}
					user_text_info.setList(text_list);
					loadingDialog.dismiss();
					Intent intent = new Intent();
					intent.setClass(activity_context, TestActivity.class);
					intent.putExtra("user_text_info", user_text_info);
					intent.putExtra("total", json.optString("total"));
					intent.putExtra("test_flag", 1);
					intent.putExtra("user", user);
					dialog.dismiss();
					activity_context.startActivity(intent);
					StatService.onEvent(activity_context, "start_test_for_chatting", "为了聊天开始测试点击次数", 1);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					loadingDialog.dismiss();
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * 取消注册广播
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(my_receiver);
			my_receiver = null;
		} catch (Exception e) {
		}
	}

	/**
	 * 监听的广播
	 */
	class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if ("MyReceiver_Test".equals(action)) {
				is_can_talk = intent.getBooleanExtra("is_can_talk", false);
				Toast.makeText(activity_context, "" + is_can_talk, 0).show();
			}

		}

	}
}
