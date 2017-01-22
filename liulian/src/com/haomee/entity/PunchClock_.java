package com.haomee.entity;

public class PunchClock_ {

	private String id;
	private String create_time;
	private String text_content;
	private String type;
	private Users user;
	private boolean say_hi;

	public boolean isSay_hi() {
		return say_hi;
	}

	public void setSay_hi(boolean say_hi) {
		this.say_hi = say_hi;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getText_content() {
		return text_content;
	}

	public void setText_content(String text_content) {
		this.text_content = text_content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

}
