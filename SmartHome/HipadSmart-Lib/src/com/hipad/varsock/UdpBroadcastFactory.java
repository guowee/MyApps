package com.hipad.varsock;

import java.util.Hashtable;

public class UdpBroadcastFactory {
	private Hashtable<String, UdpBroadcast> mRepository;
	
	private static UdpBroadcastFactory mInstance;
	private UdpBroadcastFactory() {
		mRepository = new Hashtable<String, UdpBroadcast>(8);
	}
		
	public synchronized static UdpBroadcastFactory getInstance(){
		if(null == mInstance){
			mInstance = new UdpBroadcastFactory();
		}
		return mInstance;
	}
	
	public UdpBroadcast getSocket(int port){
		final String key = UdpBroadcast.keyString(port);
		UdpBroadcast sock = mRepository.get(key);
		if(null == sock){
			sock = create(port);
			mRepository.put(key, sock);
		}
		
		return sock;
	}
	
	public UdpBroadcast remove(UdpBroadcast sock){
		return mRepository.remove(sock.keyString());
	}
	
	private UdpBroadcast create(int port){
		return UdpBroadcast.create(port, true);
	}
}
