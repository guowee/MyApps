package com.haomee.player;

import java.io.Serializable;

import android.util.Log;

import com.haomee.util.FileDownloadUtil;

public class M3u8Info implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public String resource_url;		// 源url地址
	public String id;	// 自己id
	//public String vid;	// 外站id
	public int split_num;			// 分片数
	public String[] urls;			// 每个分片地址
	public float[] seconds;			// 每个分片时长
	public long[] split_bytes;		// 每个分片大小
	public float total_seconds;		// 总时长
	public int current_index;	// 当前分片序号
	public long split_offset;	// 当前分片的偏移量
	//public int type;	// m3u8的类型: http / local         (准备删掉)
	public boolean is_current_split_local;	// 当前分片是否是本地
	//public String extension;	// 视频文件后缀
	
	public boolean[] split_cached;		// 分片缓冲完成情况
	
	public float split_num_downloaded;		// 已下载分片数
	public float total_seconds_downloaded;		// 总时长
	
	public String str_m3u8;		// 原始的m3u8文本
	
	// 清晰度选择
	public int current_clear;
	public int[] clears;
	
	public M3u8Info(){
	}
	
	// 只有一个分片（源地址为非m3u8的）
	public M3u8Info(String resource_url, String video_url){
		//this.vid = vid;
		this.resource_url = resource_url;
		this.split_num = 1;
		this.urls = new String[]{video_url};
		this.seconds = new float[1];
		//this.type = type;
		split_cached = new boolean[split_num];
	}

	public M3u8Info(String resource_url, int split_num, String[] urls, float[] seconds) {
		//this.vid = vid;
		this.resource_url = resource_url;
		this.split_num = split_num;
		this.urls = urls;
		this.seconds = seconds;
		//this.type = type;
		
		this.total_seconds = 0;
		for(float second:seconds){
			this.total_seconds += second;
		}
		
		this.current_index = 0;
		
		split_cached = new boolean[split_num];
	}
	
	public String[] getUrls() {
		return urls;
	}
	public float[] getSeconds() {
		return seconds;
	}
	public void setSeconds(float[] seconds) {
		this.seconds = seconds;
	}
	public void setUrls(String[] urls) {
		this.urls = urls;
	}

	// 获取第index分片之前的时长,毫秒。(注意是之前的)
	public int getSplitPosition(int index){		
		return getSplit_seconds(index)*1000;
	}
	
	// 获取第index分片之前的时长（秒）
	public int getSplit_seconds(int index){
		
		if(index<=0){
			return 0;
		}else if(index >= split_num){
			return (int)(total_seconds);
		}
		
		float position = 0;
		for(int i=0;i<index;i++){
			position += seconds[i];
		}
		
		return Math.round(position);
	}
	
	
	/**
	 * 每次获取当前分片的时候进行优化，如果本地有，则从本地取地址
	 * @param isFindAtLocal 是否在本地寻找对应分片
	 * @return
	 */
	public String getCurrentUrl(boolean isFindAtLocal){
		String current_url = urls[current_index];
		
		/*if(isFindAtLocal){
				String localName = current_index + Consts.video_extension;
				String local_url = FileDownloadUtil.getVideoFromLocal(id, localName);
				if(local_url!=null){
					Log.i("test","在线分片："+current_url+",在本地文件找到对应地址："+local_url);
					current_url = local_url;
					is_current_split_local = true;
				}else{
					is_current_split_local = false;
				}
		}		
		*/
		return current_url;
	}
	
	// 当前分片的毫秒时长
	public int getCurrentMilliSecond(){
		return Math.round(seconds[current_index]*1000);
	}
	
	public int getCurrentSplitPosition(){
		return this.getSplitPosition(current_index);
	}
	
	/*
	 * 获取拖动之后切换分片位置
	 * position 单位为毫秒
	 * 返回是否要切换分片
	 */
	public boolean switchSplitIndexByPostion(long position){
		
		int p = (int) (position/1000);
		float temp = 0;
		int index =0;
		for(int i=0;i<split_num;i++){
			
			temp += seconds[i];
			
			if(seconds[i] == 0){	// 只有一个分片的时候，或者没有下载完成的情况
				index = i>0 ? i-1:0;
				this.split_offset = (long) (position - temp);
				break;
			}
			
			
			if(temp >= p){		// 找到当前分片index
				index = i;
				float time_bef = temp-seconds[i];
				this.split_offset = (long) (position-time_bef*1000);
				break;
			}
		}
		
		if(index != current_index){
			current_index = index;
			return true;
		}else{
			return false;
		}
		
	}
	
	// 是否到达最后一个分片
	public boolean isLastSplit(){
		return this.current_index >= this.split_num-1;
	}
	
	
	
}

