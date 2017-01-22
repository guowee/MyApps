/**
 * $(#) NegativeIonCmd.java 2015-1-14
 */
package com.hipad.smart.airpurifier;

import com.hipad.smart.util.MsgBodyData;

/**
 * @author wangbaoming
 *
 */
public class PhotocatalysisCmd extends AirPurifierCmd {
	private PhotocatalysisBody mCmdBody = new PhotocatalysisBody();

	@Override
	public MsgBodyData getBody() {
		return mCmdBody;
	}
	
	@Override
	public byte getCmdNo() {
		return AirPurifierCmd.CMD_NEGATIVE_ION;
	}	

	public void setPhotocatalysis(boolean on){
		mCmdBody.setPhotocatalysis(on);
	}
	
	private static class PhotocatalysisBody extends MsgBodyData{
		private final static int SIZE = 1;

		public PhotocatalysisBody() {
			super(SIZE);
		}
		
		public void setPhotocatalysis(boolean on){
			setByte(0, (byte) (on ? 1 : 0));
		}
	}
}
