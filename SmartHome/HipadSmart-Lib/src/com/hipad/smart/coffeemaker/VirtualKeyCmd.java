/**
 * VirtualKeyCmd.java 2014-12-17
 */
package com.hipad.smart.coffeemaker;

import com.hipad.smart.util.MsgBodyData;

/**
 * @author wangbaoming
 *
 */
public class VirtualKeyCmd extends CoffeeMakerCmd {
	public final static byte KEY_NONE = 0;
	public final static byte KEY_BIG = 1;
	public final static byte KEY_SMALL = 2;

	private VirtualKeyCmdBody mCmdBody;
	
	public VirtualKeyCmd() {
		mCmdBody = new VirtualKeyCmdBody();
	}
	
	@Override
	public MsgBodyData getBody() {
		return null;
	}

	public void setKey(byte key){
		mCmdBody.setKey(key);
	}
	
	@Override
	public byte getCmdNo() {
		return CMD_VIRTUAL_KEY;
	}

	private final static class VirtualKeyCmdBody extends MsgBodyData{

		public VirtualKeyCmdBody() {
			super(new byte[]{0});
		}
		
		public void setKey(byte key){
			setByte(0, key);
		}
		
	}
}
