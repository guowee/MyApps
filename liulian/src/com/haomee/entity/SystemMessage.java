package com.haomee.entity;

public class SystemMessage {

	private String s_id;
	private String s_meg_type;
	private String s_cont;
	private String pic;
	private String url;
	private String create_time;
	private String icon;
	private Users users;
	private Topic topic;
	private Content content;
	private String feed_str;

	private String h5_url;

	public String getH5_url() {
		return h5_url;
	}

	public void setH5_url(String h5_url) {
		this.h5_url = h5_url;
	}

	public String getFeed_str() {
		return feed_str;
	}

	public void setFeed_str(String feed_str) {
		this.feed_str = feed_str;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public String getS_id() {
		return s_id;
	}

	public void setS_id(String s_id) {
		this.s_id = s_id;
	}

	public String getS_meg_type() {
		return s_meg_type;
	}

	public void setS_meg_type(String s_meg_type) {
		this.s_meg_type = s_meg_type;
	}

	public String getS_cont() {
		return s_cont;
	}

	public void setS_cont(String s_cont) {
		this.s_cont = s_cont;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
