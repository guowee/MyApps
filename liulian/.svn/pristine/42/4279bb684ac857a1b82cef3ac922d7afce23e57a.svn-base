package com.haomee.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.liulian.LiuLianApplication;

/**
 * 版本更新管理
 *
 */
public class UpdateUtil {

	private Context context;
	private Handler handler_update;

	public UpdateUtil(Context context, Handler handler_update) {
		this.context = context;
		this.handler_update = handler_update;
	}

	/**
	 * 检查软件更新
	 * @throws JSONException 
	 */
	public JSONObject chechUpdate() {

		JSONObject json = null;

		try {

			if (NetworkUtil.dataConnected(context)) {
				//if (StorageUtil.isExternalStorageAvailable()) {
				//if (StorageUtil.isEnoughSDSpace(5 * 1024 * 1024)) {		// 不足5M

				//int current_version = context.getPackageManager().getPackageInfo("com.haomee.liulian", 0).versionCode;			
				new UpdateTask(LiuLianApplication.appVersion).execute();
				//} else {
				//Toast.makeText(context, context.getString(R.string.no_space),Toast.LENGTH_SHORT).show();
				//}
				//} else {
				//Toast.makeText(context, context.getString(R.string.no_sdcard),Toast.LENGTH_SHORT).show();
				//}
			} else {
				//Toast.makeText(context, context.getString(R.string.no_network),Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;

	}

	/**
	 * 检测更新,获取更新地址
	 * 
	 */
	private class UpdateTask extends AsyncTask<Integer, Integer, JSONObject> {

		private int current_version;

		public UpdateTask(int current_version) {
			this.current_version = current_version;
		}

		@Override
		protected JSONObject doInBackground(Integer... params) {

			JSONObject updateInfo = null;
			try {
				String urlPath = PathConst.URL_UPDATE;
				Log.e("地址：", urlPath+"");
				updateInfo = NetworkUtil.getJsonObject(urlPath, null, CommonConst.NETWORK_TIMEOUT_LENGTH);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return updateInfo;
		}

		@Override
		protected void onPostExecute(JSONObject updateInfo) {

			if (null != updateInfo) {
				try {
					Log.e("版本更新", updateInfo.toString()+"");
					Message msg = new Message();
					Bundle data = new Bundle();
					//if (current_version < versionCode) {					
					//	data.putBoolean("update", true);
//					data.putString("newFeature", updateInfo.getString("newFeature"));
					data.putInt("version_num", Integer.parseInt(updateInfo.getString("version_num")));
					data.putString("version_desc", updateInfo.getString("version_desc"));
					data.putString("size", updateInfo.getString("size"));
					data.putString("version", updateInfo.getString("version"));
					data.putString("down_url", updateInfo.getString("down_url"));
					String force = updateInfo.optString("is_force");
					boolean is_force = false;
					if("0".equals(force)){
						is_force  = false;
						
					}else{
						
						is_force =  true;
					}
					data.putBoolean("is_force",is_force); // 是否弹出提示
					//}else{
					//	data.putBoolean("update", false);
					//	data.putBoolean("isNewest", true);	// 最新，不用更新
					//}				
					msg.setData(data);
					handler_update.sendMessage(msg);

				} catch (JSONException e) {
					Log.e("test", e.getMessage());
					e.printStackTrace();
				}

			}

		}

	}

}
