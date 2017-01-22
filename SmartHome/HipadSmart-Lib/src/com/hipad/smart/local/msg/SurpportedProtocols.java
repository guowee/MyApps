/**
 * SurpportedProtocols.java 2014-9-28
 */
package com.hipad.smart.local.msg;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangbaoming
 *
 */
public class SurpportedProtocols {
	
	private static Map<String, Protocol> sSurpportedProtocols = new HashMap<String, Protocol>();
	static {
		sSurpportedProtocols.put(DeviceProtocolV15.DEVICE_PROTOCOL_V15, new DeviceProtocolV15());
	}
	
	public static Protocol get(String name){
		return sSurpportedProtocols.get(name);
	}
}
