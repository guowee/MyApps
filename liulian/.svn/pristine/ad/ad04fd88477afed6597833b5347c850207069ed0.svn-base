package com.haomee.chat.task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.haomee.consts.PathConst;
import com.haomee.util.FileDownloadUtil;
import com.haomee.util.NetworkUtil;
import com.haomee.util.StringUtil;
import com.haomee.view.MyToast;

public class LevelTask extends AsyncTask<String, Void, Bitmap> {

	private String url_download;
	private TextView text_title;
	private Context context;
	public LevelTask(String url_download,TextView text_title,Context context){
		this.url_download=url_download;
		this.text_title=text_title;
		this.context=context;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		File cache = null;
		String path_cache = "";
		String file_name_md5 = "";
		Bitmap bitmap=null;
		if (NetworkUtil.dataConnected(context)) {
			path_cache = FileDownloadUtil.getDefaultLocalDir(PathConst.IMAGE_CACHDIR);
			try {
				if (url_download != null && !url_download.trim().equals("")) {
					file_name_md5 = StringUtil.getMD5Str(url_download);
					cache = new File(path_cache + file_name_md5);
					if(search_local_emotins(file_name_md5)){
						bitmap=BitmapFactory.decodeFile(path_cache + file_name_md5);
						return bitmap;
					}
					HttpURLConnection httpConnection = null;
					try {
						URL url = new URL(url_download);
						httpConnection = (HttpURLConnection) url.openConnection();
						BufferedInputStream bis = new BufferedInputStream(httpConnection.getInputStream());
						FileOutputStream fos = new FileOutputStream(cache);
						int len = 0;
						byte[] buffer = new byte[1024 * 10]; // 文件写缓存

						long current = 0;
						while ((len = bis.read(buffer)) != -1) {
							fos.write(buffer, 0, len);
							fos.flush();
							current += len;
						}

						fos.flush();
						fos.close();
						return BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
					}catch(Exception e){

					}
				}
			} catch (Exception e) {
				MyToast.makeText(context, "下载失败", 1).show();
			} finally {
			}
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		if(result!=null){
			ImageSpan imgSpan = new ImageSpan(context,result);
			SpannableString spanString = new SpannableString("icon");
			spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			text_title.append(spanString);
		}
	}
	/**
	 * 遍历本地文件展示
	 */
	private boolean search_local_emotins(String name) {
		String emotions_base_path = FileDownloadUtil.getDefaultLocalDir(PathConst.IMAGE_CACHDIR);
		File file = new File(emotions_base_path);
		File[] files = file.listFiles();
		for (File f : files) {
			if(f.getName().equals(name)){
				return true;
			}
		}
		return false;
	}
}
