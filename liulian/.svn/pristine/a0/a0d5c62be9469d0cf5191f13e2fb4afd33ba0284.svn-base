package com.haomee.liulian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haomee.adapter.PunchAdapter;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.PunchClock_;
import com.haomee.entity.ShareContent;
import com.haomee.entity.Users;
import com.haomee.player.MySoundPlayer;
import com.haomee.util.NetworkUtil;
import com.haomee.view.HintEditText;
import com.haomee.view.LoadingDialog;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

//打卡
public class PunchClockActivity extends BaseActivity {

	private PullToRefreshListView pull_ListView;
	private PunchAdapter punchAdapter;
	private Context context;
	private String last_id = "";
	private String limit = "20";
	private boolean have_next_new = false;
	private LoadingPunchTask loading_punch_task;
	private List<PunchClock_> list_punch;
	private EditText et_punch;
	private RelativeLayout bt_punch;
	private ImageView have_more;
	private SharedPreferences preference;
	SharedPreferences.Editor editor = null;

	private SharedPreferences preferences_is_first;

	private LoadingDialog loadingDialog;

	private String shareIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_punch_list);
		this.context = this;
		preference = context.getSharedPreferences(CommonConst.PREFERENCES_MEDAL, Activity.MODE_PRIVATE);
		editor = preference.edit();
		pull_ListView = (PullToRefreshListView) findViewById(R.id.list_punch);
		et_punch = (EditText) findViewById(R.id.et_punch);
		et_punch.setOnFocusChangeListener(HintEditText.onFocusAutoClearHintListener);

		bt_punch = (RelativeLayout) findViewById(R.id.bt_punch);
		have_more = (ImageView) findViewById(R.id.have_more);

		punchAdapter = new PunchAdapter(context);
		list_punch = new ArrayList<PunchClock_>();
		pull_ListView.setAdapter(punchAdapter);

		preferences_is_first = context.getSharedPreferences(CommonConst.PREFERENCES_FIRST, Context.MODE_PRIVATE);

		findViewById(R.id.bt_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});
		pull_ListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if (NetworkUtil.dataConnected(context)) {
					if (!have_next_new) {
						MyToast.makeText(context, context.getResources().getString(R.string.is_the_last_page), Toast.LENGTH_LONG).show();
					} else {

						// 彩蛋埋点儿 好奇宝宝打開最新話題等于100次
						if (preference.getInt(CommonConst.PUBLISH_CONTENT, 0) == 50) {
							addMedal();
						} else {
							editor.putInt(CommonConst.PUBLISH_CONTENT, preference.getInt(CommonConst.PUBLISH_CONTENT, 0) + 1);
							editor.commit();
						}

						if (loading_punch_task != null) {
							loading_punch_task.cancel(true);
						}
						loading_punch_task = new LoadingPunchTask();
						loading_punch_task.execute("");
					}
				} else {

					Toast.makeText(context, "网络不给力", 1).show();

				}
			}
		});

		have_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!NetworkUtil.dataConnected(PunchClockActivity.this)) {
					MyToast.makeText(PunchClockActivity.this, PunchClockActivity.this.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
				} else {
					if (list_punch != null && list_punch.size() > 0) {
						Intent intent_send = new Intent();
						intent_send.setClass(PunchClockActivity.this, ShareActivity.class);
						ShareContent share = new ShareContent();
						share.setId(list_punch.get(0).getId());
						share.setTitle(list_punch.get(0).getText_content());
						share.setImg_url(shareIcon);
						share.setRedirect_url(CommonConst.URL_SHARE_PUNCH);
						intent_send.putExtra("share", share);
						PunchClockActivity.this.startActivity(intent_send);
					}
				}
			}
		});

		pull_ListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				// 背景重复出现的bug
				pull_ListView.getChildAt(0).setVisibility(View.INVISIBLE);

				list_punch.clear();
				last_id = "";
				if (loading_punch_task != null) {
					loading_punch_task.cancel(true);
				}
				loading_punch_task = new LoadingPunchTask();
				loading_punch_task.execute("");

			}
		});

		bt_punch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String et_String = et_punch.getText().toString().trim();
				if (et_String.equals("")) {
					et_String = et_punch.getHint().toString();
				}
				punch(et_String);
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(PunchClockActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				MySoundPlayer.getInstance(PunchClockActivity.this).play_background(R.raw.sound_click, false);

			}
		});

		loadingDialog = new LoadingDialog(this);
		loadingDialog.show();

		loading_punch_task = new LoadingPunchTask();
		loading_punch_task.execute("1");

	}

	private void showHelpTip() {
		Intent intent = new Intent();
		intent.putExtra("from", "tip_hi");
		intent.setClass(context, HelpTipActivity.class);
		this.startActivity(intent);

		Editor editor = preferences_is_first.edit();
		editor.putBoolean("is_first_tip_hi", false);
		editor.commit();
	}

	public void punch(String content) {
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		RequestParams re = new RequestParams();
		re.put("uid", LiuLianApplication.current_user.getUid());
		re.put("Luid", LiuLianApplication.current_user.getUid());
		re.put("accesskey", LiuLianApplication.current_user.getAccesskey());
		re.put("content", content);
		Log.e("请求地址", PathConst.URL_PUNCH + "&uid=" + LiuLianApplication.current_user.getUid() + "&accesskey=" + LiuLianApplication.current_user.getAccesskey() + "&content=" + content);
		asyncHttp.get(PathConst.URL_PUNCH, re, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					if (arg0 == null || arg0.length() == 0) {
						return;
					}
					JSONObject json = new JSONObject(arg0);
					if (json == null || "".equals(json)) {
						return;
					}
					Log.e("返回数据：", json.toString());
					if (1 == json.optInt("flag")) {
						et_punch.setText("");
						list_punch.clear();
						last_id = "";
						loading_punch_task = new LoadingPunchTask();
						loading_punch_task.execute("");
						Toast.makeText(PunchClockActivity.this, "打卡成功", 1).show();
					} else {
						Toast.makeText(PunchClockActivity.this, "打卡失败", 1).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	class LoadingPunchTask extends AsyncTask<String, Integer, List<PunchClock_>> {
		@Override
		protected List<PunchClock_> doInBackground(String... url) {
			try {
				String urlPath = "";
				if (url != null && !url[0].equals("")) {
					urlPath = PathConst.URL_PUNCH_LIST + "&uid=" + LiuLianApplication.current_user.getUid() + "&limit=" + limit + "&last_id=" + last_id + "&login_id=" + LiuLianApplication.current_user.getUid() + "&needPunch=" + url[0];
				} else {
					urlPath = PathConst.URL_PUNCH_LIST + "&uid=" + LiuLianApplication.current_user.getUid() + "&limit=" + limit + "&last_id=" + last_id + "&login_id=" + LiuLianApplication.current_user.getUid();
				}
				Log.e("请求地址:", urlPath + "");
				String str_json;
				try {
					str_json = NetworkUtil.getHttpString(urlPath, null, 5000);// 5s延迟
					JSONObject json = null;
					if (str_json != null) {
						json = new JSONObject(str_json);
					} else {
						json = NetworkUtil.getJsonObject(urlPath, null, 5000);
					}
					if (json != null) {
						Log.e("返回结果：", json + "");
						have_next_new = json.getBoolean("have_next");
						last_id = json.getString("last_id");
						shareIcon = json.optString("share_icon");
						JSONArray array = json.getJSONArray("list");
						int array_length = array.length();
						PunchClock_ punchClock = null;
						for (int i = 0; i < array_length; i++) {
							JSONObject item_topic = array.getJSONObject(i);
							punchClock = new PunchClock_();
							punchClock.setId(item_topic.getString("id"));
							punchClock.setText_content(item_topic.getString("text_content"));
							punchClock.setCreate_time(item_topic.getString("create_time"));
							punchClock.setSay_hi(item_topic.getBoolean("is_sayhi"));

							JSONObject json_user = item_topic.getJSONObject("user");
							Users users = new Users();
							users.setUid(json_user.optString("uid"));
							users.setName(json_user.optString("username"));
							users.setImage(json_user.optString("head_pic"));
							users.setHx_username(json_user.optString("hx_username"));
							users.setSex(Integer.parseInt(json_user.optString("sex")));
							users.setUser_level_icon(json_user.optString("user_level_icon"));
							users.setIs_can_talk(json_user.optBoolean("is_can_talk"));
							punchClock.setUser(users);
							list_punch.add(punchClock);
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
			return list_punch;
		}

		@Override
		protected void onPostExecute(List<PunchClock_> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			punchAdapter.setData(result);
			pull_ListView.onRefreshComplete();

			loadingDialog.dismiss();

			// 弹出提示
			boolean is_first_open = preferences_is_first.getBoolean("is_first_tip_hi", true);
			if (is_first_open) {
				showHelpTip();
			}

		}
	}

	public void addMedal() {
		String url = PathConst.URL_ADD_MEDAL + "&uid=" + LiuLianApplication.current_user.getUid() + "&Luid=" + LiuLianApplication.current_user.getUid() + "&id=22";
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
	protected void onDestroy() {
		super.onDestroy();

		if (punchAdapter != null) {
			punchAdapter.stopRefresh();
		}
	}
}
