/**
 * SmartPlugResponseBody.java 2014年11月21日
 */
package com.hipad.smart.smartplug;

import com.hipad.smart.util.MsgBodyData;

/**
 * the response to the command from smart plug. 
 * @author wangbaoming
 *
 */
public class SmartPlugStatusInfo extends MsgBodyData {

	public SmartPlugStatusInfo(byte[] data) {
		super(data);
	}
	
	public SmartPlugStatusInfo(String dataBase64) {
		super(dataBase64);
	}
	
	/**
	 * judge if the device is power on.
	 * @return
	 * 		true if power on; false otherwise
	 */
	public boolean isPowerOn(){
		return 0 != getByte(0);
	}

	public void dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("isPowerOn: " + isPowerOn());
		System.out.println("dump:\n" + sb.toString());
	}
}
