/**
 * $(#) NegativeIonCmd.java 2015-1-14
 */
package com.hipad.smart.airpurifier;

import com.hipad.smart.util.MsgBodyData;

/**
 * @author wangbaoming
 *
 */
public class HumidifyCmd extends AirPurifierCmd {
	private HumidifyCmdBody mCmdBody = new HumidifyCmdBody();

	@Override
	public MsgBodyData getBody() {
		return mCmdBody;
	}
	
	@Override
	public byte getCmdNo() {
		return AirPurifierCmd.CMD_NEGATIVE_ION;
	}	

	public void setHumidification(boolean on){
		mCmdBody.setHumidification(on);
	}
	
	private static class HumidifyCmdBody extends MsgBodyData{
		private final static int SIZE = 1;

		public HumidifyCmdBody() {
			super(SIZE);
		}
		
		public void setHumidification(boolean on){
			setByte(0, (byte) (on ? 1 : 0));
		}
	}
}
