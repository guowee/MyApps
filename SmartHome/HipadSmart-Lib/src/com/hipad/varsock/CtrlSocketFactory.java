package com.hipad.varsock;

import java.util.Hashtable;



public class CtrlSocketFactory{	
	private Hashtable<String, CtrlSocket> mRepository;
	
	private static CtrlSocketFactory mInstance;
	private CtrlSocketFactory(){
		mRepository = new Hashtable<String, CtrlSocket>(8);
	}
	
	public synchronized static CtrlSocketFactory getInstance(){
		if(null == mInstance){
			mInstance = new CtrlSocketFactory();
		}
		
		return mInstance;
	}
	
	public CtrlSocket get(String ip, int port){
		final String key = CtrlSocket.keyString(ip, port);
		CtrlSocket sock = mRepository.get(key);
		if(null == sock){
			sock = create(ip, port);
			mRepository.put(key, sock);
		}
		
		return sock;
	}
	
	public CtrlSocket remove(CtrlSocket sock){
		return mRepository.remove(sock.keyString());
		
	}
	
	private CtrlSocket create(String ip, int port){
		return CtrlSocket.create(ip, port);
	}
}
