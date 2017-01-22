package com.haomee.liulian;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.haomee.chat.activity.ChatActivity;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.BookPage;
import com.haomee.entity.Users;
import com.haomee.util.BitmapUtil;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.util.StringUtil;
import com.haomee.util.TaskStack;
import com.haomee.util.ViewUtil;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class ImgsBrowseActivity_users extends BaseActivity {

	// private AnimActions action;

	private ViewPager viewpager;
	private ViewPagerAdapter viewPagerAdapter;
	private LayoutInflater inflater;

	private ArrayList<View> views;
	private TaskStack taskStack; //

	private ArrayList<BookPage> pages;
	private int current_position; // viewpager的index
	private BookPage current_page;

	private ArrayList<LoadImageTask> tasks; // 已经开启的任务，需要及时关掉

	private TextView txt_title;
	private View bt_back, bt_download;
	// private ImageView bt_love;

	private String path_cache;

	private ArrayList<Users> users;

	private SharedPreferences preferences_is_first;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_imgs_browser_users);

		path_cache = FileDownloadUtil.getDefaultLocalDir(PathConst.IMAGE_CACHDIR);
		tasks = new ArrayList<LoadImageTask>();
		taskStack = new TaskStack();
		initView();

		Intent intent = this.getIntent();

		users = (ArrayList<Users>) intent.getSerializableExtra("users");

		// List<String> imgs = action.getDesc_pics();
		pages = new ArrayList<BookPage>();

		for (int i = 0; i < users.size(); i++) {
			Users user = users.get(i);
			if (user != null) {
				BookPage page = new BookPage();
				page.setPage_index(i);
				page.setImg_url(user.getBack_pic());

				pages.add(page);
			}
		}

		viewPagerAdapter = new ViewPagerAdapter(views);
		viewpager.setAdapter(viewPagerAdapter);
		viewpager.setOnPageChangeListener(new ViewPagerChangeListener());

		updatePageInfo();

		current_position = intent.getIntExtra("index", 0);
		viewpager.setCurrentItem(current_position, false);

		new LoadImageTask(current_position).execute(); // 当前页可能在viewpager刷新之后不显示了，重新加载一次

		preferences_is_first = this.getSharedPreferences(CommonConst.PREFERENCES_FIRST, Context.MODE_PRIVATE);

		// 弹出提示
		boolean is_first_open = preferences_is_first.getBoolean("is_first_tip_users", true);
		if (is_first_open) {
			showHelpTip();
		}

	}

	private void showHelpTip() {
		Intent intent = new Intent();
		intent.putExtra("from", "tip_users");
		intent.setClass(this, HelpTipActivity.class);
		this.startActivity(intent);

		Editor editor = preferences_is_first.edit();
		editor.putBoolean("is_first_tip_users", false);
		editor.commit();
	}

	private void initView() {

		// layout_menu = this.findViewById(R.id.layout_menu);

		txt_title = (TextView) this.findViewById(R.id.txt_title);
		viewpager = (ViewPager) this.findViewById(R.id.viewpager);
		inflater = LayoutInflater.from(this);

		bt_back = (ImageView) this.findViewById(R.id.bt_back);
		// bt_love = (ImageView) this.findViewById(R.id.bt_love);
		bt_download = this.findViewById(R.id.bt_download);

		views = new ArrayList<View>();

		for (int i = 0; i < 3; i++) {
			View view = inflater.inflate(R.layout.book_page_user, null);
			/*
			 * View bt_reload = view.findViewById(R.id.bt_reload);
			 * bt_reload.setOnClickListener(reloadCurrentListener);
			 */
			view.setTag(-1); // 初始时 、失败时为 -1
			views.add(view);
		}

		// bt_share.setOnClickListener(btClickListener);
		bt_back.setOnClickListener(btClickListener);
		bt_download.setOnClickListener(btClickListener);

	}

	/*
	 * private OnClickListener reloadCurrentListener = new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { new
	 * LoadImageTask(current_position).execute(); } };
	 */

	private OnClickListener btClickListener = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_back:
				ImgsBrowseActivity_users.this.finish();
				break;
			case R.id.bt_download:
				saveCurrentImg();
				break;
			}
		}
	};

	// 获取当前页面Bitmap图片
	private Bitmap getCurrentBitmap() {
		View view = views.get(current_position % views.size());
		ImageView item_image = (ImageView) view.findViewById(R.id.item_image);
		BitmapDrawable bd = (BitmapDrawable) item_image.getDrawable();

		Bitmap bitmap = null;
		if (bd != null) {
			bitmap = bd.getBitmap();
		}

		return bitmap;
	}

	private void saveCurrentImg() {
		String path_root = FileDownloadUtil.getSDcardRoot();
		if (path_root == null) {
			Toast.makeText(this, "存储卡不可用", Toast.LENGTH_SHORT).show();
		} else {

			Bitmap bitmap = getCurrentBitmap();

			boolean re = false;

			if (bitmap != null) {

				String fileName = StringUtil.getMD5Str(current_page.getImg_url()) + ".jpg";

				String dir = FileDownloadUtil.getDefaultLocalDir(PathConst.DIR_USER_IMG);
				String path_img = dir + fileName;
				File file_img = new File(path_img);
				re = FileDownloadUtil.saveBitmapToLocal(bitmap, file_img, Bitmap.CompressFormat.JPEG);

				if (re) {
					Toast.makeText(this, "图片保存至：" + PathConst.DIR_USER_IMG, Toast.LENGTH_SHORT).show();
				}
			}

			if (!re) {
				Toast.makeText(this, "图片保存失败", Toast.LENGTH_SHORT).show();
			}

		}
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

	class ViewPagerAdapter extends PagerAdapter {

		private List<View> views;

		public ViewPagerAdapter(List<View> views) {
			this.views = views;
		}

		@Override
		public int getCount() {
			// return Integer.MAX_VALUE;
			return pages == null ? 0 : pages.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			/*
			 * if(views==null || views.size()==0){ return null; }
			 */

			if (Math.abs(current_position - position) > 2) {
				return null;
			}

			Log.i("test", "instantiateItem:" + position + ",current_position:" + current_position);

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
			((ViewPager) container).addView(view, 0);

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
				ImageView item_image = (ImageView) view.findViewById(R.id.item_image);
				View frame_loading = view.findViewById(R.id.frame_loading);
				// View frame_tip = view.findViewById(R.id.frame_tip);
				view.setTag(-1);

				// 获取到Bitmap
				BitmapDrawable bd = (BitmapDrawable) item_image.getDrawable();
				Bitmap bitmap = null;
				if (bd != null) {
					bitmap = bd.getBitmap();
				}

				item_image.setImageDrawable(null); // 干掉之前的图片
				item_image.setVisibility(View.GONE);
				frame_loading.setVisibility(View.VISIBLE);
				// frame_tip.setVisibility(View.GONE);

				if (bitmap != null) {

					if (!bitmap.isRecycled()) {
						Log.i("test", "回收Bitmap,index=" + position);
						bitmap.recycle(); // 回收图片所占的内存
						bitmap = null;
						System.gc(); // 提醒系统及时回收 }

					}
				}

			}

			// ((ViewPager) container).removeView(views.get(position %
			// views.size()));
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
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
	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 */
	public class ViewPagerChangeListener implements OnPageChangeListener {
		public void onPageSelected(int position) {
			if (pages == null || pages.size() == 0) {
				return;
			}

			current_position = position;

			clearTasks(false);
			updatePageInfo(); // 更新界面

			View view = views.get(position % views.size());
			TextView txt_page_index = (TextView) view.findViewById(R.id.txt_page_index);
			txt_page_index.setText("" + (current_page.getPage_index() + 1));

			View img_loading = (ImageView) view.findViewById(R.id.img_loading);
			startRotate_loading(img_loading);

			if (position > 0) {
				View view_pre = views.get((position - 1) % views.size());
				TextView txt_pre = (TextView) view_pre.findViewById(R.id.txt_page_index);
				txt_pre.setText("" + current_page.getPage_index());
			}

			View view_next = views.get((position + 1) % views.size());
			TextView txt_next = (TextView) view_next.findViewById(R.id.txt_page_index);
			txt_next.setText("" + (current_page.getPage_index() + 2));

			taskStack.push(position);
		}

		public void onPageScrollStateChanged(int arg0) {
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	}

	private void updatePageInfo() {

		StatService.onEvent(ImgsBrowseActivity_users.this, "pics_show_in_card", "卡片翻页次数", 1);
		if (pages == null || pages.size() == 0) {
			return;
		}

		current_page = pages.get(current_position);

		final Users user = users.get(current_position);
		View view = views.get(current_position % views.size());
		View frame_info = view.findViewById(R.id.frame_info);
		TextView user_name = (TextView) view.findViewById(R.id.user_name);
		TextView user_age = (TextView) view.findViewById(R.id.user_age);
		TextView user_city = (TextView) view.findViewById(R.id.user_city);
		// ImageView user_image = (ImageView)
		// view.findViewById(R.id.user_image);
		final ImageView bt_hi = (ImageView) view.findViewById(R.id.bt_hi);

		user_name.setText(user.getName());
		user_age.setText(user.getAge());
		user_city.setText(user.getCity());

		// ImageLoaderCharles.getInstance(this).addTask(user.getImage(),
		// user_image);

		int bt_res = 0;

		switch (user.getSex()) {
		case 1:
			frame_info.setBackgroundResource(R.drawable.head_button_1);
			bt_res = R.drawable.head_button_hi_w;
			break;
		case 2:
			frame_info.setBackgroundResource(R.drawable.head_button_2);
			bt_res = R.drawable.head_button_hi_w;
			break;
		case 3:
			frame_info.setBackgroundResource(R.drawable.head_button_3);
			bt_res = R.drawable.head_button_hi_m;
			break;
		case 4:
			frame_info.setBackgroundResource(R.drawable.head_button_4);
			bt_res = R.drawable.head_button_hi_m;
			break;
		default:
			break;
		}

		if (user.isIs_sayhi()) {
			bt_res = R.drawable.head_button_hi_p;
		}

		if (bt_res != 0) {
			bt_hi.setImageResource(bt_res);
		}

		/*
		 * user_image.setBackgroundResource(CommonConst.user_sex[user.getSex()]);
		 */

		frame_info.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StatService.onEvent(ImgsBrowseActivity_users.this, "pics_click_in_card", "卡片展示时候照片区域点击次数", 1);
				Intent intent = new Intent();
				intent.setClass(ImgsBrowseActivity_users.this, UserInfoDetail.class);
				intent.putExtra("uid", user.getUid());
				startActivity(intent);
			}
		});

		bt_hi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (LiuLianApplication.current_user != null && LiuLianApplication.current_user.getUid().equals(user.getUid())) {
					Toast.makeText(ImgsBrowseActivity_users.this, "这是你自己哦", Toast.LENGTH_SHORT).show();
				} else {
					StatService.onEvent(ImgsBrowseActivity_users.this, "time_of_hi_in_card", "卡片展示时候hi点击次数", 1);
					say_hi(user);
					bt_hi.setImageResource(R.drawable.head_button_hi_p);
					user.setIs_sayhi(true);
				}

			}
		});

	}

	public void say_hi(final Users user) {
		String url = PathConst.URL_SAY_HI + "&login_uid=" + LiuLianApplication.current_user.getUid() + "&Luid=" + LiuLianApplication.current_user.getUid() + "&to_hx_username=" + user.getHx_username();

		Log.e("say_hi", url + "");

		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					JSONObject json_obj = new JSONObject(arg0);
					if (json_obj.getString("flag").equals("1")) {
						// sayhi成功
						/*
						 * Intent intent = new Intent();
						 * intent.setClass(ImgsBrowseActivity_users.this,
						 * ChatActivity.class); intent.putExtra("uId",
						 * user.getUid());// 聊天对象的uid intent.putExtra("userId",
						 * user.getHx_username());// 聊天对象的环信ID
						 * intent.putExtra("nickname", user.getName());//
						 * 聊天对象的昵称 intent.putExtra("is_from_hi", true);
						 * ImgsBrowseActivity_users.this.startActivity(intent);
						 */

						EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
						EMConversation conversation = EMChatManager.getInstance().getConversation(user.getHx_username());
						TextMessageBody txtBody = new TextMessageBody("hi");
						// 设置消息body
						message.addBody(txtBody);
						// 设置要发给谁,用户username或者群聊groupid
						message.setReceipt(user.getHx_username());
						// 把messgage加到conversation中

						conversation.addMessage(message);
						sendMsgInBackground(message);

						// 告诉后台我已经say hi过
					} else {
						MyToast.makeText(ImgsBrowseActivity_users.this, "今天已经跟ta打过招呼了", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void sendMsgInBackground(final EMMessage message) {
		// 调用sdk发送异步发送方法
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onSuccess() {
			}

			@Override
			public void onError(int code, String error) {
			}

			@Override
			public void onProgress(int progress, String status) {
			}

		});

	}

	// 等待动画
	private RotateAnimation rotateAnimation;

	private void startRotate_loading(View view) {

		if (rotateAnimation == null) {
			rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
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
			item_image = (ImageView) view.findViewById(R.id.item_image);
			frame_loading = view.findViewById(R.id.frame_loading);

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
				View img_loading = (ImageView) frame_loading.findViewById(R.id.img_loading);
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
						bitmap = FileDownloadUtil.getLocalBitmap(cache.getAbsolutePath());
					}

					if (bitmap == null && NetworkUtil.dataConnected(ImgsBrowseActivity_users.this)) {
						bitmap = NetworkUtil.getHttpBitmap(url, null, 10000);

						if (bitmap != null) {
							if (path_cache != null) {
								FileDownloadUtil.saveBitmapToLocal(bitmap, cache, Bitmap.CompressFormat.JPEG);
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

				if (bitmap.getHeight() < ViewUtil.getScreenHeight(ImgsBrowseActivity_users.this) / 2) {
					bitmap = BitmapUtil.createReflectedImage(bitmap);
				}

				item_image.setImageBitmap(bitmap);
				isLoaded = true;
				// imgs_loaded.put(page_index, bitmap);
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
				if (is_clear_current || Math.abs(task.getId() - current_position) > 1) {

					Log.i("test", "放弃加载：" + task.getId());
					task.cancel(true);
				}
			}
			tasks.clear();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler_load_img != null) {
			handler_load_img.removeMessages(0);
		}
		clearTasks(true);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

}
