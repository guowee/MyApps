package com.haomee.liulian;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.haomee.entity.ShareContent;

public class WebPageActivity extends BaseActivity {
	private WebView webView;
	private TextView txt_title;
	private ImageView bt_back;

	private String url;
	private String afterLogin; // 回调js函数

	private String after_share_js; // 回调js函数

	private SharedPreferences preference_h5_values;

	private final int requestCode_gotoLogin = 111;

	private final int requestCode_share = 222;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.activity_web);

		txt_title = (TextView) this.findViewById(R.id.txt_title);

		url = this.getIntent().getStringExtra("url");
		String title = this.getIntent().getStringExtra("title");
		txt_title.setText(title);

		bt_back = (ImageView) findViewById(R.id.bt_back);
		bt_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				/*if (webView.canGoBack()) {

					webView.goBack();

				} else {*/

					WebPageActivity.this.finish();
				//}
			}
		});

		webView = (WebView) findViewById(R.id.webView);

		webView.getSettings().setJavaScriptEnabled(true);

		webView.getSettings().setDefaultTextEncodingName("UTF-8");

		//设置可以支持缩放   
		webView.getSettings().setSupportZoom(true);

		// 设置webviewChromClient
		webView.setWebChromeClient(new WebChromeClient());

		// 与JavaScript交互
		webView.addJavascriptInterface(new WebAppInterface(this), "app");

		// 防止跳出浏览器
		webView.setWebViewClient(new WebViewClient() {
			//  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);

				webView.loadUrl(String.format("javascript:appWebViewHasReady()"));// 页面加载完成回调
			}

		});

		// 点击文件下载
		webView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});

		//		url = "http://172.16.100.192/~wayde/1.html";

		webView.loadUrl(url);

		Log.i("test", "webView:" + url);

		// 保存与javascript交互的数据
		preference_h5_values = getSharedPreferences("h5_values", Context.MODE_PRIVATE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// 登陆成功失败都回调一下
		if (requestCode == requestCode_gotoLogin) {
			webView.loadUrl(String.format("javascript:" + afterLogin + "()"));// java端调用webview的JS  
		}

		if (requestCode == requestCode_share) {
			webView.loadUrl(String.format("javascript:" + after_share_js + "()"));// java端调用webview的JS  
		}

	}

	/**
	 * 自定义的Android代码和JavaScript代码之间的桥梁类
	 * 如果target 大于等于API 17，则需要加上如下注解 @JavascriptInterface
	 * 
	 * @author 1
	 * 
	 */
	public class WebAppInterface {
		Activity context;

		/** Instantiate the interface and set the context */
		WebAppInterface(Activity c) {
			context = c;
		}

		/**
		 * JavaScript接口，摇一摇
		 */
		@JavascriptInterface
		public void goMotionShake() {

		}

		/**
		 * JavaScript接口，进入其他人页面
		 */
		@JavascriptInterface
		public void goToOtherUserPage(String uid) {

			Intent intent = new Intent();

			if (uid.equals(LiuLianApplication.current_user.getUid())) {

				intent.setClass(context, MainActivity.class);

			} else {

				intent.setClass(context, UserInfoDetail.class);
			}
			intent.putExtra("uid", uid);
			context.startActivity(intent);

		}

		/**
		 * JavaScript接口，进入话题详情页
		 */
		@JavascriptInterface
		public void goToTopic(String topic_id) {

			Intent intent = new Intent();
			intent.setClass(context, ContentList.class);
			intent.putExtra("topic_id", topic_id);
			context.startActivity(intent);

		}

		/**
		 * JavaScript接口，打开分享界面
		 * 
		 */
		@JavascriptInterface
		public void goToShare(String url, String title, String summary, String img_url, int scope, String share_js) {
			after_share_js = share_js;
			Intent intent = new Intent();
			intent.setClass(context, ShareActivity.class);
			ShareContent share = new ShareContent();
			share.setTitle(title);
			share.setSummary(summary);
			share.setImg_url(img_url);
			share.setRedirect_url(url);
			share.setScope(scope);
			intent.putExtra("share", share);
			context.startActivityForResult(intent, requestCode_share);
		}

		@JavascriptInterface
		public void goToLogin(String after) {
			afterLogin = after;
			Intent intent = new Intent();
			intent.setClass(context, LoginActivity.class);
			context.startActivityForResult(intent, requestCode_gotoLogin);
		}

		/**
		 * JavaScript接口，获取用户id
		 * 
		 */
		public String getUserId() {

			return LiuLianApplication.current_user.getUid();
		}

		/**
		 * 退出当前页
		 * 
		 */
		@JavascriptInterface
		public void jsCloseWebView() {
			context.finish();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		WebPageActivity.this.finish();
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
//			webView.goBack();// 返回前一个页面
//			return true;
//		} else {
//			WebPageActivity.this.finish();
//		}
//		return super.onKeyDown(keyCode, event);
//	}

}
