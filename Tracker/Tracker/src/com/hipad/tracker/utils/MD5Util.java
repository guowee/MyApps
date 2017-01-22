package com.hipad.tracker.utils;

import java.security.MessageDigest;

public class MD5Util {
	
	public static String encoding(String code){
		StringBuffer sb=new StringBuffer();
		if(code==null){
			return null;
		}
		try{
			MessageDigest digest=MessageDigest.getInstance("MD5");
			byte[] bt=digest.digest(code.getBytes("UTF-8"));
			for (int i = 0; i < bt.length; i++) {
				int n=bt[i];
				if(n<0){
					n+=256;
				}
				if(n<16){
					sb.append("0");
				}
				sb.append(Integer.toHexString(n));
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			sb=null;
		}
		return sb.toString();
	}

}
