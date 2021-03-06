package com.haomee.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.haomee.util.imageloader.ImageLoaderCharles;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haomee.adapter.TopicRecAdapter;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.ShareContent;
import com.haomee.entity.Topic;
import com.haomee.liulian.AddTopicActivity;
import com.haomee.liulian.BaseFragment;
import com.haomee.liulian.HelpTipActivity;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.LoginPageActivity;
import com.haomee.liulian.MainActivity;
import com.haomee.liulian.PunchClockActivity;
import com.haomee.liulian.R;
import com.haomee.liulian.ShareMedalActivity;
import com.haomee.liulian.TopicTypeActivity2;
import com.haomee.liulian.TopicsDetailActivity;
import com.haomee.liulian.WebPageActivity;
import com.haomee.player.MySoundPlayer;
import com.haomee.util.NetworkUtil;
import com.haomee.util.ViewUtil;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class DiscoverFragment extends BaseFragment {

	private FragmentActivity context;
	private View view;
	private PullToRefreshListView pull_ListView;

	private LoadingRecTask loading_rec_task;
	private List<Topic> topic_list_all;
	private String last_id = "";
	private boolean have_next;

	private SharedPreferences preferences_is_first;
	private SharedPreferences preference;
	SharedPreferences.Editor editor = null;

	public static Bitmap view_bitmap; // 用于下个界面的毛玻璃

	private TopicRecAdapter topicAdapter;
	private View view_head;

	private View layout_blank_tip;
	private TextView tip1, tip2, bt_refresh;

	private View footer_loading;
	private LoadingDialog loadingDialog;

	private int topic_type;

	public static final int REQUEXTCODE = 2;// 相应刷新数据

	private static final int requestCode_select_type = 1;
	private boolean is_first_toast = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i("test", "onCreateView CartoonFragment");

		context = getActivity();

		if (view == null) {
			context = getActivity();
			preference = context.getSharedPreferences(CommonConst.PREFERENCES_MEDAL, Activity.MODE_PRIVATE);
			editor = preference.edit();
			view = inflater.inflate(R.layout.fragment_discover3, null);

			pull_ListView = (PullToRefreshListView) view.findViewById(R.id.list_rec);

			footer_loading = inflater.inflate(R.layout.refresh_footer_loading, null);
			footer_loading.setVisibility(View.GONE);
			pull_ListView.getRefreshableView().addFooterView(footer_loading, null, false);

			init_topic_rec_view();

			context = (MainActivity) this.getActivity();
			preference = context.getSharedPreferences(CommonConst.PREFERENCES_MEDAL, Activity.MODE_PRIVATE);
			editor = preference.edit();
			preferences_is_first = context.getSharedPreferences(CommonConst.PREFERENCES_FIRST, Context.MODE_PRIVATE);

			initBlankTip();

			view.findViewById(R.id.bt_add).setOnClickListener(btClickListener);
			loadingDialog = new LoadingDialog(context);
			if (topic_list_all == null || topic_list_all.size() == 0) {
				loadingDialog.show();
				reloadData();
			}
		} else {
			((ViewGroup) view.getParent()).removeView(view);

		}

		return view;
	}

	private void initBlankTip() {
		layout_blank_tip = view.findViewById(R.id.layout_blank_tip);
		tip1 = (TextView) layout_blank_tip.findViewById(R.id.tip1);
		tip2 = (TextView) layout_blank_tip.findViewById(R.id.tip2);
		bt_refresh = (TextView) layout_blank_tip.findViewById(R.id.bt_refresh);
		hideBlankTip();
	}

	// 空白页提示
	public void showBlankTip(String t1, String t2, boolean bt_refresh_visible) {
		layout_blank_tip.setVisibility(View.VISIBLE);

		tip1.setText(t1);
		tip2.setText(t2);

		bt_refresh.setVisibility(bt_refresh_visible ? View.VISIBLE : View.GONE);
	}

	public void hideBlankTip() {
		layout_blank_tip.setVisibility(View.GONE);
	}

	public View getRefreshButton() {
		return bt_refresh;
	}

	private void showHelpTip() {
		Intent intent = new Intent();
		intent.putExtra("from", "tip_daka");
		intent.setClass(context, HelpTipActivity.class);
		this.startActivity(intent);

		Editor editor = preferences_is_first.edit();
		editor.putBoolean("is_first_tip_main", false);
		editor.commit();
	}

	private OnClickListener btClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_add: {
				if (LiuLianApplication.current_user == null) {
					gotoLogin();
					return;
				}

				Intent intent = new Intent();
				intent.setClass(context, AddTopicActivity.class);
				// startActivityForResult(intent, REQUEXTCODE);
				startActivity(intent);
				StatService.onEvent(context, "main_discover_add", "发现页添加话题点击次数", 1);
				break;
			}
			}
		}
	};

	private void gotoLogin() {
		Intent intent = new Intent();
		intent.setClass(context, LoginPageActivity.class);
		startActivity(intent);
		getActivity().finish();
		MyToast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == requestCode_select_type) {
			if (data != null) {
				int type = data.getIntExtra("type", -1);
				if (type != -1) {
					loadingDialog.show();

					topic_list_all = new ArrayList<Topic>();
					last_id = "";
					topic_type = type;
					reloadData();
				}
			}

			view_bitmap = null; // 释放
		}
		// else if(requestCode==REQUEXTCODE){//回调7
		// if(data!=null){
		// Topic topicResult = (Topic) data.getSerializableExtra("topic");
		// if(topicResult!=null){
		// topic_list_all.add(0, topicResult);
		// topic_type = 0;
		// topicAdapter.notifyDataSetChanged();
		// }
		// }
		//
		//
		// }
	};

	private void reloadData() {

		if (loading_rec_task != null) {
			loading_rec_task.cancel(true);
		}
		loading_rec_task = new LoadingRecTask();
		loading_rec_task.execute();
	}

	private ImageView header_bg;
	private ImageView icon_float;
	private ImageView anim_drag;
	private AnimationDrawable animationDrawable;

	private void init_header() {

		final View header = view_head.findViewById(R.id.header);
		icon_float = (ImageView) header.findViewById(R.id.icon_float);
		header_bg = (ImageView) header.findViewById(R.id.header_bg);
		anim_drag = (ImageView) header.findViewById(R.id.anim_drag);

		// 顶部宽高
		int width = ViewUtil.getScreenWidth(context);
		int height = width * 5 / 12;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
		header_bg.setLayoutParams(params);

		header.setOnTouchListener(new OnTouchListener() {

			private boolean is_down;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (is_down) {
					v.getParent().requestDisallowInterceptTouchEvent(true); // 防止和竖向滚动干扰
				}

				int x = (int) event.getX();
				switch (event.getAction()) {

				// 主点按下
				case MotionEvent.ACTION_DOWN: {
					if (x < icon_float.getWidth()) {
						v.getParent().requestDisallowInterceptTouchEvent(true); // 防止和竖向滚动干扰
						is_down = true;
						icon_float.clearAnimation();
						icon_float.setImageResource(R.anim.discover_icon_float);
						if (animationDrawable != null) {
							animationDrawable.stop();
						}
						animationDrawable = (AnimationDrawable) icon_float.getDrawable();
						animationDrawable.start();

						anim_drag.setVisibility(View.INVISIBLE);

					}

					break;
				}
				case MotionEvent.ACTION_UP: {
					if (is_down) {
						if (x > header.getWidth() * 3 / 4) {
							// 没登陆跳转登陆页
							if (LiuLianApplication.current_user == null) {
								Intent intent = new Intent();
								intent.setClass(context, LoginPageActivity.class);
								startActivity(intent);
								getActivity().finish();
								MyToast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
							} else {
								Intent intent = new Intent();
								intent.setClass(getActivity(), PunchClockActivity.class);
								getActivity().startActivity(intent);
								StatService.onEvent(context, "main_discover_punch_card", "发现页打卡点击次数", 1);

								MySoundPlayer.getInstance(context).play_background(R.raw.sound_click, false);

							}

							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									moveBackIcon(-icon_float.getWidth());
								}
							}, 500);

						} else {
							moveBackIcon(x - icon_float.getWidth());
						}

					}

					is_down = false;
					break;
				}
				case MotionEvent.ACTION_MOVE: {
					if (is_down) {
						// Log.i("test", "ACTION_MOVE " + x);
						int start = x - icon_float.getWidth();
						if (start > -icon_float.getWidth() / 2) {
							moveIcon(x - icon_float.getWidth());
						}
					}
					break;
				}
				}
				return true;
			}
		});
	}   
	private void moveIcon(int x) {
		RelativeLayout.LayoutParams params = (LayoutParams) icon_float.getLayoutParams();
		params.leftMargin = x;
		icon_float.setLayoutParams(params);
	}
	private int icon_x;
	private void moveBackIcon(int start_x) {
		icon_x = start_x;
		handler_goback.sendEmptyMessage(0);
		icon_float.clearAnimation();
		icon_float.setImageResource(R.anim.discover_icon);
		animationDrawable.stop();
		animationDrawable = (AnimationDrawable) icon_float.getDrawable();
		animationDrawable.start();
	}

	private Handler handler_goback = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			if (icon_x > -icon_float.getWidth() / 2) {
				icon_x -= 60;
				moveIcon(icon_x);
				handler_goback.sendEmptyMessageDelayed(0, 60);
			} else {
				moveIcon(-icon_float.getWidth() / 2);
				handler_goback.removeMessages(0);
				anim_drag.setVisibility(View.VISIBLE);
			}
		}
	};

	// private RotateAnimation rotateAnimation;
	// private ImageView icon_loading;

	private void init_topic_rec_view() {

		topic_list_all = new ArrayList<Topic>();
		topicAdapter = new TopicRecAdapter(getActivity(), LiuLianApplication.current_user.getUid(),pull_ListView);

		pull_ListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// 背景重复出现的bug
				if (topic_list_all != null && topic_list_all.size() > 0) {
					pull_ListView.getChildAt(0).setVisibility(View.INVISIBLE);
				}

				// Do work to refresh the list here.
				if (NetworkUtil.dataConnected(context)) {
					topic_list_all = new ArrayList<Topic>();
					last_id = "";
					reloadData();
				} else {
					MyToast.makeText(getActivity(), context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
					pull_ListView.onRefreshComplete();
				}
			}
		});

		pull_ListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if (NetworkUtil.dataConnected(context)) {
					if (!have_next) {
						if (is_first_toast) {
							is_first_toast = false;
							MyToast.makeText(getActivity(), context.getResources().getString(R.string.is_the_last_page), 1).show();
						}

					} else {
						// 彩蛋埋点儿 向下刷新等于100次
						if (preference.getInt(CommonConst.LOAD_MAX, 0) == 100) {
							addMedal();
						} else {
							editor.putInt(CommonConst.LOAD_MAX, preference.getInt(CommonConst.LOAD_MAX, 0) + 1);
							editor.commit();
						}

						footer_loading.setVisibility(View.VISIBLE);
						reloadData();
					}
				} else {
					MyToast.makeText(getActivity(), context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
					pull_ListView.onRefreshComplete();
				}
			}
		});

		pull_ListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (LiuLianApplication.current_user == null) {
					gotoLogin();
					return;
				}

				Topic topic = topic_list_all.get(position - 2);
				if (topic.getGoto_type() == 1) {

					Intent intent = new Intent();
					// intent.setClass(context, TopicDetailActivity.class);
					intent.setClass(context, TopicsDetailActivity.class);
					intent.putExtra("topic_id", topic.getId());
					startActivity(intent);
					StatService.onEvent(context, "item_of_topic_list", "发现页点击进入兴趣话题的次数", 1);
				} else if (topic.getGoto_type() == 2) {
					Intent intent = new Intent();
					intent.setClass(context, WebPageActivity.class);
					intent.putExtra("title", topic.getTitle());
					intent.putExtra("url", topic.getGoto_url());
					startActivity(intent);
				}

				MySoundPlayer.getInstance(context).play_background(R.raw.sound_click, false);

			}
		});

	}

	/** 加载推荐话题列表 */
	class LoadingRecTask extends AsyncTask<String, Integer, List<Topic>> {

		private boolean is_black = false; // 是否被列入黑名单，直接退出
		private String url_header_bg;

		@Override
		protected List<Topic> doInBackground(String... url) {

			ArrayList<Topic> topic_list = new ArrayList<Topic>();
			try {

				String urlPath = PathConst.URL_TOPIC_REC + "&limit=10" + "&last_id=" + last_id + "&category=" + topic_type;

				try {
					JSONObject json = NetworkUtil.getJsonObject(urlPath, null, 5000);

					Log.e("返回数据", "");

					if (json != null) {
						have_next = json.getBoolean("have_next");
						last_id = json.getString("last_id");
						url_header_bg = json.optString("pic");
						is_black = json.optBoolean("is_black");
						JSONArray array = json.getJSONArray("list");
						for (int i = 0; i < array.length(); i++) {
							JSONObject item_topic = array.getJSONObject(i);
							Topic topic = new Topic();
							topic.setId(item_topic.getString("id"));
							topic.setTitle(item_topic.optString("title"));
							topic.setCreate_time(item_topic.optString("create_time"));
							topic.setUser_num(item_topic.optInt("user_num"));
							topic.setBack_img(item_topic.optString("back_img"));
							topic.setCategory_icon(item_topic.optString("category_icon"));
							topic.setGoto_type(item_topic.optInt("goto_type"));
							topic.setGoto_url(item_topic.optString("url"));
							topic.setIs_recTopic(item_topic.optBoolean("is_top"));
							topic_list.add(topic);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return topic_list;
		}

		@Override
		protected void onPostExecute(List<Topic> topic_list) {

			loadingDialog.dismiss();

			// 黑名单强行退出登录。
			if (is_black) {
				LiuLianApplication.getInstance().logout();
			}

			if (view_head == null) {
				view_head = LayoutInflater.from(context).inflate(R.layout.rec_head, null);
				init_header();
				pull_ListView.getRefreshableView().addHeaderView(view_head);

				icon_float.setImageResource(R.anim.discover_icon);
				icon_float.clearAnimation();
				animationDrawable = (AnimationDrawable) icon_float.getDrawable();
				animationDrawable.start();

				((AnimationDrawable) anim_drag.getDrawable()).start();
				// 弹出提示
				boolean is_first_open = preferences_is_first.getBoolean("is_first_tip_main", true);
				if (is_first_open) {
					showHelpTip();
				}
			}

			if (topic_list != null) {
				if (topic_list_all == null || topic_list_all.size() == 0) {// 第一次加载
					topic_list_all = topic_list;
					ImageLoaderCharles.getInstance(getActivity()).addTask(url_header_bg, header_bg);

					pull_ListView.setAdapter(topicAdapter);

				} else {
					topic_list_all.addAll(topic_list);
				}
				topicAdapter.setData(topic_list_all);
			}

			footer_loading.setVisibility(View.GONE);
			pull_ListView.onRefreshComplete();

		}
	}

	public void addMedal() {
		String url = PathConst.URL_ADD_MEDAL + "&uid=" + LiuLianApplication.current_user.getUid() + "&Luid=" + LiuLianApplication.current_user.getUid() + "&id=5";
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					JSONObject egg_obj = new JSONObject(arg0);
					if (egg_obj.getBoolean("is_new")) {
						Intent intent_send = new Intent();
						intent_send.setClass(context, ShareMedalActivity.class);
						ShareContent share = new ShareContent();
						share.setId(egg_obj.getString("id"));
						share.setTitle(egg_obj.getString("name"));
						share.setSummary(egg_obj.getString("desc"));
						share.setImg_url(egg_obj.getString("icon"));
						share.setRedirect_url(CommonConst.GOV_URL);
						intent_send.putExtra("share", share);
						context.startActivity(intent_send);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			// 相当于Fragment的onResume
			MainActivity.setTopBar(2);
		} else {
		}
	}
}
