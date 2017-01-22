/**
 * Kettle.java 2014-12-16
 */
package com.hipad.smart.kettle;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.http.HttpUtil.ResponseResultHandler;
import com.hipad.smart.json.CmdResponse;
import com.hipad.smart.kettle.v14.KettleCmd;

/**
 * represent the kettle device.
 * @author wangbaoming
 *
 */
public class Kettle {
	private CommonDevice mDevice;
	
	public Kettle(CommonDevice device) {
		mDevice = device;
	}
	
	private void sendCmd(KettleCmd kettleCmd, ResponseResultHandler<CmdResponse> handler){
		mDevice.sendCmd(kettleCmd, handler);
	}
}
