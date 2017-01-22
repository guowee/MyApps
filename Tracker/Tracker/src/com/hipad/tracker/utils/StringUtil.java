package com.hipad.tracker.utils;

public class StringUtil {
	/**
	 * test if the string is null or empty.
	 * @param string
	 * 		the string to be test
	 * @return
	 * 		true if the string is null or empty, false otherwise
	 */
	public static boolean isNullOrEmpty(String string){
		return null == string || string.length() <= 0;
	}
}
