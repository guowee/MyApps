package com.haomee.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.haomee.consts.PathConst;
import com.haomee.liulian.LiuLianApplication;

public class FileDownloadUtil {

	public static String getSDcardRoot() {

		if (!StorageUtil.isExternalStorageAvailable()) {
			Log.i("test", "sd卡不可用");
			return null;
		}
		// 获得sd卡根目录
		File root = Environment.getExternalStorageDirectory();
		String path_root = root.getAbsolutePath();

		return path_root;
	}

	public static String getSDcardRoot_default() {

		if (!StorageUtil.isExternalStorageAvailable()) {
			Log.i("test", "sd卡不可用");
			return null;
		}
		// 获得sd卡根目录
		File root = Environment.getExternalStorageDirectory();
		String path_root = root.getAbsolutePath();

		return path_root;
	}

	public static boolean isFileExisted(String path) {
		String path_root = getSDcardRoot();
		if (path_root == null) {
			return false;
		}

		String path_dir = path_root + path;
		File file = new File(path_dir);
		return file.exists();

	}
	
	/**
	 * 清理缓冲文件（没有下载日志文件的目录）
	 */
	public static void clearVideoCacheFiles() {
		String path_video = FileDownloadUtil.getVideoRoot();
		if (path_video == null) {
			return;
		}
		File dir_video = new File(path_video);

		File[] dirs = dir_video.listFiles();
		for (File file : dirs) {
			if (file.isDirectory()) {
				String path_log = file.getAbsolutePath() + "/" + PathConst.IMAGE_CACHDIR;
				File file_log = new File(path_log);
				if (!file_log.exists()) { // 如果日志文件不存在，认为是缓冲文件,删掉。（用户点击下载的文件不删掉）
					deleteDir(file);
				}

			} else {
				file.delete(); // 单个文件当垃圾删掉
			}
		}
	}
	
	public static String getVideoRoot() {

		String path_root = LiuLianApplication.download_selected_sdcard;
		if (path_root == null) {
			return null;
		}

		String path_dir = path_root + PathConst.IMAGE_CACHDIR;

		return makeDir(path_dir);
	}

	/*
	 * public static String getBookDir(String id){
	 * 
	 * CacheBook book = LiuLianApplication.db_download_book.getById(id);
	 * 
	 * String path_dir = null; if(book!=null){ path_dir = book.getLocal_path();
	 * }
	 * 
	 * // 先从数据库取，如果没有就取默认的。 if(path_dir==null || path_dir.equals("")){ String
	 * path_root = LiuLianApplication.download_selected_sdcard;
	 * if(path_root==null){ return null; } //String book_dir_name =
	 * book.getId()+"_"+book.getBook_name()+book.getEpisode_name(); //path_dir =
	 * LiuLianApplication.download_selected_sdcard +
	 * PathConst.BOOK_CACHE_DIR+"/"+book_dir_name; path_dir = path_root +
	 * PathConst.BOOK_CACHE_DIR+"/"+id; }
	 * 
	 * 
	 * return makeDir(path_dir); }
	 */

	public static String makeDir(String path_dir) {
		// 新建目录
		File dir = new File(path_dir);
		if (!dir.exists()) {
			boolean success = dir.mkdirs();
			if (!success) {
				Log.e("test", "创建目录失败:" + path_dir);
				return null;
			} else {
				Log.i("test", "创建成功:" + path_dir);
			}
		}

		return path_dir;
	}

	/**
	 * 获取系统默认SD卡的文件保存目录,没有就新建
	 * 
	 * @return
	 */
	public static String getDefaultLocalDir(String subDir) {

		String path_root = getSDcardRoot();
		// String path_root = LiuLianApplication.sdCardRoot;
		if (path_root == null) {
			return null;
		}

		String path_dir = path_root + subDir;

		return makeDir(path_dir);
	}

	/**
	 * 根据相对路径获取文件
	 * 
	 * @return
	 */
	/*
	 * public static File getLocalFile(String path) {
	 * 
	 * if(!StorageUtil.isExternalStorageAvailable()){ Log.i("test","sd卡不可用");
	 * return null; } // 获得sd卡根目录 File root =
	 * Environment.getExternalStorageDirectory(); String path_root =
	 * root.getAbsolutePath();
	 * 
	 * File file= new File(path_root + path);
	 * 
	 * return file; }
	 */

	/**
	 * 根据http contentType获取视频文件后缀
	 * 
	 * @return
	 */
	public static String getVideoExtension(String contentType) {

		return "mp4";

		/*
		 * String[] types = {"mp4","flv"};
		 * 
		 * String extension = null;
		 * 
		 * for(String type : types){ Pattern p_video =
		 * Pattern.compile(type,Pattern.CASE_INSENSITIVE); //建立一个模式。 Matcher
		 * m_video = p_video.matcher(contentType); //建立一个匹配器。
		 * if(m_video.find()){ // 取第一个地址就可以了 extension = type; break; } }
		 * 
		 * if(extension==null){ // 如果没有配成功，默认采用mp4 extension = "mp4"; }
		 * 
		 * return extension;
		 */
	}

	/**
	 * 获取http返回的ContentType
	 * 
	 * @param str_url
	 */
	public static String getVideoContentType(String str_url) {

		HttpURLConnection httpConnection = null;
		try {
			URL url = new URL(str_url);
			httpConnection = (HttpURLConnection) url.openConnection();
			int responseCode = httpConnection.getResponseCode();
			if (responseCode != 200) { // 请求不成功
				Log.i("test", "http responseCode:" + responseCode);
			} else {
				String content_type = httpConnection.getContentType();
				return content_type;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpConnection.disconnect();
		}

		return null;
	}

	/**
	 * 修改文件名
	 * 
	 * @param vid
	 * @param oldName
	 * @param newName
	 * @return 文件修改是否成功
	 */
	public static String rename(String path_dir, String oldName, String newName) {
		// String path_dir = FileDownloadUtil.getVideoDir(id);
		if (path_dir == null) {
			return null;
		}

		String path_old = path_dir + "/" + oldName;
		String path_new = path_dir + "/" + newName;
		File file_old = new File(path_old);
		File file_new = new File(path_new);
		if (file_old.exists() && !file_new.exists()) {
			file_old.renameTo(file_new);
		}

		return file_new.getAbsolutePath();
	}

	/**
	 * 删除视频文件夹（点击下载的文件）
	 * 
	 * @param vid
	 */
	public static void deleteDownloadFiles(String path_dir) {
		// final String path_dir = FileDownloadUtil.getVideoDir(id);
		if (path_dir == null) {
			return;
		}
		deleteDir(new File(path_dir));
	}

	/**
	 * 删除下载日志文件（视频文件定时去清理，现在只删除日志文件）
	 * 
	 * @param vid
	 */
	/*
	 * public static void deleteLogFile(String id){
	 * 
	 * String dir = FileDownloadUtil.getLocalDir(PathConst.VIDEO_CACHE_DIR)+
	 * "/"+ id; String path_log = dir +"/" + PathConst.download_log_name;
	 * 
	 * File log = new File(path_log); if(log.exists()){ log.delete(); }
	 * 
	 * }
	 */

	/**
	 * 删除文件夹
	 * 
	 * @param path
	 */
	public static void deleteDir(File dir) {
		if (dir != null && dir.exists()) {
			File[] files = dir.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						deleteDir(file); // 递归删除子文件夹
					} else {
						file.delete(); // 删除子文件
					}
				}
			}
			dir.delete(); // 最后删掉自己
		}
	}

	public static boolean moveDir(String dir_from, String dir_to) {

		if (dir_from == null || dir_to == null) {
			return false;
		}

		if (dir_from.equals(dir_to)) {
			return false;
		}

		boolean success = copyDir(dir_from, dir_to);
		if (success) {
			deleteDir(new File(dir_from)); // 复制完成之后，
		} else {
			deleteDir(new File(dir_to));
			return false;
		}

		return true;
	}

	public static boolean copyDir(String dir_from, String dir_to) {

		/*
		 * if(dir_from==null || dir_to==null){ return false; }
		 * 
		 * if(dir_from.equals(dir_to)){ return false; }
		 */

		try {
			File dir = new File(dir_from);
			if (!dir.exists()) {
				return true; // 如果dir_from目录不存在，说明不需要移动，所以认为成功。
			}

			// 文件一览
			File[] files = dir.listFiles();
			if (files == null) {
				return false;
			}
			// 目标
			File moveDir = new File(dir_to);
			if (!moveDir.exists()) {
				moveDir.mkdirs();
			}
			// 文件移动
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					boolean success = copyDir(files[i].getPath(), dir_to + "/" + files[i].getName());
					// 成功，删除原文件
					if (success) {
						files[i].delete();
					}

				}
				File moveFile = new File(moveDir.getPath() + "/" + files[i].getName());
				// 目标文件夹下存在的话，删除
				if (moveFile.exists()) {
					moveFile.delete();
				}

				copyFile(files[i], moveFile);
				// files[i].renameTo(moveFile);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean copyFile(File sourceFile, File targetFile) throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
		return false;
	}

	/**
	 * 获取文件夹大小
	 */
	public static long getDirSize(File dir) {
		if (dir == null || !dir.exists()) {
			return 0;
		}

		long size = 0;
		File[] flist = dir.listFiles();
		if (flist != null) {
			for (int i = 0; i < flist.length; i++) {
				if (flist[i].isDirectory()) {
					size = size + getDirSize(flist[i]);
				} else {
					size = size + flist[i].length();
				}
			}
		}

		return size;
	}

	public static boolean saveImageToLocal(String url_img, File file_img) {

		HttpURLConnection httpConnection = null;
		try {
			URL url = new URL(url_img);
			httpConnection = (HttpURLConnection) url.openConnection();
			BufferedInputStream bis = new BufferedInputStream(httpConnection.getInputStream());
			FileOutputStream fos = new FileOutputStream(file_img);

			int len = 0;
			byte[] buffer = new byte[1024 * 10]; // 文件写缓存
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				fos.flush();
			}

			fos.flush();
			fos.close();

		} catch (IOException e) {
			Log.w("test", "I/O error while retrieving bitmap from " + url_img, e);
		} catch (IllegalStateException e) {
			Log.w("test", "Incorrect URL: " + url_img);
		} catch (Exception e) {
			Log.w("test", "Error while retrieving bitmap from " + url_img, e);
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}
		return true;
	}

	/*	public static InputStream getImageStream(String url_img) {

			HttpURLConnection httpConnection = null;
			BufferedInputStream bis = null;
			try {
				URL url = new URL(url_img);
				httpConnection = (HttpURLConnection) url.openConnection();
				bis = new BufferedInputStream(httpConnection.getInputStream());

			} catch (Exception e) {
				Log.w("test", "Error while retrieving bitmap from " + url_img, e);
			} finally {
				if (httpConnection != null) {
					httpConnection.disconnect();
				}
			}
			return bis;
		}
		public static InputStream getLocalImageStream(File cache) {

			FileInputStream is = null;
			try {
				is = new FileInputStream(cache);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return is;
		}
		
		public static File saveImageStream(InputStream bis, File file_img) {

			try {
				FileOutputStream fos = new FileOutputStream(file_img);

				int len = 0;
				byte[] buffer = new byte[1024 * 10]; // 文件写缓存
				while ((len = bis.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
					fos.flush();
				}

				fos.flush();
				fos.close();
			} catch (Exception e) {
				Log.w("test", e.getMessage(), e);
			}
			return file_img;
		}*/

	public static boolean saveStreamToLocal(InputStream stream, File file_img) {

		try {
			BufferedInputStream bis = new BufferedInputStream(stream);
			FileOutputStream fos = new FileOutputStream(file_img);

			int len = 0;
			byte[] buffer = new byte[1024 * 10]; // 文件写缓存
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				fos.flush();
			}

			fos.flush();
			fos.close();

		} catch (IOException e) {
		} catch (IllegalStateException e) {
		} catch (Exception e) {
		} finally {

		}
		return true;
	}

	public static byte[] getBytesFromFile(File file) {
		byte[] ret = null;
		try {
			if (file == null) {
				// log.error("helper:the file is null!");  
				return null;
			}
			FileInputStream in = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
			byte[] b = new byte[4096];
			int n;
			while ((n = in.read(b)) != -1) {
				out.write(b, 0, n);
			}
			in.close();
			out.close();
			ret = out.toByteArray();
		} catch (IOException e) {
			// log.error("helper:get bytes from file process error!");  
			e.printStackTrace();
		}
		return ret;
	}

	public static boolean saveBitmapToLocal(Bitmap bitmap, File file_img, CompressFormat format) {

		// CompressFormat format:
		// Bitmap.CompressFormat.JPEG

		try {
			file_img.createNewFile();
			OutputStream outStream = new FileOutputStream(file_img);
			bitmap.compress(format, 100, outStream);
			outStream.flush();
			outStream.close();

			return true;
		} catch (FileNotFoundException e) {
			Log.w("test", "FileNotFoundException");
		} catch (IOException e) {
			Log.w("test", "IOException" + e.getMessage());
		}

		return false;
	}

	/**
	 * 加载本地图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLocalBitmap(String path) {
		try {
			FileInputStream fis = new FileInputStream(path);
			return BitmapFactory.decodeStream(fis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将字符串写入本地文件
	 * 
	 * @param str
	 *            目标字符串
	 * @param file_path
	 *            文件保存路径
	 * @return
	 */
	public static boolean saveStringToLocal(String str, File file_local) {
		try {
			FileOutputStream fos = new FileOutputStream(file_local);
			Writer out = new OutputStreamWriter(fos);
			out.write(str);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 读取本地文件到字符流
	 */

	public static InputStream getLocalStream(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			return fis;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 读取本地文件到字符串
	 * 
	 * @param url
	 * @return
	 */
	public static String getLocalString(File file) {
		try {
			StringBuffer buffer = new StringBuffer();
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			Reader in = new BufferedReader(isr);

			int i;
			while ((i = in.read()) > -1) {
				buffer.append((char) i);
			}
			in.close();

			return buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
