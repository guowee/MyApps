package com.haomee.kandongman.wxapi;

import android.content.Context;
import android.widget.Toast;

import com.haomee.view.MyToast;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class QQUIListener implements IUiListener {

	Context context;
	int type;
	String show_toast;
	public QQUIListener(Context context,int type){
		this.context = context;
		this.type = type;
	}
	@Override
	public void onCancel() {
		show_toast = (type == 0)?"取消分享到QQ好友":"取消分享到QQ空间";
		MyToast.makeText(context, show_toast, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onComplete(Object arg0) {
		show_toast = (type == 0)?"成功分享到QQ好友":"成功分享到QQ空间";
		MyToast.makeText(context, show_toast, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onError(UiError arg0) {
		show_toast = (type == 0)?"分享到QQ好友失败":"分享到QQ空间失败";
		MyToast.makeText(context, "QQ版本过低，分享到QQ失败", Toast.LENGTH_SHORT).show();
	}

   

}