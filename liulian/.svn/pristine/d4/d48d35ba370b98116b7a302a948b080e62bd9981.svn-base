package com.haomee.liulian;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import com.haomee.util.imageloader.ImageLoaderCharles;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.entity.ShareContent;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class ShareActivity extends BaseActivity {

	public static final int RESUlT_CODE_WEIBO = 1;
	public static final int RESUlT_CODE_LOCAL = 6;

	private static final int THUMB_SIZE = 150;

	private ShareContent share;

	private LinearLayout layout_report;

	private ImageView img_fav;
	private boolean isFav; // 是否收藏

	//private int share_type = -1;

	private LinearLayout share_to_qq, share_to_zone, share_to_weibo, share_to_weixin, share_to_friends;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_share);

		share_to_qq = (LinearLayout) findViewById(R.id.share_to_qq);
		share_to_zone = (LinearLayout) findViewById(R.id.share_to_zone);
		share_to_weibo = (LinearLayout) findViewById(R.id.share_to_weibo);
		share_to_weixin = (LinearLayout) findViewById(R.id.share_to_weixin);
		share_to_friends = (LinearLayout) findViewById(R.id.share_to_friends);

		layout_report = (LinearLayout) findViewById(R.id.report_layout);
		if (getIntent().getBooleanExtra("have_report", false)) {
			layout_report.setVisibility(View.VISIBLE);
		} else {
			layout_report.setVisibility(View.GONE);
		}
		img_fav = (ImageView) findViewById(R.id.img_fav);
		share = (ShareContent) this.getIntent().getSerializableExtra("share");
		isFav = this.getIntent().getBooleanExtra("isFav", false);

		int share_type = share.getScope();
		
		if(share_type==-1){
			// 全部显示
		}else{
			share_to_weixin.setVisibility(share_type == 0?View.VISIBLE:View.GONE);
			share_to_friends.setVisibility(share_type == 1?View.VISIBLE:View.GONE);
			share_to_weibo.setVisibility(share_type == 2?View.VISIBLE:View.GONE);
			share_to_qq.setVisibility(share_type == 3?View.VISIBLE:View.GONE);
			share_to_zone.setVisibility(share_type == 4?View.VISIBLE:View.GONE);
		}
		

		img_fav.setImageResource(isFav ? R.drawable.share_faved : R.drawable.share_fav);


		findViewById(R.id.layout_top).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShareActivity.this.finish();
			}
		});

		findViewById(R.id.share_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareActivity.this.finish();
			}
		});

	}

	public void onItemClick(View item) {
		switch (item.getId()) {
		case R.id.share_to_weibo: {
			if (!getIntent().getBooleanExtra("have_report", false)) {
				StatService.onEvent(ShareActivity.this, "share_content_weibo", "话题详情页分享内容到新浪微博", 1);
			} else {
				StatService.onEvent(ShareActivity.this, "share_topic_weibo", "话题详情页分享话题到新浪微博", 1);
			}
			Intent intent = this.getIntent();
			intent.setClass(ShareActivity.this, ShareResponseActivity.class);
			intent.putExtra("share", share);
			startActivity(intent);
			finish();
		}
			break;
		case R.id.share_to_qq: {

			if (!getIntent().getBooleanExtra("have_report", false)) {
				StatService.onEvent(ShareActivity.this, "share_content_qq", "分享内容到QQ", 1);
			} else {
				StatService.onEvent(ShareActivity.this, "share_topic_qq", "分享话题到QQ", 1);
			}
			shareToQQFriend(share.getTitle(), share.getSummary(), share.getImg_url(), share.getRedirect_url());
			finish();
			break;
		}
		case R.id.share_to_zone: {
			if (!getIntent().getBooleanExtra("have_report", false)) {
				StatService.onEvent(ShareActivity.this, "share_content_qq_zone", "分享内容到QQ空间", 1);
			} else {
				StatService.onEvent(ShareActivity.this, "share_topic_qq_zone", "分享话题到QQ空间", 1);
			}
			ArrayList<String> imageUrls = new ArrayList<String>();
			String img = share.getImg_thumb_url() == null ? share.getImg_url() : share.getImg_thumb_url();
			if (img != null&&!img.equals("")) {
				imageUrls.add(share.getImg_url());
			} else {
				imageUrls.add(CommonConst.ICON_DEFAULT_URL);
			}
			shareToQQZone(share.getTitle(), share.getSummary(), imageUrls, share.getRedirect_url());
			finish();
			break;
		}
		case R.id.share_to_weixin: {
			if (!getIntent().getBooleanExtra("have_report", false)) {
				StatService.onEvent(ShareActivity.this, "share_content_wx", "分享内容到微信", 1);
			} else {
				StatService.onEvent(ShareActivity.this, "share_topic_wx", "分享话题到微信", 1);
			}
			String img = share.getImg_thumb_url() == null ? share.getImg_url() : share.getImg_thumb_url();
			//String redirect_url = share.getWeb_url()==null?share.getRedirect_url():share.getWeb_url();
			shareToWX(share.getTitle(), share.getSummary(), img, share.getRedirect_url());
			finish();
			break;
		}
		case R.id.share_to_friends: {
			if (!getIntent().getBooleanExtra("have_report", false)) {
				StatService.onEvent(ShareActivity.this, "share_content_wx_friends", "分享内容到微信朋友圈", 1);
			} else {
				StatService.onEvent(ShareActivity.this, "share_topic_wx_friends", "分享话题到微信朋友圈", 1);
			}
			String img = share.getImg_thumb_url() == null ? share.getImg_url() : share.getImg_thumb_url();
			//String redirect_url = share.getWeb_url()==null?share.getRedirect_url():share.getWeb_url();
			shareToWXFriends(share.getTitle(), share.getSummary(), img, share.getRedirect_url());
			finish();
			break;
		}
		case R.id.layout_fav: {
			if (isFav) { // 1、收藏 2、取消收藏
				topicFav(2);
			} else {
				topicFav(1);
				StatService.onEvent(ShareActivity.this, "collect_topic", "话题详情页收藏话题", 1);
			}
			break;
		}
		case R.id.share_to_report:
			Intent intent = this.getIntent();
			intent.putExtra("report_type", CommonConst.REPOST_TYPE_DISCOVER);
			intent.putExtra("title", share.getTitle());
			intent.putExtra("content", share.getDesc());
			intent.putExtra("content_id", share.getId());
			intent.setClass(ShareActivity.this, ReportActivity.class);
			startActivity(intent);
			finish();
			StatService.onEvent(ShareActivity.this, "report_topic", "话题详情页举报话题", 1);
			break;
		}
	}

	/**
	 * 收藏
	 * @param flag 1、收藏 2、取消收藏
	 */
	private void topicFav(final int flag_fav) {

		if (LiuLianApplication.current_user == null) {
			MyToast.makeText(this, "还没有登录", Toast.LENGTH_SHORT).show();
		} else {
			String url = PathConst.URL_TOPIC_FAV + "&id=" + share.getId() + "&flag=" + flag_fav + "&uid=" + LiuLianApplication.current_user.getUid() + "&Luid=" + LiuLianApplication.current_user.getUid() + "&accesskey=" + LiuLianApplication.current_user.getAccesskey();
			AsyncHttpClient asyncHttp = new AsyncHttpClient();
			asyncHttp.get(url, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					try {
						JSONObject json = new JSONObject(arg0);
						if (1 == json.optInt("flag")) {
							if (flag_fav == 1) {
								MyToast.makeText(ShareActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
								isFav = true;
							} else {
								MyToast.makeText(ShareActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
								isFav = false;
							}

							img_fav.setImageResource(isFav ? R.drawable.share_faved : R.drawable.share_fav);

						} else {
							MyToast.makeText(ShareActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			LiuLianApplication.is_update_fav = true; // 提示刷新列表
		}

	}

	// 获取缩略图(按宽度)
	private Bitmap getScaledBitmap(Bitmap bitmap, int thumb_width) {
		float scale = thumb_width * 1.0f / bitmap.getWidth();
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	// 图片缩放
	public static Bitmap zoomBitmap(Bitmap bitmap, float scale) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	private IWXAPI wx_api;

	private void shareToWXFriends(String title, String summary, final String img_thumb_url, String redirect_url) {
		if (wx_api == null) {
			wx_api = WXAPIFactory.createWXAPI(this, CommonConst.WX_APP_ID, true);
			wx_api.registerApp(CommonConst.WX_APP_ID);
		}

		WXWebpageObject webpageObj = new WXWebpageObject();
		webpageObj.webpageUrl = redirect_url;
		final WXMediaMessage mediaMsg = new WXMediaMessage(webpageObj);
		mediaMsg.title = title;// 不能太长，否则微信会提示出错。不过博主没验证过具体能输入多长。
		mediaMsg.description = summary;

		// 请求网络图片，必须异步啊，虽然有本地缓存，但是不靠谱
		new AsyncTask<Object, Object, Bitmap>() {
			@Override
			protected Bitmap doInBackground(Object... params) {
				Bitmap bitmap = null;
				if (img_thumb_url == null) {
					Resources res = getResources();
					bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);
				} else {
                    bitmap= ImageLoaderCharles.getInstance(ShareActivity.this).getBitmap(img_thumb_url);
				}
				if (bitmap != null) {
					bitmap = getScaledBitmap(bitmap, THUMB_SIZE);
				}

				return bitmap;
			}

			@Override
			protected void onPostExecute(Bitmap result) {

				mediaMsg.setThumbImage(result);
				SendMessageToWX.Req localReq = new SendMessageToWX.Req();
				localReq.scene = SendMessageToWX.Req.WXSceneTimeline;
				localReq.transaction = System.currentTimeMillis() + "";
				localReq.message = mediaMsg;
				wx_api.sendReq(localReq);
			};
		}.execute();

	}

	private void shareToWX(String title, String summary, final String img_thumb_url, String redirect_url) {
		if (wx_api == null) {
			wx_api = WXAPIFactory.createWXAPI(this, CommonConst.WX_APP_ID, true);
			wx_api.registerApp(CommonConst.WX_APP_ID);
		}

		WXWebpageObject webpageObj = new WXWebpageObject();
		webpageObj.webpageUrl = redirect_url;
		final WXMediaMessage mediaMsg = new WXMediaMessage(webpageObj);
		mediaMsg.title = title;// 不能太长，否则微信会提示出错。不过博主没验证过具体能输入多长。
		mediaMsg.description = summary;

		// 请求网络图片，必须异步啊，虽然有本地缓存，但是不靠谱
		new AsyncTask<Object, Object, Bitmap>() {
			@Override
			protected Bitmap doInBackground(Object... params) {
				Bitmap bitmap = null;
				if (img_thumb_url == null) {
					Resources res = getResources();
					bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);
				} else {
                    bitmap=ImageLoaderCharles.getInstance(ShareActivity.this).getBitmap(img_thumb_url);
				}
				if (bitmap != null) {
					bitmap = getScaledBitmap(bitmap, THUMB_SIZE);
				}
				return bitmap;
			}

			@Override
			protected void onPostExecute(Bitmap result) {

				mediaMsg.setThumbImage(result);
				SendMessageToWX.Req localReq = new SendMessageToWX.Req();
				localReq.transaction = System.currentTimeMillis() + "";
				localReq.message = mediaMsg;
				wx_api.sendReq(localReq);
			};
		}.execute();

	}

	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private Tencent mTencent;

	private void shareToQQFriend(String title, String summary, String img_url, String redirect_url) {
		if (mTencent == null) {
			mTencent = Tencent.createInstance(CommonConst.QQ_APP_ID, this);
		}
		Bundle params = new Bundle();
		params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, redirect_url);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
		if (img_url != null) {
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, img_url);
		}

		mTencent.shareToQQ(this, params, new QQUIListener(this, 0));
	}

	private void shareToQQZone(String title, String summary, ArrayList<String> imageUrls, String redirect_url) {
		if (mTencent == null) {
			mTencent = Tencent.createInstance(CommonConst.QQ_APP_ID, this);
		}
		Bundle params_qzone = new Bundle();
		params_qzone.putString(QQShare.SHARE_TO_QQ_TITLE, title);
		params_qzone.putString(QQShare.SHARE_TO_QQ_TARGET_URL, redirect_url);
		params_qzone.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
		if (imageUrls != null && imageUrls.size() > 0) {
			params_qzone.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrls); // 多张图
		}
		mTencent.shareToQzone(this, params_qzone, new QQUIListener(this, 1));
	}

	private Bitmap getScaleBitmap(Bitmap mBitmap) {
		int width = mBitmap.getWidth();
		int height = mBitmap.getHeight();

		Matrix matrix = new Matrix();

		int densityDpi = this.getResources().getDisplayMetrics().densityDpi; // 屏幕密度DPI
		float icon_size = (float) (densityDpi * 0.3);
		matrix.postScale(icon_size / width, icon_size / height);
		Bitmap mScaleBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, true);
		return mScaleBitmap;
	}

	// 生成圆角图片
	private Bitmap GetRoundedCornerBitmap(Bitmap bitmap) {
		try {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
			int densityDpi = this.getResources().getDisplayMetrics().densityDpi; // 屏幕密度DPI

			float roundPx = (float) (densityDpi / 16);
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(Color.BLACK);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

			Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

			canvas.drawBitmap(bitmap, src, rect, paint);
			return output;
		} catch (Exception e) {
			return bitmap;
		}
	}

	public class QQUIListener implements IUiListener {

		Context context;
		int type;
		String show_toast;

		public QQUIListener(Context context, int type) {
			this.context = context;
			this.type = type;
		}

		@Override
		public void onCancel() {
			show_toast = (type == 0) ? "取消分享到QQ好友" : "取消分享到QQ空间";
			MyToast.makeText(context, show_toast, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onComplete(Object arg0) {
			show_toast = (type == 0) ? "成功分享到QQ好友" : "成功分享到QQ空间";
			MyToast.makeText(context, show_toast, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(UiError error) {
			MyToast.makeText(context, "分享失败，\n" + error.errorMessage, Toast.LENGTH_SHORT).show();
		}

	}

}
