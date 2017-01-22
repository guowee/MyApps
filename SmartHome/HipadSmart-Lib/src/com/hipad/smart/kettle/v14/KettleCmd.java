/**
 * KettleCmd.java 2014-12-16
 */
package com.hipad.smart.kettle.v14;

import com.hipad.smart.service.Cmd;
import com.hipad.smart.util.MsgBodyData;

/**
 * represents one cmd to kettle.
 * @author wangbaoming
 *
 */
public abstract class KettleCmd extends Cmd {
	public final static byte CMD_QUERY = 0x01;
	public final static byte CMD_OK_BOIL = 0x02;
	public final static byte CMD_SPECIFIC_BOIL = 0x06;
	public final static byte CMD_SPECIFIC_RESET_TEMP_C_TO_BE_KEPT = 0x07;
	public final static byte CMD_CLEAN_WORKED_RECORD = 0x08;
	public final static byte CMD_SELFCHECK = 0x09;
	public final static byte CMD_LIGHT = 0x0B;
	
	public final static byte NOTIFY = 0x10;
	
	@Override
	public byte getFlag() {
		return 0; // not bypass
	}
}
