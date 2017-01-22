package com.haomee.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;

import com.haomee.entity.SDcardInfo;

/**
 * 存储工具类
 */
public class StorageUtil {

	/**
	 * 获取外部存储的根目录
	 */
	/*public static String getRootPath(){
		if(isExternalStorageAvailable()){
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}else{
			return null;
		}
		
	}*/

	/**
	 * 判断外部存储是否可用
	 * 
	 * @return
	 */
	public static boolean isExternalStorageAvailable() {
		return android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
	}

	/**
	 * SD卡是否有足够空间
	 */
	public static boolean isEnoughSDSpace(long trackSize) {
		boolean flag = false;
		StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
		if (((long) sf.getAvailableBlocks() * sf.getBlockSize()) < trackSize) {
			flag = false;
		} else {
			flag = true;
		}
		return flag;
	}

	/**
	 * 获取内存存储可用空间
	 * 
	 * @return long
	 */
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory(); // 获取数据目录
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	/**
	 * 获取内存存储总空间
	 * 
	 * @return long
	 */
	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	/**
	 * 获取外部可用空间大小
	 * 
	 * @return
	 */
	public static long getAvailableExternalMemorySize() {
		if (isExternalStorageAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return availableBlocks * blockSize;
		} else {
			return -1;
		}
	}

	/**
	 * 获取外部总共空间大小
	 * 
	 * @return
	 */
	public static long getTotalExternalMemorySize() {
		if (isExternalStorageAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return totalBlocks * blockSize;
		} else {
			return -1;
		}
	}

	public static long getFileSize(String path) {
		if (path == null || !new File(path).exists()) {
			return 0;
		}

		StatFs stat = new StatFs(path);
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	public static long getFileAvailable(String path) {
		if (path == null || !new File(path).exists()) {
			return 0;
		}

		StatFs stat = new StatFs(path);
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	// 将大小转为字符串GB，MB
	public static String getSizeString(long size) {
		double size_mb = size * 1.0 / (1024 * 1024); // 转为MB

		if (size_mb > 1024) {
			double size_gb = size_mb / 1024;
			return String.format("%.2f", size_gb) + "GB";
		} else {
			return (long) size_mb + "MB";
		}

	}

	@SuppressLint("NewApi")
	public static ArrayList<SDcardInfo> listAllSDcard(Context context) {

		ArrayList<SDcardInfo> list_sdcard = new ArrayList<SDcardInfo>();

		try {
			StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

			Class<?>[] paramClasses = {};
			Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
			getVolumePathsMethod.setAccessible(true);
			Object[] params = {};
			Object invoke = getVolumePathsMethod.invoke(storageManager, params);

			if (invoke != null) {
				for (String path : (String[]) invoke) {
					File sd_root = new File(path);
					if (sd_root.exists() && sd_root.isDirectory()) {
						long size = getFileSize(path);
						long available = getFileAvailable(path);

						if (size > 0) {

							String root_path = null;

							// 测试是否可以写文件
							String test_tmp = "test_dongman.tmp";
							File file_test = null;
							boolean android4_4 = false;
							try {
								file_test = new File(path + "/" + test_tmp);
								file_test.createNewFile();
								file_test.delete(); // 测试，不抛出异常表示成功

								root_path = path;
							} catch (IOException e) {
								Log.e("test", "第1次创建文件失败 " + path + e.getMessage());

								// 如果外置Sdcard没有根目录写入权限，尝试写入/Android/data/应用包名/files
								//String app_data_path = Environment.getExternalStoragePublicDirectory(null).getAbsolutePath();
								//String app_data_path = context.getExternalFilesDir(null).getAbsolutePath();
								//String app_data_path = path+"/Android/data/"+context.getPackageName()+"/";

								String app_data_path = null;

								File[] sddirs = context.getExternalFilesDirs(null); // Android4.4以后才提供的
								for (File s : sddirs) {
									if (s.getAbsolutePath().startsWith(path)) {
										app_data_path = s.getAbsolutePath();
										break;
									}
								}

								if (app_data_path != null) {
									try {

										File dir_root = new File(app_data_path);
										if (!dir_root.exists()) {
											dir_root.mkdirs();
										}

										file_test = new File(app_data_path + "/" + test_tmp);
										file_test.createNewFile();
										file_test.delete();

										root_path = app_data_path;
										android4_4 = true;

										Log.e("test", "第2次创建文件成功" + app_data_path);
									} catch (IOException e2) {
										Log.e("test", "第2次创建文件失败 " + app_data_path + " " + e2.getMessage());
									}
								}

							}

							if (root_path != null) {
								SDcardInfo sd = new SDcardInfo();
								sd.path = root_path;
								sd.size = size;
								sd.available = available;
								sd.android4_4 = android4_4;
								list_sdcard.add(sd);
							}

						}

					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// 旧版本android上面的方法有问题,只能取默认的了
		if (list_sdcard.size() == 0) {

			String path = FileDownloadUtil.getSDcardRoot_default();
			if (path != null) {
				File sd_root = new File(path);
				//Log.i("test",path);
				if (sd_root.exists() && sd_root.isDirectory()) {
					long size = getFileSize(path);
					long available = getFileAvailable(path);

					if (size > 0) {
						SDcardInfo sd = new SDcardInfo();
						sd.path = path;
						sd.size = size;
						sd.available = available;
						list_sdcard.add(sd);
					}
				}
			}
		}

		return list_sdcard;
	}

}
