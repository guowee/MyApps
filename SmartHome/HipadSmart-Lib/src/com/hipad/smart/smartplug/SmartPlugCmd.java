/**
 * SmartPlugCmd.java 2014年11月21日
 */
package com.hipad.smart.smartplug;

import com.hipad.smart.service.Cmd;
import com.hipad.smart.util.MsgBodyData;

/**
 * cmd to smart plug.
 * @author wangbaoming
 *
 */
public class SmartPlugCmd extends Cmd {
	public final static byte CMD_QUERY = 0x01;
	public final static byte CMD_SWITCH = 0x02;	
	
	private SmartPlugCmdBody mCmdBody;
	
	public SmartPlugCmd() {
		mCmdBody = new SmartPlugCmdBody();
	}
	
	@Override
	public MsgBodyData getBody() {
		return mCmdBody;
	}

	@Override
	public byte getFlag() {
		// not bypass
		return 0;
	}
	
	@Override
	public byte getCmdNo() {
		return CMD_SWITCH;
	}

	/**
	 * switch the device on/off.
	 * @param on
	 * 		if true, switch the device on; off otherwise
	 */
	public void switCh(boolean on){
		mCmdBody.setPower(on);
	}
}
