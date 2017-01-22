package com.haomee.entity;

public class SDcardInfo {

	public String path;
	public long size;
	public long available;
	public boolean android4_4;		// 是否是android4.4之后，外置sdcard只能写入指定位置，卸载会自动删掉相关数据
}
