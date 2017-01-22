/**
 * SocketCmd.java 2015-3-19
 */
package com.hipad.smart.socket;

import com.hipad.smart.service.Cmd;
import com.hipad.smart.util.MsgBodyData;

/**
 * cmd to smart plug.
 * @author wangbaoming
 *
 */
public class SocketCmd extends Cmd {
	public final static byte CMD_QUERY = 0x01;
	public final static byte CMD_SWITCH = 0x02;	
	
	private SocketCmdBody mCmdBody;
	
	public SocketCmd() {
		mCmdBody = new SocketCmdBody();
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
	 * control the plug state on socket. 
	 * @param index
	 * 		the plug index, base 0
	 * @param on
	 * 		if true, turn the plug on; off otherwise 
	 * 		
	 */
	public void setPlugState(int index, boolean on){
		mCmdBody.setPlugState(index, on);
	}
	
}
