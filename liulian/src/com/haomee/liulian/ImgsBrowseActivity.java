package com.haomee.liulian;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haomee.consts.PathConst;
import com.haomee.entity.BookPage;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.util.StringUtil;
import com.haomee.util.TaskStack;

public class ImgsBrowseActivity extends BaseActivity {

	//private AnimActions action;

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
	private int screen_width;
	// private ImageView bt_share;
	
	private String title;
	private ArrayList<String> urls;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_imgs_browser);

		path_cache = FileDownloadUtil.getDefaultLocalDir(PathConst.DIR_TEMP);
		tasks = new ArrayList<LoadImageTask>();
		taskStack = new TaskStack();
		initView();

		Intent intent = this.getIntent();
		title = intent.getStringExtra("title");
		urls = intent.getStringArrayListExtra("urls");
		

		//List<String> imgs = action.getDesc_pics();
		pages = new ArrayList<BookPage>();

		for (int i = 0; i < urls.size(); i++) {
			String url = urls.get(i);
			if (url != null) {
				BookPage page = new BookPage();
				page.setPage_index(i);
				page.setImg_url(url);

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

	}

	private void initView() {

		// bt_share = (ImageView) findViewById(R.id.bt_share);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_width = dm.widthPixels;

		// layout_menu = this.findViewById(R.id.layout_menu);

		txt_title = (TextView) this.findViewById(R.id.txt_title);
		viewpager = (ViewPager) this.findViewById(R.id.viewpager);
		inflater = LayoutInflater.from(this);

		bt_back = (ImageView) this.findViewById(R.id.bt_back);
		// bt_love = (ImageView) this.findViewById(R.id.bt_love);
		bt_download = this.findViewById(R.id.bt_download);
		
		views = new ArrayList<View>();

		for (int i = 0; i < 3; i++) {
			View view = inflater.inflate(R.layout.book_page, null);
			View bt_reload = view.findViewById(R.id.bt_reload);
			bt_reload.setOnClickListener(reloadCurrentListener);
			view.setTag(-1); // 初始时 、失败时为 -1
			views.add(view);
		}

		// bt_share.setOnClickListener(btClickListener);
		bt_back.setOnClickListener(btClickListener);
		bt_download.setOnClickListener(btClickListener);

	}

	private OnClickListener reloadCurrentListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			new LoadImageTask(current_position).execute();
		}
	};

	private OnClickListener btClickListener = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_back:
				ImgsBrowseActivity.this.finish();
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

				String fileName = StringUtil.getMD5Str(current_page.getImg_url())+".jpg";

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
				View frame_tip = view.findViewById(R.id.frame_tip);
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
				frame_tip.setVisibility(View.GONE);

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
		if (pages == null || pages.size() == 0) {
			return;
		}

		current_page = pages.get(current_position);
		if(title!=null && !title.equals("")){
			txt_title.setText(title + "（" + (current_position + 1) + "/" + pages.size() + "）");
		}else{
			txt_title.setText("（" + (current_position + 1) + "/" + pages.size() + "）");
		}
		
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
		private View view, frame_loading, frame_tip;
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
			frame_tip = view.findViewById(R.id.frame_tip);

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
				frame_tip.setVisibility(View.GONE);
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

					if (bitmap == null && NetworkUtil.dataConnected(ImgsBrowseActivity.this)) {
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
				item_image.setImageBitmap(bitmap);
				isLoaded = true;
				// imgs_loaded.put(page_index, bitmap);
			}

			if (isLoaded && item_image.getDrawable() != null) {

				Log.i("test", "加载成功：" + (page_index + 1));
				// 不要在非UI线程里面修改界面（onPostExecute 属于UI线程中）
				item_image.setVisibility(View.VISIBLE);
				frame_loading.setVisibility(View.GONE);
				frame_tip.setVisibility(View.GONE);
			} else {
				Log.i("test", "加载失败：" + (page_index + 1));
				view.setTag(-1);
				frame_loading.setVisibility(View.GONE);
				frame_tip.setVisibility(View.VISIBLE);
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
