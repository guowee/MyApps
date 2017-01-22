package com.haomee.entity;

import java.io.Serializable;

/**
 * 分享内容
 */
public class ShareContent implements Serializable{
	private static final long serialVersionUID = 1L;

/*	public static final int TYPE_LOCAL_DESK = 1;
	public static final int TYPE_LOCAL_DOWNLOAD = 2;
	public static final int TYPE_LOCAL_INVISIBLE = 3;*/

	private String id;		// 对象的id 
	private String title;		// 分享标题
	private String summary;		// 分享内容简介
	private String img_url;		// 图片
	private String img_thumb_url;	// 缩略图
	private String redirect_url;	// 分享内容的点击链接
	//private String web_url;		// 分享页面时的地址，（用于微信，apk什么的不行）
	private int scope=-1;		// 控制分享到哪些平台
	
	private String desc;
	
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getImg_url() {
		return img_url;
	}
	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}
	public String getImg_thumb_url() {
		return img_thumb_url;
	}
	public void setImg_thumb_url(String img_thumb_url) {
		this.img_thumb_url = img_thumb_url;
	}
	public String getRedirect_url() {
		return redirect_url;
	}
	public void setRedirect_url(String redirect_url) {
		this.redirect_url = redirect_url;
	}
	public int getScope() {
		return scope;
	}
	public void setScope(int scope) {
		this.scope = scope;
	}

	
	
	
}
