/**
 * $(#) PowerCmd.java 2015-1-13
 */
package com.hipad.smart.airpurifier;

import com.hipad.smart.util.MsgBodyData;

/**
 * @author wangbaoming
 *
 */
public class PowerCmd extends AirPurifierCmd {
	
	private PowerCmdBody mCmdBody;
	
	public PowerCmd() {
		mCmdBody = new PowerCmdBody();
	}
	
	@Override
	public MsgBodyData getBody() {
		return mCmdBody;
	}
	
	@Override
	public byte getCmdNo() {
		return CMD_POWER;
	}	

	public void setPower(boolean on){
		mCmdBody.setPower(on);
	}
	
	private static class PowerCmdBody extends MsgBodyData{
		private final static int SIZE = 1;

		public PowerCmdBody() {
			super(SIZE);
		}
		
		public void setPower(boolean on){
			setByte(0, (byte) (on ? 1 : 0));
		}
	}

}
