package com.hipad.varsock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class CtrlSocket {
	private Socket mSocket;
	private String mIp;
	private int mPort;
	
	private InputStream mIns;
	private OutputStream mOs;
	
	private CtrlSocket(String ip, int port) {
		mIp = ip;
		mPort = port;
		
		mSocket = new Socket();	
	}
	
	static CtrlSocket create(String ip, int port){
		return new CtrlSocket(ip, port);
	}
	
	private boolean init(){
		try {
			mSocket.bind(null);
			mSocket.connect(new InetSocketAddress(mIp, mPort));
			
			mIns = mSocket.getInputStream();
			mOs = mSocket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				mSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * check if the connection is alive. if not, try to make it alive. 
	 */
	private synchronized void precheck(){

		if(!isConnected()) init();
		if(isClosed()) connect();
	}
	
	public boolean send(byte[] data){
		precheck();
		if(null == mOs) return false;
		
		try {
			mOs.write(data);
			mOs.flush();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				mSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			return false;
		}
		
		return true;
	}
	
	public byte[] receive(){
		precheck();
		if(null == mIns) return null;
		
		byte[] data = new byte[CommonSetting.DATA_PACK_MAX_SIZE];
		
		try {
			int len = mIns.read(data, 0, CommonSetting.DATA_PACK_MAX_SIZE);
			if(-1 == len) {
				mSocket.close();
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				mSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			return null;
		}
		
		return data;
	}
	
	public boolean isConnected(){
		return mSocket.isConnected();
	}
	
	private boolean isClosed(){
		return mSocket.isClosed();
	}
	
	private boolean connect(){
		mSocket = new Socket();		
		
		return init();
	}
	
	public void close(){
		try {
			if(null != mIns) mIns.close();
			if(null != mOs) mOs.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String keyString(){
		final String key = String.format("%s:%d", mIp, mPort);
		return key;
	}
	
	public final static String keyString(String ip, int port) {
		final String key = String.format("%s:%d", ip, port);
		return key;
	}
	
	public String getIp(){
		return mIp;
	}
	
	public int getPort(){
		return mPort;
	}
}
