/**
 * GsonInstance.java 2014-11-20
 */
package com.hipad.smart.json.util;

import com.google.gson.Gson;

/**
 * hold the {@link Gson} instance.
 * @author wangbaoming
 *
 */
public class GsonInstance {
	private final static Gson sGson = new Gson();
	
	public static Gson get(){
		return sGson;
	}
}
