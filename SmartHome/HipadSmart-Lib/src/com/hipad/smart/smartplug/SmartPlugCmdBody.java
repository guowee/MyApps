/**
 * SmartPlugCmdBody.java 2014年11月21日
 */
package com.hipad.smart.smartplug;

import com.hipad.smart.util.MsgBodyData;

/**
 * command body to smart plug.
 * @author wangbaoming
 *
 */
public class SmartPlugCmdBody extends MsgBodyData {
	private final static int SIZE = 1;

	public SmartPlugCmdBody() {
		super(SIZE);
	}
	
	/**
	 * set the power state.
	 * @param on
	 * 		true, power on; false, power off
	 */
	public void setPower(boolean on){
		setByte(0, (byte) (on ? 1 : 0));
	}
}
