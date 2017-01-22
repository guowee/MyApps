package com.haomee.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Content implements Serializable {

	private String id;
	private String create_time;
	private String content;
	private int type;
	private String praise_num;
	private Music music;
	private Users user;
	private List<Users> praise_users;
	private Movie movie;
	private String view_num;
	private ContentPicture picture;
	private boolean is_praised;
	private String text_content;
	private ArrayList<Users> list_view;
	private ArrayList<Users> list_praise;

	private String topic;
	

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public ArrayList<Users> getList_view() {
		return list_view;
	}

	public void setList_view(ArrayList<Users> list_view) {
		this.list_view = list_view;
	}

	public ArrayList<Users> getList_praise() {
		return list_praise;
	}

	public void setList_praise(ArrayList<Users> list_praise) {
		this.list_praise = list_praise;
	}

	public String getText_content() {
		return text_content;
	}

	public void setText_content(String text_content) {
		this.text_content = text_content;
	}

	public boolean isIs_praised() {
		return is_praised;
	}

	public void setIs_praised(boolean is_praised) {
		this.is_praised = is_praised;
	}

	public ContentPicture getPicture() {
		return picture;
	}

	public void setPicture(ContentPicture picture) {
		this.picture = picture;
	}

	public String getView_num() {
		return view_num;
	}

	public void setView_num(String view_num) {
		this.view_num = view_num;
	}

	public String getPraise_num() {
		return praise_num;
	}

	public void setPraise_num(String praise_num) {
		this.praise_num = praise_num;
	}

	public List<Users> getPraise_users() {
		return praise_users;
	}

	public void setPraise_users(List<Users> praise_users) {
		this.praise_users = praise_users;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Music getMusic() {
		return music;
	}

	public void setMusic(Music music) {
		this.music = music;
	}

	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

}
