/**
 * SpecificResetTimerToKeepTempCCmd.java
 */
package com.hipad.smart.kettle.v14;

import com.hipad.smart.util.MsgBodyData;

/**
 * 	in specific mode, the cmd to reset the temperature to be kept.
 * @author wangbaoming
 *
 */
public class SpecificResetTimerToKeepTempCCmd extends KettleCmd {
	
	@Override
	public MsgBodyData getBody() {
		return null;
	}
	
	@Override
	public byte getCmdNo() {
		return CMD_SPECIFIC_RESET_TEMP_C_TO_BE_KEPT;
	}

}
