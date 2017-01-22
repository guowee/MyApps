package com.haomee.fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
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
import com.haomee.adapter.ChestAdapter;
import com.haomee.adapter.InterestAdapter;
import com.haomee.adapter.PicAdapter;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.Chest;
import com.haomee.entity.ShareContent;
import com.haomee.entity.Topic;
import com.haomee.entity.Users;
import com.haomee.liulian.AlbumActivity;
import com.haomee.liulian.BaseFragment;
import com.haomee.liulian.ImgsBrowseActivity_Single;
import com.haomee.liulian.ImgsBrowseActivity_users;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.MainActivity;
import com.haomee.liulian.R;
import com.haomee.liulian.ShareMedalActivity;
import com.haomee.liulian.TopicsDetailActivity;
import com.haomee.liulian.WebPageActivity;
import com.haomee.liulian.upyun.UpYunException;
import com.haomee.liulian.upyun.UpYunUtils;
import com.haomee.liulian.upyun.Uploader;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.util.ViewUtil;
import com.haomee.util.imageloader.ImageLoaderCharles;
import com.haomee.view.CircleImageView;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sina.weibo.sdk.utils.LogUtil;

public class PersonalFragment extends BaseFragment {

	private View view;
	private com.haomee.view.CircleImageView iv_icon;
	private TextView tv_u_name, tv_join_date;
	private TextView pic_tag, topic_chatted, label;

	private Activity activity_context;
	private PullToRefreshListView listview_content;
	private InterestAdapter content_new_adapter;
	private static PicAdapter pic_adapter;
	LoadingPicTask loading_pic_task;
	LoadingTopicTask loading_topic_task;
	LoadingLabelTask loading_label_task;
	private boolean have_next_topic = false;
	private String last_id_content;
	private String uid = "";
	private List<Topic> list_all_content;
	private List<Chest> list_chest;
	private List<Topic> list_content;

	public String pic_large = "";
	public String pic_small = "";
	private View View_head;

	private int current_list_content = 1;
	public static final int REQUEST_DETAIL = 1000;
	private int location = 0;
	SharedPreferences.Editor editor = null;

	static SharedPreferences preference_person;

	private View layout_blank_tip;
	private LinearLayout layout_user;
	private ChestAdapter chestAdapter;
	private ArrayList<Users> list_view_user;
	private EditText tv_signature;
	private TextView tv_age;
	private View v_pic_tag, v_label, v_topic_chatted;
	private String dir_temp;
	public static File vFile;
	public File tempFile;
	private String head_pic = "";
	private Dialog dialog;
	private static PersonalFragment personalFragment1;
	private static ArrayList<String> str_list_all_small, str_list_all_big;
	private LinearLayout top_bg;
	private TextView tv_ok;
	private InputMethodManager imm;
	private LoadingDialog loadingDialog;
	private static ImageView iv_bg;
	private ImageView user_level_icon;
	private int num = 0;
	// private TextView tv_Visitor;

	public static final int PHOTORESOULT = 3;// 结果
	public static final int CROPIMAGES = 4;
	private String picturePath;

	public static PersonalFragment getPersonalFragment1() {

		if (personalFragment1 == null) {

			personalFragment1 = new PersonalFragment();
		}
		return personalFragment1;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i("test", "onCreateView CartoonFragment");

		if (view == null) {

			LiuLianApplication.initLocation();
			activity_context = getActivity();

			loadingDialog = new LoadingDialog(getActivity());

			preference_person = activity_context.getSharedPreferences(CommonConst.PREFERENCES_SETTING, Activity.MODE_PRIVATE);

			uid = LiuLianApplication.current_user.getUid();

			DisplayMetrics dm = new DisplayMetrics();
			this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

			view = inflater.inflate(R.layout.fragment_personal_1, null);

			listview_content = (PullToRefreshListView) view.findViewById(R.id.list_content);

			View_head = inflater.inflate(R.layout.person_head_1, null);
			ListView listView = listview_content.getRefreshableView();
			listView.addHeaderView(View_head);

			user_level_icon = (ImageView) View_head.findViewById(R.id.user_level_icon);
			iv_icon = (CircleImageView) View_head.findViewById(R.id.iv_icon);
			tv_u_name = (TextView) View_head.findViewById(R.id.tv_u_name);
			tv_join_date = (TextView) View_head.findViewById(R.id.tv_join_date);
			pic_tag = (TextView) View_head.findViewById(R.id.pic_tag);
			topic_chatted = (TextView) View_head.findViewById(R.id.topic_chatted);
			layout_user = (LinearLayout) View_head.findViewById(R.id.layout_user);
			label = (TextView) View_head.findViewById(R.id.label);
			tv_signature = (EditText) View_head.findViewById(R.id.tv_signature);
			tv_age = (TextView) View_head.findViewById(R.id.tv_age);
			v_pic_tag = View_head.findViewById(R.id.v_pic_tag);
			v_label = View_head.findViewById(R.id.v_label);
			v_topic_chatted = View_head.findViewById(R.id.v_topic_chatted);
			top_bg = (LinearLayout) View_head.findViewById(R.id.top_bg);
			tv_ok = (TextView) View_head.findViewById(R.id.tv_ok);
			iv_bg = (ImageView) View_head.findViewById(R.id.iv_bg);
			// tv_Visitor = (TextView) View_head.findViewById(R.id.tv_Visitor);

			iv_icon.setOnClickListener(myClickListener);
			pic_tag.setOnClickListener(myClickListener);
			topic_chatted.setOnClickListener(myClickListener);
			label.setOnClickListener(myClickListener);

			content_new_adapter = new InterestAdapter(activity_context);
			chestAdapter = new ChestAdapter(activity_context);
			pic_adapter = new PicAdapter(activity_context);

			listview_content.setAdapter(pic_adapter);
			listview_content.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (position > 1) {
						location = position - 2;
						if (current_list_content == 3) {
							Topic topic = list_all_content.get(location);
							if (topic.getGoto_type() == 1) {
								Intent intent = new Intent();
								intent.setClass(getActivity(), TopicsDetailActivity.class);
								intent.putExtra("topic_id", topic.getId());
								startActivity(intent);
							} else if (topic.getGoto_type() == 2) {
								Intent intent = new Intent();
								intent.setClass(getActivity(), WebPageActivity.class);
								intent.putExtra("title", topic.getTitle());
								intent.putExtra("url", topic.getGoto_url());
								startActivity(intent);
							}
						}
					}
				}
			});

			num = tv_signature.getText().length();

			tv_signature.addTextChangedListener(new TextWatcher() {

				private CharSequence temp;
				private int selectionEnd;

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// TODO Auto-generated method stub
					temp = s;
				}

				@Override
				public void afterTextChanged(Editable s) {
					selectionEnd = tv_signature.getSelectionEnd();
					if (temp.length() > num) {
						tv_ok.setVisibility(View.VISIBLE);
						int tempSelection = selectionEnd;
						tv_signature.setSelection(tempSelection);// 设置光标在最后
					}
				}
			});

			tv_ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					StatService.onEvent(activity_context, "main_person_sign", "个人界面签名点击次数", 1);
					loadSignature(uid, tv_signature.getText().toString().trim());

				}
			});

			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LiuLianApplication.height_fragment_main);
			view.setLayoutParams(params);
			initdata();
		} else {
			((ViewGroup) view.getParent()).removeView(view);
		}
		return view;
	}

	public void initdata() {

		listview_content.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {

				if (NetworkUtil.dataConnected(activity_context)) {
					if (current_list_content == 3) {
						if (!have_next_topic) {
							listview_content.onRefreshComplete();
							// MyToast.makeText(activity_context,
							// activity_context.getResources().getString(R.string.is_the_last_page),
							// 1).show();
						} else {
							if (loading_topic_task != null) {
								loading_topic_task.cancel(true);
							}
							loading_topic_task = new LoadingTopicTask();
							loading_topic_task.execute();
						}
					} else {
						listview_content.onRefreshComplete();
					}
				}
			}
		});

		listview_content.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				tv_ok.setVisibility(View.GONE);

				listview_content.getChildAt(0).setVisibility(View.INVISIBLE);
				// Do work to refresh the list here.
				if (NetworkUtil.dataConnected(activity_context)) {

					// loadingDialog.show();

					if (current_list_content == 1) {

						if (loading_pic_task != null) {
							loading_pic_task.cancel(true);
						}
						loading_pic_task = new LoadingPicTask();
						loading_pic_task.execute();

					} else if (current_list_content == 2) {

						if (loading_label_task != null) {
							loading_label_task.cancel(true);
						}

						loading_label_task = new LoadingLabelTask();
						loading_label_task.execute();

					} else if (current_list_content == 3) {

						if (loading_topic_task != null) {
							loading_topic_task.cancel(true);
						}
						last_id_content = "";
						list_all_content = null;
						loading_topic_task = new LoadingTopicTask();
						loading_topic_task.execute();

					}

				} else {
					MyToast.makeText(activity_context, activity_context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
				}

			}
		});

		if (LiuLianApplication.current_user != null) {
			if (NetworkUtil.dataConnected(getActivity())) {
				loadingDialog.show();
				if (loading_pic_task != null) {
					loading_pic_task.cancel(true);
				}
				loading_pic_task = new LoadingPicTask();
				loading_pic_task.execute();
			} else {
				init_person_data();
			}
		}
	}

	OnClickListener myClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_icon: {
				final AlertDialog dialog = new AlertDialog.Builder(activity_context).create();
				dialog.show();
				Window window = dialog.getWindow();
				window.setContentView(R.layout.user_image);
				RelativeLayout rela_main = (RelativeLayout) window.findViewById(R.id.main);
				ImageView iv_icon = (ImageView) window.findViewById(R.id.iv_icon);
                ImageLoaderCharles.getInstance(getActivity()).addTask(LiuLianApplication.current_user.getImage(), iv_icon);
				rela_main.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				StatService.onEvent(activity_context, "main_mine_icon", "我的主页头像点击次数", 1);
				break;
			}

			// 照片
			case R.id.pic_tag:
				current_list_content = 1;
				v_pic_tag.setBackgroundColor(getActivity().getResources().getColor(R.color.my_person_tab_sel));
				v_label.setBackgroundColor(getActivity().getResources().getColor(R.color.my_person_tab));
				v_topic_chatted.setBackgroundColor(getActivity().getResources().getColor(R.color.my_person_tab));
				if (str_list_all_small == null) {
					loadingDialog.show();
					listview_content.setAdapter(pic_adapter);
					if (loading_pic_task != null) {
						loading_pic_task.cancel(true);
					}
					str_list_all_small = null;
					loading_pic_task = new LoadingPicTask();
					loading_pic_task.execute();
				} else {
					pic_adapter.setData(str_list_all_small, str_list_all_big);
					listview_content.setAdapter(pic_adapter);
				}
				StatService.onEvent(getActivity(), "count_of_pic_personal", "个人主页照片tab点击次数", 1);
				break;
			// 标签
			case R.id.label:
				current_list_content = 2;
				v_pic_tag.setBackgroundColor(getActivity().getResources().getColor(R.color.my_person_tab));
				v_label.setBackgroundColor(getActivity().getResources().getColor(R.color.my_person_tab_sel));
				v_topic_chatted.setBackgroundColor(getActivity().getResources().getColor(R.color.my_person_tab));

				if (list_chest == null) {
					loadingDialog.show();
					listview_content.setAdapter(chestAdapter);
					if (loading_label_task != null) {
						loading_label_task.cancel(true);
					}
					loading_label_task = new LoadingLabelTask();
					loading_label_task.execute();
				} else {
					chestAdapter.setData(list_chest);
					listview_content.setAdapter(chestAdapter);
				}
				StatService.onEvent(getActivity(), "count_of_label_personal", "个人主页照片tab点击次数", 1);
				break;
			// 内容
			case R.id.topic_chatted:
				current_list_content = 3;
				last_id_content = "";
				v_pic_tag.setBackgroundColor(getActivity().getResources().getColor(R.color.my_person_tab));
				v_label.setBackgroundColor(getActivity().getResources().getColor(R.color.my_person_tab));
				v_topic_chatted.setBackgroundColor(getActivity().getResources().getColor(R.color.my_person_tab_sel));

				if (list_content == null) {
					loadingDialog.show();
					listview_content.setAdapter(content_new_adapter);
					if (loading_topic_task != null) {
						loading_topic_task.cancel(true);
					}
					list_all_content = null;
					loading_topic_task = new LoadingTopicTask();
					loading_topic_task.execute();
				} else {
					content_new_adapter.setData(list_all_content);
					listview_content.setAdapter(content_new_adapter);
				}
				StatService.onEvent(activity_context, "count_of_content_personal", "我的主页内容Tab点击次数", 1);
				break;
			default:
				break;
			}
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	public void onResume() {
		super.onResume();

        ImageLoaderCharles.getInstance(getActivity()).addTask(LiuLianApplication.current_user.getImage(), iv_icon);
		iv_icon.setBackgroundResource(CommonConst.user_sex[LiuLianApplication.current_user.getSex()]);
		tv_u_name.setText(LiuLianApplication.current_user.getName());
		// new LevelTask(LiuLianApplication.current_user.getUser_level_icon(),
		// tv_u_name, activity_context).execute();
		int width = ViewUtil.getScreenWidth(activity_context);
		int height = top_bg.getHeight();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
		iv_bg.setLayoutParams(params);
		iv_bg.setAlpha(200);

		if (preference_person.getString("person_img_bg", "").equals("")) {
            ImageLoaderCharles.getInstance(getActivity()).addTask(LiuLianApplication.current_user.getImage(), iv_bg);
		} else {
            ImageLoaderCharles.getInstance(getActivity()).addTask(preference_person.getString("person_img_bg", ""), iv_bg);
		}
        ImageLoaderCharles.getInstance(getActivity()).addTask(LiuLianApplication.current_user.getUser_level_icon(), user_level_icon);
		tv_age.setText("年龄：" + LiuLianApplication.current_user.getAge());
	}

	// 初始化个人信息
	@SuppressWarnings("deprecation")
	public void init_person_data() {


        ImageLoaderCharles.getInstance(getActivity()).addTask(LiuLianApplication.current_user.getHead_pic_small(), iv_icon);
		iv_icon.setBackgroundResource(CommonConst.user_sex[LiuLianApplication.current_user.getSex()]);
		tv_u_name.setText(LiuLianApplication.current_user.getName());
		// new LevelTask(LiuLianApplication.current_user.getUser_level_icon(),
		// tv_u_name, activity_context).execute();
		tv_join_date.setText("位置：" + LiuLianApplication.current_user.getCity());

		if (!"".equals(LiuLianApplication.current_user.getSignature())) {
			tv_signature.setText(LiuLianApplication.current_user.getSignature() + "");
			tv_ok.setVisibility(View.GONE);
		}

		// if (!LiuLianApplication.current_user.getSignature().equals("")) {
		// tv_signature.setText(LiuLianApplication.current_user.getSignature() +
		// "");
		// tv_ok.setVisibility(View.GONE);
		// }
		tv_age.setText("年龄：" + LiuLianApplication.current_user.getAge());

		int width = ViewUtil.getScreenWidth(activity_context);
		int height = top_bg.getHeight();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
		iv_bg.setLayoutParams(params);
		iv_bg.setAlpha(200);
		if (preference_person.getString("person_img_bg", "").equals("")) {
            ImageLoaderCharles.getInstance(getActivity()).addTask(LiuLianApplication.current_user.getImage(), iv_bg);
		} else {
            ImageLoaderCharles.getInstance(getActivity()).addTask(preference_person.getString("person_img_bg", ""), iv_bg);
		}
        ImageLoaderCharles.getInstance(getActivity()).addTask(LiuLianApplication.current_user.getUser_level_icon(), user_level_icon);
		if (list_view_user != null && list_view_user.size() > 0) {
			LayoutInflater inflater = activity_context.getLayoutInflater();
			layout_user.removeAllViews();
			for (int i = 0; i < list_view_user.size(); i++) {
				Users user = list_view_user.get(i);
				View item = inflater.inflate(R.layout.user_item, null);
				ImageView img = (ImageView) item.findViewById(R.id.iv_icon);

                ImageLoaderCharles.getInstance(getActivity()).addTask(user.getImage(), img);
				item.setTag(i);
				item.setOnClickListener(itemListener);
				layout_user.addView(item);
			}
			// tv_Visitor.setText(list_view_user.size()+"");
		}
	}

	OnClickListener itemListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (NetworkUtil.dataConnected(getActivity())) {
				int index = (Integer) v.getTag();
				if (list_view_user.get(index).getUid().equals(LiuLianApplication.current_user.getUid())) {

					Toast.makeText(activity_context, "这是你自己哦", 1).show();

				} else {
					StatService.onEvent(activity_context, "main_person_icons", "个人界面访客头像组点击次数", 1);
					Intent intent = new Intent();
					intent.setClass(activity_context, ImgsBrowseActivity_users.class);
					intent.putExtra("users", list_view_user);
					intent.putExtra("index", index);
					startActivity(intent);
				}
			} else {
				MyToast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			}
		}
	};

	// 加载我发的内容
	class LoadingTopicTask extends AsyncTask<String, Integer, List<Topic>> {
		@Override
		protected List<Topic> doInBackground(String... url) {
			list_content = new ArrayList<Topic>();
			try {
				// 测试
				String urlPath = PathConst.URL_MY_INTEREST + "&uid=" + uid + "&login_uid=" + uid + "&last_id=" + last_id_content + "&limit=10";
				LogUtil.e("地址", urlPath + "");
				try {
					JSONObject json = null;
					json = NetworkUtil.getJsonObject(urlPath, null, 5000);
					JSONObject json_send_content = new JSONObject(json.optString("interest"));
					have_next_topic = json_send_content.getBoolean("have_next");
					last_id_content = json_send_content.getString("last_id");
					JSONArray array = json_send_content.getJSONArray("list");
					int array_length = array.length();
					Topic topic = null;
					for (int i = 0; i < array_length; i++) {
						JSONObject item_content = array.getJSONObject(i);
						topic = new Topic();
						topic.setId(item_content.getString("id"));
						topic.setCreate_time(item_content.getString("create_time"));
						topic.setTitle(item_content.getString("title"));
						topic.setLeft_time(item_content.getString("left_time"));
						topic.setView_range(item_content.getString("view_num"));
						topic.setView_user_num(item_content.getInt("user_num"));
						topic.setGoto_type(item_content.getInt("goto_type"));
						topic.setMy(item_content.getBoolean("is_my"));
						list_content.add(topic);
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
			return list_content;
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(List<Topic> list_content) {
			super.onPostExecute(list_content);
			loadingDialog.dismiss();
			if (list_content != null) {
				if (list_all_content == null || list_all_content.size() == 0) {
					list_all_content = list_content;
				} else {
					listview_content.getRefreshableView().removeFooterView(layout_blank_tip);
					list_all_content.addAll(list_content);
				}
				content_new_adapter.setData(list_all_content);
				listview_content.onRefreshComplete();
			}
		}
	}

	// 照片
	class LoadingPicTask extends AsyncTask<String, Integer, List<String>> {
		@Override
		protected List<String> doInBackground(String... url) {
			try {
				str_list_all_small = new ArrayList<String>();
				str_list_all_big = new ArrayList<String>();
				String urlPath = PathConst.URL_MY_PERSON_HOME + "&uid=" + uid + "&login_uid=" + uid + "&last_id=" + last_id_content + "&limit=10";
				LogUtil.e("地址", urlPath + "");
				try {
					JSONObject json = null;
					json = NetworkUtil.getJsonObject(urlPath, null, 5000);
					if (!json.optString("user").equals("")) {
						JSONObject json_user = new JSONObject(json.optString("user"));
						Users user = new Users();
						user.setUid(json_user.optString("id"));
						user.setName(json_user.optString("username"));
						user.setSex(json_user.optInt("sex"));
						user.setImage(json_user.optString("head_pic"));
						user.setPhone(json_user.optString("mobile"));
						user.setHx_username(json_user.optString("hx_username"));
						user.setHx_password(json_user.optString("hx_password"));
						user.setReg_time(json_user.optString("reg_time"));
						user.setAccesskey(json_user.optString("accesskey"));
						user.setCity(json_user.optString("city"));
						user.setBirthday(json_user.optString("birthday"));
						user.setStar(json_user.optString("star"));
						user.setSignature(json_user.optString("signature"));
						user.setAge(json_user.optString("age"));
						user.setBack_pic(json_user.optString("back_pic"));
						user.setUser_level_icon(json_user.optString("user_level_icon"));
						LiuLianApplication.current_user = user;
						LiuLianApplication.getInstance().saveLoginedUser();
					}
					JSONArray json_user_view = new JSONArray(json.optString("visit_user_list"));
					int array_length = json_user_view.length();
					list_view_user = new ArrayList<Users>();
					for (int i = 0; i < array_length; i++) {
						JSONObject json_user = json_user_view.getJSONObject(i);
						Users user = new Users();
						user.setUid(json_user.optString("uid"));
						user.setName(json_user.optString("username"));
						user.setSex(json_user.optInt("sex"));
						user.setImage(json_user.optString("head_pic"));
						user.setCity(json_user.optString("city"));
						user.setBirthday(json_user.optString("birthday"));
						user.setStar(json_user.optString("star"));
						user.setSignature(json_user.optString("signature"));
						user.setAge(json_user.optString("age"));
						user.setBack_pic(json_user.optString("back_pic"));
						user.setIs_sayhi(json_user.optBoolean("is_sayHi"));
						user.setHx_username(json_user.optString("hx_username"));
						user.setUser_level_icon(json_user.optString("user_level_icon"));
						list_view_user.add(user);
					}
					JSONArray json_phones_small = new JSONArray(json.optString("photos_small"));
					int json_length_small = json_phones_small.length();
					if (json_length_small > 0) {
						for (int i = 0; i < json_length_small; i++) {
							str_list_all_small.add(json_phones_small.getString(i));
						}
					}

					JSONArray json_phones_big = new JSONArray(json.optString("photos"));
					int json_length = json_phones_small.length();
					if (json_length > 0) {
						for (int i = 0; i < json_length; i++) {
							str_list_all_big.add(json_phones_big.getString(i));
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
			return str_list_all_small;
		}

		@Override
		protected void onPostExecute(List<String> str_list) {
			super.onPostExecute(str_list);

			loadingDialog.dismiss();
			// 初始化个人信息数据
			init_person_data();
			if (str_list != null && str_list.size() > 0) {
				pic_adapter.setData(str_list_all_small, str_list_all_big);
				listview_content.onRefreshComplete();
			}
			listview_content.onRefreshComplete();
		}
	}

	// 加载标签
	class LoadingLabelTask extends AsyncTask<String, Integer, List<Chest>> {
		@Override
		protected List<Chest> doInBackground(String... url) {
			list_chest = new ArrayList<Chest>();
			try {
				String urlPath = PathConst.URL_PERSON_LABEL + LiuLianApplication.current_user.getUid();
				LogUtil.e("地址", urlPath + "");
				try {
					JSONObject json = null;
					json = NetworkUtil.getJsonObject(urlPath, null, 5000);
					JSONArray json_array = new JSONArray(json.getString("label"));
					if (null != json_array) {
						for (int i = 0; i < json_array.length(); i++) {
							JSONObject json_object = (JSONObject) json_array.get(i);
							Chest chest = new Chest();
							chest.setId(json_object.optString("id"));
							chest.setIcon(json_object.optString("icon"));
							chest.setName(json_object.optString("name"));
							chest.setTime(json_object.optString("time"));
							list_chest.add(chest);
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
			return list_chest;
		}

		@Override
		protected void onPostExecute(List<Chest> list_chest) {
			super.onPostExecute(list_chest);

			loadingDialog.dismiss();
			if (list_chest != null) {
				chestAdapter.setData(list_chest);
			}
			listview_content.onRefreshComplete();
		}
	}

	// 显示相册
	public void showImage(final Activity context) {

		if (str_list_all_small.size() < 9) {

			dir_temp = FileDownloadUtil.getDefaultLocalDir(PathConst.DIR_TEMP);
			dialog = new Dialog(context, R.style.Transparent_);
			dialog.show();
			Window window = dialog.getWindow();
			window.setContentView(R.layout.alert_dialog);
			window.findViewById(R.id.btn_take_photo).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					vFile = new File(dir_temp + "user_icon_temp.jpg");
					Uri uri = Uri.fromFile(vFile);
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					context.startActivityForResult(intent, PathConst.PHOTOHRAPH);
				}
			});
			window.findViewById(R.id.btn_pick_photo).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					Intent intent2 = new Intent();
					intent2.setClass(context, AlbumActivity.class);
					context.startActivityForResult(intent2, PathConst.CROPIMAGES);

				}
			});
			window.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

		} else {

			Toast.makeText(context, "超过最大上传张数,请先删除照片", 1).show();

		}

	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal(final Activity context) {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		context.startActivityForResult(intent, PHOTORESOULT);
	}

	// 显示删除
	public void showDel(final Activity context, final ArrayList<String> pics, final int index) {

		dialog = new Dialog(context, R.style.Transparent_);
		dialog.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.alert_dialog_del);

		window.findViewById(R.id.bt_del).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				delImage(LiuLianApplication.current_user.getUid(), pics.get(index), index);
			}
		});

		window.findViewById(R.id.look_image).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent intent = new Intent();
				intent.setClass(context, ImgsBrowseActivity_Single.class);
				intent.putExtra("index", index);
				intent.putExtra("url", pics.get(index));
				context.startActivity(intent);
			}
		});

		window.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		// 设置背景图
		window.findViewById(R.id.set_bg).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				setbg(context, pics.get(index));
			}
		});

		StatService.onEvent(getActivity(), "count_of_my_pic_personal", "个人主页已上传照片的点击次数", 1);
	}

	private boolean edit_bg = false;

	// 设置背景图
	public void setbg(Activity context, String img_url) {

		PersonalFragment.iv_bg.setAlpha(200);
        ImageLoaderCharles.getInstance(getActivity()).addTask(img_url, PersonalFragment.iv_bg);
		PersonalFragment.preference_person.edit().putString("person_img_bg", img_url).commit();
		edit_bg = true;
		loadImage(LiuLianApplication.current_user.getUid(), LiuLianApplication.current_user.getAccesskey(), img_url, edit_bg);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if (requestCode == REQUEST_DETAIL) {
		// if (data != null) {
		// Content content = (Content) data.getSerializableExtra("content");
		// if (content != null) {
		// list_all_content.get(location).setPraise_num(content.getPraise_num());
		// list_all_content.get(location).setIs_praised(content.isIs_praised());
		// content_new_adapter.setData(list_all_content);
		// }
		// }
		// }
		// if(requestCode ==PHOTORESOULT){//打开系统相册进行裁剪图片
		// if(data==null){//处理返回，取消键被点击报空指针异常
		// return;
		// }
		// Toast.makeText(activity_context, "开始裁剪图片", 0).show();
		// Uri startCrop = data.getData();
		// findPicByUri(startCrop);
		// }
	}

	class ImageUploadTask extends AsyncTask<String, Void, String> {
		private static final String TEST_API_KEY = "yuIOo0F9DDf8ZbkZa1syRG/zdes="; // 测试使用的表单api验证密钥
		private static final String BUCKET = "haomee"; // 存储空间
		private final long EXPIRATION = System.currentTimeMillis() / 1000 + 1000 * 5 * 10; // 过期时间，必须大于当前时间
		String SAVE_KEY = File.separator + "haomee" + File.separator + System.currentTimeMillis() + ".jpg";

		@Override
		protected String doInBackground(String... arg0) {
			String string = null;
			try {
				String policy = UpYunUtils.makePolicy(SAVE_KEY, EXPIRATION, BUCKET);
				String signature = UpYunUtils.signature(policy + "&" + TEST_API_KEY);
				string = Uploader.upload(policy, signature, BUCKET, arg0[0]);
			} catch (UpYunException e) {
				e.printStackTrace();
			}
			return string;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				head_pic = "http://haomee.b0.upaiyun.com" + SAVE_KEY;
				loadImage(LiuLianApplication.current_user.getUid(), LiuLianApplication.current_user.getAccesskey(), head_pic, false);
			} else {
				head_pic = null;
			}
		}
	}

	public void addMedal(int id) {
		String url = PathConst.URL_ADD_MEDAL + "&uid=" + LiuLianApplication.current_user.getUid() + "&Luid=" + LiuLianApplication.current_user.getUid() + "&id" + id;
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					JSONObject egg_obj = new JSONObject(arg0);
					if (egg_obj.getBoolean("is_new")) {
						Intent intent_send = new Intent();
						intent_send.setClass(activity_context, ShareMedalActivity.class);
						ShareContent share = new ShareContent();
						share.setId(egg_obj.getString("id"));
						share.setTitle(egg_obj.getString("name"));
						share.setSummary(egg_obj.getString("desc"));
						share.setImg_url(egg_obj.getString("icon"));
						share.setRedirect_url(CommonConst.GOV_URL);
						intent_send.putExtra("share", share);
						activity_context.startActivity(intent_send);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	// 回调上传图片
	public void startResult(String path) {

		new ImageUploadTask().execute(path);

	}

	// 上传图片
	public void loadImage(String uid, String accesskey, final String head_pic, final boolean edit_bg) {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("uid", uid);
		re.put("Luid", LiuLianApplication.current_user.getUid());
		re.put("accesskey", accesskey);
		if (edit_bg) {
			re.put("back_pic", head_pic);
		} else {
			re.put("photo_url", head_pic);
		}
		asyncHttp.get(PathConst.URL_EDIT_USER_INFO, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					JSONObject json = new JSONObject(arg0);
					Log.e("返回数据：", json.toString());
					if (1 == json.optInt("flag")) {
						if (!edit_bg) {
							str_list_all_small.add(head_pic);
							str_list_all_big.add(head_pic);
							pic_adapter.setData(str_list_all_small, str_list_all_big);
						} else {
							// 修改背景图片
							JSONObject bg_user = json.getJSONObject("user");
							String temp = bg_user.getString("back_pic");
                            ImageLoaderCharles.getInstance(getActivity()).addTask(temp, iv_bg);
						}
					} else {
						MyToast.makeText(getActivity(), json.optString("msg"), 1).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		StatService.onEvent(getActivity(), "count_of_load_pic_personal", "个人主页照片上传点击次数", 1);
	}

	// 上传图片
	public void delImage(String uid, final String url, final int index) {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("uid", uid);
		re.put("Luid", LiuLianApplication.current_user.getUid());
		re.put("url", url);
		re.put("accesskey", LiuLianApplication.current_user.getAccesskey());
		asyncHttp.get(PathConst.URL_DEL_IMAGE, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					JSONObject json = new JSONObject(arg0);
					Log.e("返回数据：", json.toString());
					if (1 == json.optInt("flag")) {
						str_list_all_small.remove(index);
						str_list_all_big.remove(index);
						pic_adapter.setData(str_list_all_small, str_list_all_big);
						// 修改背景图片
						JSONObject bg_user = json.getJSONObject("user");
						String temp = bg_user.getString("back_pic");
                        ImageLoaderCharles.getInstance(getActivity()).addTask(temp, iv_bg);
					} else {
						MyToast.makeText(getActivity(), json.optString("msg"), 1).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	// 修改签名
	public void loadSignature(String uid, final String signature) {

		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("uid", uid);
		re.put("Luid", LiuLianApplication.current_user.getUid());
		re.put("signature", signature);
		re.put("accesskey", LiuLianApplication.current_user.getAccesskey());
		asyncHttp.get(PathConst.URL_EDIT_USER_INFO, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					JSONObject json = new JSONObject(arg0);
					Log.e("返回数据：", json.toString());
					if (1 == json.optInt("flag")) {
						MyToast.makeText(getActivity(), json.optString("msg"), 1).show();
						imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
						pic_tag.requestFocus();
						tv_signature.setCursorVisible(false);
						tv_signature.setText(signature + "");
						tv_ok.setVisibility(View.GONE);
					} else {
						tv_ok.setVisibility(View.GONE);
						MyToast.makeText(getActivity(), json.optString("msg"), 1).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		StatService.onEvent(getActivity(), "count_of_signature", "个人签名点击次数", 1);
	}
	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
        	MainActivity.setTopBar(3);
        } else {
            //相当于Fragment的onPause
        }
    }
}
