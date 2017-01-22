package com.example.helper;



import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpClientHelper {

	public static byte[] loadByteFromURL(String url) {
		
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpGet httpGet = new HttpGet(url);
		
		HttpResponse response;
		
		try {
			
			response = httpClient.execute(httpGet);
			
			if (response.getStatusLine().getStatusCode() == 200) {
				
				HttpEntity httpEntity = response.getEntity();
				
				return EntityUtils.toByteArray(httpEntity);
				
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		return null;
	}

	public static String loadTextFromURL(String url, String charset) {

		HttpClient httpClient = new DefaultHttpClient();

		HttpGet httpGet = new HttpGet(url);

		try {

			HttpResponse response = httpClient.execute(httpGet);

			if (response.getStatusLine().getStatusCode() == 200) {

				HttpEntity entity = response.getEntity();

				return EntityUtils.toString(entity, charset);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}
}