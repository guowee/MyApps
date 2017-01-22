/**
 * HttpUtil.java 2014-11-12
 */
package com.hipad.smart.http;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hipad.smart.json.util.GsonInstance;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * http helper.
 * @author wangbaoming
 *
 */
public final class HttpUtil {
	private static final String TAG = "HttpUtil";
	
	private static final Handler sHandler = new Handler(Looper.getMainLooper());

//	private final static HttpClient sHttpClient = new DefaultHttpClient();
	private final static HttpClient sHttpClient;
	static {
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setTimeout(params, 1000 * 10);
		ConnManagerParams.setMaxTotalConnections(params, 8);
		
		HttpConnectionParams.setConnectionTimeout(params, 1000 * 10);
		HttpConnectionParams.setSoTimeout(params, 1000 * 10);
		
		Scheme https = new Scheme("https", SSLSocketFactory.getSocketFactory(), 443);
		Scheme http = new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(https);
		schemeRegistry.register(http);
		
		ClientConnectionManager clientConnectionManager = new ThreadSafeClientConnManager(params, schemeRegistry); 
		
		sHttpClient = new DefaultHttpClient(clientConnectionManager, params);
//        HttpConnectionParams.setConnectionTimeout(sHttpClient.getParams(), 1000 * 10);
//        HttpConnectionParams.setSoTimeout(sHttpClient.getParams(), 1000 * 10);
	}
	
	/**
	 * execute the http post, the result will be returned in json obj.
	 * @param post
	 * 		the http post
	 * @return
	 * 		the request result in json.
	 */
	public static JSONObject post(HttpPost post){
		try {
			HttpResponse response = sHttpClient.execute(post);
			if(HttpStatus.SC_OK == response.getStatusLine().getStatusCode()){
				HttpEntity entity = response.getEntity();
				if(null != entity){
					try {
						JSONObject jobj = new JSONObject(EntityUtils.toString(entity));
						return jobj;
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	/**
	 * post a http request. the result will be return in T format in {@link ResponseResultHandler} <b>asynchronously</b>.
	 * @param post
	 * 		http post
	 * @param handler
	 * 		the result handler, the T represents the return result in {@link ResponseResultHandler}.<br>
	 * 		As the weak reference is used to avoid the object leak, we should use the handler as a field rather than a local variable.
	 * @param classOfT
	 * 		the class of T, the result will return in T format in {@link ResponseResultHandler}
	 */
	public static <T> void postAvoidLeak(final HttpPost post, ResponseResultHandler<T> handler, final Class<T> classOfT){
		// TODO we should use the weak reference of the handler to avoid the handler leak.
		final WeakReference<ResponseResultHandler<T>> wrhandler = new WeakReference<HttpUtil.ResponseResultHandler<T>>(handler);
		
		new Thread(){
			@Override
			public void run() {
				
				try {
					HttpResponse response = sHttpClient.execute(post, new ResponseHandler<HttpResponse>() {

						@Override
						public HttpResponse handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
							if(HttpStatus.SC_OK == response.getStatusLine().getStatusCode()){
								String jstr = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
								T obj = null;
								if(JSONObject.class.equals(classOfT)){
									try {
										obj = (T) new JSONObject(jstr);
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}else{
									try {
										obj = GsonInstance.get().fromJson(jstr, classOfT);
									} catch (JsonSyntaxException e) {
										e.printStackTrace();
									}	
								}
								
								handle(wrhandler, false, obj);
								
								System.out.println("Respnse: " + jstr);
							}else{
								post.abort();
								handle(wrhandler, false, null);
								System.out.println("Error Response: " + response.getStatusLine().toString());
							}
							return response;
						}
					});			

//					Log.e(TAG, "response: " + EntityUtils.toString(response.getEntity()));
				} catch (ConnectTimeoutException e) {
					handle(wrhandler, true, null);
					System.out.println("ConnectTimeoutException");
					e.printStackTrace();
				}catch (ClientProtocolException e) {
					handle(wrhandler, false, null);
					System.out.println("ClientProtocolException");
					e.printStackTrace();
				} catch (SocketTimeoutException e) {
					handle(wrhandler, true, null);
					System.out.println("SocketTimeoutException");
					e.printStackTrace();
				}catch (IOException e) {
					handle(wrhandler, false, null);
					System.out.println("IOException");
					e.printStackTrace();
				}
			};			
			
		}.start();		
	}
	
	public static <T> void post(final HttpPost post, final ResponseResultHandler<T> handler, final Class<T> classOfT){
		
		new Thread(){
			@Override
			public void run() {
				
				try {
					HttpResponse response = sHttpClient.execute(post, new ResponseHandler<HttpResponse>() {

						@Override
						public HttpResponse handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
							if(HttpStatus.SC_OK == response.getStatusLine().getStatusCode()){
								String jstr = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
								T obj = null;
								if(JSONObject.class.equals(classOfT)){
									try {
										obj = (T) new JSONObject(jstr);
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}else{
									try {
										obj = GsonInstance.get().fromJson(jstr, classOfT);
									} catch (JsonSyntaxException e) {
										e.printStackTrace();
									}	
								}
								
								handle(handler, false, obj);
								
								System.out.println("Respnse: " + jstr);
							}else{
								post.abort();
								handle(handler, false, null);
								System.out.println("Error Response: " + response.getStatusLine().toString());
							}
							return response;
						}
					});			

//					Log.e(TAG, "response: " + EntityUtils.toString(response.getEntity()));
				} catch (ConnectTimeoutException e) {
					handle(handler, true, null);
					System.out.println("ConnectTimeoutException");
					e.printStackTrace();
				}catch (ClientProtocolException e) {
					handle(handler, false, null);
					System.out.println("ClientProtocolException");
					e.printStackTrace();
				} catch (SocketTimeoutException e) {
					handle(handler, true, null);
					System.out.println("SocketTimeoutException");
					e.printStackTrace();
				}catch (IOException e) {
					handle(handler, false, null);
					System.out.println("IOException");
					e.printStackTrace();
				}
			};			
			
		}.start();		
	}
	
	private static <T> void handle(final ResponseResultHandler<T> handler, final boolean timeout, final T obj){
		System.out.println("handler: " + handler);
		if(null != handler){
			sHandler.post(new Runnable() {
				
				@Override
				public void run() {
					handler.handle(timeout, obj);						
				}
			});
		}
	}
	
	private static <T> void handle(WeakReference<ResponseResultHandler<T>> weakReference, final boolean timeout, final T obj){
		if(null != weakReference){
			final ResponseResultHandler<T> handler = weakReference.get();
			System.out.println("handler: " + handler);
			if(null != handler){
				sHandler.post(new Runnable() {
					
					@Override
					public void run() {
						handler.handle(timeout, obj);						
					}
				});
			}
		}
	}
	
	/**
	 * helper used to handle the http response.
	 * @author wangbaoming
	 *
	 */
	public static interface ResponseResultHandler<T>{
		/**
		 * handle the http response.
		 * @param timeout
		 * 		if true, the http request is timeout; false otherwise.
		 * @param obj
		 * 		if the http request is successful, obj represents the response result; null otherwise.
		 */
		public void handle(boolean timeout, T obj);
	}
}
