/**
 * UdpBroadcast.java 2014-9-11
 */

package com.hipad.varsock;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.util.Log;

/**
 * @author wangbaoming
 *
 */
public class UdpMulticast {	
	private final static String TAG = "UdpBroadcast";
	
	private final String mIp;
	private final int mPort;
	
	private MulticastSocket mMulticastSocket;
	private InetAddress mBroadcastAddr;	
	
	private UdpMulticast(String ip, int port){
		mIp = ip;
		mPort = port;
		
		try {
			mMulticastSocket = new MulticastSocket(mPort);
			mBroadcastAddr = InetAddress.getByName(mIp); 
			mMulticastSocket.joinGroup(mBroadcastAddr);
		} catch (IOException e) {
			e.printStackTrace();
			mMulticastSocket.close();
		}
	}
	
	static UdpMulticast create(String ip, int port){
		UdpMulticast instance = new UdpMulticast(ip, port);
		
		return instance;
	}	
	
	public DatagramPacket receive() throws IOException{
		Log.e(TAG, "receive");
		if(isClosed()) reconnect();
		
		byte[] data = new byte[CommonSetting.DATA_PACK_MAX_SIZE];
		DatagramPacket dataPack = new DatagramPacket(data, data.length);
		
		mMulticastSocket.receive(dataPack);
		
		return dataPack;
	}
	
	public void send(byte[] data) throws IOException {
		Log.e(TAG, "broadcast");
		if(isClosed()) reconnect();
		
		DatagramPacket dataPack = new DatagramPacket(data, data.length, mBroadcastAddr, mPort);

		mMulticastSocket.send(dataPack);
	}
	
	public String keyString() {
		return keyString(mIp, mPort);
	}
	
	public void reconnect(){
		try {
			mMulticastSocket = new MulticastSocket(mPort);
			mBroadcastAddr = InetAddress.getByName(mIp); 
			mMulticastSocket.joinGroup(mBroadcastAddr);
		} catch (IOException e) {
			e.printStackTrace();
			mMulticastSocket.close();
		}
	}
	
	public boolean isClosed(){
		return mMulticastSocket.isClosed();
	}
	
	public void close(){
		try {
			mMulticastSocket.leaveGroup(mBroadcastAddr);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			mMulticastSocket.disconnect();
			mMulticastSocket.close();
		}
	}	
	
	public final static String keyString(String ip, int port) {
		final String key = String.format("%s:%d", ip, port);
		return key;
	}
	
}
