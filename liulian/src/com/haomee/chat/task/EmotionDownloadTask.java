package com.haomee.chat.task;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.haomee.consts.PathConst;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.util.StringUtil;
import com.haomee.view.MyToast;



public class EmotionDownloadTask extends AsyncTask<String, Integer, String> {

	private Context context;
	// private String apk_id;
	private String url_download;

	private boolean isCancel = false;

	private Handler handler_download;

	private int pre_progress;
	private String coner_iamge;
	private int position;

	public EmotionDownloadTask(Context context, String url_download, String cover_image, Handler handler_download, int position) {
		this.context = context;
		this.url_download = url_download;
		this.handler_download = handler_download;
		this.position = position;
		this.coner_iamge = cover_image;
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		File cache = null;
		String path_cache = "";
		String file_name_md5 = "";
		if (NetworkUtil.dataConnected(context)) {
			path_cache = FileDownloadUtil.getDefaultLocalDir(PathConst.DOWNLOAD_EMOTIONS);
			try {
				if (url_download != null && !url_download.trim().equals("")) {
					file_name_md5 = StringUtil.getMD5Str(url_download);
					cache = new File(path_cache + file_name_md5);
					if (!cache.exists()) {
						HttpURLConnection httpConnection = null;
						try {
							URL url = new URL(url_download);
							httpConnection = (HttpURLConnection) url.openConnection();
							BufferedInputStream bis = new BufferedInputStream(httpConnection.getInputStream());
							FileOutputStream fos = new FileOutputStream(cache);
							int len = 0;
							byte[] buffer = new byte[1024 * 10]; // 文件写缓存

							long current = 0;
							long fileSize = httpConnection.getContentLength();

							while ((len = bis.read(buffer)) != -1) {
								fos.write(buffer, 0, len);
								fos.flush();
								current += len;

								int percent = (int) (current * 100 / fileSize);

								// 当调用这个方法的时候会自动去调用onProgressUpdate方法，传递下载进度
								if (percent != pre_progress) {
									publishProgress(percent);
									pre_progress = percent;
								}
							}
							fos.flush();
							fos.close();
						} catch (IOException e) {
							MyToast.makeText(context, "下载失败", 1).show();
						} catch (IllegalStateException e) {
							MyToast.makeText(context, "下载失败", 1).show();
						} catch (Exception e) {
							MyToast.makeText(context, "下载失败", 1).show();
						} finally {
							if (httpConnection != null) {
								httpConnection.disconnect();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!isCancel) {
			// 解压文件到该文件夹
			String current_dir = FileDownloadUtil.getDefaultLocalDir(PathConst.DOWNLOAD_EMOTIONS);
			try {
				unZip(path_cache + file_name_md5, current_dir + get_package_name(url_download)+"/");
				File f = new File(path_cache + file_name_md5);
				if (f.exists()) {
					f.delete();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return path_cache + file_name_md5;

	}

	private int buffer = 2048;

	public void unZip(String path, String savepath) {
		int count = -1;
		File file = null;
		InputStream is = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		new File(savepath).mkdir(); // 创建保存目录
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(path); // 解决中文乱码问题
			Enumeration<?> entries = zipFile.getEntries();

			while (entries.hasMoreElements()) {
				byte buf[] = new byte[buffer];

				ZipEntry entry = (ZipEntry) entries.nextElement();

				String filename = entry.getName();
				boolean ismkdir = false;
				if (filename.lastIndexOf("/") != -1) { // 检查此文件是否带有文件夹
					ismkdir = true;
				}
				filename = savepath+ filename;

				if (entry.isDirectory()) { // 如果是文件夹先创建
					file = new File(filename);
					file.mkdirs();
					continue;
				}
				file = new File(filename);
				if (!file.exists()) { // 如果是目录先创建
					if (ismkdir) {
						new File(filename.substring(0, filename.lastIndexOf("/"))).mkdirs(); // 目录先创建
					}
				}
				file.createNewFile(); // 创建文件

				is = zipFile.getInputStream(entry);
				fos = new FileOutputStream(file);
				bos = new BufferedOutputStream(fos, buffer);

				while ((count = is.read(buf)) > -1) {
					bos.write(buf, 0, count);
				}
				bos.flush();
				bos.close();
				fos.close();

				is.close();
			}

			zipFile.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (is != null) {
					is.close();
				}
				if (zipFile != null) {
					zipFile.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void cancelTask() {
		isCancel = true;
//		MyLog.i("test", "放弃下载");
	}

	/**
	 * 下载完成，运行下载的文件进行更新。
	 */
	@Override
	protected void onPostExecute(String zipFile) {

		String new_file_emo_path = get_package_name(url_download);
			Intent intent = new Intent("MyReceiver_Emotion_download");
			// 可通过Intent携带消息
			intent.putExtra("emotions", new_file_emo_path);
			intent.putExtra("coner_iamge", coner_iamge);
			// 发送广播消息
			context.sendBroadcast(intent);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		if (handler_download != null) {
			int progress = values[0];

			Message msg = new Message();
			msg.what = position;
			msg.arg1 = progress;
			handler_download.sendMessage(msg);
		}
	}

	/**
	 * 
	 * @param package_path
	 * @return
	 */
	private String get_package_name(String package_path) {
		String[] split = package_path.split("/");
		String[] split2 = split[split.length - 1].split("\\.");
		return split2[0];
	}
}