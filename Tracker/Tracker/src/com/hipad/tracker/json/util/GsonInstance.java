package com.hipad.tracker.json.util;

import com.google.gson.Gson;
/**
 * 
 * @author guowei
 *
 */
public class GsonInstance {
	
	private final static Gson sGson = new Gson();

	public static Gson get() {
		return sGson;
	}

}
