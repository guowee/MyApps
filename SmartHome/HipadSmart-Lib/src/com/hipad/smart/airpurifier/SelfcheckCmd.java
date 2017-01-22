/**
 * SelfcheckCmd.java
 */
package com.hipad.smart.airpurifier;

import com.hipad.smart.util.MsgBodyData;

/**
 * selfcheck cmd.
 * @author wangbaoming
 *
 */
public class SelfcheckCmd extends AirPurifierCmd {
	
	@Override
	public MsgBodyData getBody() {
		return null;
	}
	
	@Override
	public byte getCmdNo() {
		return CMD_SELFCHECK;
	}

}
