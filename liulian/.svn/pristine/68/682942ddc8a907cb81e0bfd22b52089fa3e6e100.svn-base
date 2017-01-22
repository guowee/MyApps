package com.haomee.adapter;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.Html;
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

import com.baidu.mobstat.StatService;
import com.haomee.chat.activity.ChatActivity;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.Content;
import com.haomee.entity.ContentPicture;
import com.haomee.entity.Movie;
import com.haomee.entity.Music;
import com.haomee.entity.ShareContent;
import com.haomee.liulian.ImgsBrowseActivity_Single;
import com.haomee.liulian.LiuLianApplication;
import com.haomee.liulian.R;
import com.haomee.liulian.ReportActivity;
import com.haomee.liulian.ShareActivity;
import com.haomee.liulian.ShareMedalActivity;
import com.haomee.liulian.UserInfoDetail;
import com.haomee.player.MediaPlayerActivity;
import com.haomee.player.MyMusicPlayer;
import com.haomee.util.NetworkUtil;
import com.haomee.util.ViewUtil;
import com.haomee.util.imageloader.ImageLoaderCharles;
import com.haomee.view.MyToast;
import com.haomee.view.RoundProgressBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class ContentAdapter extends BaseAdapter {

	private List<Content> list_content;
	private Context context;
	private LayoutInflater inflater;
	public boolean is_remove_item = false;
	// 获取当前进度条
	private RoundProgressBar progressBar;

	private int margin_image; // 图片的间距

	private Content content;

	public RoundProgressBar getCurrentProgressBar() {
		return progressBar;
	}

	public ContentAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		margin_image = ViewUtil.dip2px(context, 10);
	}

	public void setData(List<Content> list_content) {
		this.list_content = list_content;
		notifyDataSetChanged();
	}

	ViewHolder viewHolder;

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// 只有当convertView不存在的时候才去inflate子元素
		// 不能使用重用了，不然播放时进度条不对
		viewHolder = new ViewHolder();
		convertView = inflater.inflate(R.layout.item_topic_detail, null);
		viewHolder.ll_top = (LinearLayout) convertView.findViewById(R.id.ll_top);
		viewHolder.linear_mm = (LinearLayout) convertView.findViewById(R.id.linear_mm);
		viewHolder.m_name = (TextView) convertView.findViewById(R.id.m_name);
		viewHolder.m_director = (TextView) convertView.findViewById(R.id.m_director);
		viewHolder.m_actor = (TextView) convertView.findViewById(R.id.m_actor);
		viewHolder.item_image = (ImageView) convertView.findViewById(R.id.item_image);
		viewHolder.item_content = (TextView) convertView.findViewById(R.id.item_content);
		viewHolder.user_img = (ImageView) convertView.findViewById(R.id.user_img);
		viewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
		viewHolder.item_create_time = (TextView) convertView.findViewById(R.id.item_create_time);
		viewHolder.item_praise = (TextView) convertView.findViewById(R.id.item_praise);
		viewHolder.view_num = (TextView) convertView.findViewById(R.id.view_num);
		viewHolder.start_session = (ImageView) convertView.findViewById(R.id.start_session);
		viewHolder.bt_love = (ImageView) convertView.findViewById(R.id.bt_love);
		viewHolder.report = (TextView) convertView.findViewById(R.id.report);
		viewHolder.bt_play = (RoundProgressBar) convertView.findViewById(R.id.bt_play);
		viewHolder.report = (TextView) convertView.findViewById(R.id.report);
		viewHolder.bt_share = (ImageView) convertView.findViewById(R.id.bt_share);
		viewHolder.content_line = (View) convertView.findViewById(R.id.content_line);

		viewHolder.user_img.setOnClickListener(btOnClickListener);
		viewHolder.report.setOnClickListener(btOnClickListener);
		viewHolder.bt_play.setOnClickListener(btOnClickListener);
		viewHolder.start_session.setOnClickListener(btOnClickListener);
		viewHolder.bt_love.setOnClickListener(btOnClickListener);
		viewHolder.bt_share.setOnClickListener(btOnClickListener);

		Content content = list_content.get(position);

		viewHolder.start_session.setTag(content);
		viewHolder.bt_love.setTag(viewHolder.item_praise);
		viewHolder.item_praise.setTag(content);
		viewHolder.bt_share.setTag(content);

		viewHolder.item_image.setImageResource(R.drawable.item_default);
		viewHolder.bt_play.setVisibility(View.GONE);
		switch (content.getType()) {
		case 1:// 纯文字
			viewHolder.linear_mm.setVisibility(View.GONE);
			viewHolder.item_image.setVisibility(View.GONE);
			break;
		case 2: { // 文字图片
			viewHolder.linear_mm.setVisibility(View.GONE);
			viewHolder.item_image.setVisibility(View.VISIBLE);
			ContentPicture pic = content.getPicture();

			// 控制图片宽高
			int width = parent.getWidth();
			if (width > 0 && pic.getWidth() > 0) {
				// int height = pic.getHeight() * (width - margin_image * 2) /
				// pic.getWidth();
				int height = width - margin_image * 2;
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
				viewHolder.item_image.setLayoutParams(params);
			}
			
			ImageLoaderCharles.getInstance(context).addTask(pic.getSmall(), viewHolder.item_image);

			viewHolder.item_image.setOnClickListener(btOnClickListener);
			viewHolder.item_image.setTag(R.id.content_list_image, position);

			break;
		}
		case 3: { // 音乐

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

				MyMusicPlayer.getInstance().setProgressBar(progressBar);

			} else {
				viewHolder.bt_play.setProgress(0);
				viewHolder.bt_play.setImageResource(R.drawable.content_button_music_start);
			}

			// 控制图片宽高
			int width = parent.getWidth();
			if (width > 0) {
				int height = width - margin_image * 2; // 1:1
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
				params.setMargins(margin_image, margin_image, margin_image, 0);
				viewHolder.item_image.setLayoutParams(params);
			}

			
			ImageLoaderCharles.getInstance(context).addTask(music.getCover(), viewHolder.item_image);
			break;
		}
		case 4: { // 电影
			viewHolder.item_image.setVisibility(View.VISIBLE);
			viewHolder.linear_mm.setVisibility(View.VISIBLE);
			Movie movie = content.getMovie();
			// 控制图片宽高
			viewHolder.m_name.setText("电影:" + movie.getCname() + " (" + movie.getEnam() + ") " + movie.getRtime() + " " + movie.getLocation());
			viewHolder.m_director.setText("导演:" + movie.getDirect());
			viewHolder.m_actor.setText("演员:" + movie.getActor());

			int width = parent.getWidth();
			if (width > 0) {
				// int height = (width - margin_image * 2) / 2 * 3; // 1:1
				int height = width - margin_image * 2;
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
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

			/*
			 * String[] strs = content.getContent().split("[\t]");
			 * if(strs.length>1){ viewHolder.item_content.setText(Html.fromHtml(
			 * "<font size=\"2\" color=\"#555555\">" + strs[0] +
			 * "</font><font size=\"2\" color=\"#Fe7700\">" + strs[1] +
			 * "</font>")); }else{
			 */
			viewHolder.item_content.setText(content.getContent());
			// }
		}

		if (content.getUser().getUid().equals(LiuLianApplication.current_user.getUid())) {
			viewHolder.start_session.setImageResource(R.drawable.content_button_delete_default);
			viewHolder.report.setVisibility(View.GONE);
		} else {
			viewHolder.start_session.setImageResource(R.drawable.content_button_comment_default);
			viewHolder.report.setVisibility(View.VISIBLE);
		}

		viewHolder.user_img.setImageResource(R.drawable.item_icon);
		ImageLoaderCharles.getInstance(context).addTask(content.getUser().getImage(), viewHolder.user_img);
		viewHolder.user_img.setBackgroundResource(CommonConst.user_sex[content.getUser().getSex()]);

		viewHolder.user_name.setText(content.getUser().getName());
		viewHolder.item_create_time.setText(content.getCreate_time());
		viewHolder.item_praise.setText(content.getPraise_num());
		viewHolder.view_num.setText(content.getView_num());
		if (content.isIs_praised()) {
			viewHolder.bt_love.setImageResource(R.drawable.content_button_like_pressed);
		} else {
			viewHolder.bt_love.setImageResource(R.drawable.content_button_like_default);
		}

		viewHolder.user_img.setTag(list_content.get(position).getUser().getUid());
		viewHolder.report.setTag(list_content.get(position));

		return convertView;
	}

	// 点赞
	private void praise_content(final Content content, final int is_praise, final TextView textview) {

		if (LiuLianApplication.current_user == null) {
			MyToast.makeText(context, "还没有登录", Toast.LENGTH_SHORT).show();
		} else {
			String url = PathConst.URL_PRAISE_CONTENT + "&id=" + content.getId() + "&flag=" + is_praise + "&uid=" + LiuLianApplication.current_user.getUid() +"&Luid=" + LiuLianApplication.current_user.getUid() + "&accesskey=" + LiuLianApplication.current_user.getAccesskey();
			AsyncHttpClient asyncHttp = new AsyncHttpClient();
			asyncHttp.get(url, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					try {
						JSONObject json = new JSONObject(arg0);

						if (1 == json.optInt("flag")) {
							if (is_praise == 1) {

								int num = 0;
								try {
									num = Integer.parseInt(content.getPraise_num());
								} catch (Exception e) {
									e.printStackTrace();
								}
								num++;

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

	// 删除
	private void delete_content(final Content content) {

		String url = PathConst.URL_DEL_CONTENT + "&id=" + content.getId() +"&Luid=" + LiuLianApplication.current_user.getUid() + "&uid=" + LiuLianApplication.current_user.getUid() + "&accesskey=" + LiuLianApplication.current_user.getAccesskey();
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
							MyToast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
						}

						list_content.remove(content);
						notifyDataSetChanged();
					} else {
						MyToast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

	private class ViewHolder {

		private LinearLayout ll_top;
		private LinearLayout linear_mm;
		private TextView m_name, m_director, m_actor;
		private ImageView item_image;
		private TextView item_content;
		private ImageView user_img;
		private TextView user_name;
		private TextView item_create_time;
		private TextView item_praise;
		private ImageView start_session, bt_share;
		private TextView view_num;
		private ImageView bt_love;
		private TextView report;
		private RoundProgressBar bt_play;
		private View content_line;
	}

	private Button bt_commit, bt_cancel;
	private AlertDialog alertDialog;
	private OnClickListener btOnClickListener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.user_img: {
				Intent intent = new Intent();
				String user_id = (String) v.getTag();
				if (user_id.equals(LiuLianApplication.current_user.getUid())) {

					Toast.makeText(context, "这是你自己哦", 1).show();

				} else {
					intent.putExtra("uid", user_id);
					intent.setClass(context, UserInfoDetail.class);
					context.startActivity(intent);

					StatService.onEvent(context, "content_list_ta_detail", "话题详情页ta的头像点击次数", 1);
				}

				break;
			}
			case R.id.report: {
				Intent intent = new Intent();
				Content content = (Content) v.getTag();
				intent.putExtra("report_type", CommonConst.REPOST_TYPE_CONTENT);
				intent.putExtra("title", content.getUser().getName());
				intent.putExtra("content", content.getContent());
				intent.putExtra("content_id", content.getId());
				intent.setClass(context, ReportActivity.class);
				context.startActivity(intent);
				StatService.onEvent(context, "content_list_report", "话题详情页举报点击次数", 1);
				break;
			}
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

					StatService.onEvent(context, "content_list_play_music", "话题详情页播放音乐", 1);
				} else if (v.getTag() instanceof Movie) {
					MyMusicPlayer.getInstance().pause(); // 如果有音乐先暂停
					Movie movie = (Movie) v.getTag();
					Intent intent = new Intent();
					intent.setClass(context, MediaPlayerActivity.class);
					intent.putExtra("movie", movie);
					context.startActivity(intent);
					StatService.onEvent(context, "content_list_play_music", "话题详情页播放电影", 1);
				}
				break;
			}

			case R.id.start_session: {

				final Content content = (Content) v.getTag();

				// 发起会话
				if (!content.getUser().getUid().equals(LiuLianApplication.current_user.getUid())) {
					Intent intent = new Intent();
					intent.setClass(context, ChatActivity.class);
					intent.putExtra("uId", content.getUser().getUid());// 聊天对象的uid
					intent.putExtra("userId", content.getUser().getHx_username());// 聊天对象的环信ID
					intent.putExtra("nickname", content.getUser().getName());// 聊天对象的昵称
					intent.putExtra("attachmentContent", content.getContent());
					intent.putExtra("attachmentId", content.getId());
					intent.putExtra("is_from_content", true);
					// 文字
					// 图文
					switch (content.getType()) {
					case 1:
						intent.putExtra("attachmentImage", "");
						intent.putExtra("attachmentTitle", "");
						break;
					case 2:
						intent.putExtra("attachmentTitle", "");
						intent.putExtra("attachmentImage", content.getPicture().getSmall() + "!300");
						break;
					case 3:// 音乐
						intent.putExtra("attachmentType", false);
						intent.putExtra("attachmentTitle", content.getMusic().getTitle());
						intent.putExtra("attachmentImage", content.getMusic().getCover());
						break;
					case 4:// 电影
						intent.putExtra("attachmentType", true);
						intent.putExtra("attachmentTitle", content.getMovie().getCname());
						intent.putExtra("attachmentImage", content.getMovie().getCover());
						break;
					}
					StatService.onEvent(context, "content_list_start_session", "话题详情页聊天点击次数", 1);
					context.startActivity(intent);
				} else {
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
							delete_content(content);// 删除内容
							alertDialog.dismiss();
						}
					});
					bt_cancel.setOnClickListener(btOnClickListener);

				}
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
						praise_content(content, 2, txt_count);// 取消
					} else {
						content.setIs_praised(true);
						temp.setImageResource(R.drawable.content_button_like_pressed);
						praise_content(content, 1, txt_count);// 赞
						StatService.onEvent(context, "content_list_praise", "话题详情页赞点击次数", 1);
					}
				} else {
					MyToast.makeText(context, "请检查网络", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case R.id.bt_share: {
				// 分享内容
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
						share.setImg_url(com.haomee.consts.CommonConst.ICON_DEFAULT_URL);// logo图片
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
					StatService.onEvent(context, "content_list_share", "话题详情页分享点击次数", 1);
				}
				break;
			}

			case R.id.item_image:

				int index = (Integer) v.getTag(R.id.content_list_image);
				Intent intent = new Intent();
				intent.setClass(context, ImgsBrowseActivity_Single.class);
				intent.putExtra("url", list_content.get(index).getPicture().getLarge());
				context.startActivity(intent);
				break;
			default:
				break;
			}

		}

	};

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
		// TODO Auto-generated method stub
		return position;
	}

}
