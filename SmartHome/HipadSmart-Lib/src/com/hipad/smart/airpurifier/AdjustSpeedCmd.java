/**
 * $(#) AdjustSpeedCmd.java 2015-1-13
 */
package com.hipad.smart.airpurifier;

import com.hipad.smart.util.MsgBodyData;

/**
 * @author wangbaoming
 *
 */
public class AdjustSpeedCmd extends AirPurifierCmd {
	private AdjustSpeedCmdBody mCmdBody;
	
	public AdjustSpeedCmd() {
		mCmdBody = new AdjustSpeedCmdBody();
	}
	
	@Override
	public MsgBodyData getBody() {
		return mCmdBody;
	}

	@Override
	public byte getCmdNo() {
		return CMD_ADJUST_SPEED;
	}
	
	/**
	 * set the wind speed level.
	 * @see {@link AirPurifierStatusInfo#SPEED_LOW} and other SPEED_XXX.
	 * @param speed
	 * 		the speed level
	 */
	public void setSpeedLevel(byte speed){
		mCmdBody.setSpeedLevel(speed);
	}

	private static class AdjustSpeedCmdBody extends MsgBodyData{
		private final static int SIZE = 1;

		public AdjustSpeedCmdBody() {
			super(SIZE);
		}
		
		public void setSpeedLevel(byte speed){
			setByte(0, speed);
		}
	}
}
