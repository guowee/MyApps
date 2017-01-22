/**
 * Device.java 2014-11-18
 */
package com.hipad.smart.device;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hipad.smart.http.HttpUtil.ResponseResultHandler;
import com.hipad.smart.json.CmdResponse;
import com.hipad.smart.json.QueryDeviceInfoResponse;
import com.hipad.smart.json.Response;
import com.hipad.smart.json.UpdateDeviceInfoResponse;
import com.hipad.smart.service.Cmd;
import com.hipad.smart.service.Service;
import com.hipad.smart.service.ServiceImpl;

/**
 * represent one device.
 * @author wangbaoming
 *
 */
public abstract class Device {
	@Expose
	@SerializedName("aliasName")
	/*private*/ String mName;
	@Expose
	@SerializedName("catagoryID")
	/*private*/ int mCategory;
	@Expose
	@SerializedName("smartDeviceID")
	/*private*/ String mDeviceId;
	@Expose
	@SerializedName("igatewayID")
	/*private*/ String mGateway;
	@Expose
	@SerializedName("manufacturerName")
	/*private*/ String mVender;
	@Expose
	@SerializedName("deviceSpec")
	/*private*/ String mModel;
	
	@Expose (serialize = false, deserialize = false)
	private transient Service mService = new ServiceImpl();
	
	Device() {
	}
	
	public Device(String gateway, String device) {
		mGateway = gateway;
		mDeviceId = device;
	}
	
	public String getName() {
		return mName;
	}

	public int getCategory() {
		return mCategory;
	}
	
	public String getDeviceId(){
		return mDeviceId;
	}
	
	public String getGateway(){
		return mGateway;
	}
	
	public String getModel(){
		return mModel;
	}
	
	public String getVender(){
		return mVender;
	}

	public void getDeviceInfo(ResponseResultHandler<QueryDeviceInfoResponse> handler){
		mService.getDeviceInfo(mGateway, mDeviceId, handler);
	}
	public void sendCmd(Cmd cmd, ResponseResultHandler<CmdResponse> handler){
		mService.managerDevice(mDeviceId, mGateway, cmd, handler);
	}
	public void remove(ResponseResultHandler<Response> handler){
		mService.delDevice(mDeviceId, mGateway, handler);
	}
	public void rename(String newName, ResponseResultHandler<UpdateDeviceInfoResponse> handler){
		mService.updateDeviceInfo(mDeviceId, mGateway, newName, handler);
	}
	
	/**
	 * device category.
	 * @author wangbaoming
	 *
	 */
	public static class Category {
		public final static int KongTiao = 1;  
		public final static int JingShuiJi = 2; 
		public final static int DianShan = 3; 
		public final static int LenFengShang = 4; 
		public final static int QuNuanQi = 5; 
		public final static int WeiBoLu = 6; 
		public final static int DianCiLu = 7; 
		public final static int DianFanBao = 8; 
		public final static int DuoTouZao = 9; 
		public final static int BinXiang = 10; 
		public final static int XiYiJi = 11; 
		public final static int KaFeiJi = 12; 
		public final static int ShuiHu = 13; 		
		public final static int KongQiJIngHuaQi = 14; 
		
		public final static int ZhiNengChaZhuo = 17; 
		
		public final static int DengGuangTiaojie = 96; 
		public final static int YinYueBeijing = 97; 
		public final static int ChuangLian = 98;
		
		public final static int YingErCheng = 128; 
		public final static int XieYaYi = 129; 
		public final static int XinLuJi = 130; 
		public final static int JianKangCheng = 131; 
		public final static int ZhiNengShouBiao = 132; 
		public final static int ZhiNengShouHuan = 133; 
		
		public final static int KeShiMenLing = 160; 
		public final static int YanWuBaoJinQi = 161; 		
		public final static int IpCamera = 162; 		

		public final static int DianShiJi = 32; 
		public final static int PC = 33; 
		public final static int YinXiang = 34; 
		
		public final static int UNKNOWN = 0;
	}
}
