/**
 * ResetFilterWorkedTimeCmd.java
 */
package com.hipad.smart.airpurifier;

import com.hipad.smart.util.MsgBodyData;

/**
 * cmd to reset the filter worked time.
 * @author wangbaoming
 *
 */
public class ResetFilterWorkedTimeCmd extends AirPurifierCmd {
	
	@Override
	public MsgBodyData getBody() {
		return null;
	}
	
	@Override
	public byte getCmdNo() {
		return CMD_RESET_FILTER_WORKED_TIME;
	}

}
