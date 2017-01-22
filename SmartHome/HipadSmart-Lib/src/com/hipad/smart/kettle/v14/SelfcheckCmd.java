/**
 * SelfcheckCmd.java
 */
package com.hipad.smart.kettle.v14;

import com.hipad.smart.util.MsgBodyData;

/**
 * selfcheck cmd.
 * @author wangbaoming
 *
 */
public class SelfcheckCmd extends KettleCmd {
	
	@Override
	public MsgBodyData getBody() {
		return null;
	}
	
	@Override
	public byte getCmdNo() {
		return CMD_SELFCHECK;
	}

}
