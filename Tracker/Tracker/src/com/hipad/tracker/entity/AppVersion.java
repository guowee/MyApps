package com.hipad.tracker.entity;

public class AppVersion {
    private String appname;
	
	private String apkname;
	
	private String verName;
	
	private int verCode;

	
	public AppVersion() {
		// TODO Auto-generated constructor stub
	}
	public AppVersion(String appname, String apkname, String verName,
			int verCode) {
		super();
		this.appname = appname;
		this.apkname = apkname;
		this.verName = verName;
		this.verCode = verCode;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public String getApkname() {
		return apkname;
	}

	public void setApkname(String apkname) {
		this.apkname = apkname;
	}

	public String getVerName() {
		return verName;
	}

	public void setVerName(String verName) {
		this.verName = verName;
	}

	public int getVerCode() {
		return verCode;
	}

	public void setVerCode(int verCode) {
		this.verCode = verCode;
	}
	
	
	
	
	
	
}
