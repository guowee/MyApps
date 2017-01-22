package com.wosai.proname.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;

public class GodUtils {
	/**
	 * @param list
	 * @return
	 */
	public static boolean CheckListNull(List<?> list) {
		if (list == null || list.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean CheckMapNull(Map<?, ?> map) {
		if (map == null || map.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean CheckStringNull(String str) {
		if (str == null || "".equals(str.trim())) {
			return true;
		}
		return false;
	}

	public static boolean CheckIntegerNull(Integer it) {
		if (it == null || it == 0) {
			return true;
		}
		return false;
	}

	/**
	 * json转string，转码
	 * 
	 * @param obj
	 * @return
	 */
	public static String JsonToString(Object obj) {
		String json = "";
		if (obj instanceof String) {
			json = Encoder(obj.toString(), "gb2312");
		} else if (obj instanceof JSONArray) {
			json = Encoder(obj.toString(), "gb2312");
		}
		return json;
	}

	public static String Encoder(String str, String Code) {
		String result = "";
		try {
			result = URLEncoder.encode(str, "gb2312");
		} catch (UnsupportedEncodingException e) {
			System.out.println("转码异常");
			e.printStackTrace();
		}
		return result;
	}

	public String getRootPath() {
		String rootPath = getClass().getResource("/").toString();
		System.out.println("======================");
		System.out.println("rootPath =" + rootPath);
		System.out.println("======================");

		rootPath = rootPath.split("WEB-INF")[0];
		rootPath = rootPath.substring(6);

		System.out.println("======================");
		System.out.println("rootPath =" + rootPath);
		System.out.println("======================");
		return rootPath;
	}
}
