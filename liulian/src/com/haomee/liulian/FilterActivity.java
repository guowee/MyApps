package com.haomee.liulian;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.haomee.chat.activity.ChatActivity;
import com.haomee.consts.PathConst;
import com.haomee.entity.BookPage;
import com.haomee.entity.TextItem;
import com.haomee.entity.UserTextList;
import com.haomee.entity.Users;
import com.haomee.util.BitmapUtil;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.util.StringUtil;
import com.haomee.util.TaskStack;
import com.haomee.util.ViewUtil;
import com.haomee.util.imageloader.ImageLoaderCharles;
import com.haomee.view.CircleImageView;
import com.haomee.view.HackyViewPager;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class FilterActivity extends BaseActivity implements OnClickListener {

	private Adapter_Users viewPagerAdapter;

	private List<View> views;
	private TaskStack taskStack; //

	private ArrayList<BookPage> pages;
	private int current_position = 0; // viewpager的index
	private BookPage current_page;

	private ArrayList<LoadImageTask> tasks; // 已经开启的任务，需要及时关掉

	private String path_cache;

	private ArrayList<Users> list_users;

	private String topic_id;
	private AsyncHttpClient client = null;

	private ImageView iv_filter_back;
	private RelativeLayout rl_loading;
	private ImageView iv_loading;

	private ImageView iv_filter_left;
	private ImageView iv_filter_right;
	private TextView tv_filter_title;

	private Users currentUser;

	private ImageView iv_filter_lock;
	private RelativeLayout rl_filter_chat;

	private HackyViewPager vp_filter_users;

	private LayoutInflater mInflater;
	private Context mContext;
	Dialog dialog;

	private LoadingDialog loadingDialog;
	private int flag = 0;
	private int count = 0;

	private int currentPageScrollStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter);
		findViews();
		bindListener();
		initData();
	}

	private void findViews() {
		iv_filter_back = (ImageView) findViewById(R.id.iv_filter_back);
		vp_filter_users = (HackyViewPager) findViewById(R.id.vp_filter_users);
		rl_loading = (RelativeLayout) findViewById(R.id.rl_filter_loading);
		iv_loading = (ImageView) findViewById(R.id.iv_filter_loding);

		iv_filter_left = (ImageView) findViewById(R.id.iv_filter_left);
		iv_filter_right = (ImageView) findViewById(R.id.iv_filter_right);
		rl_filter_chat = (RelativeLayout) findViewById(R.id.rl_filter_chat);
		iv_filter_lock = (ImageView) findViewById(R.id.iv_filter_lock);
		tv_filter_title = (TextView) findViewById(R.id.tv_filter_title);

	}

	private void bindListener() {

		iv_filter_back.setOnClickListener(this);
		iv_filter_left.setOnClickListener(this);
		iv_filter_right.setOnClickListener(this);
		rl_filter_chat.setOnClickListener(this);
	}

	private void initData() {
		loadingDialog = new LoadingDialog(this);

		path_cache = FileDownloadUtil
				.getDefaultLocalDir(PathConst.IMAGE_CACHDIR);
		// 此处获取users数据
		Intent intent = getIntent();
		flag = intent.getIntExtra("flag", 0);
		mContext = this;
		mInflater = LayoutInflater.from(mContext);
		list_users = new ArrayList<Users>();
		tasks = new ArrayList<LoadImageTask>();
		taskStack = new TaskStack();
		pages = new ArrayList<BookPage>();

		views = new ArrayList<View>();

		for (int i = 0; i < 3; i++) {
			View view = mInflater.inflate(R.layout.layout_lookall, null);
			view.setTag(-1);
			views.add(view);
		}

		client = new AsyncHttpClient();
		String url = "";
		RequestParams params = new RequestParams();
		params.put("Luid", LiuLianApplication.current_user.getUid());
		switch (flag) {
		// 根据标识位去请求不同的接口
		case 2:
			tv_filter_title.setText("查看全部");
			url = PathConst.URL_GETALLPEOPLE;

			break;
		case 3:
			tv_filter_title.setText("分题回顾");
			url = PathConst.URL_GETITEMPEOPLE;
			String item_id = intent.getStringExtra("item_id");
			String qid = intent.getStringExtra("qid");
			params.put("item_id", item_id);
			params.put("qid", qid);
			break;

		}
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFinish() {
				super.onFinish();

				viewPagerAdapter = new Adapter_Users(views);
				vp_filter_users.setAdapter(viewPagerAdapter);
				vp_filter_users
						.setOnPageChangeListener(new ViewPagerChangerListener());
				vp_filter_users.postDelayed(new Runnable() {

					@Override
					public void run() {
						FilterActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								viewPagerAdapter.notifyDataSetChanged();
								updatePageInfo();
							}
						});

					}
				}, 500);

				rl_loading.setVisibility(View.GONE);
			}

			@Override
			public void onStart() {
				super.onStart();
				rl_loading.setVisibility(View.VISIBLE);
				startRotate_loading(iv_loading);
			}

			@Override
			public void onSuccess(int arg0, String json) {
				super.onSuccess(arg0, json);

				if ("".equals(json) || json == null) {
					MyToast.makeText(FilterActivity.this, "没有数据",
							Toast.LENGTH_SHORT).show();
					FilterActivity.this.finish();
					return;
				}

				try {
					JSONObject object = new JSONObject(json);

					if (object != null) {

						JSONArray array = object.getJSONArray("list");

						if (array == null || array.length() == 0) {
							MyToast.makeText(FilterActivity.this, "没有数据",
									Toast.LENGTH_SHORT).show();
							FilterActivity.this.finish();
							return;
						}

						count = array.length();
						for (int i = 0; i < array.length(); i++) {
							JSONObject jobj = array.getJSONObject(i);
							Users users = new Users();
							users.setName(jobj.optString("username"));
							users.setUid(jobj.optString("uid"));
							users.setHx_username(jobj.optString("hx_username"));
							users.setBack_pic(jobj.optString("back_pic"));
							users.setUser_level(jobj.optString("user_level"));
							users.setIs_online(jobj.optBoolean("is_online"));
							users.setPercent(jobj.optString("percent"));
							users.setTest_percent(jobj
									.optString("test_percent"));
							users.setIs_can_talk(jobj.optBoolean("is_can_talk"));
							list_users.add(users);
						}

						for (int i = 0; i < list_users.size(); i++) {
							Users user = list_users.get(i);
							if (user != null) {
								BookPage page = new BookPage();
								page.setPage_index(i);
								page.setImg_url(user.getBack_pic());
								pages.add(page);
							}
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_filter_back:
			finish();
			break;

		case R.id.iv_filter_left:
			if (current_position - 1 >= 0) {

				vp_filter_users.setCurrentItem(current_position - 1);
			} else {
				MyToast.makeText(mContext, "没有更多了", 0).show();
			}

			break;
		case R.id.iv_filter_right:
			if (current_position + 1 < list_users.size()) {

				vp_filter_users.setCurrentItem(current_position + 1);
			} else {
				MyToast.makeText(mContext, "没有更多了", 0).show();
			}
			break;

		case R.id.rl_filter_chat:

			chat(currentUser);

			break;
		default:
			break;
		}

	}

	class Adapter_Users extends PagerAdapter {
		private List<View> views;

		public Adapter_Users(List<View> views) {
			this.views = views;
		}

		@Override
		public int getCount() {
			// return Integer.MAX_VALUE;
			return list_users == null ? 0 : list_users.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			/*
			 * if(views==null || views.size()==0){ return null; }
			 */

			if (Math.abs(current_position - position) > 2) {
				return null;
			}

			Log.i("test", "instantiateItem:" + position + ",current_position:"
					+ current_position);

			if (pages != null) {
				taskStack.push(position);

				handler_load_img.removeMessages(0);
				handler_load_img.sendEmptyMessageDelayed(0, 100);
				// handler_load_img.sendEmptyMessage(0);
			}

			View view = views.get(position % views.size());
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeView(view);
			}
			((ViewPager) container).addView(view);

			Log.d("======================", "============initview");
			return view;
		}

		@Override
		public void destroyItem(View container, int position, Object arg2) {

			/*
			 * if(views==null || views.size()==0){ return; }
			 */

			if (Math.abs(current_position - position) > 2) {
				return;
			}

			Log.i("test", "destroyItem:" + position);

			if (position != current_position) {
				View view = views.get(position % views.size());

				ImageView item_image = null;
				View frame_loading = null;
				BitmapDrawable bd = null;
				Bitmap bitmap = null;

				if (flag == 1) {
					item_image = (ImageView) view
							.findViewById(R.id.iv_item_filter_bg);
					frame_loading = view
							.findViewById(R.id.rl_item_filter_loading);
					view.setTag(-1);
					bd = (BitmapDrawable) item_image.getDrawable();
				} else if (flag == 2 || flag == 3) {
					item_image = (ImageView) view
							.findViewById(R.id.iv_lookall_bg);
					frame_loading = view.findViewById(R.id.rl_lookall_loading);
					view.setTag(-1);
					bd = (BitmapDrawable) item_image.getDrawable();

				}
				// 获取到Bitmap

				if (bd != null) {
					bitmap = bd.getBitmap();
				}
				if (item_image != null) {
					item_image.setImageDrawable(null); // 干掉之前的图片
					item_image.setVisibility(View.GONE);
					frame_loading.setVisibility(View.VISIBLE);

					Log.d("======================", "============destroy");
				}

				if (bitmap != null) {

					if (!bitmap.isRecycled()) {
						bitmap.recycle(); // 回收图片所占的内存
						bitmap = null;
						System.gc(); // 提醒系统及时回收 }

					}
				}

			}

		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}
	}

	public class ViewPagerChangerListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int position) {

			if (list_users == null || list_users.size() == 0) {
				return;
			}
			current_position = position;

			clearTasks(false);
			updatePageInfo(); // 更新界面

			View view = views.get(position % views.size());

			View img_loading = null;

			img_loading = (ImageView) view
					.findViewById(R.id.iv_lookall_loading);
			startRotate_loading(img_loading);
			if (position > 0) {
				View view_pre = views.get((position - 1) % views.size());
			}
			View view_next = views.get((position + 1) % views.size());
			taskStack.push(position);
		}

		public void onPageScrollStateChanged(int arg0) {

			currentPageScrollStatus = arg0;
		}

		public void onPageScrolled(int pos, float positionOffset,
				int positionOffsetPixels) {

			if (pos == 0) {
				// 如果offsetPixels是0页面也被滑动了，代表在第一页还要往左划
				if (positionOffsetPixels == 0 && currentPageScrollStatus == 1) {
					MyToast.makeText(mContext, "没有更多了", 0).show();
				}
			} else if (pos == list_users.size() - 1) {
				// 已经在最后一页还想往右划
				if (positionOffsetPixels == 0 && currentPageScrollStatus == 1) {
					MyToast.makeText(mContext, "没有更多了", 0).show();
				}
			}

		}

	}

	private void updatePageInfo() {

		StatService.onEvent(FilterActivity.this, "pics_show_in_card", "卡片翻页次数",
				1);
		if (list_users == null || list_users.size() == 0) {
			return;
		}

		current_page = pages.get(current_position);

		final Users user = list_users.get(current_position);
		currentUser = user;
		View view = views.get(current_position % views.size());
		TextView user_name = null;
		TextView level = null;
		TextView tv_count = null;
		TextView history = null;
		TextView today = null;
		RelativeLayout rl_lookall_root;

		user_name = (TextView) view.findViewById(R.id.tv_lookall_name);

		level = (TextView) view.findViewById(R.id.tv_lookall_level);

		history = (TextView) view.findViewById(R.id.tv_lookall_history);

		today = (TextView) view.findViewById(R.id.tv_lookall_today);

		rl_lookall_root = (RelativeLayout) view
				.findViewById(R.id.rl_lookall_root);

		rl_lookall_root.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				StatService.onEvent(FilterActivity.this, "pics_click_in_card",
						"卡片展示时候照片区域点击次数", 1);
				Intent intent = new Intent();
				intent.setClass(FilterActivity.this, UserInfoDetail.class);
				intent.putExtra("uid", user.getUid());
				startActivity(intent);
			}
		});

		if (user.isIs_can_talk()) {
			iv_filter_lock.setVisibility(View.INVISIBLE);
		} else {
			iv_filter_lock.setVisibility(View.VISIBLE);
		}

		// String minelever = LiuLianApplication.current_user.getUser_level();
		// String theirlevel = user.getUser_level();
		//
		// if (minelever != null && theirlevel != null &&
		// Integer.valueOf(minelever) < Integer.valueOf(theirlevel)) {
		// //自己等级小于对方等级，加锁
		// iv_filter_lock.setVisibility(View.VISIBLE);
		// rl_filter_chat.setClickable(false);
		// } else {
		// iv_filter_lock.setVisibility(View.INVISIBLE);
		// }

		history.setText(user.getTest_percent() + "%");
		today.setText(user.getPercent() + "%");
		user_name.setText(user.getName());
		level.setText("LV." + user.getUser_level());

	}

	// 加载图片，延时处理(从任务栈里面取出最上面的两个)
	private Handler handler_load_img = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			int index_next = taskStack.pop();
			int index_current = taskStack.pop();

			if (index_current == -1) {
				index_current = index_next;
			} 
			

			taskStack.clear();

			if (index_current != -1) {
				new LoadImageTask(index_current).execute();
			}

			if (index_current != index_next) {
				taskStack.push(index_next);
				handler_load_img.removeMessages(0);
				handler_load_img.sendEmptyMessageDelayed(0, 100); // 缓冲加载下一张图片
				// handler_load_img.sendEmptyMessage(0);
			}

		}
	};

	private RotateAnimation rotateAnimation;

	private void startRotate_loading(View view) {

		if (rotateAnimation == null) {
			rotateAnimation = new RotateAnimation(0f, 360f,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			rotateAnimation.setDuration(1000);
			rotateAnimation.setRepeatCount(-1);
			rotateAnimation.setInterpolator(new LinearInterpolator());
		}
		view.startAnimation(rotateAnimation);
	}

	/**
	 * 加载图片
	 * 
	 */
	class LoadImageTask extends AsyncTask<Integer, Integer, Bitmap> {

		private int page_index;
		private View view, frame_loading;
		private ImageView item_image;

		private int tag;
		private boolean isLoaded = false; // 防止重复请求

		public int getId() {
			return page_index;
		}

		public LoadImageTask(int page_index) {

			if (page_index < 0) {
				page_index = 0;
			}
			this.page_index = page_index;

			int index = page_index % views.size();
			view = views.get(index);

			item_image = (ImageView) view.findViewById(R.id.iv_lookall_bg);
			frame_loading = view.findViewById(R.id.rl_lookall_loading);

			if (view.getTag() == null) {
				tag = -1;
			} else {
				tag = Integer.parseInt(view.getTag().toString());
			}

			tasks.add(this);
		}

		@Override
		protected void onPreExecute() {

			if (page_index == tag) { // 已经加载过了
				Log.i("test", "已经加载过：" + (page_index + 1));
			} else {
				// 不要在非UI线程里面修改界面（onPreExecute 属于UI线程中）
				item_image.setVisibility(View.GONE);
				frame_loading.setVisibility(View.VISIBLE);
				View img_loading = (ImageView) frame_loading
						.findViewById(R.id.iv_lookall_loading);
				startRotate_loading(img_loading);
			}
		}

		@Override
		protected Bitmap doInBackground(Integer... args) {

			Bitmap bitmap = null;
			try {

				if (page_index == tag && item_image.getDrawable() != null) { // 已经加载过了
					isLoaded = true;
					return null;
				}

				if (this.isCancelled()) {
					return null;
				}

				view.setTag(page_index);
				// imgs_loaded.remove(page_index);

				BookPage page = pages.get(page_index);
				String url = page.getImg_url();

				/*
				 * if(page_index%2==0){ url+="test404"; }
				 */

				if (page_index == 1) {
					Log.i("test", "test_1");
				}

				Log.i("test", "taskStack:" + taskStack.printAll());
				Log.i("test", "开始加载：" + page.getPage_index() + "___" + url);

				if (url != null && !url.trim().equals("")) {

					String file_name_md5 = StringUtil.getMD5Str(url);

					File cache = new File(path_cache + file_name_md5);
					// 检查本地是否存在
					if (cache.exists()) {
						bitmap = FileDownloadUtil.getLocalBitmap(cache
								.getAbsolutePath());
					}

					if (bitmap == null && NetworkUtil.dataConnected(mContext)) {
						bitmap = NetworkUtil.getHttpBitmap(url, null, 10000);

						if (bitmap != null) {
							if (path_cache != null) {
								FileDownloadUtil.saveBitmapToLocal(bitmap,
										cache, Bitmap.CompressFormat.JPEG);
							}

						}
					}

				}

				// Thread.sleep(1000);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {

			if (this.isCancelled()) {
				Log.i("test", "放弃加载：" + this.getId());
				return;
			}

			if (bitmap != null) {

				RelativeLayout.LayoutParams flp = (RelativeLayout.LayoutParams) vp_filter_users
						.getLayoutParams();

				int width = ViewUtil.getScreenWidth(FilterActivity.this)
						- flp.leftMargin - flp.rightMargin;

				bitmap = BitmapUtil.zoomBitmap(bitmap, width, width);
				// bitmap=BitmapUtil.zoomBitmap(bitmap, scale);

				item_image.setImageBitmap(bitmap);
				isLoaded = true;
			}

			if (isLoaded && item_image.getDrawable() != null) {

				Log.i("test", "加载成功：" + (page_index + 1));
				// 不要在非UI线程里面修改界面（onPostExecute 属于UI线程中）
				item_image.setVisibility(View.VISIBLE);
				frame_loading.setVisibility(View.GONE);
			} else {
				Log.i("test", "加载失败：" + (page_index + 1));
				view.setTag(-1);
				frame_loading.setVisibility(View.GONE);
				// frame_tip.setVisibility(View.VISIBLE);

				item_image.setVisibility(View.VISIBLE);
				item_image.setImageResource(R.drawable.bg_default);
			}

		}
	}

	// 清理正在请求的任务
	private void clearTasks(boolean is_clear_current) {
		if (tasks != null) {
			// 关掉之前页的图片请求
			for (LoadImageTask task : tasks) {
				if (is_clear_current
						|| Math.abs(task.getId() - current_position) > 1) {

					Log.i("test", "放弃加载：" + task.getId());
					task.cancel(true);
				}
			}
			tasks.clear();
		}
	}

	private void chat(Users user) {

		if (user == null) {
			return;
		}

		if (!user.isIs_can_talk()) {
			show_dialog(user);
		}

		else if (user.getUid().equals(LiuLianApplication.current_user.getUid())) {
			// if(mContext instanceof TopicsDetailActivity){
			// interest_delete(LiuLianApplication.current_user.getUid());
			// }else{
			// MyToast.makeText(context, "不能和自己聊天哦", 1).show();
			// }
			MyToast.makeText(mContext, "不能和自己聊天哦", 1).show();
		} else {
			Intent intent = new Intent();
			intent.setClass(mContext, ChatActivity.class);
			intent.putExtra("uId", user.getUid()); // 聊天对象的uid
			intent.putExtra("userId", user.getHx_username());// 聊天对象的环信id
			intent.putExtra("nickname", user.getName());
			mContext.startActivity(intent);
			// StatService.onEvent(mContext, "count_of_chat_topic_detail",
			// "话题页聊天次数",1);
		}
	}

	private void show_dialog(final Users user) {

		if (user == null) {
			return;
		}

		final Intent intent = new Intent();
		View view = LayoutInflater.from(FilterActivity.this).inflate(
				R.layout.dialog_user_chat, null);

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
					get_user_question(user);
				}
			}
		});

		ImageLoaderCharles.getInstance(FilterActivity.this).addTask(
				user.getImage(), imag_icon);
		user_level.setText("Level  " + user.getUser_level());

		percent_1.setText("你的等级低于该用户");
		percent_2.setVisibility(View.GONE);
		percent_3.setVisibility(View.GONE);
		message_notice.setText("如需解锁聊天请进行匹配测试");
		ll_start_talk.setVisibility(View.VISIBLE);
		talk.setText("开始");
		can_not_talk.setVisibility(View.GONE);

		dialog = new AlertDialog.Builder(FilterActivity.this).show();
		dialog.setContentView(view);// 对比
		view.findViewById(R.id.bt_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

	}

	/**
	 * 获取测试题
	 */
	private void get_user_question(final Users user) {
		loadingDialog.show();
		if (!NetworkUtil.dataConnected(FilterActivity.this)) {
			MyToast.makeText(
					FilterActivity.this,
					FilterActivity.this.getResources().getString(
							R.string.no_network), Toast.LENGTH_SHORT).show();
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
					intent.setClass(FilterActivity.this, TestActivity.class);
					intent.putExtra("user_text_info", user_text_info);
					intent.putExtra("total", json.optString("total"));
					intent.putExtra("test_flag", 1);
					intent.putExtra("user", user);
					dialog.dismiss();
					FilterActivity.this.startActivity(intent);
					StatService.onEvent(FilterActivity.this, "start_test_for_chatting", "为了聊天开始测试点击次数", 1);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					loadingDialog.dismiss();
					e.printStackTrace();
				}

			}
		});
	}
}
