package com.haomee.liulian;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haomee.consts.PathConst;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.util.StorageUtil;
import com.haomee.util.UpdateUtil;
import com.haomee.view.MyToast;

public class UpdateAppActivity extends BaseActivity {

	private Bundle bundle_update;

	private TextView update_version;

	private int new_app_version;

	private RelativeLayout rl_update_app;

	private TextView update_info;

	private RelativeLayout rl_cancel, rl_commit;

	private String apk_url, apk_name;

	private ProgressBar progressBar_update;

	private RelativeLayout rl_start_page;

	private ImageView bt_back;

	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_update_app);

		update_version = (TextView) findViewById(R.id.update_version);

		update_version.setText("榴莲  android " + LiuLianApplication.appVersion_name);

		rl_update_app = (RelativeLayout) findViewById(R.id.rl_update_app);

		rl_start_page = (RelativeLayout) findViewById(R.id.rl_start_page);

		bt_back = (ImageView) findViewById(R.id.bt_back);
		
		bt_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		rl_update_app.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (new_app_version > 0) {
					if (new_app_version <= LiuLianApplication.appVersion) {
						MyToast.makeText(UpdateAppActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();
					} else {
						
						showUpdateDialog();
					}

				} else {
					if (NetworkUtil.dataConnected(UpdateAppActivity.this)) {
						
						MyToast.makeText(UpdateAppActivity.this, "正在获取版本信息...", Toast.LENGTH_SHORT).show();
					
					} else {
						
						MyToast.makeText(UpdateAppActivity.this, UpdateAppActivity.this.getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
					}

				}
			}
		});

		rl_start_page.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent();
				i.setClass(UpdateAppActivity.this, GuideActivity.class);
				UpdateAppActivity.this.startActivity(i);
			}
		});

		// 检查更新,只有wifi才检查
		if (NetworkUtil.isWifi(this)) {

			new UpdateUtil(this, handler_update).chechUpdate();
		}

	}

	public void showUpdateDialog() {

		dialog = new Dialog(this,R.style.Transparent_);  

		dialog.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.dialog_update_app);
		update_info = (TextView) window.findViewById(R.id.update_info);

		rl_cancel = (RelativeLayout) window.findViewById(R.id.rl_cancel);

		rl_commit = (RelativeLayout) window.findViewById(R.id.rl_commit);

		rl_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dialog.dismiss();

			}
		});

		rl_commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (null != apk_url) {
					dialog.dismiss();
					Toast.makeText(UpdateAppActivity.this, "正在下载，请稍后……", 1).show();
					String root = FileDownloadUtil.getSDcardRoot();
					File file = new File(root + PathConst.DIR_TEMP + apk_name);
					// 如果文件已经存在就直接安装
					if (file.exists()) {
						install_apk();
					} else {
						if (StorageUtil.isEnoughSDSpace(10 * 1024 * 1024)) { // 不足10M
							new DownApkTask().execute(apk_url);
						} else {
							MyToast.makeText(UpdateAppActivity.this, UpdateAppActivity.this.getString(R.string.no_space), Toast.LENGTH_SHORT).show();
						}
					}
				}
			
			}
		});

		update_info.setText(bundle_update.getString("version_desc"));

	}

	/**
	 * 获取版本更新
	 */
	@SuppressLint("HandlerLeak")
	public Handler handler_update = new Handler() {

		public void handleMessage(Message msg) {

			bundle_update = msg.getData();

			new_app_version = bundle_update.getInt("version_num");

			apk_url = bundle_update.getString("down_url");

			apk_name = UpdateAppActivity.this.getApkName(apk_url);

			if (new_app_version > LiuLianApplication.appVersion) {

				showUpdateDialog();
			}

		}
	};

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

			//			progressBar_update.setProgress(values[0]);

		}

	}

	private String getApkName(String url_apk) {
		if (null != url_apk && !url_apk.equals("")) {
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
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		finish();

	}

}
