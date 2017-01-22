package com.hipad.tracker.entity;

import java.io.Serializable;

public class BabyInfo implements Serializable{
	
	private int icon;
	private String name;
	private String gender;
	private String birthday;
	private String height;
	private String weight;
	
	public BabyInfo() {
	}

	public BabyInfo(int icon, String name, String gender, String birthday,
			String height, String weight) {
		super();
		this.icon = icon;
		this.name = name;
		this.gender = gender;
		this.birthday = birthday;
		this.height = height;
		this.weight = weight;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}
	
	
	

}
