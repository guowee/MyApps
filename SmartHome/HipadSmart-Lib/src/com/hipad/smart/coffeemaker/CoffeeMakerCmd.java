/**
 * CoffeeMakerCmd.java 2014年11月21日
 */
package com.hipad.smart.coffeemaker;

import com.hipad.smart.service.Cmd;
import com.hipad.smart.util.MsgBodyData;

/**
 * the cmd to coffee maker.
 * @author wangbaoming
 *
 */
public abstract class CoffeeMakerCmd extends Cmd {

	public final static byte CMD_QUERY = 0x01;
	public final static byte CMD_MAKE_COFFEE = 0x02;
	public final static byte CMD_PAUSE = 0x03;
	public final static byte CMD_CONTINUE = 0x04;
	public final static byte CMD_SELFCHECK = 0x05;
	public final static byte CMD_WASH = 0x06;
	public final static byte CMD_CLEAN_SERVED_TIMES = 0x07;
	
	public final static byte CMD_VIRTUAL_KEY = 0x0A;
	
	public final static byte NOTIFY = 0x10;
	
	@Override
	public byte getFlag() {
		return 0;
	}
}
