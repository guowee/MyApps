package com.haomee.entity;

import java.io.Serializable;
import java.util.ArrayList;

//话题
public class Topic implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String title;
	private String icon;
	private String desc;
	private String create_time;
	private String input_type;
	private String content_table_id;
	private String content_num;
	private String view_range;
	private String location_x;
	private String location_y;
	private String content;
	private int user_num;
	private int view_user_num;

	private String pic;
	private int pic_width;
	private int pic_height;

	private String update_time;
	
	private boolean fav;	// 是否收藏
	
	private String back_img;
	
	private Users create_user;
	private ArrayList<Users> list_user;
	
	private boolean is_recTopic;
	
	private boolean is_rangeTopic;
	
	private String category_icon;
	
	private int goto_type;	// 跳转类型
	private String goto_url;
	
	//时间
	private String left_time;
	
	
	private boolean my;		// 是否自己的
	

	public String getLeft_time() {
		return left_time;
	}

	public void setLeft_time(String left_time) {
		this.left_time = left_time;
	}

	public boolean isIs_recTopic() {
		return is_recTopic;
	}

	public void setIs_recTopic(boolean is_recTopic) {
		this.is_recTopic = is_recTopic;
	}

	public boolean isIs_rangeTopic() {
		return is_rangeTopic;
	}

	public void setIs_rangeTopic(boolean is_rangeTopic) {
		this.is_rangeTopic = is_rangeTopic;
	}

	public ArrayList<Users> getList_user() {
		return list_user;
	}

	public void setList_user(ArrayList<Users> list_user) {
		this.list_user = list_user;
	}

	public int getView_user_num() {
		return view_user_num;
	}

	public void setView_user_num(int view_user_num) {
		this.view_user_num = view_user_num;
	}

	public String getBack_img() {
		return back_img;
	}

	public void setBack_img(String back_img) {
		this.back_img = back_img;
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getInput_type() {
		return input_type;
	}

	public void setInput_type(String input_type) {
		this.input_type = input_type;
	}

	public String getContent_table_id() {
		return content_table_id;
	}

	public void setContent_table_id(String content_table_id) {
		this.content_table_id = content_table_id;
	}

	public String getContent_num() {
		return content_num;
	}

	public void setContent_num(String content_num) {
		this.content_num = content_num;
	}

	public int getUser_num() {
		return user_num;
	}

	public void setUser_num(int user_num) {
		this.user_num = user_num;
	}

	public String getView_range() {
		return view_range;
	}

	public void setView_range(String view_range) {
		this.view_range = view_range;
	}

	public String getLocation_x() {
		return location_x;
	}

	public void setLocation_x(String location_x) {
		this.location_x = location_x;
	}

	public String getLocation_y() {
		return location_y;
	}

	public void setLocation_y(String location_y) {
		this.location_y = location_y;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}


	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public int getPic_width() {
		return pic_width;
	}

	public void setPic_width(int pic_width) {
		this.pic_width = pic_width;
	}

	public int getPic_height() {
		return pic_height;
	}

	public void setPic_height(int pic_height) {
		this.pic_height = pic_height;
	}

	public boolean isFav() {
		return fav;
	}

	public void setFav(boolean fav) {
		this.fav = fav;
	}

	public String getCategory_icon() {
		return category_icon;
	}

	public void setCategory_icon(String category_icon) {
		this.category_icon = category_icon;
	}

	public Users getCreate_user() {
		return create_user;
	}

	public void setCreate_user(Users create_user) {
		this.create_user = create_user;
	}

	public int getGoto_type() {
		return goto_type;
	}

	public void setGoto_type(int goto_type) {
		this.goto_type = goto_type;
	}

	public String getGoto_url() {
		return goto_url;
	}

	public void setGoto_url(String goto_url) {
		this.goto_url = goto_url;
	}

	public boolean isMy() {
		return my;
	}

	public void setMy(boolean my) {
		this.my = my;
	}


	


}
