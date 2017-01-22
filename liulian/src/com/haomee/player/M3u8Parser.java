package com.haomee.player;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * M3u8格式的获取、分片解析
 * @author Administrator
 *
 */
public class M3u8Parser {
	

	/**
	 * http获取m3u8文本
	 */
	public static String getHtml(String url){
		HttpClient httpclient = new DefaultHttpClient();
		String response = null;
		try{
			HttpGet httpget = new HttpGet(url);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = httpclient.execute(httpget, responseHandler);	
		} catch(Exception e){
			e.printStackTrace();
		} finally {
            httpclient.getConnectionManager().shutdown();
        }
		
		return response;
		
	}
	
	
	// 检查返回的数据是否正确
	public static boolean check(M3u8Info m3u8){
		if(m3u8 == null || m3u8.seconds==null || m3u8.urls==null ||
				m3u8.seconds.length==0 || m3u8.urls.length==0 || 
				m3u8.seconds.length!=m3u8.urls.length){
			return false;
		}
		
		/*for(String url : m3u8.urls){
			if(url==null || url.equals("")){
				return false;
			}
		}*/
		
		return true;
	}
	
	// 更严格的检查，如果返回了空的url，认为失败
	public static boolean check_not_null(M3u8Info m3u8){
		if(m3u8 == null || m3u8.seconds==null || m3u8.urls==null ||
				m3u8.seconds.length==0 || m3u8.urls.length==0 || 
				m3u8.seconds.length!=m3u8.urls.length){
			return false;
		}
		
		for(String url : m3u8.urls){
			if(url==null || url.equals("")){
				return false;
			}
		}
		
		return true;
	}

	/*// 从本地文件生成
	public static M3u8Info getM3u8FromLocal(String path){
		
		M3u8Info m3u8 = new M3u8Info();
		BufferedReader bfr = null;
		try {
			File download_log = new File(path);
			if(!download_log.exists()){
				return null;
			}
			
			bfr = new BufferedReader(new FileReader(download_log));

			// 第一行为头部信息
			String head = bfr.readLine();
			if(head==null || head.equals("")){
				Log.i("test","m3u8文件内容为空："+path);
				return null;
			}
			String[] info = head.split("\t");
			String vid = info[0];
			m3u8.vid = vid;
			m3u8.total_seconds = Float.parseFloat(info[1]);
			m3u8.split_num = Integer.parseInt(info[2]);
			m3u8.type = VideoFrom.local_m3u8;
			m3u8.resource_url = path;
			m3u8.split_cached = new boolean[m3u8.split_num];
			
			String dir_path = FileDownloadUtil.getSavePath(PathConst.video_download_path+"/"+vid);
			
			String[] urls = new String[m3u8.split_num];
			float[] seconds = new float[m3u8.split_num];
			m3u8.split_num_downloaded = 0;
			m3u8.total_seconds_downloaded = 0;
			String line = null;
			while((line=bfr.readLine())!=null){		// 从日志文件中读取下载信息
				String[] split = line.split("\t");
				int index = Integer.parseInt(split[0]);
				float length = Float.parseFloat(split[1]);
				String name = split[2];
				

				
				String path_local = dir_path+"/"+name;
				File file = new File(path_local);
				if(file.exists()){		// 如果存在才放入
					urls[index] = path_local;
					seconds[index] = length;
					m3u8.split_num_downloaded ++;
					m3u8.total_seconds_downloaded +=length;
					m3u8.split_cached[index] = true;
				}
				else if(m3u8.split_num==1){		// 如果是单一的文件，没有下载完成的情况
					String path_cache = dir_path+"/"+Common.PREFIX_CACHE+name;
					File file_cache = new File(path_cache);
					if(file_cache.exists()){		// 如果存在才放入
						urls[index] = path_cache;
						seconds[index] = length;
						m3u8.split_num_downloaded ++;
						m3u8.total_seconds_downloaded +=length;
						m3u8.split_cached[index] = true;
					}
				}
				
				
			}
			
			m3u8.setUrls(urls);
			m3u8.setSeconds(seconds);
			
			if(m3u8.split_num>0){
				m3u8.extension = StringUtil.getFileExtension(urls[0]);
				Log.i("test","视频文件后缀名："+m3u8.extension);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			
			try {
				if(bfr!=null){
					bfr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return m3u8;
	}
	*/
	/*// 从本地文件判断和获取分片
	public static String getSplitFromLocal(String vid, String fileName){

		String dir_path = FileDownloadUtil.getSavePath(PathConst.video_download_path+"/"+vid);
		String path_local = dir_path+"/"+fileName;
		File file = new File(path_local);
		if(file.exists()){		// 如果存在才放入
			return path_local;
		}else{
			return null;
		}
	}
	*/
	
	
}
