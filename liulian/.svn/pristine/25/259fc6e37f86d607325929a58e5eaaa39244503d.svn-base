package com.haomee.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haomee.adapter.MyIdolAdapters;
import com.haomee.consts.PathConst;
import com.haomee.entity.Users;
import com.haomee.liulian.BaseFragment;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.R;
import com.haomee.util.NetworkUtil;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MyIdolFragment extends BaseFragment {
	private MyIdolAdapters idolAdapter;
	private List<Users> list_idols;
	private boolean has_idols_next = false;
	private String last_id = "";
	private PullToRefreshListView listview_idols;
	private View view;

	private Context instance;
	private View footer_loading;

	private RelativeLayout layout_blank_tip;
	private TextView tip1, tip2, bt_refresh;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			instance = MyIdolFragment.this.getActivity();
			view = inflater.inflate(R.layout.fragment_idols, null);
			layout_blank_tip = (RelativeLayout) view.findViewById(R.id.layout_blank_tip);
			tip1 = (TextView) layout_blank_tip.findViewById(R.id.tip1);
			tip2 = (TextView) layout_blank_tip.findViewById(R.id.tip2);
			bt_refresh = (TextView) layout_blank_tip.findViewById(R.id.bt_refresh);
			bt_refresh.setVisibility(View.GONE);
			listview_idols = (PullToRefreshListView) view.findViewById(R.id.list_idols);
			footer_loading = inflater.inflate(R.layout.refresh_footer_loading, null);
			footer_loading.setVisibility(View.GONE);
			listview_idols.getRefreshableView().addFooterView(footer_loading, null, false);
			idolAdapter = new MyIdolAdapters(getActivity());
			list_idols = new ArrayList<Users>();
			listview_idols.setAdapter(idolAdapter);
			listview_idols.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
				@Override
				public void onLastItemVisible() {
					if (NetworkUtil.dataConnected(instance)) {
						if (!has_idols_next) {
							listview_idols.onRefreshComplete();
							MyToast.makeText(instance, instance.getResources().getString(R.string.is_the_last_page), 1).show();
						} else {
							footer_loading.setVisibility(View.VISIBLE);
							init_idol_data();
						}
					} else {
						MyToast.makeText(instance, instance.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
					}
				}
			});
			listview_idols.setOnRefreshListener(new OnRefreshListener<ListView>() {
				@Override
				public void onRefresh(PullToRefreshBase<ListView> refreshView) {
					// 背景重复出现的bug
					if (list_idols != null && list_idols.size() > 0) {
						listview_idols.getChildAt(0).setVisibility(View.INVISIBLE);
					}
					if (NetworkUtil.dataConnected(instance)) {
						has_idols_next = false;
						last_id = "";
						list_idols = null;
						init_idol_data();
					} else {
						MyToast.makeText(instance, instance.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
					}
				}
			});
			if (NetworkUtil.dataConnected(instance)) {
				init_idol_data();
			} else {
				MyToast.makeText(instance, instance.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			}

		} else {
			((ViewGroup) view.getParent()).removeView(view);
		}
		return view;
	}

	public void init_idol_data() {
		String url = PathConst.URL_MY_IDOLS + LiuLianApplication.current_user.getUid()+"&Luid=" +LiuLianApplication.current_user.getUid()+ "&last_id=" + last_id;
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		final List<Users> list_idol = new ArrayList<Users>();
		asyncHttp.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					if (arg0 == null) {
						return;
					}
					JSONObject idol_obj = new JSONObject(arg0);
					has_idols_next = idol_obj.optBoolean("have_next");
					last_id = idol_obj.optString("last_id");
					JSONArray idol_arr = idol_obj.getJSONArray("list");
					for (int i = 0; i < idol_arr.length(); i++) {
						Users idol = new Users();
						JSONObject json_idol = (JSONObject) idol_arr.get(i);
						idol.setUid(json_idol.optString("uid"));
						idol.setName(json_idol.optString("username"));
						idol.setHead_pic_small(json_idol.optString("head_pic"));
						idol.setSex(json_idol.optInt("sex"));
						idol.setTime(json_idol.optString("refresh_topic_time"));
						idol.setUser_level_icon(json_idol.optString("user_level_icon"));
						idol.setIs_online(json_idol.optBoolean("is_online"));
						list_idol.add(idol);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (list_idol != null) {
					layout_blank_tip.setVisibility(View.GONE);
					if (list_idols == null || list_idols.size() == 0) {// 第一次加载
						list_idols = list_idol;
					} else {
						list_idols.addAll(list_idol);
					}
				}
				if (list_idols == null || list_idols.size() == 0) {
					showBlankTip("你还没有关注的小伙伴,去TA的页面点关注", "TA的一举一动都在这里显示哦");
				} else {
					hideBlankTip();
				}
				footer_loading.setVisibility(View.GONE);
				idolAdapter.setData(list_idols,listview_idols);
				listview_idols.onRefreshComplete();
			}
		});
	}

	public void showBlankTip(String t1, String t2) {
		layout_blank_tip.setVisibility(View.VISIBLE);
		if (LiuLianApplication.height_fragment_liulian > 0) {
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layout_blank_tip.getLayoutParams();
			params.height = LiuLianApplication.height_fragment_liulian;
			layout_blank_tip.setLayoutParams(params);
		}
		tip1.setText(t1);
		tip2.setText(t2);
	}

	public void hideBlankTip() {
		layout_blank_tip.setVisibility(View.GONE);
	}

}
