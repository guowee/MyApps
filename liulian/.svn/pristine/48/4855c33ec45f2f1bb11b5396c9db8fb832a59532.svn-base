package com.haomee.entity;

import java.io.Serializable;

public class Music implements Serializable {

	private String id;
	private String cover;
	private String title;
	private String author;
	private String album;
	private String url;
	private String playtime;
	
	@Override
	public boolean equals(Object o) {
		if(o!=null && o instanceof Music){
			return ((Music)o).getId().equals(this.id);
		}else{
			return false;
		}		
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPlaytime() {
		return playtime;
	}

	public void setPlaytime(String playtime) {
		this.playtime = playtime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

}
