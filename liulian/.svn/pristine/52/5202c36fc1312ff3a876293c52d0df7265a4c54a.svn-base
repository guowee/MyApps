package com.haomee.liulian;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.haomee.consts.CommonConst;
import com.haomee.consts.PathConst;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.StorageUtil;

public class UpdateActivity extends BaseActivity {

	private ViewGroup frame_info, frame_update;
	private ProgressBar progressBar_update;
	private String apk_url, apk_name;

	private View bt_close, bt_update, layout_bt, view_line;
	private TextView txt_newFeature, txt_version, txt_size;
	private CheckBox box_cancel;
	private int last_app_version; // 上一个版本号（用户勾选不更新）
	private int new_app_version; // 服务器最新版本号
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);

		preferences = getSharedPreferences(CommonConst.PREFERENCES_SETTING, Activity.MODE_PRIVATE);

		frame_info = (ViewGroup) this.findViewById(R.id.frame_info);
		frame_update = (ViewGroup) this.findViewById(R.id.frame_update);
		layout_bt = this.findViewById(R.id.layout_bt);
		bt_close = this.findViewById(R.id.bt_close);
		bt_update = this.findViewById(R.id.bt_update);
		view_line = this.findViewById(R.id.view_line);
		progressBar_update = (ProgressBar) findViewById(R.id.pb_update);
		txt_newFeature = (TextView) this.findViewById(R.id.txt_newFeature);
		txt_version = (TextView) this.findViewById(R.id.txt_version);
		txt_size = (TextView) this.findViewById(R.id.txt_size);
		box_cancel = (CheckBox) UpdateActivity.this.findViewById(R.id.box_cancel);

		bt_update.setOnClickListener(btClickListener);
		bt_close.setOnClickListener(btClickListener);

		Bundle bundle_update = this.getIntent().getExtras();
		String version = bundle_update.getString("version");
		String newFeature = bundle_update.getString("version_desc");
		apk_url = bundle_update.getString("down_url");
		String size  =  bundle_update.getString("size");
		apk_name = this.getApkName(apk_url);
		new_app_version = bundle_update.getInt("version_num");
		last_app_version = preferences.getInt("last_app_version", 0); // 上个版本勾选不提示

		txt_newFeature.setText(newFeature);
		txt_version.setText(version);
		txt_size.setText(size);

		if (last_app_version >= new_app_version) { // 用户勾选不更新
			box_cancel.setChecked(true);
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		exit();
	}

	private OnClickListener btClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_close: {
				exit();
				break;
			}
			case R.id.bt_update: {
				if (null != apk_url) {

					String root = FileDownloadUtil.getSDcardRoot();
					File file = new File(root + PathConst.DIR_TEMP + apk_name);
					// 如果文件已经存在就直接安装
					if (file.exists()) {
						install_apk();
					} else {

						if (StorageUtil.isEnoughSDSpace(10 * 1024 * 1024)) { // 不足10M
							new DownApkTask().execute(apk_url);
						} else {
							Toast.makeText(UpdateActivity.this, UpdateActivity.this.getString(R.string.no_space), Toast.LENGTH_SHORT).show();
						}

					}
				}

				layout_bt.setVisibility(View.GONE);
				frame_info.setVisibility(View.GONE);
				view_line.setVisibility(View.GONE);
				frame_update.setVisibility(View.VISIBLE);
				break;
			}
			}
		}
	};

	private void exit() {

		SharedPreferences.Editor editor = preferences.edit();
		if (box_cancel.isChecked()) {
			Log.i("test", "这个版本不再提示更新：" + new_app_version);
			editor.putInt("last_app_version", new_app_version);
		} else {
			editor.putInt("last_app_version", 0);
		}
		editor.commit();

		UpdateActivity.this.finish();
	}

	private void install_apk() {
		String root = FileDownloadUtil.getSDcardRoot();

		File file = new File(root + PathConst.DIR_TEMP + apk_name);

		if (file.exists()) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setDataAndType(Uri.parse("file://" + root + PathConst.DIR_TEMP + apk_name), "application/vnd.android.package-archive");
			startActivity(i);
			finish();
		} else {
			Toast.makeText(this, "安装文件下载失败！", Toast.LENGTH_LONG).show();
		}

	}

	private class DownApkTask extends AsyncTask<String, Integer, String> {
		private URL url = null;
		private HttpURLConnection httpURLConnection = null;
		private InputStream inputStream = null;
		private RandomAccessFile outputStream = null;
		private File outFile = null;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				url = new URL(params[0]);
				httpURLConnection = (HttpURLConnection) url.openConnection();
				// 设置维持长连接
				httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
				// 设置连接服务器超时时间
				httpURLConnection.setConnectTimeout(600 * 1000);
				// 设置从服务器读取数据超时时间
				httpURLConnection.setReadTimeout(600 * 1000);
				// httpURLConnection.setAllowUserInteraction(true);

				String root = FileDownloadUtil.getSDcardRoot();
				File file = new File(root + PathConst.DIR_TEMP);
				if (!file.exists()) {
					file.mkdirs();
					// Runtime.getRuntime().exec("attrib +H "+ControlConsts.path);//隐藏文件夹
				}

				String temp_file = apk_name + "_temp";
				outFile = new File(root + PathConst.DIR_TEMP + temp_file);
				if (outFile.exists()) {
					outFile.delete();
				}
				file.createNewFile();

				// 设置当前线程下载的起点，终点
				int length = httpURLConnection.getContentLength();
				int startPosition = 0;
				inputStream = httpURLConnection.getInputStream();
				// 使用java中的RandomAccessFile 对文件进行随机读写操作
				outputStream = new RandomAccessFile(outFile, "rw");
				outputStream.seek(startPosition);

				byte[] buf = new byte[1024 * 10];
				int read = 0;
				int curSize = startPosition;
				while (true) {
					read = inputStream.read(buf);
					if (read == -1) {
						break;
					}
					outputStream.write(buf, 0, read);
					curSize = curSize + read;
					// 当调用这个方法的时候会自动去调用onProgressUpdate方法，传递下载进度
					publishProgress((curSize * 100 / length));
					if (curSize == length) {
						break;
					}
					Thread.sleep(10);
				}
				inputStream.close();
				outputStream.close();
				httpURLConnection.disconnect();

				// 下载完成之后修改名字
				FileDownloadUtil.rename(root + PathConst.DIR_TEMP, temp_file, apk_name);

			} catch (Exception e) {
				timeout(outFile);
				e.printStackTrace();
			} finally {

			}
			return "";
		}

		/**
		 * 下载完成，运行下载的文件进行更新。
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			install_apk();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			progressBar_update.setProgress(values[0]);
		}

	}

	private String getApkName(String url_apk) {
		if (null != url_apk) {
			int index = url_apk.lastIndexOf("/");
			apk_name = url_apk.substring(index);
		}
		return apk_name;
	}

	private void timeout(File outFile) {
		if (outFile.exists()) {
			outFile.delete();
		}
		finish();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

}
