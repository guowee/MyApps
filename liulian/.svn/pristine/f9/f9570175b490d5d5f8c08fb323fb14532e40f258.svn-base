package com.haomee.adapter;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.ShareContent;
import com.haomee.entity.Topic;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.R;
import com.haomee.liulian.ShareMedalActivity;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sina.weibo.sdk.utils.LogUtil;

public class ListViewAdapter extends BaseSwipeAdapter {

	private Context mContext;
	private List<Topic> list;
	private Animation animation;
	private String list_type; // 列表来源

	public ListViewAdapter(Context mContext, String list_type) {
		this.mContext = mContext;
		this.list_type = list_type;
		animation = AnimationUtils.loadAnimation(mContext, R.anim.push_out);
	}

	public void setData(List<Topic> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public View generateView(final int position, ViewGroup parent) {
		View v = LayoutInflater.from(mContext).inflate(R.layout.item_liulian, null);
		return v;
	}

	@Override
	public void fillValues(final int position, View convertView) {
		// ImageView item_image = (ImageView)
		// convertView.findViewById(R.id.item_image);
		TextView tv_name = (TextView) convertView.findViewById(R.id.item_name);
		TextView tv_tler = (TextView) convertView.findViewById(R.id.item_desc);
		TextView tv_number = (TextView) convertView.findViewById(R.id.item_count);

		View bt_delete = convertView.findViewById(R.id.delete);
		View bt_top = convertView.findViewById(R.id.top);

		tv_name.setText(list.get(position).getTitle());
		tv_tler.setText("已经有" + list.get(position).getUser_num() + "个LIer聊过它");
		tv_number.setText(list.get(position).getContent_num());
		// imageLoader.addTask(list.get(position).getIcon(),item_image);

		final SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));

		bt_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteItem(swipeLayout, position);
				if ("history".equals(list_type)) {
					deleteHistory(position);
				} else if ("fav".equals(list_type)) {
					deleteFav(2, position);
				}

				swipeLayout.close();
			}
		});

		if ("history".equals(list_type)) {
			bt_top.setVisibility(View.GONE);
		} else if ("fav".equals(list_type)) {
			bt_top.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					swipeLayout.close();
					set_top(1, position);
				}
			});
		}

		Log.i("test", list_type + " " + position);
	}

	@Override
	public int getCount() {

		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void deleteItem(View view, final int position) {
		view.startAnimation(animation);

		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {

				list.remove(position);
				notifyDataSetChanged();
			}
		});

	}

	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe;
	}

	/**
	 * 置顶
	 */
	private void set_top(final int flag, final int position) {
		String url = PathConst.URL_SET_TOP + "&id=" + list.get(position).getId() + "&flag=" + flag + "&uid=" + LiuLianApplication.current_user.getUid() + "&Luid=" + LiuLianApplication.current_user.getUid() + "&accesskey="
				+ LiuLianApplication.current_user.getAccesskey();
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					JSONObject json = new JSONObject(arg0);
					if (1 == json.optInt("flag")) {
						MyToast.makeText(mContext, "置顶成功", Toast.LENGTH_SHORT).show();
						Topic current_topic = list.get(position);
						list.add(0, current_topic);
						list.remove(position + 1);
						notifyDataSetChanged();
					} else {
						MyToast.makeText(mContext, "已经置顶过该话题", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

	// 刪除收藏
	private void deleteFav(final int flag_fav, final int position) {

		if (LiuLianApplication.current_user == null) {
			MyToast.makeText(mContext, "还没有登录", Toast.LENGTH_SHORT).show();
		} else {
			String url = PathConst.URL_TOPIC_FAV + "&id=" + list.get(position).getId() + "&flag=" + flag_fav + "&uid=" + LiuLianApplication.current_user.getUid() + "&accesskey=" + LiuLianApplication.current_user.getAccesskey();
			LogUtil.e("请求地址：", url + "");
			AsyncHttpClient asyncHttp = new AsyncHttpClient();
			asyncHttp.get(url, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					try {
						JSONObject json = new JSONObject(arg0);

						// 彩蛋埋点儿
						if (1 == json.optInt("flag")) {
							if (json.has("egg")) {
								JSONArray json_arr = json.getJSONArray("egg");
								JSONObject egg_obj = json_arr.getJSONObject(0);
								Intent intent_send = new Intent();
								intent_send.setClass(mContext, ShareMedalActivity.class);
								ShareContent share = new ShareContent();
								share.setId(egg_obj.getString("id"));
								share.setTitle(egg_obj.getString("name"));
								share.setSummary(egg_obj.getString("desc"));
								share.setImg_url(egg_obj.getString("icon"));
								share.setRedirect_url(CommonConst.GOV_URL);
								intent_send.putExtra("share", share);
								mContext.startActivity(intent_send);
							} else {
								MyToast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
							}
						} else {
							MyToast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
						MyToast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}

	}

	// 刪除收藏
	private void deleteHistory(final int position) {

		if (LiuLianApplication.current_user == null) {
			MyToast.makeText(mContext, "还没有登录", Toast.LENGTH_SHORT).show();
		} else {
			String url = PathConst.URL_TOPIC_DEL_HISTORY + "&id=" + list.get(position).getId() + "&uid=" + LiuLianApplication.current_user.getUid() + "&accesskey=" + LiuLianApplication.current_user.getAccesskey();
			AsyncHttpClient asyncHttp = new AsyncHttpClient();
			asyncHttp.get(url, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					try {
						JSONObject json = new JSONObject(arg0);

						// 彩蛋埋点儿
						if (1 == json.optInt("flag")) {

							if (json.has("egg")) {
								JSONArray json_arr = json.getJSONArray("egg");
								JSONObject egg_obj = json_arr.getJSONObject(0);
								Intent intent_send = new Intent();
								intent_send.setClass(mContext, ShareMedalActivity.class);
								ShareContent share = new ShareContent();
								share.setId(egg_obj.getString("id"));
								share.setTitle(egg_obj.getString("name"));
								share.setSummary(egg_obj.getString("desc"));
								share.setImg_url(egg_obj.getString("icon"));
								share.setRedirect_url(CommonConst.GOV_URL);
								intent_send.putExtra("share", share);
								mContext.startActivity(intent_send);
							} else {
								MyToast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
							}
						} else {
							MyToast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
						MyToast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}

	}
}
