/**
 * $(#) AirPurifierCmd.java 2015-1-13
 */
package com.hipad.smart.airpurifier;

import com.hipad.smart.service.Cmd;

/** air purifier cmd.
 * @author wangbaoming
 *
 */
public abstract class AirPurifierCmd extends Cmd {
	public final static byte CMD_QUERY = 0x01;
	public final static byte CMD_POWER = 0x02;
	public final static byte CMD_CHANGE_MODE = 0x03;
	public final static byte CMD_ADJUST_SPEED = 0x04;
	public final static byte CMD_NEGATIVE_ION = 0x05;
	public final static byte CMD_PHOTOCATALYSIS = 0x06;
	public final static byte CMD_HUMIDIFY = 0x07;
	public final static byte CMD_ORDER_TO_POWER_OFF = 0x08;
	public final static byte CMD_SELFCHECK = 0x09;
	public final static byte CMD_RESET_FILTER_WORKED_TIME = 0x0A;
	
	public final static byte NOTIFY = 0x10;
	
	@Override
	public byte getFlag() {
		return 0; // not bypass
	}

}
