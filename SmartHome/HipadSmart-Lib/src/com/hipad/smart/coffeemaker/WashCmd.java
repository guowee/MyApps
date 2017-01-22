/**
 * WashCmd.java 2014-12-17
 */
package com.hipad.smart.coffeemaker;

import com.hipad.smart.util.MsgBodyData;

/**
 * @author wangbaoming
 *
 */
public class WashCmd extends CoffeeMakerCmd {

	
	@Override
	public MsgBodyData getBody() {
		return null;
	}

	
	@Override
	public byte getCmdNo() {
		return CMD_WASH;
	}

}
