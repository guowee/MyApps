package com.haomee.liulian;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.haomee.consts.CommonConst;
import com.haomee.entity.ShareContent;
import com.haomee.util.imageloader.ImageLoaderCharles;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

public class ShareResponseActivity extends BaseActivity implements IWeiboHandler.Response {

	/** 微博分享的接口实例 */
	private IWeiboShareAPI mWeiboShareAPI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 创建微博 SDK 接口实例
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(ShareResponseActivity.this, CommonConst.WB_APP_KEY);
		mWeiboShareAPI.registerApp(); // 将应用注册到微博客户端

		// 获取微博客户端相关信息，如是否安装、支持 SDK 的版本
		boolean isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();
		int supportApiLevel = mWeiboShareAPI.getWeiboAppSupportAPI();

		// 如果未安装微博客户端，设置下载微博对应的回调
		if (!isInstalledWeibo) {
			mWeiboShareAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
				@Override
				public void onCancel() {
					Toast.makeText(ShareResponseActivity.this, "取消下载", Toast.LENGTH_SHORT).show();
					ShareResponseActivity.this.finish();
				}
			});
		} else {

			ShareContent share = (ShareContent) this.getIntent().getSerializableExtra("share");
			if (share == null) {
				this.finish();
			}

			// 1. 初始化微博的分享消息
			// 用户可以分享文本、图片、网页、音乐、视频中的一种
			WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
			String temp = "";
			if (share.getTitle() != null) {
				temp += (share.getTitle() + "\n");
			}
			if (share.getSummary() != null) {
				temp += share.getSummary();
			}
			weiboMessage.textObject = getTextObj(temp);
			String img = share.getImg_thumb_url() == null ? share.getImg_url() : share.getImg_thumb_url();
			weiboMessage.mediaObject = getImageObj(img);

			// 2. 初始化从第三方到微博的消息请求
			SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
			// 用transaction唯一标识一个请求
			request.transaction = String.valueOf(System.currentTimeMillis());
			request.multiMessage = weiboMessage;

			// 3. 发送请求消息到微博，唤起微博分享界面
			mWeiboShareAPI.sendRequest(request);

		}

	}

	/**
	 * 创建文本消息对象。
	 * 
	 * @return 文本消息对象。
	 */
	private TextObject getTextObj(String text) {
		TextObject textObject = new TextObject();
		textObject.text = text;
		return textObject;
	}

	/**
	 * 创建图片消息对象。从本地图片取
	 * 
	 * @return 图片消息对象。
	 */
	private ImageObject getImageObj(int resId) {
		ImageObject imageObject = new ImageObject();
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
		imageObject.setImageObject(bitmap);
		return imageObject;
	}

	/**
	 * 创建图片消息对象。从网络Url取
	 * 
	 * @return 图片消息对象。
	 */
	private ImageObject getImageObj(String url) {
		ImageObject imageObject = new ImageObject();
		Bitmap bitmap = null;
		if (url == null) {
			Resources res = getResources();
			bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);
		} else {
			bitmap = ImageLoaderCharles.getInstance(ShareResponseActivity.this).getBitmap(url);
		}

		imageObject.setImageObject(bitmap);
		return imageObject;
	}

	/**
	 * 创建图片消息对象。从Bitmap
	 * 
	 * @return 图片消息对象。
	 */
	private ImageObject getImageObj(Bitmap bitmap) {
		ImageObject imageObject = new ImageObject();
		imageObject.setImageObject(bitmap);
		return imageObject;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.w("test", "ShareResponseActivity onNewIntent");
		mWeiboShareAPI.handleWeiboResponse(intent, this); // 当前应用唤起微博分享后，返回当前应用
	}

	@Override
	public void onResponse(BaseResponse baseResp) {// 接收微客户端博请求的数据。

		Log.w("test", "ShareResponseActivity onResponse");

		boolean result = false;

		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			result = true;
			Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			result = false;
			Toast.makeText(this, "取消分享", Toast.LENGTH_SHORT).show();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			result = false;
			Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
			break;
		}

		// 关闭当前页, 传递回调参数
		/*
		 * Intent intent = new Intent(); intent.putExtra("result", result);
		 * setResult(ShareActivity.RESUlT_CODE_WEIBO,intent);
		 */
		this.finish();
	}

}
