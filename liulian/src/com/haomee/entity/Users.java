package com.haomee.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Users implements Serializable {
	private static final long serialVersionUID = 1L;

	private String uid;
	private String name;
	private String image; // 头像
	private String back_pic;	// 背景大图
	private String phone;
	private String qq_id;
	private String sina_id;
	private int sex; // m,w	
	private String sign; // 签名
	private String birthday;
	private String accesskey;
	private boolean show_like; // 是否显示收藏
	private boolean attentioned; // 是否被当前用户关注了

	private String hx_username;
	private String hx_password;
	private int is_new;
	private boolean is_sayhi;

	private boolean is_can_talk;
	//话题数
	private int topic_num;
	//内容数
	private int content_num;
	private int fans;
	private int comment_num;
	private boolean show_comment; // 是否显示评论

	private String reg_time;
	private String city;

	private int distance;	// 距离

	public String head_pic_small;
	private String head_pic_big;
	
	private String age;
	private String star;
	//签名
	private String signature;
	private boolean is_online;
	private String percent; //今日合拍度
	
	private ArrayList<String> photos;
	private ArrayList<String> photos_small;
	private Label[] label;
	private ArrayList<Topic> list_topic;
	private Background_Img background_img;
	private double location_x;
	private double location_y;
	private Content content;		// 最近发的一条
	
	private String time;
	private String distance_str;
	
	private boolean focused;

	private String user_level;
	private String speekContent;//话题页发送的内容
	
	private String test_percent;//历史重合度
	
	
	
	public String getTest_percent() {
		return test_percent;
	}
	public void setTest_percent(String test_percent) {
		this.test_percent = test_percent;
	}

	private String user_level_icon;
	public String getSpeekContent(){
		return speekContent;
	}
	public void setSpeekContent(String speekContent){
		this.speekContent=speekContent;
	}
	public String getUser_level_icon() {
		return user_level_icon;
	}

	public void setUser_level_icon(String user_level_icon) {
		this.user_level_icon = user_level_icon;
	}

	
	public String getUser_level() {
		return user_level;
	}

	public void setUser_level(String user_level) {
		this.user_level = user_level;
	}

	public boolean isIs_can_talk() {
		return is_can_talk;
	}

	public void setIs_can_talk(boolean is_can_talk) {
		this.is_can_talk = is_can_talk;
	}

	public String getDistance_str() {
		return distance_str;
	}

	public void setDistance_str(String distance_) {
		this.distance_str = distance_;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public ArrayList<Topic> getList_topic() {
		return list_topic;
	}

	public void setList_topic(ArrayList<Topic> list_topic) {
		this.list_topic = list_topic;
	}


	public Background_Img getBackground_img() {
		return background_img;
	}

	public void setBackground_img(Background_Img background_img) {
		this.background_img = background_img;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public boolean isIs_online() {
		return is_online;
	}

	public void setIs_online(boolean is_online) {
		this.is_online = is_online;
	}


	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	public ArrayList<String> getPhotos() {
		return photos;
	}

	public void setPhotos(ArrayList<String> photos) {
		this.photos = photos;
	}

	public ArrayList<String> getPhotos_small() {
		return photos_small;
	}

	public void setPhotos_small(ArrayList<String> photos_small) {
		this.photos_small = photos_small;
	}

	public Label[] getLabel() {
		return label;
	}

	public void setLabel(Label[] label) {
		this.label = label;
	}

	public String getHead_pic_small() {
		return head_pic_small;
	}

	public void setHead_pic_small(String head_pic_small) {
		this.head_pic_small = head_pic_small;
	}

	public boolean isIs_sayhi() {
		return is_sayhi;
	}

	public void setIs_sayhi(boolean is_sayhi) {
		this.is_sayhi = is_sayhi;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getTopic_num() {
		return topic_num;
	}

	public void setTopic_num(int topic_num) {
		this.topic_num = topic_num;
	}

	public int getContent_num() {
		return content_num;
	}

	public void setContent_num(int content_num) {
		this.content_num = content_num;
	}

	public String getReg_time() {
		return reg_time;
	}

	public void setReg_time(String reg_time) {
		this.reg_time = reg_time;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getHx_username() {
		return hx_username;
	}

	public void setHx_username(String hx_username) {
		this.hx_username = hx_username;
	}

	public String getHx_password() {
		return hx_password;
	}

	public void setHx_password(String hx_password) {
		this.hx_password = hx_password;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getQq_id() {
		return qq_id;
	}

	public void setQq_id(String qq_id) {
		this.qq_id = qq_id;
	}

	public String getSina_id() {
		return sina_id;
	}

	public void setSina_id(String sina_id) {
		this.sina_id = sina_id;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getAccesskey() {
		return accesskey;
	}

	public void setAccesskey(String accesskey) {
		this.accesskey = accesskey;
	}

	public boolean isShow_like() {
		return show_like;
	}

	public void setShow_like(boolean show_like) {
		this.show_like = show_like;
	}

	public boolean isAttentioned() {
		return attentioned;
	}

	public void setAttentioned(boolean attentioned) {
		this.attentioned = attentioned;
	}

	public int getIs_new() {
		return is_new;
	}

	public void setIs_new(int is_new) {
		this.is_new = is_new;
	}

	public int getFans() {
		return fans;
	}

	public void setFans(int fans) {
		this.fans = fans;
	}

	public int getComment_num() {
		return comment_num;
	}

	public void setComment_num(int comment_num) {
		this.comment_num = comment_num;
	}

	public boolean getShow_comment() {
		return show_comment;
	}

	public void setShow_comment(boolean show_comment) {
		this.show_comment = show_comment;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
	public double getLocation_x() {
		return location_x;
	}

	public void setLocation_x(double location_x) {
		this.location_x = location_x;
	}

	public double getLocation_y() {
		return location_y;
	}

	public void setLocation_y(double location_y) {
		this.location_y = location_y;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public String getBack_pic() {
		return back_pic;
	}

	public void setBack_pic(String back_pic) {
		this.back_pic = back_pic;
	}

	public String getHead_pic_big() {
		return head_pic_big;
	}

	public void setHead_pic_big(String head_pic_big) {
		this.head_pic_big = head_pic_big;
	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}
}
