package com.haomee.adapter;

import java.util.List;

import com.haomee.util.imageloader.ImageLoaderCharles;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.Content;
import com.haomee.entity.ContentPicture;
import com.haomee.entity.Movie;
import com.haomee.entity.Music;
import com.haomee.entity.ShareContent;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.R;
import com.haomee.liulian.ShareActivity;
import com.haomee.liulian.ShareMedalActivity;
import com.haomee.player.MediaPlayerActivity;
import com.haomee.player.MyMusicPlayer;
import com.haomee.util.NetworkUtil;
import com.haomee.util.ViewUtil;
import com.haomee.view.MyToast;
import com.haomee.view.RoundProgressBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MySendContentAdapter extends BaseAdapter {

	private List<Content> list_content;
	private Context context;
	private LayoutInflater inflater;
	public boolean is_remove_item = false;

	private RoundProgressBar progressBar;

	private int margin_image; // 图片的间距

	public RoundProgressBar getCurrentProgressBar() {
		return progressBar;
	}

	public MySendContentAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);

		margin_image = ViewUtil.dip2px(context, 10);
	}

	public void setData(List<Content> list_content) {
		this.list_content = list_content;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_content == null ? 0 : list_content.size();
	}

	@Override
	public Object getItem(int position) {
		return list_content.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 只有当convertView不存在的时候才去inflate子元素

		ViewHolder viewHolder = new ViewHolder();
		convertView = inflater.inflate(R.layout.item_my_send_content, null);
		viewHolder.linear_mm = (LinearLayout) convertView.findViewById(R.id.linear_mm);
		viewHolder.m_name = (TextView) convertView.findViewById(R.id.m_name);
		viewHolder.m_director = (TextView) convertView.findViewById(R.id.m_director);
		viewHolder.m_actor = (TextView) convertView.findViewById(R.id.m_actor);
		viewHolder.item_image = (ImageView) convertView.findViewById(R.id.item_image);
		viewHolder.item_content = (TextView) convertView.findViewById(R.id.item_content);
		viewHolder.item_create_time = (TextView) convertView.findViewById(R.id.item_create_time);
		viewHolder.item_delete = (ImageView) convertView.findViewById(R.id.item_delete);
		viewHolder.bt_share = (ImageView) convertView.findViewById(R.id.bt_share);
		viewHolder.item_see = (TextView) convertView.findViewById(R.id.item_see);
		viewHolder.item_praise = (TextView) convertView.findViewById(R.id.item_praise);
		viewHolder.item_love = (ImageView) convertView.findViewById(R.id.bt_love);
		viewHolder.bt_play = (RoundProgressBar) convertView.findViewById(R.id.bt_play);
		viewHolder.content_line = (View) convertView.findViewById(R.id.content_line);
		viewHolder.bt_play.setOnClickListener(btOnClickListener);
		viewHolder.item_delete.setOnClickListener(btOnClickListener);
		viewHolder.item_love.setOnClickListener(btOnClickListener);
		viewHolder.bt_share.setOnClickListener(btOnClickListener);

		final Content content = list_content.get(position);

		viewHolder.item_delete.setTag(content);
		viewHolder.bt_share.setTag(content);
		viewHolder.item_love.setTag(viewHolder.item_praise);
		viewHolder.item_praise.setTag(content);

		viewHolder.item_image.setImageResource(R.drawable.item_default);
		viewHolder.bt_play.setVisibility(View.GONE);
		if (content.isIs_praised()) {
			viewHolder.item_love.setImageResource(R.drawable.content_button_like_pressed);
		} else {
			viewHolder.item_love.setImageResource(R.drawable.content_button_like_default);
		}
		switch (content.getType()) {
		case 1://纯文字
			viewHolder.item_image.setVisibility(View.GONE);
			break;
		case 2: { //文字图片
			viewHolder.item_image.setVisibility(View.VISIBLE);

			ContentPicture pic = content.getPicture();

			// 控制图片宽高
			int width = parent.getWidth();
			if (width > 0 && pic.getWidth() > 0) {
				int height = pic.getHeight() * (width - margin_image * 2) / pic.getWidth();
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
				params.setMargins(margin_image, margin_image, margin_image, 0);
				viewHolder.item_image.setLayoutParams(params);
			}

            ImageLoaderCharles.getInstance(context).addTask(pic.getSmall(), viewHolder.item_image);
			break;
		}
		case 3: { //音乐
			viewHolder.linear_mm.setVisibility(View.VISIBLE);
			Music music = content.getMusic();

			viewHolder.m_name.setText("歌曲:" + music.getTitle());
			viewHolder.m_director.setText("歌手:" + music.getAuthor());
			viewHolder.m_actor.setText("专辑名称:" + music.getAlbum());

			viewHolder.item_image.setVisibility(View.VISIBLE);
			viewHolder.bt_play.setVisibility(View.VISIBLE);
			viewHolder.bt_play.setTag(music);

			if (MyMusicPlayer.getInstance().isCurrentMusic(music)) {
				progressBar = viewHolder.bt_play;
				viewHolder.bt_play.setProgress(MyMusicPlayer.getInstance().getProgress());

				MyMusicPlayer.getInstance().setProgressBar(progressBar);

				if (MyMusicPlayer.getInstance().getStatus() == MyMusicPlayer.STATUS_PLAYING) {
					viewHolder.bt_play.setImageResource(R.drawable.content_button_music_pause);
				} else {
					viewHolder.bt_play.setImageResource(R.drawable.content_button_music_start);
				}

			} else {
				viewHolder.bt_play.setProgress(0);
				viewHolder.bt_play.setImageResource(R.drawable.content_button_music_start);
			}

			// 控制图片宽高
			int width = parent.getWidth();
			if (width > 0) {
				int height = (width - margin_image * 2); // 1:1
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
				params.setMargins(margin_image, margin_image, margin_image, 0);
				viewHolder.item_image.setLayoutParams(params);
			}

            ImageLoaderCharles.getInstance(context).addTask(music.getCover(), viewHolder.item_image);
			break;
		}
		case 4: { //电影
			viewHolder.item_image.setVisibility(View.VISIBLE);

			viewHolder.linear_mm.setVisibility(View.VISIBLE);
			Movie movie = content.getMovie();
			// 控制图片宽高
			viewHolder.m_name.setText("电影:" + movie.getCname() + " (" + movie.getEnam() + ") " + movie.getRtime() + " " + movie.getLocation());
			viewHolder.m_director.setText("导演:" + movie.getDirect());
			viewHolder.m_actor.setText("演员:" + movie.getActor());

			// 控制图片宽高
			int width = parent.getWidth();
			if (width > 0) {
				int height = (width - margin_image * 2) / 2 * 3; // 2:3
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
				params.setMargins(margin_image, margin_image, margin_image, 0);
				viewHolder.item_image.setLayoutParams(params);
			}
			ImageLoaderCharles.getInstance(context).addTask(movie.getCover(), viewHolder.item_image);

			if (!"".equals(movie.getUrl())) {
				viewHolder.bt_play.setVisibility(View.VISIBLE);
				viewHolder.bt_play.setTag(movie);
			}
			break;
		}
		}
		if (content.getContent().equals("")) {
			viewHolder.item_content.setVisibility(View.GONE);
			viewHolder.content_line.setVisibility(View.GONE);
		} else {
			viewHolder.content_line.setVisibility(View.VISIBLE);
			viewHolder.item_content.setVisibility(View.VISIBLE);
			viewHolder.item_content.setText(content.getContent());
		}
		viewHolder.item_create_time.setText(content.getCreate_time());
		viewHolder.item_see.setText(content.getView_num());
		viewHolder.item_praise.setText(content.getPraise_num());
		return convertView;
	}

	private class ViewHolder {
		private LinearLayout linear_mm;
		private TextView m_name, m_director, m_actor;
		private ImageView item_image;
		private TextView item_content;
		private TextView item_create_time;
		private ImageView item_delete, bt_share;
		private ImageView item_love;
		private TextView item_see;
		private TextView item_praise;
		private RoundProgressBar bt_play;
		private View content_line;
	}

	private Button bt_commit, bt_cancel;
	private AlertDialog alertDialog;

	private OnClickListener btOnClickListener = new OnClickListener() {
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.bt_play: {

				if (v.getTag() instanceof Music) {

					Music music = (Music) v.getTag();

					// 恢复以前的
					if (progressBar != null && progressBar != v) {
						progressBar.setProgress(0);
						progressBar.setImageResource(R.drawable.content_button_music_start);
					}

					progressBar = (RoundProgressBar) v;

					MyMusicPlayer.getInstance().setProgressBar(progressBar);

					if (MyMusicPlayer.getInstance().isCurrentMusic(music)) {
						if (MyMusicPlayer.getInstance().getStatus() == MyMusicPlayer.STATUS_PLAYING) {
							((ImageView) v).setImageResource(R.drawable.content_button_music_start);
							MyMusicPlayer.getInstance().pause();
						} else if (MyMusicPlayer.getInstance().getStatus() == MyMusicPlayer.STATUS_PAUSED) {
							((ImageView) v).setImageResource(R.drawable.content_button_music_pause);
							MyMusicPlayer.getInstance().play();
						}
					} else {

						final RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
						rotateAnimation.setDuration(1000);
						rotateAnimation.setRepeatCount(-1);
						rotateAnimation.setInterpolator(new LinearInterpolator());

						v.startAnimation(rotateAnimation);

						MyMusicPlayer.getInstance().setOnPreparedListener(new OnPreparedListener() {
							@Override
							public void onPrepared(MediaPlayer mp) {
								rotateAnimation.cancel();

								((ImageView) v).setImageResource(R.drawable.content_button_music_pause);
							}
						});

						boolean success = MyMusicPlayer.getInstance().start(music);
						if (!success) {
							Toast.makeText(context, "播放失败", Toast.LENGTH_SHORT).show();
							rotateAnimation.cancel();
						}
					}
				} else if (v.getTag() instanceof Movie) {

					MyMusicPlayer.getInstance().pause(); // 如果有音乐先暂停

					Movie movie = (Movie) v.getTag();
					Intent intent = new Intent();
					intent.setClass(context, MediaPlayerActivity.class);
					intent.putExtra("movie", movie);
					context.startActivity(intent);
				}

				break;
			}
			case R.id.item_delete: {
				alertDialog = new AlertDialog.Builder(context).create();
				alertDialog.show();
				Window window = alertDialog.getWindow();
				window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				window.setContentView(R.layout.dialog_del);
				bt_commit = (Button) window.findViewById(R.id.bt_commit);
				bt_cancel = (Button) window.findViewById(R.id.bt_cancel);
				bt_commit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						Content content = (Content) v.getTag();
						//删除聊天记录
						delete_content(content);//删除内容
					}
				});

				bt_cancel.setOnClickListener(btOnClickListener);

				break;
			}
			case R.id.bt_cancel:
				alertDialog.dismiss();
				break;
			case R.id.bt_love: {
				ImageView temp = (ImageView) v;
				if (NetworkUtil.dataConnected(context)) {
					TextView txt_count = (TextView) v.getTag();
					Content content = (Content) txt_count.getTag();
					if (content.isIs_praised()) {
						content.setIs_praised(false);
						temp.setImageResource(R.drawable.content_button_like_default);
						praise_content(content, 2, txt_count);//取消
					} else {
						content.setIs_praised(true);
						temp.setImageResource(R.drawable.content_button_like_pressed);
						praise_content(content, 1, txt_count);//赞
					}
				} else {
					MyToast.makeText(context, "请检查网络", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case R.id.bt_share: {
				//分享内容
				if (!NetworkUtil.dataConnected(context)) {
					MyToast.makeText(context, context.getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
				} else {
					Content content = (Content) v.getTag();
					Intent intent_send = new Intent();
					intent_send.setClass(context, ShareActivity.class);
					ShareContent share = new ShareContent();
					share.setId(content.getId());
					share.setTitle("榴莲");
					String summary = content.getContent();
					share.setSummary(summary);

					switch (content.getType()) {
					case 1:
						share.setImg_url(com.haomee.consts.CommonConst.ICON_DEFAULT_URL);//logo图片
						break;
					case 2:
						share.setImg_url(content.getPicture().getSmall());
						break;
					case 3:
						share.setImg_url(content.getMusic().getCover());
						break;
					case 4:
						share.setImg_url(content.getMovie().getCover());
						break;
					}
					share.setRedirect_url(CommonConst.URL_SHARE_CONTENT + content.getId());
					intent_send.putExtra("share", share);
					intent_send.putExtra("have_report", false);
					context.startActivity(intent_send);

				}
				break;
			}
			default:
				break;
			}

		}
	};

	private void praise_content(final Content content, final int is_praise, final TextView textview) {

		if (LiuLianApplication.current_user == null) {
			MyToast.makeText(context, "还没有登录", Toast.LENGTH_SHORT).show();
		} else {
			String url = PathConst.URL_PRAISE_CONTENT + "&id=" + content.getId() + "&flag=" + is_praise + "&uid=" + LiuLianApplication.current_user.getUid() + "&accesskey=" + LiuLianApplication.current_user.getAccesskey();
			AsyncHttpClient asyncHttp = new AsyncHttpClient();
			asyncHttp.get(url, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					try {
						JSONObject json = new JSONObject(arg0);
						if (1 == json.optInt("flag")) {
							if (is_praise == 1) {
								int num = Integer.parseInt(content.getPraise_num()) + 1;
								content.setPraise_num(num + "");
								textview.setText(num + "");
								MyToast.makeText(context, "恭喜赞成功", Toast.LENGTH_SHORT).show();
							} else {
								if (json.has("egg")) {
									JSONArray json_arr = json.getJSONArray("egg");
									JSONObject egg_obj = json_arr.getJSONObject(0);
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
								} else {
									MyToast.makeText(context, "恭喜取消赞成功", Toast.LENGTH_SHORT).show();
								}
								int num = Integer.parseInt(content.getPraise_num()) - 1;
								content.setPraise_num(num + "");
								textview.setText(num + "");

								// 如果是当前用户就删掉当前行
								if (is_remove_item) {
									list_content.remove(content);
									notifyDataSetChanged();
								}
							}

							LiuLianApplication.is_update_praise = true;

						} else {
							MyToast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}

	}

	private void delete_content(final Content content) {

		String url = PathConst.URL_DEL_CONTENT + "&id=" + content.getId() + "&uid=" + LiuLianApplication.current_user.getUid() +"&Luid=" + LiuLianApplication.current_user.getUid() + "&accesskey=" + LiuLianApplication.current_user.getAccesskey();
		AsyncHttpClient asyncHttp = new AsyncHttpClient();
		asyncHttp.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {
					JSONObject json = new JSONObject(arg0);
					if (1 == json.optInt("flag")) {
						if (json.has("egg")) {
							JSONArray json_arr = json.getJSONArray("egg");
							JSONObject egg_obj = json_arr.getJSONObject(0);
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
						} else {
							alertDialog.dismiss();
							MyToast.makeText(context, json.optString("msg"), Toast.LENGTH_SHORT).show();
						}
						list_content.remove(content);
						notifyDataSetChanged();
					} else {
						alertDialog.dismiss();
						MyToast.makeText(context, json.optString("msg"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

}
