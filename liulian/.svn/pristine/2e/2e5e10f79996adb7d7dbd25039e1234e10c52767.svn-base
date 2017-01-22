package com.haomee.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理字符串格式
 * 
 */
public class StringUtil {

	/**
	 * 判断字符串是否是数字
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 获取数字的缩略，多少万
	 */
	public static String getShortNum(int num) {
		if (num > 10000) {
			return String.format("%.1f", num * 1.0 / 10000) + "万";
		} else {
			return "" + num;
		}
	}

	/**
	 * 将当前日期字符串
	 * @return 字符串，如：2014-09-22 
	 */
	public static String getDateFormat(Date date) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		String str = f.format(date);
		return str;
	}

	/**
	 * 将时间转化为字符串
	 * @param time 毫秒时间
	 * @param isShoart 当小时为0时是否不显示时
	 * @return 字符串，如：09:22:34 
	 */
	public static String getTimeFormat(long time, boolean isShort) {
		long seconds = time / 1000;
		long m = seconds / 60;
		long h = m / 60;

		if (isShort && h == 0) {
			if (m < 0) {
				seconds = -seconds;
			}
			return MessageFormat.format("{0,number,00}:{1,number,00}", m % 60, seconds % 60);
		} else {
			return MessageFormat.format("{0,number,00}:{1,number,00}:{2,number,00}", h, m % 60, seconds % 60);
		}

	}

	/**
	 * 获取拖动的时间格式
	 * @param time 毫秒时间
	 * @return 字符串，如：00:34 
	 */
	public static String getTimeFormat_scrolled(long progress_scrolled) {
		String txt_offset = getTimeFormat(progress_scrolled, true);
		String c = "";
		if (progress_scrolled > 0) {
			c = "+";
		}
		return "[" + c + txt_offset + "]";
	}

	/**
	 * 从Url地址中截取文件地址
	 */
	public static String getFileNameFromUrl(String url) {
		int index = url.lastIndexOf('/');
		if (index == -1) {
			url.lastIndexOf('\\');
		}
		if (index == -1) {
			return null;
		}

		return url.substring(index + 1);
	}

	/**
	 * 获取文件后缀名
	 */
	public static String getFileExtension(String url) {
		if (url == null) {
			return null;
		}

		int index = url.lastIndexOf('.');
		if (index == -1) {
			return null;
		}

		return url.substring(index + 1);
	}

	/** 
	 * MD5 加密 
	 */
	//	public static String getMD5Str(String str) {
	//		MessageDigest messageDigest = null;
	//		try {
	//			messageDigest = MessageDigest.getInstance("MD5");
	//
	//			messageDigest.reset();
	//
	//			messageDigest.update(str.getBytes("UTF-8"));
	//		} catch (NoSuchAlgorithmException e) {
	//		} catch (UnsupportedEncodingException e) {
	//			e.printStackTrace();
	//		}
	//
	//		byte[] byteArray = messageDigest.digest();
	//		StringBuffer md5StrBuff = new StringBuffer();
	//		for (int i = 0; i < byteArray.length; i++) {
	//			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
	//				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
	//			else
	//				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
	//		}
	//		return md5StrBuff.toString();
	//	}

	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	public static String getMD5Str(String s) {
		try {
			// Create MD5 Hash  
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return "";
	}

}
