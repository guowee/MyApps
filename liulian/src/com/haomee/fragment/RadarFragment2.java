package com.haomee.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.haomee.adapter.FlowTypesAdapter2;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.Radar_Category;
import com.haomee.entity.TopicType;
import com.haomee.entity.Users;
import com.haomee.liulian.HelpTipActivity;
import com.haomee.liulian.ImgsBrowseActivity_users;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.MainActivity;
import com.haomee.liulian.R;
import com.haomee.util.BitmapUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.util.ViewUtil;
import com.haomee.util.imageloader.ImageLoaderCharles;
import com.haomee.view.FancyCoverFlow;
import com.haomee.view.LoadingDialog;
import com.haomee.view.RadarNode;
import com.haomee.view.SlidingLayout;

public class RadarFragment2 extends Fragment {

	private View view;
	private Activity context;
	private int screen_width;

	private int center_x, center_y;
	private int radar_width;

	private FancyCoverFlow flow_types;

	private ArrayList<RadarNode> nodes;

	private FrameLayout layout_radar, container_node;
	private ImageView radar_light;

	private SeekBar seekBar;

	private int radius_max;
	private int radius_min;

	private View layout_left, layout_right, layout_mask;

	private ImageView img_bg, img_mine;

	private TextView text_title, txt_distance_left, txt_distance_right, txt_num_left, txt_num_right, txt_distance_center;
	private ImageView icon_title;

	private ViewGroup layout_person;
	private HorizontalScrollView scrollView_person;
	private LinearLayout ll_radar_itme_content;
	private int max_distance, min_distance; // 最远的用户距离

	private LoadingDialog loadingDialog;

	private TextView bt_login_recent;

	// private GestureDetector gestureDetector;

	// private HorizontalScrollView horizontal_scroll_layout;

	private SharedPreferences preferences_is_first;

	private ArrayList<TopicType> topicTypes;
	private int topic_type;
	private LoadingTask loadingTask;

	private SlidingLayout slidingLayout;

	private boolean is_first_init = true;// 是否第一次初始化头部选项
	private List<Radar_Category> category_list;

	public RadarFragment2() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (view == null) {
			context = this.getActivity();
			view = inflater.inflate(R.layout.fragment_radar2, null);
			layout_radar = (FrameLayout) view.findViewById(R.id.layout_radar);
			radar_light = (ImageView) view.findViewById(R.id.radar_light);
			container_node = (FrameLayout) view.findViewById(R.id.container_node);
			seekBar = (SeekBar) view.findViewById(R.id.seekBar);

			flow_types = (FancyCoverFlow) view.findViewById(R.id.flow_types);
			flow_types.setSpacing(ViewUtil.dip2px(context, 10));

			// horizontal_scroll_layout=(HorizontalScrollView)
			// view.findViewById(R.id.horizontal_scroll_layout);
			// ll_radar_itme_content=(LinearLayout)
			// view.findViewById(R.id.ll_radar_itme_content);
			loadingDialog = new LoadingDialog(context);

			layout_mask = view.findViewById(R.id.layout_mask);
			layout_left = view.findViewById(R.id.layout_left);
			img_bg = (ImageView) view.findViewById(R.id.img_bg);
			img_mine = (ImageView) view.findViewById(R.id.img_mine);

			txt_distance_left = (TextView) view.findViewById(R.id.txt_distance_left);
			txt_distance_right = (TextView) view.findViewById(R.id.txt_distance_right);
			txt_num_left = (TextView) view.findViewById(R.id.txt_num_left);
			txt_num_right = (TextView) view.findViewById(R.id.txt_num_right);
			txt_distance_center = (TextView) view.findViewById(R.id.txt_distance_center);

			slidingLayout = (SlidingLayout) view.findViewById(R.id.slidingLayout);
			slidingLayout.inital(ViewUtil.dip2px(context, 14), 30, true);

			layout_person = (ViewGroup) view.findViewById(R.id.layout_person);
			scrollView_person = (HorizontalScrollView) view.findViewById(R.id.scrollView_person);

			DisplayMetrics dm = new DisplayMetrics();
			context.getWindowManager().getDefaultDisplay().getMetrics(dm);
			screen_width = dm.widthPixels;

			radar_width = screen_width * 5 / 6;

			LinearLayout.LayoutParams params_radar = (LinearLayout.LayoutParams) layout_radar.getLayoutParams();
			params_radar.width = radar_width;
			params_radar.height = radar_width;
			layout_radar.setLayoutParams(params_radar);

			if (LiuLianApplication.height_fragment_main > 0) {
				ViewGroup.LayoutParams params = img_bg.getLayoutParams();
				params.height = LiuLianApplication.height_fragment_main;
				img_bg.setLayoutParams(params);
			}

			ImageLoaderCharles.getInstance(context).addTask(LiuLianApplication.current_user.getImage(), img_mine);

			layout_radar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!seekBar.isShown()) {
						seekBar.setVisibility(View.VISIBLE);
						txt_distance_center.setVisibility(View.VISIBLE);
						scrollView_person.setVisibility(View.GONE);
						removeSelectedNodes();
					}

				}
			});

			// 顶部自动收起
			/*
			 * new Handler().postDelayed(new Runnable() {
			 * 
			 * @Override public void run() { slidingLayout.close(); } }, 3000);
			 */

		} else {
			((ViewGroup) view.getParent()).removeView(view);
		}

		if (LiuLianApplication.city == null || "null".equals(LiuLianApplication.city)) {
			Toast.makeText(context, "定位失败，请检查网络或GPS", 1).show();
		} else {

			if (topic_type != 0) {
				// 每次恢复到全部
				topic_type = 0;

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						flow_types.requestFocus();
						flow_types.setSelection(category_list.size() * 10);
						flow_types.setSelected(true);
					}

				}, 500);
			}

			// if(nodes==null || nodes.size()==0){ // 每次进来都更新数据
			// loadingDialog.show();

			if (loadingTask != null) {
				loadingTask.cancel(true);
			}

			loadingTask = new LoadingTask();
			loadingTask.execute();
			// }
		}

		return view;
	}

	private void initTop(final List<Radar_Category> category_list) {

		// ImageLoaderCharles
		// mImagLoader=ImageLoaderCharles.getInstance(context);
		// for (int index=0;index<category_list.size();index++){
		// Radar_Category category=category_list.get(index);
		// if(category==null){
		// return;
		// }
		// View add_view =
		// LayoutInflater.from(context).inflate(R.layout.item_topic_type2,
		// null);
		// ImageView item_icon=(ImageView) add_view.findViewById(R.id.item_img);
		// TextView item_title=(TextView)
		// add_view.findViewById(R.id.item_title);
		// RelativeLayout rl_item_content=(RelativeLayout)
		// add_view.findViewById(R.id.rl_item_content);
		// item_title.setText(category.getName());
		// mImagLoader.addTask(category.getPic(), item_icon);
		// rl_item_content.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		//
		// ll_radar_itme_content.addView(add_view);
		// }

		flow_types.setAdapter(new FlowTypesAdapter2(context, category_list));
		flow_types.setSelection(category_list.size() * 10);
		flow_types.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				int current_position = position % category_list.size();
				int index = Integer.parseInt(category_list.get(current_position).getId());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}

		});

		flow_types.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {

					loadingDialog.show();

					// 加延时，不然滚动还没有停止。
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							int current_position = flow_types.getSelectedItemPosition() % category_list.size();
							int index = Integer.parseInt(category_list.get(current_position).getId());
							if (topic_type != index) {
								topic_type = index;

								if (loadingTask != null) {
									loadingTask.cancel(true);
								}

								loadingTask = new LoadingTask();
								loadingTask.execute();
							} else {
								loadingDialog.dismiss();
							}
						}
					}, 500);

				}
				return false;
			}
		});
	}

	private void showHelpTip() {
		Intent intent = new Intent();
		intent.putExtra("from", "tip_radar");
		intent.setClass(this.getActivity(), HelpTipActivity.class);
		this.startActivity(intent);

		Editor editor = preferences_is_first.edit();
		editor.putBoolean("is_first_tip_radar", false);
		editor.commit();
	}

	private int pic_index = 0; // 背景图片

	private void setRadarBackground() {

		if (list_pics != null && list_pics.size() > 0) {

			if (pic_index >= list_pics.size()) {
				pic_index = 0;
			}
			final String url_pic = list_pics.get(pic_index);

			new AsyncTask<Object, Object, Bitmap>() {

				Bitmap bg_blur = null;

				@Override
				protected Bitmap doInBackground(Object... params) {

					Bitmap bg_img = ImageLoaderCharles.getInstance(context).getBitmap(url_pic);

					try {
						if (bg_img != null) {
							bg_blur = BitmapUtil.fastblur(context, bg_img, 10, true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					return bg_img;
				}

				protected void onPostExecute(final Bitmap result) {

					if (result != null) {

						img_bg.setImageBitmap(result);

						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								if (bg_blur != null) {
									img_bg.setImageBitmap(bg_blur);
								}
							}
						}, 3000);

					}
				};

			}.execute();

			pic_index++;

		}

	}

	private Handler handler_changeBackground = new Handler() {
		public void handleMessage(Message msg) {
			setRadarBackground();
			handler_changeBackground.sendEmptyMessageDelayed(0, 30000); // 多长时间更新背景
		};
	};

	private String getDistanceText(int distance) {
		String str_distance = null;
		if (distance > 1000) {
			str_distance = String.format("%.1f", distance * 1.0f / 1000) + " Km";
		} else {
			str_distance = distance + " M";
		}
		return str_distance;
	}

	private void initNodes() {

		container_node.removeAllViews();

		if (list_users == null || list_users.size() == 0) {
			return;
		}

		min_distance = list_users.get(0).getDistance();
		max_distance = list_users.get(list_users.size() - 1).getDistance();

		// start_progress = list_users.get(list_users.size()/2).getDistance();
		// // 中间的
		seekBar.setMax(list_users.size() - 1);
		seekBar.setProgress(list_users.size() / 2);

		String str_distance = getDistanceText(list_users.get(list_users.size() / 2).getDistance());
		txt_distance_center.setText(list_users.size() / 2 + " People in " + str_distance);

		center_x = radar_width / 2;
		center_y = radar_width / 2;

		radius_max = radar_width / 2 - ViewUtil.dip2px(context, 15); // 增加边界的偏移
		radius_min = ViewUtil.dip2px(context, 46);

		nodes = new ArrayList<RadarNode>();

		Random rdm = new Random();
		for (int i = 0; i < list_users.size(); i++) {
			Users user = list_users.get(i);
			int r_start = user.getDistance() * radius_max / max_distance;

			if (r_start < radius_min) {
				r_start = radius_min;
			}

			int degree = rdm.nextInt(360); // 随机角度

			RadarNode node = new RadarNode(context);
			node.user = user;
			node.init(center_x, center_y, r_start, degree);

			int r = r_start + (radius_max - radius_min) / 2;

			if (r > radius_max) {
				r = radius_max;
			}

			node.setRadius(r);

			if (i > list_users.size() / 2) {
				node.setVisibility(View.GONE);
			}

			if (user.getSex() == 1 || user.getSex() == 2) {
				node.setImageResource(R.drawable.radar_icon_w);
			} else {
				node.setImageResource(R.drawable.radar_icon_m);
			}

			container_node.addView(node);

			node.setOnClickListener(nodeListener);

			nodes.add(node);

		}

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				layout_mask.setVisibility(View.VISIBLE);
				txt_distance_center.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if (list_users == null || list_users.size() == 0) {
					return;
				}

				StatService.onEvent(context, "radar_area_seek", "雷达区域控制条滑动次数", 1);
				layout_mask.setVisibility(View.GONE);
				txt_distance_center.setVisibility(View.VISIBLE);

				String str_distance = getDistanceText(list_users.get(seekBar.getProgress()).getDistance());
				txt_distance_center.setText((seekBar.getProgress() + 1) + " People in " + str_distance);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (list_users == null || list_users.size() == 0) {
					return;
				}

				int progress_max = list_users.size();

				if (progress_max == 0) {
					return;
				}

				for (int i = 0; i < nodes.size(); i++) {

					RadarNode node = nodes.get(i);
					int r = node.radius_start + (radius_max - radius_min) * (progress_max - progress) / progress_max;

					if (r < radius_min) {
						r = radius_min;
					}

					node.setRadius(r);
					if (i > progress) {
						node.setVisibility(View.GONE);
					} else {
						node.setVisibility(View.VISIBLE);
					}
				}

				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout_left.getLayoutParams();
				params.width = screen_width * progress / progress_max;
				layout_left.setLayoutParams(params);

				int distance = list_users.get(progress).getDistance();
				String str_distance = getDistanceText(distance);

				if (progress < progress_max / 2) {
					txt_distance_left.setVisibility(View.GONE);
					txt_num_left.setVisibility(View.GONE);
					txt_distance_right.setVisibility(View.VISIBLE);
					txt_num_right.setVisibility(View.VISIBLE);

					txt_distance_right.setText(str_distance);
					txt_num_right.setText((progress + 1) + " People");
				} else {
					txt_distance_left.setVisibility(View.VISIBLE);
					txt_num_left.setVisibility(View.VISIBLE);
					txt_distance_right.setVisibility(View.GONE);
					txt_num_right.setVisibility(View.GONE);

					txt_distance_left.setText(str_distance);
					txt_num_left.setText((progress + 1) + " People");
				}
			}
		});

	}

	private double getDistance(RadarNode node1, RadarNode node2) {
		double _x = node1.x - node2.x;
		double _y = node1.y - node2.y;
		double r = Math.sqrt(_x * _x + _y * _y);
		return r;
	}

	private OnClickListener nodeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			RadarNode node = (RadarNode) v;
			node.setBackgroundResource(R.drawable.radar_icon_selected);

			StatService.onEvent(context, "radar_area", "雷达区域的点击次数", 1);
			setAroundNodes(node);

			setAroundPerson();

			seekBar.setVisibility(View.GONE);
			txt_distance_center.setVisibility(View.GONE);
			scrollView_person.setVisibility(View.VISIBLE);

			// Log.i("test", "onClick"+node.x+","+node.y);
		}
	};

	private List<RadarNode> selected_nodes;

	private void setAroundNodes(RadarNode node_selected) {

		if (selected_nodes != null) {
			for (RadarNode node : selected_nodes) {
				node.setBackgroundResource(0);
			}
		}

		selected_nodes = new ArrayList<RadarNode>();
		for (RadarNode node : nodes) {
			if (node.isShown() && getDistance(node_selected, node) < node.node_size * 2) {
				node.setBackgroundResource(R.drawable.radar_icon_selected);

				selected_nodes.add(node);
			}
		}
	}

	private void removeSelectedNodes() {
		if (selected_nodes != null) {
			for (RadarNode node : selected_nodes) {
				node.setBackgroundResource(0);
			}
			selected_nodes.clear();
		}
	}

	private void setAroundPerson() {
		LayoutInflater inflater = LayoutInflater.from(context);

		layout_person.removeAllViews();

		users_selected = new ArrayList<Users>();

		// int count = 0;
		for (int i = 0; i < selected_nodes.size(); i++) {

			if (i > 12) { // 最多12个
				break;
			}

			View item = inflater.inflate(R.layout.item_radar_person, null);
			ImageView item_image = (ImageView) item.findViewById(R.id.item_image);
			TextView item_title = (TextView) item.findViewById(R.id.item_title);
			TextView item_distance = (TextView) item.findViewById(R.id.item_distance);

			RadarNode node = selected_nodes.get(i);
			users_selected.add(node.user);

			item_title.setText(node.user.getName());

			String str_distance = null;
			if (node.user.getDistance() > 1000) {
				str_distance = node.user.getDistance() * 1.0f / 1000 + " Km";
			} else {
				str_distance = node.user.getDistance() + " M";
			}
			item_distance.setText(str_distance);

			ImageLoaderCharles.getInstance(context).addTask(node.user.getImage(), item_image);

			LinearLayout.LayoutParams params_item = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params_item.leftMargin = 6;
			params_item.rightMargin = 6;
			params_item.bottomMargin = 10;

			item.setTag(i);
			item.setOnClickListener(userListener);

			layout_person.addView(item, params_item);

		}
	}

	private ArrayList<Users> users_selected;

	private OnClickListener userListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int index = (Integer) v.getTag();
			StatService.onEvent(context, "radar_user_icon", "雷达页用户头像点击次数", 1);
			if (users_selected.get(index).getUid().equals(LiuLianApplication.current_user.getUid())) {

				Toast.makeText(RadarFragment2.this.getActivity(), "这是你自己哦", 1).show();

			} else {
				Intent intent = new Intent();
				intent.setClass(context, ImgsBrowseActivity_users.class);
				intent.putExtra("users", users_selected);
				intent.putExtra("index", index);
				startActivity(intent);
			}
		}
	};

	private int current_angle = 0;

	@SuppressLint("NewApi")
	private void rotate(double angle) {

		current_angle += angle;
		current_angle = current_angle % 360;

		radar_light.setRotation(current_angle);
	}

	private Handler handler_anim = new Handler() {
		public void handleMessage(Message msg) {

			if (android.os.Build.VERSION.SDK_INT >= 11) { // 低版本手机不支持旋转

				handler_anim.sendEmptyMessageDelayed(0, 30);

				if (nodes != null && nodes.size() > 0) {
					rotate(2);

					int radar_degree = 360 - current_angle;
					// Log.i("test", "radar_degree:"+radar_degree);

					for (RadarNode node : nodes) {
						if (node.isShown() && Math.abs(node.degree - radar_degree) < 5) {
							if (selected_nodes != null && selected_nodes.contains(node)) {
							} else {
								node.startAnim();
							}
						}
					}
				}
			}
		};
	};

	public void onResume() {
		super.onResume();
		// Log.i("test", "fragment onResume");

		handler_anim.sendEmptyMessageDelayed(0, 1000);
		handler_changeBackground.sendEmptyMessageDelayed(0, 15000); // 进入界面后多少秒换背景

	};

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			// 相当于Fragment的onResume
			MainActivity.setTopBar(0);
			// 弹出提示
			preferences_is_first = this.getActivity().getSharedPreferences(CommonConst.PREFERENCES_FIRST, Context.MODE_PRIVATE);
			boolean is_first_open = preferences_is_first.getBoolean("is_first_tip_radar", true);
			if (is_first_open) {
				showHelpTip();
			}
		} else {
			// 相当于Fragment的onPause
		}
	}

	public void onPause() {
		super.onPause();

		// Log.i("test", "fragment onPause");

		handler_anim.removeMessages(0);
		handler_changeBackground.removeMessages(0);

		if (nodes != null) {
			for (RadarNode node : nodes) { // 还原动画状态
				node.is_anim_started = false;
			}
		}

	};

	private List<String> list_pics;
	private List<Users> list_users;

	class LoadingTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(String... url) {
			list_pics = new ArrayList<String>();
			list_users = new ArrayList<Users>();

			if (LiuLianApplication.city == null || "null".equals(LiuLianApplication.city)) {
				// Toast.makeText(context, "定位失败，请检查网络或GPS", 1).show();
				return false;
			}

			try {
				String urlPath = PathConst.URL_RADAR_NEARBY + "&uid=" + LiuLianApplication.current_user.getUid() + "&location_x=" + LiuLianApplication.latitude + "&location_y=" + LiuLianApplication.longtitude + "&category=" + topic_type + "&needCat=1";

				String str_json = null;
				if (NetworkUtil.dataConnected(context)) {
					str_json = NetworkUtil.getHttpString(urlPath, null, 5000);// 5s延迟
				}

				JSONObject json = null;
				if (str_json != null) {
					json = new JSONObject(str_json);
				}

				if (json != null) {
					JSONArray json_pics = json.getJSONArray("pics");
					for (int i = 0; i < json_pics.length(); i++) {
						String pic = json_pics.getString(i);
						list_pics.add(pic);
					}

					JSONArray json_users = json.getJSONArray("users");
					for (int i = 0; i < json_users.length(); i++) {
						JSONObject json_user = json_users.getJSONObject(i);

						Users user = new Users();
						user.setUid(json_user.getString("uid"));
						user.setName(json_user.getString("username"));
						user.setImage(json_user.getString("head_pic"));
						user.setBack_pic(json_user.optString("back_pic"));
						user.setCity(json_user.getString("city"));
						user.setSex(json_user.getInt("sex"));
						user.setAge(json_user.getString("age"));
						user.setHx_username(json_user.getString("hx_username"));
						user.setIs_sayhi(json_user.optBoolean("is_sayHi"));
						int distance = (int) (json_user.getDouble("distance") * 1000);
						user.setDistance(distance);

						list_users.add(user);

					}

					category_list = new ArrayList<Radar_Category>();
					JSONArray json_category = json.getJSONArray("category");
					for (int i = 0; i < json_category.length(); i++) {
						JSONObject json_cate = json_category.getJSONObject(i);

						Radar_Category category = new Radar_Category();
						category.setId(json_cate.optString("id"));
						category.setName(json_cate.optString("name"));
						category.setPic(json_cate.optString("pic"));

						category_list.add(category);
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
			loadingDialog.dismiss();

			if (re && list_users != null) {
				initNodes();
			} else {
				Toast.makeText(context, "获取数据失败！", Toast.LENGTH_SHORT).show();
			}

			if (re && is_first_init) {
				if (category_list == null || category_list.size() == 0) {
					return;
				}

				initTop(category_list);
				is_first_init = false;
			}
		}
	}

}
