package com.hipad.tracker.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**
 * 
 * @author guowei
 *
 */
public class HttpPostWraper extends HttpPost{
	
	private final List<NameValuePair> mParams;

	/**
	 * construct one http post.
	 * 
	 * @param url
	 *            the http post url
	 */
	public HttpPostWraper(String url) {
		super(url);
		mParams = new ArrayList<NameValuePair>(8);
	}

	/**
	 * add the name-value pair to the http post. after all the name-value pairs
	 * is added, the {@link HttpPostWraper#commitParams()} should be called.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	public void addParam(String name, String value) {
		mParams.add(new BasicNameValuePair(name, value));
	}

	/**
	 * finish the process to put the params to the http post.
	 */
	public void commitParams() {
		try {
			setEntity(new UrlEncodedFormEntity(mParams, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	

}
