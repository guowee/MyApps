/**
 * SpecificResetTempToBeKept.java
 */
package com.hipad.smart.kettle.v14;

import com.hipad.smart.util.MsgBodyData;

/**
 * reset the work record that how long the kettle has worked.
 * @author wangbaoming
 *
 */
public class ResetWorkedRecordCmd extends KettleCmd {
	
	@Override
	public MsgBodyData getBody() {
		return null;
	}

	@Override
	public byte getCmdNo() {
		return CMD_CLEAN_WORKED_RECORD;
	}
}
