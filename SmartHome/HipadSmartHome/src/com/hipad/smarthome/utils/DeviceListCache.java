/**
 * $(#)DeviceListCache.java 2015Äê4ÔÂ14ÈÕ
 */
package com.hipad.smarthome.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.device.CommonDevice.Group;
import com.hipad.smart.http.HttpUtil;
import com.hipad.smart.json.QueryDevicesResponse;
import com.hipad.smart.json.util.GsonInstance;
import com.hipad.smart.service.Service;
import com.hipad.smart.service.ServiceImpl;
import com.hipad.smarthome.MyApplication;


/**
 * @author wangbaoming
 *
 */
public class DeviceListCache {
	private Context mContext;
	private WeakReference<ArrayList<CommonDevice>> mWeakRefDeviceList = new WeakReference<ArrayList<CommonDevice>>(null);
	
	private static DeviceListCache sInstance;
	
	private final HttpUtil.ResponseResultHandler<QueryDevicesResponse> mDeviceListHandler = new HttpUtil.ResponseResultHandler<QueryDevicesResponse>() {
		
		@Override
		public void handle(boolean timeout, QueryDevicesResponse response) {
			if(!timeout && null != response && response.isSuccessful()){
				ArrayList<CommonDevice> deviceList = response.getData();
				synchronized (this) {
					mWeakRefDeviceList = new WeakReference<ArrayList<CommonDevice>>(deviceList);
				}
				

				File devListCacheFile = getDevListCacheFile();
				if(devListCacheFile.exists()){
					devListCacheFile.delete();
				}
				FileOutputStream fos = null;
				try {
					devListCacheFile.createNewFile();
					fos = new FileOutputStream(devListCacheFile);
					String json = GsonInstance.get().toJson(deviceList);					
					fos.write(json.getBytes());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
					if(null != fos){
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
			}			
		}
	};
	
	private File getDevListCacheFile(){
		File userDir = new File(mContext.getCacheDir() + File.separator + MyApplication.user.getName());
		if(!userDir.exists()) userDir.mkdirs();
		File devListCacheFile = new File(userDir, "device_list");
		return devListCacheFile;
	}
	
	private DeviceListCache() {
	}
	
	public void init(Context appContext){
		mContext = appContext;
	}
	
	public synchronized static DeviceListCache getInstance(){
		if(null == sInstance){
			sInstance = new DeviceListCache();
		}
		
		return sInstance;
	}
	
	public void syncDeviceList(){
		Service service = new ServiceImpl();
		// sync all devices
		service.getDevices("", null, mDeviceListHandler);
	}
	
	public CommonDevice getDevice(String id){
		ArrayList<CommonDevice> deviceList = getDeviceListFromMemCache();
		
		for(CommonDevice device : deviceList){
			if(device.getDeviceId().equals(id)){
				return device;	
			}
		}
		
		return null;
	}
	
	private synchronized ArrayList<CommonDevice> getDeviceListFromMemCache(){
		ArrayList<CommonDevice> deviceList = mWeakRefDeviceList.get();
		if(null == deviceList){
			deviceList = getDeviceListFromSdCache();
			mWeakRefDeviceList = new WeakReference<ArrayList<CommonDevice>>(deviceList);
		}
		
		return deviceList;
	}
	
	private ArrayList<CommonDevice> getDeviceListFromSdCache(){
		ArrayList<CommonDevice> deviceList = null;
		// read the list from cache
		File devListCacheFile = getDevListCacheFile();
		if(devListCacheFile.exists()){
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(devListCacheFile);
				InputStreamReader isr = new InputStreamReader(fis);
				
				StringBuilder sb = new StringBuilder();
				char[] buff = new char[512];
				int len = -1;
				while(-1 != (len = isr.read(buff)) ){
					sb.append(buff, 0, len);
				}
				isr.close();
				
				Type listType = new TypeToken<ArrayList<CommonDevice>>() {}.getType();

				String json = sb.toString();
				deviceList = GsonInstance.get().fromJson(json, listType);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				if(null != fis){
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			deviceList = new ArrayList<CommonDevice>(0);
		}			
		
		return deviceList;
	}
	
	public ArrayList<CommonDevice> getDeviceList(Group group){
		ArrayList<CommonDevice> deviceList = getDeviceListFromMemCache();
		
		ArrayList<CommonDevice> groupedList = new ArrayList<CommonDevice>();
		for(CommonDevice device : deviceList){
			if(device.getGroup().equals(group)){
				groupedList.add(device);	
			}
		}
		
		return groupedList;
	}
}
