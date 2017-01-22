package com.haomee.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.haomee.util.imageloader.ImageLoaderCharles;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mobstat.StatService;
import com.haomee.consts.PathConst;
import com.haomee.entity.Content;
import com.haomee.entity.ContentPicture;
import com.haomee.entity.Users;
import com.haomee.liulian.ContentDetailActivity;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.R;
import com.haomee.liulian.UserInfoDetail;
import com.haomee.util.BitmapUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.util.ViewUtil;
import com.haomee.view.Arrow;

public class MapFragment extends Fragment {

	private View view;
	private Activity context;
	private LayoutInflater inflater;

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private FrameLayout layout_arrows;

	private int screen_width, screen_height;

	private LinearLayout container_points;
	private ScrollView scrollView;
	private List<Arrow> arrows;
	private List<Users> list_users;
	private List<Users> list_top;

	private final int list_top_size = 5;

	private int user_icon_width;
	private int user_icon_width_border;

	private boolean isMapLoaded = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (view == null) {
			this.inflater = inflater;
			context = this.getActivity();
			user_icon_width = ViewUtil.dip2px(context, 40);
			user_icon_width_border = ViewUtil.dip2px(context, 4);

			// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
			// 注意该方法要再setContentView方法之前实现
			SDKInitializer.initialize(context.getApplicationContext());

			view = inflater.inflate(R.layout.fragment_map, null);

			initMap();

		} else {
			((ViewGroup) view.getParent()).removeView(view);

			// 如果上次加载失败，重新加载
			if (isMapLoaded && (list_users == null || list_users.size() == 0)) {
				new LoadingTask().execute();
			}
		}

		return view;
	}

	private void initMap() {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_width = dm.widthPixels;
		screen_height = dm.heightPixels;

		// myLayout = (FrameLayout) view.findViewById(R.id.myLayout);
		layout_arrows = (FrameLayout) view.findViewById(R.id.layout_arrows);

		// 获取地图控件引用
		mMapView = (MapView) view.findViewById(R.id.bmapView);

		mBaiduMap = mMapView.getMap();

		mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {

			@Override
			public void onMapLoaded() {

				isMapLoaded = true;

				initViews();

				new LoadingTask().execute();

			}
		});

		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

			@Override
			public void onMapStatusChangeStart(MapStatus arg0) {
				clearAllTips();

				// Log.i("test","onMapStatusChangeStart");
			}

			@Override
			public void onMapStatusChangeFinish(MapStatus status) {

				if (container_points != null) {

					// 如果上次加载失败，重新加载
					if (isMapLoaded && (list_users == null || list_users.size() == 0)) {
						new LoadingTask().execute();
					} else {
						addTips();
					}
				}

				// Log.i("test","onMapStatusChangeFinish");
			}

			@Override
			public void onMapStatusChange(MapStatus status) {

				// updateArraws();

				// Log.i("test","onMapStatusChange");
			}
		});

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// 获得marker中的数据
				Bundle bundle = marker.getExtraInfo();
				// Toast.makeText(context, ""+bundle.getString("uid"),
				// Toast.LENGTH_LONG).show();
				StatService.onEvent(context, "discover_surround_icon", "发现页周边头像点击次数", 1);
				String uid = bundle.getString("uid");
				Intent intent = new Intent();
				intent.setClass(context, UserInfoDetail.class);
				intent.putExtra("uid", uid);
				startActivity(intent);

				return true;
			}

		});

	}

	private boolean isOutOfScreen(Users user) {
		LatLng point = new LatLng(user.getLocation_x(), user.getLocation_y());
		Point p_screen = mBaiduMap.getProjection().toScreenLocation(point);
		int left = 0;
		if (container_points != null) {
			left = container_points.getRight();
		}
		return p_screen.x < left || p_screen.x > screen_width || p_screen.y < 0 || p_screen.y > screen_height;
	}

	private void initUsers() {

		if (list_users == null || list_users.size() == 0) {
			return;
		}

		LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();

		for (int i = 0; i < list_users.size(); i++) {

			Users user = list_users.get(i);

			LatLng point = new LatLng(user.getLocation_x(), user.getLocation_y());

			boundBuilder.include(point);

			// addImage(point);

			addImage(i);

		}

		// 根据分布的点，自动拉伸到屏幕界面
		LatLngBounds bounds = boundBuilder.build();
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
		mBaiduMap.animateMapStatus(u, 800);
		// mBaiduMap.setMapStatus(u);

	}

	private void initViews() {
		container_points = (LinearLayout) view.findViewById(R.id.container_points);
		scrollView = (ScrollView) view.findViewById(R.id.scrollView);
		scrollView.setOnTouchListener(new OnTouchListener() {
			private int lastY = 0;
			Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					if (lastY == scrollView.getScrollY()) {
						// 停止了
						Log.i("test", "滚动停止");
					} else {
						handler.sendMessageDelayed(handler.obtainMessage(0), 10);
						lastY = scrollView.getScrollY();
					}

					updateArraws();
				}
			};

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {
					handler.sendMessageDelayed(handler.obtainMessage(0), 10);
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					updateArraws();
				}

				return false;
			}

		});
	}

	private void addTips() {

		if (list_users == null || list_users.size() == 0) {
			return;
		}

		arrows = new ArrayList<Arrow>();

		list_top = new ArrayList<Users>();

		Random rdm = new Random();

		int count = 0;
		while (list_top.size() < list_top_size) {
			int i = rdm.nextInt(list_users.size());
			Users user = list_users.get(i);
			if (!list_top.contains(user)) {

				// Point p_screen =
				// mBaiduMap.getProjection().toScreenLocation(point);

				if (!isOutOfScreen(user)) {
					list_top.add(user);
				}

			}

			count++;

			if (count > 1000) { //
				break;
			}

		}

		orderPoint(list_top);

		container_points.removeAllViews();
		for (Users point : list_top) {
			addTip(point);
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				updateArraws();
			}
		}, 200);

	}

	private void clearAllTips() {

		if (container_points != null) {
			container_points.removeAllViews();
		}

		if (layout_arrows != null) {
			layout_arrows.removeAllViews();
		}

		if (arrows != null) {
			arrows.clear();
		}
		if (list_top != null) {
			list_top.clear();
		}
	}

	private void addTip(Users user) {

		View tip = inflater.inflate(R.layout.item_map_tip, null);

		TextView item_text = (TextView) tip.findViewById(R.id.item_text);
		ImageView item_image = (ImageView) tip.findViewById(R.id.item_image);

		Content content = user.getContent();
		item_text.setText("#" + content.getTopic() + "#\n" + content.getContent());

		String img = content.getPicture().getSmall();
		// String img = user.getImage();
		if (img.equals("")) {
			item_image.setVisibility(View.GONE);
		} else {
            ImageLoaderCharles.getInstance(getActivity()).addTask(img, item_image);
		}

		LatLng point = new LatLng(user.getLocation_x(), user.getLocation_y());
		Arrow arrow = new Arrow(context);
		Point p_screen = mBaiduMap.getProjection().toScreenLocation(point);
		arrow.setEnd(p_screen);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.leftMargin = 8;
		params.topMargin = 8;

		container_points.addView(tip, params);
		arrows.add(arrow);

		layout_arrows.addView(arrow);

		tip.setTag(user);
		tip.setOnClickListener(tipListener);

	}

	private OnClickListener tipListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			StatService.onEvent(context, "discover_surround_content", "发现页周边内容点击次数", 1);
			Users user = (Users) v.getTag();
			String id = user.getContent().getId();
			Intent intent = new Intent();
			intent.setClass(context, ContentDetailActivity.class);
			intent.putExtra("topic_id", id);
			startActivity(intent);

		}
	};

	private void updateArraws() {

		if (arrows == null) {
			return;
		}

		for (int i = 0; i < arrows.size(); i++) {
			Arrow arrow = arrows.get(i);
			Users user = list_top.get(i);
			LatLng point = new LatLng(user.getLocation_x(), user.getLocation_y());

			View tip = container_points.getChildAt(i);

			int start_x = tip.getRight() - 4;
			int start_y = tip.getTop() + tip.getHeight() / 2 - scrollView.getScrollY();

			if (arrow.start == null || arrow.start.x != start_x || arrow.start.y != start_y) {
				Point p_start = new Point(start_x, start_y);
				arrow.setStart(p_start);
			}

			Point p_screen = mBaiduMap.getProjection().toScreenLocation(point);
			p_screen.x -= 40;
			p_screen.y -= 80;
			if (arrow.end == null || arrow.end.x != p_screen.x || arrow.end.y != p_screen.y) {
				arrow.setEnd(p_screen);
			}

			// text_point.setText("\n\n\n\n"+start_x+","+
			// start_y+"___"+p_screen.x+","+p_screen.y+"\n\n\n\n");
			// Log.i("test", start_x+","+
			// start_y+"___"+p_screen.x+","+p_screen.y);
		}
	}

	// 从上到下排列点
	private void orderPoint(List<Users> list_points) {
		for (int i = 0; i < list_points.size() - 1; i++) {
			boolean flag_exchange = false;
			for (int j = 0; j < list_points.size() - i - 1; j++) {
				Users p = list_points.get(j);
				Users p_next = list_points.get(j + 1);
				if (p.getLocation_x() < p_next.getLocation_x()) {
					list_points.set(j + 1, p);
					list_points.set(j, p_next);

					flag_exchange = true;
				}
			}

			if (!flag_exchange) {
				break;
			}
		}
	}

	private void addImage(final int user_index) {

		if (list_users == null || user_index >= list_users.size()) {
			return;
		}

		new Thread() {
			public void run() {

				Users user = list_users.get(user_index);

				Bitmap bitmap = ImageLoaderCharles.getInstance(getActivity()).getBitmap(user.getImage());
				if (bitmap == null) {
					bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
				}

				int color_border = 0;
				if (user.getSex() == 1 || user.getSex() == 2) {
					color_border = Color.parseColor("#FE7777");
				} else {
					color_border = Color.parseColor("#A4EBE5");
				}

				bitmap = BitmapUtil.getCircleBitmap(bitmap, user_icon_width, color_border, user_icon_width_border);

				Message msg = new Message();
				msg.arg1 = user_index;
				msg.obj = bitmap;
				handler_img_loaded.sendMessage(msg);
			};
		}.start();

	}

	private Handler handler_img_loaded = new Handler() {
		public void handleMessage(Message msg) {

			Bitmap bitmap = (Bitmap) msg.obj;
			int user_index = msg.arg1;

			Users user = list_users.get(user_index);
			LatLng point = new LatLng(user.getLocation_x(), user.getLocation_y());

			// 构建Marker图标
			BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(bitmap);
			// 构建MarkerOption，用于在地图上添加Marker
			MarkerOptions option = new MarkerOptions().position(point).icon(bd);
			option.draggable(true);
			option.zIndex(9);

			Bundle bundle = new Bundle();
			bundle.putString("uid", user.getUid());
			option.extraInfo(bundle);

			// 在地图上添加Marker，并显示
			mBaiduMap.addOverlay(option);
		};
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("test", "Map onDestroy");
		// mMapView.onDestroy();

		/*
		 * try{ mMapView.onDestroy(); }catch(Exception e){ e.printStackTrace();
		 * }
		 */

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("test", "Map onResume");
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("test", "Map onPause");
		mMapView.onPause();
	}

	class LoadingTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(String... url) {
			list_users = new ArrayList<Users>();

			try {
				String urlPath = PathConst.URL_MAP_NEARBY + "&uid=" + LiuLianApplication.current_user.getUid() + "&location_x=" + LiuLianApplication.latitude + "&location_y=" + LiuLianApplication.longtitude + "&r=2000000";

				JSONArray json_users = null;
				if (NetworkUtil.dataConnected(context)) {
					json_users = NetworkUtil.getJsonArray(urlPath, null, 5000);// 5s延迟
				}

				if (json_users != null) {
					for (int i = 0; i < json_users.length(); i++) {
						JSONObject json_user = json_users.getJSONObject(i);

						Users user = new Users();
						user.setUid(json_user.getString("uid"));
						user.setName(json_user.getString("username"));
						user.setImage(json_user.getString("head_pic"));
						user.setSex(json_user.getInt("sex"));
						user.setHx_username(json_user.getString("hx_username"));
						user.setLocation_x(json_user.getDouble("location_x"));
						user.setLocation_y(json_user.getDouble("location_y"));

						JSONObject json_content = json_user.getJSONObject("content");
						Content content = new Content();
						content.setId(json_content.getString("id"));
						content.setContent(json_content.getString("text"));
						content.setTopic(json_content.getString("topic_title"));

						String pic = json_content.getString("pic");
						// if(!"".equals(pic)){
						ContentPicture cPic = new ContentPicture();
						cPic.setSmall(pic);
						content.setPicture(cPic);
						user.setContent(content);
						// }

						list_users.add(user);

					}

				}

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean re) {
			if (list_users != null && list_users.size() > 0) {
				initUsers();

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						addTips();
					}
				}, 1000);
			}
		}
	}

}
