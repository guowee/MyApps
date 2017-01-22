/**
 * $(#) NegativeIonCmd.java 2015-1-14
 */
package com.hipad.smart.airpurifier;

import com.hipad.smart.util.MsgBodyData;

/**
 * @author wangbaoming
 *
 */
public class NegativeIonCmd extends AirPurifierCmd {
	private NegativeIonCmdBody mCmdBody = new NegativeIonCmdBody();

	@Override
	public MsgBodyData getBody() {
		return mCmdBody;
	}
	
	@Override
	public byte getCmdNo() {
		return AirPurifierCmd.CMD_NEGATIVE_ION;
	}	

	public void setNegativeIon(boolean on){
		mCmdBody.setNegativeIon(on);
	}
	
	private static class NegativeIonCmdBody extends MsgBodyData{
		private final static int SIZE = 1;

		public NegativeIonCmdBody() {
			super(SIZE);
		}
		
		public void setNegativeIon(boolean on){
			setByte(0, (byte) (on ? 1 : 0));
		}
	}
}
