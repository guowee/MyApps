/**
 * SocketCmdBody.java 2015-3-19
 */
package com.hipad.smart.socket;

import com.hipad.smart.util.MsgBodyData;

/**
 * command body to smart socket.
 * @author wangbaoming
 *
 */
public class SocketCmdBody extends MsgBodyData {

	public SocketCmdBody() {
		super(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
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
		setByte(index, (byte) (on ? 1 : 0));
	}
}
