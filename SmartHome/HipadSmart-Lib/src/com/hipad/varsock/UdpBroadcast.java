/**
 * UdpBroadcast.java 2014-9-11
 */

package com.hipad.varsock;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.util.Log;

/**
 * @author wangbaoming
 *
 */
public class UdpBroadcast {	
	private final static String TAG = "UdpBroadcast";
	
	private final int mPort;
	
	private MulticastSocket mBroadcastSocket;
	private InetAddress mBroadcastAddr;	
	
	private boolean mIsAllInterfaceAddress = false;
	
	private UdpBroadcast(int port, boolean allInterface){
		mPort = port;
		mIsAllInterfaceAddress = allInterface;
		
		try {
			mBroadcastSocket = new MulticastSocket(mPort);
			mBroadcastAddr = getBroadcastAddress(); 
		} catch (IOException e) {
			e.printStackTrace();
			mBroadcastSocket.close();
		}
	}	
	
	static UdpBroadcast create(int port, boolean allInterface){
		UdpBroadcast instance = new UdpBroadcast(port, allInterface);
		
		return instance;
	}	
	
	public DatagramPacket receive() throws IOException{
		Log.e(TAG, "receive");
		if(isClosed() || mBroadcastAddr.isLoopbackAddress()) reconnect();
		
		byte[] data = new byte[CommonSetting.DATA_PACK_MAX_SIZE];
		DatagramPacket dataPack = new DatagramPacket(data, data.length);
		
		mBroadcastSocket.receive(dataPack);
		
		return dataPack;
	}
	
	public void send(byte[] data) throws IOException {
		Log.e(TAG, "broadcast");
		if(isClosed() || mBroadcastAddr.isLoopbackAddress()) reconnect();		
		
		DatagramPacket dataPack = new DatagramPacket(data, data.length, mBroadcastAddr, mPort);

		mBroadcastSocket.send(dataPack);
	}
	
	public String keyString() {
		return keyString(mPort);
	}
	
	public void reconnect(){
		try {
			mBroadcastSocket = new MulticastSocket(mPort);
			mBroadcastAddr = getBroadcastAddress(); 
		} catch (IOException e) {
			e.printStackTrace();
			mBroadcastSocket.close();
		}
	}
	
	public boolean isClosed(){
		return mBroadcastSocket.isClosed();
	}
	
	public void close(){
		mBroadcastSocket.disconnect();
		mBroadcastSocket.close();
	}	
	
	public final static String keyString(int port) {
		final String key = String.format(":%d", port);
		return key;
	}
	

	private InetAddress getBroadcastAddress() {
		if(mIsAllInterfaceAddress){
			try {
				return InetAddress.getByName("255.255.255.255");
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return null; // will not reach here
			} 
		}
			
		try {
			Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces();
			while( niEnum.hasMoreElements()) {
				NetworkInterface ni = niEnum.nextElement();
				if (!ni.isLoopback()) {
					for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
						if (interfaceAddress.getBroadcast() != null) {
							Log.d(TAG, "broadcast ip: " + interfaceAddress.getBroadcast().toString());
							return interfaceAddress.getBroadcast();
						}
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		try {
			return InetAddress.getByName(null);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			
			return null; // will not reach here
		}
	}
	
}
