package com.haomee.adapter;

import java.util.List;

import com.haomee.util.imageloader.ImageLoaderCharles;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.liulian.BlackListActivity;
import com.haomee.liulian.MainActivity;
import com.haomee.liulian.R;
import com.haomee.util.NetworkUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class BlackListAdapter extends BaseSwipeAdapter {

	private Activity mContext;
	private Animation animation;
	List<String> black_list;
	private SharedPreferences preferences_chat_user;
	private SharedPreferences.Editor editor;

	public BlackListAdapter(Activity mContext, List<String> black_list) {
		this.mContext = mContext;
		this.black_list = black_list;
		animation = AnimationUtils.loadAnimation(mContext, R.anim.push_out);
		preferences_chat_user = mContext.getSharedPreferences(CommonConst.PREFERENCES_SESSION_USERS, Context.MODE_PRIVATE);

	}

	@Override
	public View generateView(int position, ViewGroup parent) {
		View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_blacklist, null);

		return convertView;
	}

	@Override
	public void fillValues(final int position, View convertView) {
		TextView name = (TextView) convertView.findViewById(R.id.item_name);
		ImageView avatar = (ImageView) convertView.findViewById(R.id.item_image);
		LinearLayout list_item_layout = (LinearLayout) convertView.findViewById(R.id.list_item_layout);
		if (position % 2 == 0) {
			list_item_layout.setBackgroundResource(R.drawable.mm_listitem);
		} else {
			list_item_layout.setBackgroundResource(R.drawable.mm_listitem_grey);
		}
		// 获取与此用户/群组的会话
		// 获取用户username或者群组groupid
		String username = black_list.get(position);
		String[] temp = preferences_chat_user.getString(username, "").split("######");
		String nickname = "";
		String icon = "";
		String uid = "";
		int sex = 0;

		// 每天拉取一次

		if (temp.length == 4) {
			nickname = temp[0];
			icon = temp[1];
			uid = temp[2];
			sex = Integer.parseInt(temp[3]);
		} else {
			PostUserInfo(username, avatar, name);
		}

		avatar.setImageResource(R.drawable.item_icon);
		avatar.setBackgroundResource(CommonConst.user_sex[sex]);
        ImageLoaderCharles.getInstance(mContext).addTask(icon, avatar);
		name.setText(nickname);
		name.setTag(uid);

		final SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));
		convertView.findViewById(R.id.delete).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "移出成功", Toast.LENGTH_SHORT).show();
				try {
					EMContactManager.getInstance().deleteUserFromBlackList(black_list.get(position));

				} catch (EaseMobException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				deleteItem(swipeLayout, position);
				swipeLayout.close();
			}
		});

	}

	@Override
	public int getCount() {

		return black_list.size();
	}

	@Override
	public Object getItem(int position) {
		return black_list.get(position);
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

				notifyDataSetChanged();
				if (black_list.size() == 0) {
					((BlackListActivity) mContext).showBlankTip("你还没有拉黑过一个LIer哦！","去招呼页向左滑动ta试试哦！");
				}
			}
		});

	}

	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe;
	}

	public void PostUserInfo(final String temp, final ImageView image, final TextView text) {

		if (NetworkUtil.dataConnected(mContext)) {
			// 获取会话好友头像和昵称
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams re = new RequestParams();
			re.put("hx_username", temp);
			client.post(PathConst.URL_GET_USER_INFO_FROM_HX_NAME, re, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg0) {
					try {
						JSONArray json_arr = new JSONArray(arg0);
						for (int i = 0; i < json_arr.length(); i++) {
							JSONObject json = json_arr.getJSONObject(i);
							image.setImageResource(R.drawable.item_icon);
                            ImageLoaderCharles.getInstance(mContext).addTask(json.getString("head_pic"), image);
							image.setBackgroundResource(CommonConst.user_sex[json.getInt("sex")]);
							text.setText(json.getString("username"));
							text.setTag(json.getString("id"));
							editor = preferences_chat_user.edit();
							editor.putString(temp, json.getString("username") + "######" + json.getString("head_pic") + "######" + json.getString("id") + "######" + json.getInt("sex"));
							editor.commit();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}

	}
}
