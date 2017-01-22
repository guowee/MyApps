package com.haomee.liulian;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import com.haomee.consts.PathConst;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.util.StringUtil;
import com.haomee.view.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class StartActivity extends BaseActivity {

	private boolean is_first_new_version;
	private SharedPreferences preferences_is_first;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		preferences_is_first = getSharedPreferences("preferences_is_first", Activity.MODE_PRIVATE);

		is_first_new_version = preferences_is_first.getBoolean("is_first_new_version", false);
		getJosn();//获取脏词库
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent();
				if (!is_first_new_version) {
					i.setClass(StartActivity.this, GuideActivity.class);
				} else {
					if (LiuLianApplication.current_user == null) {
						i.setClass(StartActivity.this, LoginPageActivity.class);
					} else {
						if (LiuLianApplication.current_user.getName().equals("")) {
							i.setClass(StartActivity.this, UserInfoActivity.class);
						} else {
							i.setClass(StartActivity.this, MainActivity.class);
						}
					}
				}
				StartActivity.this.startActivity(i);
				StartActivity.this.finish();
			}
		}, 2000);
	}
	/**
	 * 获取脏词库
	 */
	private void getJosn(){
		final SharedPreferences share_prefenrence_words= getSharedPreferences("config_last_day", Context.MODE_PRIVATE) ;
		String last_day = share_prefenrence_words.getString("last_day_bad_words", "");//最近一次获取臧词
		final String today=StringUtil.getDateFormat(new Date());
		if(!today.equals(last_day)){

			if (!NetworkUtil.dataConnected(this)) {
				MyToast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
				return;
			}
			AsyncHttpClient asyncHttp = new AsyncHttpClient();
			String url_bad_words=PathConst.URL_BAD_WORDS_NEW;
			asyncHttp.get(url_bad_words, new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(String arg0) {
					// TODO Auto-generated method stub
					super.onSuccess(arg0);

					if(arg0==null||arg0.length()==0){
						return;
					}
					String dir_offline = FileDownloadUtil.getDefaultLocalDir(PathConst.BAD_WORDS_PATH);
					File file_local=new File(dir_offline+PathConst.BAD_WORDS_FILE);
					boolean is_save = FileDownloadUtil.saveStringToLocal(arg0, file_local);
					if(is_save){
						Editor editor = share_prefenrence_words.edit();
						editor.putString("last_day_bad_words", today);
						editor.commit();
					}
				}
			});
		}
	}
}
