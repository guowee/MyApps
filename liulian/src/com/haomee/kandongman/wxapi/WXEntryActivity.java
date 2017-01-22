package com.haomee.kandongman.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.haomee.view.MyToast;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

//分享到微信
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	private IWXAPI api;

	public static int flag; // 区分回调来源那个Activity

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, "wx6d09bad3ad5db913", false);
		api.handleIntent(getIntent(), this);
	}

	@Override
	public void onReq(BaseReq arg0) {

	}

	@Override
	public void onResp(BaseResp resp) {
		String result = "";
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = "成功分享到微信";
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = "取消分享到微信";
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = "微信版本过低，分享到微信失败";
			break;
		default:
			result = "分享到微信出现未知错误";
			break;
		}
		MyToast.makeText(this, result, Toast.LENGTH_SHORT).show();
		// TODO 微信分享 成功之后调用接口  
		this.finish();
	}

}
