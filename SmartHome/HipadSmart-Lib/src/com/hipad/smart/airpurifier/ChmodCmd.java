/**
 * $(#) ChmodCmd.java 2015-1-13
 */
package com.hipad.smart.airpurifier;

import com.hipad.smart.util.MsgBodyData;

/**
 * @author wangbaoming
 *
 */
public class ChmodCmd extends AirPurifierCmd {
	private ChmodCmdBody mCmdBody;
	
	public ChmodCmd() {
		mCmdBody = new ChmodCmdBody();
	}
	
	@Override
	public MsgBodyData getBody() {
		return mCmdBody;
	}

	@Override
	public byte getCmdNo() {
		return CMD_CHANGE_MODE;
	}
	
	/**
	 * set the mode.
	 * @see {@link AirPurifierStatusInfo#MODE_NORMAL} and other MODE_XXX.
	 * @param mode
	 * 		the mode
	 */
	public void setMode(byte mode){
		mCmdBody.setMode(mode);
	}

	private static class ChmodCmdBody extends MsgBodyData{
		private final static int SIZE = 1;

		public ChmodCmdBody() {
			super(SIZE);
		}
		
		public void setMode(byte mode){
			setByte(0, mode);
		}
	}
}
